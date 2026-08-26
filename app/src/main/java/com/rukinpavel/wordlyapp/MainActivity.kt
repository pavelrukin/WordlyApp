package com.rukinpavel.wordlyapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.rukinpavel.wordlyapp.core.navigation.GameRoute
import com.rukinpavel.wordlyapp.core.navigation.OnboardingRoute
import com.rukinpavel.wordlyapp.core.navigation.SettingsRoute
import com.rukinpavel.wordlyapp.core.ui.WordlyTheme
import com.rukinpavel.wordlyapp.feature.game.GameScreen
import com.rukinpavel.wordlyapp.feature.settings.OnboardingScreen
import com.rukinpavel.wordlyapp.feature.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        configureOrientation()

        setContent {
            val viewModel: AppViewModel = hiltViewModel()
            val isTutorialCompleted by viewModel.isTutorialCompleted.collectAsStateWithLifecycle()
            val language by viewModel.language.collectAsStateWithLifecycle()

            val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            DisposableEffect(dispatcherOwner, onBackPressedDispatcher) {
                val inputRef = object {
                    lateinit var input: BridgingNavigationEventInput
                }
                val callback = object : OnBackPressedCallback(false) {
                    override fun handleOnBackStarted(backEvent: BackEventCompat) {
                        inputRef.input.backStarted(backEvent.toNavigationEvent())
                    }

                    override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                        inputRef.input.backProgressed(backEvent.toNavigationEvent())
                    }

                    override fun handleOnBackPressed() {
                        inputRef.input.backCompleted()
                    }

                    override fun handleOnBackCancelled() {
                        inputRef.input.backCancelled()
                    }
                }

                inputRef.input = BridgingNavigationEventInput(callback)
                dispatcherOwner.navigationEventDispatcher.addInput(inputRef.input)
                onBackPressedDispatcher?.addCallback(callback)

                onDispose {
                    callback.remove()
                }
            }

            LaunchedEffect(language) {
                languageManager.applyLanguage(language)
            }

            CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides dispatcherOwner) {
                WordlyTheme {
                    val backStack = rememberNavBackStack(GameRoute)

                    LaunchedEffect(isTutorialCompleted) {
                        if (!isTutorialCompleted && !backStack.contains(OnboardingRoute)) {
                            backStack.add(OnboardingRoute)
                        }
                    }

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry<GameRoute> {
                                GameScreen(
                                    onSettingsClick = { backStack.add(SettingsRoute) }
                                )
                            }
                            entry<SettingsRoute> {
                                SettingsScreen(
                                    onBackClick = { backStack.removeLastOrNull() },
                                    onNavigateToOnboarding = { backStack.add(OnboardingRoute) }
                                )
                            }
                            entry<OnboardingRoute> {
                                OnboardingScreen(
                                    onComplete = {
                                        viewModel.completeTutorial()
                                        backStack.remove(OnboardingRoute)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private class BridgingNavigationEventInput(
        private val callback: OnBackPressedCallback
    ) : NavigationEventInput() {
        override fun onHasEnabledHandlersChanged(hasEnabledHandlers: Boolean) {
            callback.isEnabled = hasEnabledHandlers
        }

        fun backStarted(event: NavigationEvent) = dispatchOnBackStarted(event)
        fun backProgressed(event: NavigationEvent) = dispatchOnBackProgressed(event)
        fun backCancelled() = dispatchOnBackCancelled()
        fun backCompleted() = dispatchOnBackCompleted()
    }

    private fun configureOrientation() {
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600

        requestedOrientation = if (isTablet) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}

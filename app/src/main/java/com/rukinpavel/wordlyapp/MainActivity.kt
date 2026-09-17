package com.rukinpavel.wordlyapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventInput
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import androidx.navigationevent.compose.rememberNavigationEventDispatcherOwner
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.play.core.install.model.ActivityResult
import com.rukinpavel.wordlyapp.core.navigation.FeatureNavGraph
import com.rukinpavel.wordlyapp.core.navigation.GameRoute
import com.rukinpavel.wordlyapp.core.navigation.OnboardingRoute
import com.rukinpavel.wordlyapp.core.platform.android.UpdateManager
import com.rukinpavel.wordlyapp.core.ui.LocalAppLocale
import com.rukinpavel.wordlyapp.core.ui.LocalLocalizedContext
import com.rukinpavel.wordlyapp.core.ui.WordlyTheme
import com.rukinpavel.wordlyapp.core.ui.getAppLocale
import com.rukinpavel.wordlyapp.core.ui.localizedContext
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navGraph: FeatureNavGraph

    @Inject
    lateinit var updateManager: UpdateManager

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                // Обновление принято
            }
            RESULT_CANCELED -> {
                // Обновление отклонено пользователем
            }
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                // Ошибка обновления
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateManager.setupLauncher(updateResultLauncher)

        if (BuildConfig.DEBUG) {
            val testDeviceIds = listOf("6C495C26EF9C34868949A080DE386002")
            val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
        }

        MobileAds.initialize(this)
        enableEdgeToEdge()

        configureOrientation()

        setContent {
            val viewModel: AppViewModel = hiltViewModel()
            val isTutorialCompleted by viewModel.isTutorialCompleted.collectAsStateWithLifecycle()
            val language by viewModel.language.collectAsStateWithLifecycle()

            val locale =
                remember(language) {
                    getAppLocale(language)
                }

            val context = LocalContext.current
            val localizedContext =
                remember(context, locale) {
                    context.localizedContext(locale)
                }

            val dispatcherOwner = rememberNavigationEventDispatcherOwner(parent = null)
            val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

            DisposableEffect(dispatcherOwner, onBackPressedDispatcher) {
                val inputRef =
                    object {
                        lateinit var input: BridgingNavigationEventInput
                    }
                val callback =
                    object : OnBackPressedCallback(false) {
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

            CompositionLocalProvider(
                LocalNavigationEventDispatcherOwner provides dispatcherOwner,
                LocalAppLocale provides locale,
                LocalLocalizedContext provides localizedContext,
            ) {
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
                        entryProvider = { key ->
                            navGraph.getEntry(
                                key = key,
                                onNavigate = { route -> backStack.add(route as NavKey) },
                                onBack = {
                                    if (key is OnboardingRoute) {
                                        viewModel.completeTutorial()
                                        backStack.remove(OnboardingRoute)
                                    } else {
                                        backStack.removeLastOrNull()
                                    }
                                },
                            )
                        },
                    )
                }
            }
        }
    }

    private class BridgingNavigationEventInput(private val callback: OnBackPressedCallback) : NavigationEventInput() {
        override fun onHasEnabledHandlersChanged(hasEnabledHandlers: Boolean) {
            callback.isEnabled = hasEnabledHandlers
        }

        fun backStarted(event: NavigationEvent) = dispatchOnBackStarted(event)

        fun backProgressed(event: NavigationEvent) = dispatchOnBackProgressed(event)

        fun backCancelled() = dispatchOnBackCancelled()

        fun backCompleted() = dispatchOnBackCompleted()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun configureOrientation() {
        val isTablet = resources.configuration.smallestScreenWidthDp >= 600

        requestedOrientation =
            if (isTablet) {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
    }
}

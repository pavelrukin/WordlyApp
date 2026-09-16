package com.rukinpavel.wordlyapp.feature.settings.impl.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.core.model.SubscriptionOption
import com.rukinpavel.wordlyapp.core.model.SubscriptionType
import com.rukinpavel.wordlyapp.core.ui.R as CoreUiR
import com.rukinpavel.wordlyapp.core.ui.localizedString
import com.rukinpavel.wordlyapp.feature.settings.impl.BuildConfig
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                SettingsEffect.NavigateToOnboarding -> onNavigateToOnboarding()
            }
        }
    }

    SettingsContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    state: SettingsState,
    onIntent: (SettingsIntent) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            SettingsHeader(onBackClick = onBackClick)
        },
    ) { padding ->
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            state.language?.let { currentLang ->
                LanguageSetting(
                    selectedLanguage = currentLang,
                    onLanguageChange = { onIntent(SettingsIntent.OnLanguageChange(it)) },
                )
            }

            HorizontalDivider()

            VibrationSetting(
                vibrationEnabled = state.vibrationEnabled,
                onVibrationChange = { onIntent(SettingsIntent.OnVibrationChange(it)) },
            )

            HorizontalDivider()

            PremiumSetting(
                isPremium = state.isPremium,
                options = state.subscriptionOptions,
                onPurchaseClick = { onIntent(SettingsIntent.OnPurchasePremiumClick(it)) },
            )

            HorizontalDivider()

            TutorialSetting(
                onRepeatTutorialClick = { onIntent(SettingsIntent.OnRepeatTutorialClick) },
            )

            Spacer(modifier = Modifier.weight(1f))

            AboutSetting()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsHeader(onBackClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text(localizedString(CoreUiR.string.settings), fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = localizedString(CoreUiR.string.back),
                )
            }
        },
    )
}

@Composable
private fun LanguageSetting(
    selectedLanguage: Language,
    onLanguageChange: (Language) -> Unit,
) {
    var isLanguageExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isLanguageExpanded = !isLanguageExpanded }
            .padding(bottom = 16.dp),
    ) {
        Text(
            text = localizedString(CoreUiR.string.language_dictionary),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )
        val currentLanguageName = when (selectedLanguage) {
            Language.EN -> localizedString(CoreUiR.string.lang_en)
            Language.RU -> localizedString(CoreUiR.string.lang_ru)
            Language.UK -> localizedString(CoreUiR.string.lang_uk)
        }
        Text(
            text = currentLanguageName,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
        )
    }

    AnimatedVisibility(
        visible = isLanguageExpanded,
        enter = expandVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeIn(),
        exit = shrinkVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeOut(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Language.entries.forEach { language ->
                val isSelected = language == selectedLanguage
                LanguageItem(
                    language = language,
                    isSelected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            isLanguageExpanded = false
                            onLanguageChange(language)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun VibrationSetting(
    vibrationEnabled: Boolean,
    onVibrationChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = localizedString(CoreUiR.string.vibration),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = localizedString(CoreUiR.string.vibration_description),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = vibrationEnabled,
            onCheckedChange = onVibrationChange,
        )
    }
}

@Composable
private fun PremiumSetting(
    isPremium: Boolean,
    options: List<SubscriptionOption>,
    onPurchaseClick: (SubscriptionOption) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
    ) {
        var isExpanded by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { isExpanded = !isExpanded },
            ) {
                Text(
                    text = localizedString(CoreUiR.string.premium),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = localizedString(CoreUiR.string.premium_description),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (isPremium) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text(
                        text = localizedString(CoreUiR.string.active).uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }

        val transition = updateTransition(targetState = isExpanded, label = "SubscriptionExpand")
        val progress by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 600, easing = FastOutSlowInEasing) },
            label = "progress",
        ) { state ->
            if (state) 1f else 0f
        }

        var rowWidth by remember { mutableFloatStateOf(0f) }
        val buttonCenters = remember { mutableStateMapOf<String, Float>() }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeIn(),
            exit = shrinkVertically(animationSpec = tween(400, easing = FastOutSlowInEasing)) + fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            rowWidth = coordinates.size.width.toFloat()
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    options.forEach { option ->
                        val typeText = when (option.type) {
                            SubscriptionType.WEEKLY -> localizedString(CoreUiR.string.weekly_subscription)
                            SubscriptionType.MONTHLY -> localizedString(CoreUiR.string.monthly_subscription)
                            SubscriptionType.YEARLY -> localizedString(CoreUiR.string.yearly_subscription)
                        }
                        Button(
                            onClick = {
                                if (isExpanded) {
                                    onPurchaseClick(option)
                                }
                            },
                            enabled = isExpanded,
                            modifier = Modifier
                                .weight(1f)
                                .onGloballyPositioned { coordinates ->
                                    val localX = coordinates.positionInParent().x
                                    val localCenter = localX + coordinates.size.width / 2f
                                    buttonCenters[option.id] = localCenter
                                }
                                .graphicsLayer {
                                    val currentCenter = buttonCenters[option.id] ?: (rowWidth / 2f)
                                    val targetCenter = rowWidth / 2f
                                    val totalTranslationX = targetCenter - currentCenter

                                    this.translationX = totalTranslationX * (1f - progress)
                                    this.scaleX = 0.3f + 0.7f * progress
                                    this.scaleY = 0.3f + 0.7f * progress
                                    this.alpha = progress
                                },
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 4.dp),
                            ) {
                                val splitText = typeText.split(" ")
                                if (splitText.size >= 2) {
                                    Text(
                                        text = splitText[0],
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                    )
                                    Text(
                                        text = splitText.drop(1).joinToString(" "),
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                    )
                                } else {
                                    Text(
                                        text = typeText,
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        minLines = 2,
                                    )
                                }
                                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                                Text(
                                    text = option.price,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorialSetting(onRepeatTutorialClick: () -> Unit) {
    Button(
        onClick = onRepeatTutorialClick,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors =
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Text(localizedString(CoreUiR.string.repeat_tutorial), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AboutSetting() {
    Text(
        text = "Version ${BuildConfig.APP_VERSION_NAME}",
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )
}

@Composable
fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val displayName =
        when (language) {
            Language.EN -> localizedString(CoreUiR.string.lang_en)
            Language.RU -> localizedString(CoreUiR.string.lang_ru)
            Language.UK -> localizedString(CoreUiR.string.lang_uk)
        }

    Surface(
        modifier =
        Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        color =
        if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        tonalElevation = if (isSelected) 4.dp else 0.dp,
    ) {
        Row(
            modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = displayName,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color =
                if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = localizedString(CoreUiR.string.cd_selected),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

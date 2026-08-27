package com.rukinpavel.wordlyapp.core.ui

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.rukinpavel.wordlyapp.core.model.Language
import java.util.Locale

/**
 * CompositionLocal for the current app locale.
 */
val LocalAppLocale = staticCompositionLocalOf {
    Locale.ENGLISH
}

/**
 * CompositionLocal for the localized context.
 */
val LocalLocalizedContext = staticCompositionLocalOf<Context> {
    error("Localized Context is not provided")
}

/**
 * Creates a localized context based on the provided locale.
 */
fun Context.localizedContext(locale: Locale): Context {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration)
}

/**
 * Resolves the app locale based on the selected language or system default.
 */
fun getAppLocale(language: Language?): Locale {
    return if (language != null) {
        Locale.forLanguageTag(language.code)
    } else {
        val systemLocale = Locale.getDefault()
        if (systemLocale.language in listOf("en", "ru", "uk")) {
            systemLocale
        } else {
            Locale.ENGLISH
        }
    }
}

/**
 * A localized version of stringResource that uses LocalLocalizedContext.
 */
@Composable
@ReadOnlyComposable
fun localizedString(@StringRes id: Int): String {
    return LocalLocalizedContext.current.getString(id)
}

/**
 * A localized version of stringResource with arguments that uses LocalLocalizedContext.
 */
@Composable
@ReadOnlyComposable
fun localizedString(@StringRes id: Int, vararg formatArgs: Any): String {
    return LocalLocalizedContext.current.getString(id, *formatArgs)
}

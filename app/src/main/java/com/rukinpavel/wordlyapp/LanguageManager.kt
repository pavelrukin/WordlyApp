package com.rukinpavel.wordlyapp

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.rukinpavel.wordlyapp.core.model.Language
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor() {

    fun applyLanguage(language: Language?) {
        val languageCode = language?.code ?: getSupportedSystemLanguageCode()
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        Log.d("LanguageManager", "applyLanguage: language=$language, code=$languageCode, current=${currentLocales.toLanguageTags()}")
        if (currentLocales.toLanguageTags() != languageCode) {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocale)
        }
    }

    private fun getSupportedSystemLanguageCode(): String {
        val systemLocale = Locale.getDefault().language
        return if (systemLocale in listOf("en", "ru", "uk")) {
            systemLocale
        } else {
            "en"
        }
    }
}

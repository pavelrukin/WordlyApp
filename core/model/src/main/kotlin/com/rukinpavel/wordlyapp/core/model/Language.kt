package com.rukinpavel.wordlyapp.core.model

enum class Language(val code: String) {
    EN("en"),
    RU("ru"),
    UK("uk");

    companion object {
        fun fromCode(code: String): Language {
            return entries.find { it.code == code } ?: EN
        }

        fun getSystemLanguage(): Language {
            val systemLocale = java.util.Locale.getDefault().language
            return when (systemLocale) {
                "ru" -> Language.RU
                "uk" -> Language.UK
                else -> Language.EN
            }
        }
    }
}

package com.rukinpavel.wordlyapp.domain

import com.rukinpavel.wordlyapp.core.model.Language

interface WordRepository {
    suspend fun getRandomWord(language: Language): String
}

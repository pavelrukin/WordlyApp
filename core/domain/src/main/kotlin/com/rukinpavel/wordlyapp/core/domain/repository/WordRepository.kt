package com.rukinpavel.wordlyapp.core.domain.repository

import com.rukinpavel.wordlyapp.core.model.Language

interface WordRepository {
    suspend fun getRandomWord(language: Language): String
}

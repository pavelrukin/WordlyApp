package com.rukinpavel.wordlyapp.domain

interface WordRepository {
    suspend fun getRandomWord(): String
}

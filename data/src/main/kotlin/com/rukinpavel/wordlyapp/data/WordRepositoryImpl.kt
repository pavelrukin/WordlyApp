package com.rukinpavel.wordlyapp.data

import android.content.Context
import com.rukinpavel.wordlyapp.domain.WordRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class WordRepositoryImpl @Inject constructor() : WordRepository {
    private val words = listOf(
        "APPLE", "BEACH", "CHAIR", "DANCE", "EAGLE",
        "FLAME", "GRAPE", "HOUSE", "IMAGE", "JUICE",
        "KNIFE", "LEMON", "MUSIC", "NIGHT", "OCEAN",
        "PIANO", "QUEEN", "RIVER", "SNAKE", "TABLE",
        "UNCLE", "VOICE", "WATER", "YOUNG", "ZEBRA"
    )

    override suspend fun getRandomWord(): String {
        return words[Random.nextInt(words.size)]
    }
    
}

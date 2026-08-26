package com.rukinpavel.wordlyapp.data

import com.rukinpavel.wordlyapp.core.model.Language
import com.rukinpavel.wordlyapp.domain.WordRepository
import javax.inject.Inject
import kotlin.random.Random

class WordRepositoryImpl @Inject constructor() : WordRepository {
    private val enWords = listOf(
        "APPLE", "BEACH", "CHAIR", "DANCE", "EAGLE",
        "FLAME", "GRAPE", "HOUSE", "IMAGE", "JUICE",
        "KNIFE", "LEMON", "MUSIC", "NIGHT", "OCEAN",
        "PIANO", "QUEEN", "RIVER", "SNAKE", "TABLE",
        "UNCLE", "VOICE", "WATER", "YOUNG", "ZEBRA"
    )

    private val ruWords = listOf(
        "АВТОР", "БАНКА", "ВЕЧЕР", "ГОРОД", "ДОМИК",
        "ЗАКОН", "КНИГА", "ЛАМПА", "МУЗЫКА", "НАУКА",
        "ОКЕАН", "ПИСЬМО", "РАБОТА", "СОЛНЦЕ", "ТРАВА",
        "УГОЛЬ", "ФИЛЬМ", "ХОЛОД", "ЦВЕТОК", "ШКОЛА"
    )

    private val uaWords = listOf(
        "БІЛОК", "ВІТЕР", "ГОЛКА", "ДИСКО", "ЗЕМЛЯ",
        "КНИГА", "ЛИМОН", "МЕТРО", "НАУКА", "ОЖИНА",
        "ПІСНЯ", "РУЧКА", "СОНЦЕ", "ТРАВА", "УМОВА",
        "ФІКУС", "ХВИЛЯ", "ЦУКОР", "ШАПКА", "ЯНГОЛ"
    )

    override suspend fun getRandomWord(language: Language): String {
        val words = when (language) {
            Language.EN -> enWords
            Language.RU -> ruWords
            Language.UK -> uaWords
        }.filter { it.length == 5 }
        
        if (words.isEmpty()) return ""
        
        return words[Random.nextInt(words.size)]
    }
}

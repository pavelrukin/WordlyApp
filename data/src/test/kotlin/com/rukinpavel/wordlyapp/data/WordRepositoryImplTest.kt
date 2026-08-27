package com.rukinpavel.wordlyapp.data

import com.rukinpavel.wordlyapp.core.model.Language
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WordRepositoryImplTest {

    private val repository = WordRepositoryImpl()

    @Test
    fun `getRandomWord returns 5-letter word for EN`() = runTest {
        val word = repository.getRandomWord(Language.EN)
        assertEquals(5, word.length)
        assertTrue(word.all { it.isLetter() })
    }

    @Test
    fun `getRandomWord returns 5-letter word for RU`() = runTest {
        val word = repository.getRandomWord(Language.RU)
        assertEquals(5, word.length)
    }

    @Test
    fun `getRandomWord returns 5-letter word for UK`() = runTest {
        val word = repository.getRandomWord(Language.UK)
        assertEquals(5, word.length)
    }
}

package com.rukinpavel.wordlyapp.core.domain.game

import com.rukinpavel.wordlyapp.core.model.LetterState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GameEngineTest {

    private lateinit var engine: GameEngine

    @Before
    fun setUp() {
        engine = GameEngine()
    }

    @Test
    fun `correct letter at correct position is CORRECT`() {
        val result = engine.checkGuess("CRANE", "CRANE")
        assertEquals(List(5) { LetterState.CORRECT }, result)
    }

    @Test
    fun `letter in wrong position is WRONG_POSITION`() {
        val result = engine.checkGuess("CRANE", "NARCO")
        assertEquals(LetterState.WRONG_POSITION, result[0]) // C
    }

    @Test
    fun `absent letter is NOT_IN_WORD`() {
        val result = engine.checkGuess("ZZZZZ", "CRANE")
        assertEquals(List(5) { LetterState.NOT_IN_WORD }, result)
    }

    @Test
    fun `duplicate letters handled correctly`() {
        // TARGET=ABBEY, GUESS=KEEPS -> only first E is WRONG_POSITION
        val result = engine.checkGuess("KEEPS", "ABBEY")
        assertEquals(LetterState.NOT_IN_WORD, result[0]) // K
        assertEquals(LetterState.WRONG_POSITION, result[1]) // first E
        assertEquals(LetterState.NOT_IN_WORD, result[2]) // second E
        assertEquals(LetterState.NOT_IN_WORD, result[3]) // P
        assertEquals(LetterState.NOT_IN_WORD, result[4]) // S
    }

    @Test
    fun `pickHintIndex returns null when all revealed`() {
        val allRevealed = mapOf(0 to 'A', 1 to 'B', 2 to 'C', 3 to 'D', 4 to 'E')
        assertEquals(null, engine.pickHintIndex(5, allRevealed))
    }

    @Test
    fun `buildFullGuess merges hints and typed correctly`() {
        val hints = mapOf(1 to 'R', 3 to 'N')
        val typed = "CAE"
        val result = engine.buildFullGuess(typed, hints, 5)
        // pos 0: C, 1: R(hint), 2: A, 3: N(hint), 4: E
        assertEquals("CRANE", result)
    }
}

package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.core.model.LetterState
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckGuessUseCaseTest {

    private val useCase = CheckGuessUseCase()

    @Test
    fun `when guess is exactly the same as target, all letters are CORRECT`() {
        val target = "APPLE"
        val guess = "APPLE"
        val expected = listOf(
            LetterState.CORRECT,
            LetterState.CORRECT,
            LetterState.CORRECT,
            LetterState.CORRECT,
            LetterState.CORRECT
        )
        
        val result = useCase(target, guess)
        
        assertEquals(expected, result)
    }

    @Test
    fun `when guess shares no letters with target, all letters are NOT_IN_WORD`() {
        val target = "APPLE"
        val guess = "ROBOT"
        val expected = listOf(
            LetterState.NOT_IN_WORD,
            LetterState.NOT_IN_WORD,
            LetterState.NOT_IN_WORD,
            LetterState.NOT_IN_WORD,
            LetterState.NOT_IN_WORD
        )
        
        val result = useCase(target, guess)
        
        assertEquals(expected, result)
    }

    @Test
    fun `when guess has letters in wrong position, they are marked WRONG_POSITION`() {
        val target = "APPLE"
        val guess = "PLEAS"
        // P (0) -> in target (1, 2) -> WP
        // L (1) -> in target (3) -> WP
        // E (2) -> in target (4) -> WP
        // A (3) -> in target (0) -> WP
        // S (4) -> not in target -> NIW
        
        val expected = listOf(
            LetterState.WRONG_POSITION,
            LetterState.WRONG_POSITION,
            LetterState.WRONG_POSITION,
            LetterState.WRONG_POSITION,
            LetterState.NOT_IN_WORD
        )
        
        val result = useCase(target, guess)
        
        assertEquals(expected, result)
    }

    @Test
    fun `when guess has multiple instances of a letter, it correctly handles counts`() {
        val target = "APPLE"
        val guess = "PAPAS"
        // Target: A P P L E
        // Guess:  P A P A S
        // P(0): WP
        // A(1): WP
        // P(2): C
        // A(3): NIW (only one A in target, already used)
        // S(4): NIW
        
        val expected = listOf(
            LetterState.WRONG_POSITION,
            LetterState.WRONG_POSITION,
            LetterState.CORRECT,
            LetterState.NOT_IN_WORD,
            LetterState.NOT_IN_WORD
        )
        
        val result = useCase(target, guess)
        
        assertEquals(expected, result)
    }

    @Test
    fun `when guess is mixed case, it still works correctly`() {
        val target = "Apple"
        val guess = "aPpLe"
        val expected = listOf(
            LetterState.CORRECT,
            LetterState.CORRECT,
            LetterState.CORRECT,
            LetterState.CORRECT,
            LetterState.CORRECT
        )
        
        val result = useCase(target, guess)
        
        assertEquals(expected, result)
    }
}

package com.rukinpavel.wordlyapp.domain.usecase

import com.rukinpavel.wordlyapp.core.model.LetterState
import javax.inject.Inject

class CheckGuessUseCase @Inject constructor() {
    operator fun invoke(targetWord: String, guessedWord: String): List<LetterState> {
        val target = targetWord.uppercase()
        val guess = guessedWord.uppercase()
        val result = MutableList(target.length) { LetterState.NOT_IN_WORD }
        
        val targetLetterCount = mutableMapOf<Char, Int>()
        for (char in target) {
            targetLetterCount[char] = targetLetterCount.getOrDefault(char, 0) + 1
        }

        // First pass: mark correct positions
        for (i in guess.indices) {
            if (guess[i] == target[i]) {
                result[i] = LetterState.CORRECT
                targetLetterCount[guess[i]] = targetLetterCount[guess[i]]!! - 1
            }
        }

        // Second pass: mark wrong positions
        for (i in guess.indices) {
            if (result[i] != LetterState.CORRECT) {
                val char = guess[i]
                if (targetLetterCount.getOrDefault(char, 0) > 0) {
                    result[i] = LetterState.WRONG_POSITION
                    targetLetterCount[char] = targetLetterCount[char]!! - 1
                }
            }
        }

        return result
    }
}

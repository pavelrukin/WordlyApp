package com.rukinpavel.wordlyapp.core.domain.game

import com.rukinpavel.wordlyapp.core.model.LetterState
import javax.inject.Inject

class GameEngine @Inject constructor() {

    /**
     * Проверяет введённое слово [guess] против загаданного [targetWord].
     * Возвращает список состояний для каждой позиции.
     * Алгоритм: сначала помечает CORRECT, затем WRONG_POSITION (без двойного счёта).
     */
    fun checkGuess(guess: String, targetWord: String): List<LetterState> {
        val result = MutableList(guess.length) { LetterState.NOT_IN_WORD }
        val targetChars = targetWord.toMutableList()

        // Первый проход: CORRECT
        for (i in guess.indices) {
            if (guess[i] == targetWord[i]) {
                result[i] = LetterState.CORRECT
                targetChars[i] = '\u0000' // помечаем использованной
            }
        }

        // Второй проход: WRONG_POSITION
        for (i in guess.indices) {
            if (result[i] != LetterState.CORRECT) {
                val idx = targetChars.indexOf(guess[i])
                if (idx != -1) {
                    result[i] = LetterState.WRONG_POSITION
                    targetChars[idx] = '\u0000'
                }
            }
        }

        return result
    }

    /**
     * Обновляет приоритет состояния клавиши на клавиатуре.
     * CORRECT имеет наивысший приоритет, NOT_IN_WORD — наименьший.
     */
    fun shouldUpdateKeyboardState(oldState: LetterState?, newState: LetterState): Boolean {
        if (oldState == null) return true
        if (oldState == LetterState.CORRECT) return false
        if (newState == LetterState.CORRECT) return true
        if (oldState == LetterState.WRONG_POSITION) return false
        if (newState == LetterState.WRONG_POSITION) return true
        return false
    }

    /**
     * Выбирает случайный индекс для подсказки из нераскрытых позиций [wordLength].
     * Возвращает null, если все позиции уже раскрыты.
     */
    fun pickHintIndex(wordLength: Int, revealedHints: Map<Int, Char>): Int? {
        val unknownIndices = (0 until wordLength).filter { !revealedHints.containsKey(it) }
        return unknownIndices.randomOrNull()
    }

    /**
     * Собирает полное слово из введённых [typedLetters] и раскрытых [hints].
     * Подсказки вставляются на свои позиции, введённые буквы — на оставшиеся.
     */
    fun buildFullGuess(typedLetters: String, hints: Map<Int, Char>, wordLength: Int): String {
        val sb = StringBuilder()
        var typedIdx = 0
        for (i in 0 until wordLength) {
            if (hints.containsKey(i)) {
                sb.append(hints[i])
            } else if (typedIdx < typedLetters.length) {
                sb.append(typedLetters[typedIdx++])
            }
        }
        return sb.toString()
    }
}

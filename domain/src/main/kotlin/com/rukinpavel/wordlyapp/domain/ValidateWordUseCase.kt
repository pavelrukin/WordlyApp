package com.rukinpavel.wordlyapp.domain

import javax.inject.Inject

class ValidateWordUseCase @Inject constructor() {
    operator fun invoke(word: String): Boolean {
        return word.length == 5
    }
}

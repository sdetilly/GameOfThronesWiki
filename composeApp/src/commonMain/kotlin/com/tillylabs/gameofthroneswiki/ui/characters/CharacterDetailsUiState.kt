package com.tillylabs.gameofthroneswiki.ui.characters

import com.tillylabs.gameofthroneswiki.models.CharacterWithBooks

data class CharacterDetailsUiState(
    val characterWithBooks: CharacterWithBooks? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

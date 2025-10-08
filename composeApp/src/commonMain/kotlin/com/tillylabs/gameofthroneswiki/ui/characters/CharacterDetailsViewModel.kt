package com.tillylabs.gameofthroneswiki.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.usecase.CharacterDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CharacterDetailsViewModel(
    private val characterDetailsUseCase: CharacterDetailsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharacterDetailsUiState(isLoading = true))
    val uiState: StateFlow<CharacterDetailsUiState> = _uiState.asStateFlow()

    fun loadCharacterDetails(characterUrl: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val characterWithBooks = characterDetailsUseCase.getCharacterWithBooks(characterUrl)
                if (characterWithBooks != null) {
                    _uiState.value =
                        _uiState.value.copy(
                            characterWithBooks = characterWithBooks,
                            isLoading = false,
                            error = null,
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Character not found",
                        )
                }
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred",
                    )
            }
        }
    }
}

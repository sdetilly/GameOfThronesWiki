package com.tillylabs.gameofthroneswiki.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.usecase.GetCharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CharactersViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CharactersUiState())
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    private fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val characters = getCharactersUseCase()
                _uiState.value =
                    _uiState.value.copy(
                        characters = characters,
                        isLoading = false,
                        error = null,
                        hasMoreData = getCharactersUseCase.hasMore(),
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred",
                    )
            }
        }
    }

    fun loadMoreCharacters() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreData) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            try {
                val characters = getCharactersUseCase.loadMore()
                _uiState.value =
                    _uiState.value.copy(
                        characters = characters,
                        isLoadingMore = false,
                        hasMoreData = getCharactersUseCase.hasMore(),
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoadingMore = false,
                        error = e.message ?: "Unknown error occurred",
                    )
            }
        }
    }

    fun retry() {
        loadCharacters()
    }
}

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val error: String? = null,
)

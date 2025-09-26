package com.tillylabs.gameofthroneswiki.ui.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.models.Character
import com.tillylabs.gameofthroneswiki.usecase.CharactersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CharactersViewModel(
    private val charactersUseCase: CharactersUseCase,
) : ViewModel() {
    private val isLoadingMore = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val hasMoreData = MutableStateFlow(true)

    val uiState: StateFlow<CharactersUiState> =
        combine(
            charactersUseCase.charactersFlow(),
            isLoadingMore,
            error,
            hasMoreData,
        ) { characters, isLoadingMore, error, hasMoreData ->
            CharactersUiState(
                characters = characters,
                isLoading = characters.isEmpty() && error == null,
                isLoadingMore = isLoadingMore,
                error = error,
                hasMoreData = hasMoreData,
            )
        }.catch { throwable ->
            emit(
                CharactersUiState(
                    error = throwable.message ?: "Unknown error occurred",
                ),
            )
        }.stateIn(
            scope = viewModelScope,
            started =
                SharingStarted
                    .WhileSubscribed(5000),
            initialValue = CharactersUiState(isLoading = true),
        )

    init {
        viewModelScope.launch {
            try {
                charactersUseCase.characters()
                hasMoreData.value = charactersUseCase.hasMore()
            } catch (e: Exception) {
                error.value = e.message ?: "Unknown error occurred"
            }
        }
    }

    fun loadMoreCharacters() {
        if (isLoadingMore.value || !hasMoreData.value) return

        viewModelScope.launch {
            isLoadingMore.value = true
            error.value = null

            try {
                charactersUseCase.loadMore()
                hasMoreData.value = charactersUseCase.hasMore()
                isLoadingMore.value = false
            } catch (e: Exception) {
                error.value = e.message ?: "Unknown error occurred"
                isLoadingMore.value = false
            }
        }
    }

    fun retry() {
        error.value = null
        viewModelScope.launch {
            try {
                charactersUseCase.characters()
                hasMoreData.value = charactersUseCase.hasMore()
            } catch (e: Exception) {
                error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
}

data class CharactersUiState(
    val characters: List<Character> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val error: String? = null,
)

package com.tillylabs.gameofthroneswiki.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.usecase.BooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BooksViewModel(
    private val booksUseCase: BooksUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        println("SDET: BooksViewModel initialized")
        loadBooks()
    }

    private fun loadBooks() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        booksUseCase
            .booksWithCover()
            .onEach { bookWithCovers ->
                _uiState.value =
                    _uiState.value.copy(
                        books = bookWithCovers,
                        isLoading = false,
                        error = null,
                    )
            }.catch {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error = it.message ?: "Unknown error occurred",
                    )
            }.launchIn(viewModelScope)
    }

    fun retry() {
        loadBooks()
    }
}

data class BooksUiState(
    val books: List<BookWithCover> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

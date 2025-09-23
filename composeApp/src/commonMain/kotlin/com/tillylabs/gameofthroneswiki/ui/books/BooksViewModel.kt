package com.tillylabs.gameofthroneswiki.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.models.BookWithCover
import com.tillylabs.gameofthroneswiki.usecase.BooksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BooksViewModel(
    private val booksUseCase: BooksUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    private fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val books = booksUseCase.booksWithCover()
                _uiState.value =
                    _uiState.value.copy(
                        books = books,
                        isLoading = false,
                        error = null,
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

    fun retry() {
        loadBooks()
    }
}

data class BooksUiState(
    val books: List<BookWithCover> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

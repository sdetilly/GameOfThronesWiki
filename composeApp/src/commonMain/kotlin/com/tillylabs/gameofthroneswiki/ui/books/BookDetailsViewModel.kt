package com.tillylabs.gameofthroneswiki.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.usecase.BookDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class BookDetailsViewModel(
    private val bookDetailsUseCase: BookDetailsUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BookDetailsUiState(isLoading = true))
    val uiState: StateFlow<BookDetailsUiState> = _uiState.asStateFlow()

    fun loadBookDetails(bookUrl: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val book = bookDetailsUseCase.getBookByUrl(bookUrl)
                if (book != null) {
                    _uiState.value =
                        _uiState.value.copy(
                            book = book,
                            isLoading = false,
                            error = null,
                        )
                } else {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = "Book not found",
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

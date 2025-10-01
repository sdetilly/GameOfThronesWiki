package com.tillylabs.gameofthroneswiki.ui.books

import com.tillylabs.gameofthroneswiki.models.BookWithCover

data class BookDetailsUiState(
    val book: BookWithCover? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

package com.tillylabs.gameofthroneswiki.ui.houses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.usecase.HousesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HousesViewModel(
    private val housesUseCase: HousesUseCase,
) : ViewModel() {
    private val isLoadingMore = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)
    private val hasMoreData = MutableStateFlow(true)

    val uiState: StateFlow<HousesUiState> =
        combine(
            housesUseCase.housesFlow(),
            isLoadingMore,
            error,
            hasMoreData,
        ) { houses, isLoadingMore, error, hasMoreData ->
            HousesUiState(
                houses = houses,
                isLoading = houses.isEmpty() && error == null,
                isLoadingMore = isLoadingMore,
                error = error,
                hasMoreData = hasMoreData,
            )
        }.catch { throwable ->
            emit(
                HousesUiState(
                    error = throwable.message ?: "Unknown error occurred",
                ),
            )
        }.stateIn(
            scope = viewModelScope,
            started =
                kotlinx.coroutines.flow.SharingStarted
                    .WhileSubscribed(5000),
            initialValue = HousesUiState(isLoading = true),
        )

    init {
        // Initial load to trigger database-first behavior
        viewModelScope.launch {
            try {
                housesUseCase.houses() // This will load from DB first, then refresh from API
                hasMoreData.value = housesUseCase.hasMore()
            } catch (e: Exception) {
                error.value = e.message ?: "Unknown error occurred"
            }
        }
    }

    fun loadMoreHouses() {
        if (isLoadingMore.value || !hasMoreData.value) return

        viewModelScope.launch {
            isLoadingMore.value = true
            error.value = null

            try {
                val houses = housesUseCase.loadMore()
                hasMoreData.value = housesUseCase.hasMore()
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
                housesUseCase.houses()
                hasMoreData.value = housesUseCase.hasMore()
            } catch (e: Exception) {
                error.value = e.message ?: "Unknown error occurred"
            }
        }
    }
}

data class HousesUiState(
    val houses: List<House> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val error: String? = null,
)

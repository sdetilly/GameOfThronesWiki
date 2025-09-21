package com.tillylabs.gameofthroneswiki.ui.houses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tillylabs.gameofthroneswiki.models.House
import com.tillylabs.gameofthroneswiki.usecase.GetHousesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HousesViewModel(
    private val getHousesUseCase: GetHousesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HousesUiState())
    val uiState: StateFlow<HousesUiState> = _uiState.asStateFlow()

    init {
        loadHouses()
    }

    private fun loadHouses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val houses = getHousesUseCase()
                _uiState.value =
                    _uiState.value.copy(
                        houses = houses,
                        isLoading = false,
                        error = null,
                        hasMoreData = getHousesUseCase.hasMore(),
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

    fun loadMoreHouses() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreData) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMore = true)
            try {
                val houses = getHousesUseCase.loadMore()
                _uiState.value =
                    _uiState.value.copy(
                        houses = houses,
                        isLoadingMore = false,
                        hasMoreData = getHousesUseCase.hasMore(),
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
        loadHouses()
    }
}

data class HousesUiState(
    val houses: List<House> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val error: String? = null,
)

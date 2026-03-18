package com.sedilant.cachosfridge.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val products: List<ProductEntity> = emptyList(),
    val boteCents: Int = 0,
    val isMenuOpen: Boolean = false
)

class HomeViewModel(
    private val repository: FridgeRepository
) : ViewModel() {

    private val isMenuOpen = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeProducts(),
        repository.observeBoteCents(),
        isMenuOpen
    ) { products, boteCents, menuOpen ->
        HomeUiState(
            products = products,
            boteCents = boteCents,
            isMenuOpen = menuOpen
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeedData()
        }
    }

    fun openMenu() {
        isMenuOpen.value = true
    }

    fun closeMenu() {
        isMenuOpen.value = false
    }
}


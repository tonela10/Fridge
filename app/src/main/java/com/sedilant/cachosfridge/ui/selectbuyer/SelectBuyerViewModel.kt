package com.sedilant.cachosfridge.ui.selectbuyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PersonEntity
import com.sedilant.cachosfridge.data.ProductEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

data class SelectBuyerUiState(
    val product: ProductEntity? = null,
    val people: List<PersonEntity> = emptyList()
)

class SelectBuyerViewModel(
    repository: FridgeRepository,
    productId: String
) : ViewModel() {



    val uiState: StateFlow<SelectBuyerUiState> = combine(
        flow { emit(repository.getProduct(productId)) },
        repository.observePeople()
    ) { product, people ->
        SelectBuyerUiState(product = product, people = people)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SelectBuyerUiState()
    )
}


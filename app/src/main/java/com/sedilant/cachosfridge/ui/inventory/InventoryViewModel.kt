package com.sedilant.cachosfridge.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.ProductEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class InventoryUiState(
    val products: List<ProductEntity> = emptyList()
)

class InventoryViewModel(
    private val repository: FridgeRepository
) : ViewModel() {

    val uiState: StateFlow<InventoryUiState> = repository.observeProducts().map { products ->
        InventoryUiState(products = products)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InventoryUiState()
    )

    fun increaseStock(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateStock(product.id, product.stock + 1)
        }
    }

    fun decreaseStock(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateStock(product.id, (product.stock - 1).coerceAtLeast(0))
        }
    }

    fun addNewProduct(name: String, priceCents: Int, stock: Int) {
        viewModelScope.launch {
            val newId = "product_${System.currentTimeMillis()}"
            val product = ProductEntity(
                id = newId,
                name = name,
                priceCents = priceCents,
                stock = stock,
                hasAsset = false
            )
            repository.addProduct(product)
        }
    }

    fun updatePrice(product: ProductEntity, newPriceCents: Int) {
        viewModelScope.launch {
            repository.updateProductPrice(product.id, newPriceCents)
        }
    }

    fun deleteProduct(product: ProductEntity): Boolean {
        viewModelScope.launch {
            repository.deleteProduct(product.id, product.hasAsset)
        }
        return !product.hasAsset
    }
}


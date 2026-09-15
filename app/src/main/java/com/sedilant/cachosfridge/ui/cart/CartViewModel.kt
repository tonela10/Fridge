package com.sedilant.cachosfridge.ui.cart

import androidx.lifecycle.ViewModel
import com.sedilant.cachosfridge.data.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CartItem(
    val product: ProductEntity,
    val quantity: Int
)

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val totalCents: Int = 0,
    val itemCount: Int = 0
)

class CartViewModel : ViewModel() {

    private val _cartMap = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartMap: StateFlow<Map<String, CartItem>> = _cartMap.asStateFlow()

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    private fun refreshUiState(map: Map<String, CartItem>) {
        val items = map.values.toList()
        _uiState.value = CartUiState(
            items = items,
            totalCents = items.sumOf { it.product.priceCents * it.quantity },
            itemCount = items.sumOf { it.quantity }
        )
    }

    fun addItem(product: ProductEntity) {
        _cartMap.update { current ->
            val existing = current[product.id]
            val updated = if (existing != null) {
                current + (product.id to existing.copy(quantity = existing.quantity + 1))
            } else {
                current + (product.id to CartItem(product, 1))
            }
            refreshUiState(updated)
            updated
        }
    }

    fun incrementItem(productId: String) {
        _cartMap.update { current ->
            val existing = current[productId] ?: return@update current
            val updated = current + (productId to existing.copy(quantity = existing.quantity + 1))
            refreshUiState(updated)
            updated
        }
    }

    fun decrementItem(productId: String) {
        _cartMap.update { current ->
            val existing = current[productId] ?: return@update current
            val updated = if (existing.quantity <= 1) {
                current - productId
            } else {
                current + (productId to existing.copy(quantity = existing.quantity - 1))
            }
            refreshUiState(updated)
            updated
        }
    }

    fun removeItem(productId: String) {
        _cartMap.update { current ->
            val updated = current - productId
            refreshUiState(updated)
            updated
        }
    }

    fun clearCart() {
        _cartMap.value = emptyMap()
        _uiState.value = CartUiState()
    }
}

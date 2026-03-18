package com.sedilant.cachosfridge.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PaymentMethod
import com.sedilant.cachosfridge.data.PurchaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PaymentUiState(
    val productName: String = "",
    val payerName: String = "",
    val accountCents: Int = 0,
    val totalCents: Int = 0,
    val boteCents: Int = 0,
    val canPayWithBote: Boolean = false,
    val purchaseResult: PurchaseResult? = null
)

class PaymentViewModel(
    private val repository: FridgeRepository,
    private val productId: String,
    private val personId: String
) : ViewModel() {
    private val purchaseResult = MutableStateFlow<PurchaseResult?>(null)

    val uiState: StateFlow<PaymentUiState> = combine(
        repository.observeBoteCents(),
        purchaseResult
    ) { boteCents, result ->
        val product = repository.getProduct(productId)
        val person = repository.getPerson(personId)
        val total = product?.priceCents ?: 0
        PaymentUiState(
            productName = product?.name.orEmpty(),
            payerName = person?.name.orEmpty(),
            accountCents = person?.balanceCents ?: 0,
            totalCents = total,
            boteCents = boteCents,
            canPayWithBote = boteCents >= total,
            purchaseResult = result
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaymentUiState()
    )

    fun payNow() {
        performPurchase(PaymentMethod.PAY_NOW)
    }

    fun addToAccount() {
        performPurchase(PaymentMethod.ADD_TO_ACCOUNT)
    }

    fun payWithBote() {
        performPurchase(PaymentMethod.PAY_WITH_BOTE)
    }

    fun consumeResult() {
        purchaseResult.value = null
    }

    private fun performPurchase(method: PaymentMethod) {
        viewModelScope.launch {
            purchaseResult.value = repository.purchase(productId, personId, method)
        }
    }
}


package com.sedilant.cachosfridge.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PaymentMethod
import com.sedilant.cachosfridge.data.PurchaseResult
import com.sedilant.cachosfridge.nfc.NfcManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PaymentUiState(
    val productName: String = "",
    val totalCents: Int = 0,
    val boteCents: Int = 0,
    val canPayWithBote: Boolean = false,
    val isWaitingForCard: Boolean = false,
    val cardPayerName: String? = null,
    val purchaseResult: PurchaseResult? = null,
    val isNfcAvailable: Boolean = false
)

class PaymentViewModel(
    private val repository: FridgeRepository,
    private val nfcManager: NfcManager,
    private val productId: String
) : ViewModel() {
    private val purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    private val isWaitingForCard = MutableStateFlow(false)
    private val cardPayerName = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PaymentUiState> = combine(
        repository.observeBoteCents(),
        purchaseResult,
        isWaitingForCard,
        cardPayerName
    ) { boteCents, result, waiting, payerName ->
        val product = repository.getProduct(productId)
        val total = product?.priceCents ?: 0
        PaymentUiState(
            productName = product?.name.orEmpty(),
            totalCents = total,
            boteCents = boteCents,
            canPayWithBote = boteCents >= total,
            isWaitingForCard = waiting,
            cardPayerName = payerName,
            purchaseResult = result,
            isNfcAvailable = nfcManager.isNfcAvailable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PaymentUiState()
    )

    init {
        viewModelScope.launch {
            nfcManager.tagUid.collect { uid ->
                if (isWaitingForCard.value) {
                    isWaitingForCard.value = false
                    val result = repository.purchaseWithCard(productId, uid)
                    if (result == PurchaseResult.Success) {
                        val person = repository.getPersonByNfcId(uid)
                        cardPayerName.value = person?.name
                    }
                    purchaseResult.value = result
                }
            }
        }
    }

    fun payNow() {
        viewModelScope.launch {
            val product = repository.getProduct(productId) ?: return@launch
            if (product.stock <= 0) {
                purchaseResult.value = PurchaseResult.ProductWithoutStock
                return@launch
            }
            purchaseResult.value = repository.purchase(productId, "", PaymentMethod.PAY_NOW)
        }
    }

    fun startCardPayment() {
        isWaitingForCard.value = true
        cardPayerName.value = null
        purchaseResult.value = null
    }

    fun cancelCardPayment() {
        isWaitingForCard.value = false
    }

    fun consumeResult() {
        purchaseResult.value = null
        cardPayerName.value = null
    }
}

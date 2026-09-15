package com.sedilant.cachosfridge.ui.cart

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

data class CartPaymentUiState(
    val itemCount: Int = 0,
    val totalCents: Int = 0,
    val boteCents: Int = 0,
    val canPayWithBote: Boolean = false,
    val isWaitingForCard: Boolean = false,
    val cardPayerName: String? = null,
    val cardPayerRemainingCents: Int? = null,
    val purchaseResult: PurchaseResult? = null,
    val isNfcAvailable: Boolean = false
)

class CartPaymentViewModel(
    private val repository: FridgeRepository,
    private val nfcManager: NfcManager,
    private val cartViewModel: CartViewModel
) : ViewModel() {

    private val purchaseResult = MutableStateFlow<PurchaseResult?>(null)
    private val isWaitingForCard = MutableStateFlow(false)
    private val cardPayerName = MutableStateFlow<String?>(null)
    private val cardPayerRemainingCents = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<CartPaymentUiState> = combine(
        repository.observeBoteCents(),
        cartViewModel.uiState,
        purchaseResult,
        isWaitingForCard,
        cardPayerName
    ) { boteCents, cartState, result, waiting, payerName ->
        CartPaymentUiState(
            itemCount = cartState.itemCount,
            totalCents = cartState.totalCents,
            boteCents = boteCents,
            canPayWithBote = boteCents >= cartState.totalCents,
            isWaitingForCard = waiting,
            cardPayerName = payerName,
            cardPayerRemainingCents = cardPayerRemainingCents.value,
            purchaseResult = result,
            isNfcAvailable = nfcManager.isNfcAvailable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CartPaymentUiState()
    )

    init {
        viewModelScope.launch {
            nfcManager.tagUid.collect { uid ->
                if (isWaitingForCard.value) {
                    isWaitingForCard.value = false
                    val items = cartViewModel.uiState.value.items.map { it.product.id to it.quantity }
                    val result = repository.purchaseCartWithCard(items, uid)
                    if (result == PurchaseResult.Success) {
                        val person = repository.getPersonByNfcId(uid)
                        cardPayerName.value = person?.name
                        cardPayerRemainingCents.value = person?.balanceCents
                    }
                    purchaseResult.value = result
                }
            }
        }
    }

    fun payNow() {
        viewModelScope.launch {
            val items = cartViewModel.uiState.value.items.map { it.product.id to it.quantity }
            purchaseResult.value = repository.purchaseCart(items, "", PaymentMethod.PAY_NOW)
        }
    }

    fun payWithBote() {
        viewModelScope.launch {
            val items = cartViewModel.uiState.value.items.map { it.product.id to it.quantity }
            purchaseResult.value = repository.purchaseCart(items, "", PaymentMethod.PAY_WITH_BOTE)
        }
    }

    fun startCardPayment() {
        isWaitingForCard.value = true
        cardPayerName.value = null
        cardPayerRemainingCents.value = null
        purchaseResult.value = null
    }

    fun cancelCardPayment() {
        isWaitingForCard.value = false
    }

    fun consumeResult() {
        purchaseResult.value = null
        cardPayerName.value = null
        cardPayerRemainingCents.value = null
    }
}

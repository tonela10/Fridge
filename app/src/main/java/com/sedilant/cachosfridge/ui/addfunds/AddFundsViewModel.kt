package com.sedilant.cachosfridge.ui.addfunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.AddFundsResult
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.nfc.NfcManager
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AddFundsStep {
    data object EnterAmount : AddFundsStep
    data object WaitingForCard : AddFundsStep
    data class Success(val personName: String) : AddFundsStep
    data object CardNotLinked : AddFundsStep
}

data class AddFundsUiState(
    val amount: String = "0,00",
    val step: AddFundsStep = AddFundsStep.EnterAmount,
    val isNfcAvailable: Boolean = false
)

class AddFundsViewModel(
    private val repository: FridgeRepository,
    private val nfcManager: NfcManager
) : ViewModel() {

    private val amountValue = MutableStateFlow("0,00")
    private val step = MutableStateFlow<AddFundsStep>(AddFundsStep.EnterAmount)

    val uiState: StateFlow<AddFundsUiState> = combine(
        amountValue,
        step
    ) { amount, currentStep ->
        AddFundsUiState(
            amount = amount,
            step = currentStep,
            isNfcAvailable = nfcManager.isNfcAvailable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddFundsUiState()
    )

    init {
        viewModelScope.launch {
            nfcManager.tagUid.collect { uid ->
                if (step.value == AddFundsStep.WaitingForCard) {
                    val cents = parseToCents(amountValue.value)
                    val result = repository.addFundsByNfcCard(uid, cents)
                    step.value = when (result) {
                        is AddFundsResult.Success -> AddFundsStep.Success(result.personName)
                        is AddFundsResult.CardNotLinked -> AddFundsStep.CardNotLinked
                    }
                }
            }
        }
    }

    fun onAmountChange(value: String) {
        amountValue.value = value
    }

    fun addQuickAmount(euros: Int) {
        val value = parseToCents(amountValue.value) + euros * 100
        amountValue.value = centsToInput(value)
    }

    fun startNfcScan() {
        val cents = parseToCents(amountValue.value)
        if (cents <= 0) return
        step.value = AddFundsStep.WaitingForCard
    }

    fun cancelNfcScan() {
        step.value = AddFundsStep.EnterAmount
    }

    fun resetState() {
        amountValue.value = "0,00"
        step.value = AddFundsStep.EnterAmount
    }

    private fun parseToCents(input: String): Int {
        val normalized = input.replace(',', '.').trim()
        val parsed = normalized.toBigDecimalOrNull() ?: BigDecimal.ZERO
        return parsed.multiply(BigDecimal(100)).toInt()
    }

    private fun centsToInput(cents: Int): String {
        val euros = cents / 100
        val rest = cents % 100
        return "$euros,${rest.toString().padStart(2, '0')}"
    }
}

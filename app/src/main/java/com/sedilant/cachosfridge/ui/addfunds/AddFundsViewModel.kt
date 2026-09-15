package com.sedilant.cachosfridge.ui.addfunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PersonEntity
import com.sedilant.cachosfridge.data.TopUpRequestEntity
import com.sedilant.cachosfridge.data.TopUpRequestResult
import com.sedilant.cachosfridge.nfc.NfcManager
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AddFundsStep {
    data object PoolNotConfigured : AddFundsStep
    data object ReadyToScan : AddFundsStep
    data object WaitingForCard : AddFundsStep
    data object CardNotLinked : AddFundsStep
    data object EnterAmount : AddFundsStep
    data object ShowQr : AddFundsStep
    data object Submitted : AddFundsStep
    data object AlreadyPending : AddFundsStep
    data object Error : AddFundsStep
}

data class AddFundsUiState(
    val amount: String = "0,00",
    val amountCents: Int = 0,
    val step: AddFundsStep = AddFundsStep.PoolNotConfigured,
    val paypalPoolUrl: String? = null,
    val person: PersonEntity? = null,
    val pendingRequest: TopUpRequestEntity? = null,
    val isNfcAvailable: Boolean = false
)

class AddFundsViewModel(
    private val repository: FridgeRepository,
    private val nfcManager: NfcManager
) : ViewModel() {

    private val amountValue = MutableStateFlow("0,00")
    private val step = MutableStateFlow<AddFundsStep>(AddFundsStep.ReadyToScan)
    private val person = MutableStateFlow<PersonEntity?>(null)
    private val pendingRequest = MutableStateFlow<TopUpRequestEntity?>(null)

    val uiState: StateFlow<AddFundsUiState> = combine(
        repository.observePayPalPoolUrl(),
        amountValue,
        step,
        person,
        pendingRequest
    ) { poolUrl, amount, currentStep, currentPerson, pending ->
        AddFundsUiState(
            amount = amount,
            amountCents = parseToCents(amount),
            step = if (poolUrl.isNullOrBlank()) AddFundsStep.PoolNotConfigured else currentStep,
            paypalPoolUrl = poolUrl,
            person = currentPerson,
            pendingRequest = pending,
            isNfcAvailable = nfcManager.isNfcAvailable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddFundsUiState(isNfcAvailable = nfcManager.isNfcAvailable)
    )

    init {
        viewModelScope.launch {
            nfcManager.tagUid.collect { uid ->
                if (step.value != AddFundsStep.WaitingForCard) return@collect

                val linkedPerson = repository.getPersonByNfcId(uid)
                if (linkedPerson == null) {
                    step.value = AddFundsStep.CardNotLinked
                    return@collect
                }

                person.value = linkedPerson
                val existing = repository.getPendingTopUpForPerson(linkedPerson.id)
                if (existing != null) {
                    pendingRequest.value = existing
                    step.value = AddFundsStep.AlreadyPending
                } else {
                    step.value = AddFundsStep.EnterAmount
                }
            }
        }
    }

    fun startNfcScan() {
        if (uiState.value.paypalPoolUrl.isNullOrBlank() || !nfcManager.isNfcAvailable) return
        person.value = null
        pendingRequest.value = null
        step.value = AddFundsStep.WaitingForCard
    }

    fun cancelNfcScan() {
        step.value = AddFundsStep.ReadyToScan
    }

    fun onAmountChange(value: String) {
        amountValue.value = value
    }

    fun addQuickAmount(euros: Int) {
        val value = parseToCents(amountValue.value) + euros * 100
        amountValue.value = centsToInput(value)
    }

    fun settleExactDebt() {
        val balance = person.value?.balanceCents ?: return
        if (balance < 0) amountValue.value = centsToInput(-balance)
    }

    fun showQr() {
        if (parseToCents(amountValue.value) > 0) step.value = AddFundsStep.ShowQr
    }

    fun backToAmount() {
        step.value = AddFundsStep.EnterAmount
    }

    fun submitContribution() {
        val currentPerson = person.value ?: return
        val cents = parseToCents(amountValue.value)
        viewModelScope.launch {
            when (val result = repository.createTopUpRequest(currentPerson.id, cents)) {
                is TopUpRequestResult.Success -> {
                    pendingRequest.value = result.request
                    step.value = AddFundsStep.Submitted
                }
                is TopUpRequestResult.AlreadyPending -> {
                    pendingRequest.value = result.request
                    step.value = AddFundsStep.AlreadyPending
                }
                TopUpRequestResult.PoolNotConfigured -> step.value = AddFundsStep.PoolNotConfigured
                TopUpRequestResult.InvalidAmount,
                TopUpRequestResult.PersonNotFound -> step.value = AddFundsStep.Error
            }
        }
    }

    fun resetState() {
        amountValue.value = "0,00"
        person.value = null
        pendingRequest.value = null
        step.value = AddFundsStep.ReadyToScan
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

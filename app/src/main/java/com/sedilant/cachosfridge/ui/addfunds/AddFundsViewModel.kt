package com.sedilant.cachosfridge.ui.addfunds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PersonEntity
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddFundsUiState(
    val people: List<PersonEntity> = emptyList(),
    val selectedPersonId: String = "",
    val amount: String = "0,00"
)

class AddFundsViewModel(
    repository: FridgeRepository
) : ViewModel() {

    private val peopleFlow = repository.observePeople()
    private val repo = repository
    private val selectedPersonId = MutableStateFlow("")
    private val amountValue = MutableStateFlow("0,00")

    val uiState: StateFlow<AddFundsUiState> = combine(
        peopleFlow,
        selectedPersonId,
        amountValue
    ) { people, selectedId, amount ->
        val resolvedId = if (selectedId.isBlank() && people.isNotEmpty()) {
            people.first().id
        } else {
            selectedId
        }
        AddFundsUiState(
            people = people,
            selectedPersonId = resolvedId,
            amount = amount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddFundsUiState()
    )

    fun onPersonSelected(personId: String) {
        selectedPersonId.value = personId
    }

    fun onAmountChange(value: String) {
        amountValue.value = value
    }

    fun addQuickAmount(euros: Int) {
        val value = parseToCents(amountValue.value) + euros * 100
        amountValue.value = centsToInput(value)
    }

    fun confirm(onDone: () -> Unit) {
        val personId = selectedPersonId.value
        if (personId.isBlank()) return
        val cents = parseToCents(amountValue.value)
        viewModelScope.launch {
            repo.addFunds(personId, cents)
            onDone()
        }
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


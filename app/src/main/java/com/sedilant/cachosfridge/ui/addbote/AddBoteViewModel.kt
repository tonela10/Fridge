package com.sedilant.cachosfridge.ui.addbote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AddBoteUiState(
    val amount: String = "0,00"
)

class AddBoteViewModel(
    private val repository: FridgeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBoteUiState())
    val uiState: StateFlow<AddBoteUiState> = _uiState

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amount = value)
    }

    fun addQuickAmount(euros: Int) {
        val value = parseToCents(_uiState.value.amount) + euros * 100
        _uiState.value = _uiState.value.copy(amount = centsToInput(value))
    }

    fun confirm(onDone: () -> Unit) {
        val cents = parseToCents(_uiState.value.amount)
        viewModelScope.launch {
            repository.addBote(cents)
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


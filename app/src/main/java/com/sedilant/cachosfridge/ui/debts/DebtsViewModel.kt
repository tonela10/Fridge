package com.sedilant.cachosfridge.ui.debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PersonEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DebtsUiState(
    val people: List<PersonEntity> = emptyList(),
    val totalCents: Int = 0
)

class DebtsViewModel(
    private val repository: FridgeRepository
) : ViewModel() {

    val uiState: StateFlow<DebtsUiState> = repository.observePeople().map { people ->
        DebtsUiState(
            people = people,
            totalCents = people.sumOf { it.balanceCents }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DebtsUiState()
    )

    fun liquidateDebt(personId: String) {
        viewModelScope.launch {
            repository.settleDebt(personId)
        }
    }
}


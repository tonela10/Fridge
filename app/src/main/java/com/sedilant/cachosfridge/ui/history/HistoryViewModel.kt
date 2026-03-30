package com.sedilant.cachosfridge.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PersonEntity
import com.sedilant.cachosfridge.data.TransactionEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val transactions: List<TransactionEntity> = emptyList(),
    val people: List<PersonEntity> = emptyList(),
    val selectedPersonId: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val repository: FridgeRepository
) : ViewModel() {

    private val selectedPersonId = MutableStateFlow<String?>(null)

    private val transactions = selectedPersonId.flatMapLatest { personId ->
        if (personId == null) repository.observeTransactions()
        else repository.observeTransactionsByPerson(personId)
    }

    val uiState: StateFlow<HistoryUiState> = combine(
        transactions,
        repository.observePeople(),
        selectedPersonId
    ) { txList, people, personId ->
        HistoryUiState(
            transactions = txList,
            people = people,
            selectedPersonId = personId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    fun selectPerson(personId: String?) {
        selectedPersonId.value = personId
    }
}

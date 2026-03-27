package com.sedilant.cachosfridge.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PersonEntity
import com.sedilant.cachosfridge.nfc.NfcManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminUiState(
    val people: List<PersonEntity> = emptyList(),
    val isLinkingCardForPersonId: String? = null,
    val linkResult: LinkResult? = null,
    val editingPerson: PersonEntity? = null,
    val showCreateDialog: Boolean = false,
    val showDeleteConfirm: PersonEntity? = null,
    val isNfcAvailable: Boolean = false
)

sealed interface LinkResult {
    data object Success : LinkResult
    data object AlreadyAssigned : LinkResult
}

class AdminViewModel(
    private val repository: FridgeRepository,
    private val nfcManager: NfcManager
) : ViewModel() {

    private val linkingCardForPersonId = MutableStateFlow<String?>(null)
    private val linkResult = MutableStateFlow<LinkResult?>(null)
    private val editingPerson = MutableStateFlow<PersonEntity?>(null)
    private val showCreateDialog = MutableStateFlow(false)
    private val showDeleteConfirm = MutableStateFlow<PersonEntity?>(null)

    val uiState: StateFlow<AdminUiState> = combine(
        repository.observePeople(),
        linkingCardForPersonId,
        linkResult,
        editingPerson,
        showCreateDialog
    ) { people, linkingId, result, editing, showCreate ->
        AdminUiState(
            people = people,
            isLinkingCardForPersonId = linkingId,
            linkResult = result,
            editingPerson = editing,
            showCreateDialog = showCreate,
            showDeleteConfirm = showDeleteConfirm.value,
            isNfcAvailable = nfcManager.isNfcAvailable
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AdminUiState()
    )

    init {
        viewModelScope.launch {
            nfcManager.tagUid.collect { uid ->
                val personId = linkingCardForPersonId.value ?: return@collect
                val existing = repository.getPersonByNfcId(uid)
                if (existing != null && existing.id != personId) {
                    linkResult.value = LinkResult.AlreadyAssigned
                    linkingCardForPersonId.value = null
                    return@collect
                }
                repository.linkNfcCard(personId, uid)
                linkResult.value = LinkResult.Success
                linkingCardForPersonId.value = null
            }
        }
    }

    fun createPerson(name: String, balanceCents: Int) {
        viewModelScope.launch {
            repository.createPerson(name, balanceCents)
            showCreateDialog.value = false
        }
    }

    fun updatePerson(personId: String, name: String, balanceCents: Int) {
        viewModelScope.launch {
            repository.updatePersonDetails(personId, name, balanceCents)
            editingPerson.value = null
        }
    }

    fun deletePerson(personId: String) {
        viewModelScope.launch {
            repository.deletePerson(personId)
            showDeleteConfirm.value = null
        }
    }

    fun startLinkingCard(personId: String) {
        linkResult.value = null
        linkingCardForPersonId.value = personId
    }

    fun cancelLinkingCard() {
        linkingCardForPersonId.value = null
    }

    fun unlinkCard(personId: String) {
        viewModelScope.launch {
            repository.unlinkNfcCard(personId)
        }
    }

    fun showCreateDialog() {
        showCreateDialog.value = true
    }

    fun dismissCreateDialog() {
        showCreateDialog.value = false
    }

    fun startEditing(person: PersonEntity) {
        editingPerson.value = person
    }

    fun dismissEditing() {
        editingPerson.value = null
    }

    fun showDeleteConfirm(person: PersonEntity) {
        showDeleteConfirm.value = person
    }

    fun dismissDeleteConfirm() {
        showDeleteConfirm.value = null
    }

    fun consumeLinkResult() {
        linkResult.value = null
    }
}

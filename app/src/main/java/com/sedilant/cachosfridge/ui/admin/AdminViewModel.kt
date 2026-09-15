package com.sedilant.cachosfridge.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sedilant.cachosfridge.data.FridgeRepository
import com.sedilant.cachosfridge.data.PayPalPoolUrlError
import com.sedilant.cachosfridge.data.PayPalPoolUrlValidator
import com.sedilant.cachosfridge.data.PersonEntity
import com.sedilant.cachosfridge.data.TopUpRequestEntity
import com.sedilant.cachosfridge.nfc.NfcManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val people: List<PersonEntity> = emptyList(),
    val pendingTopUps: List<TopUpRequestEntity> = emptyList(),
    val paypalPoolUrl: String = "",
    val paypalPoolUrlError: PayPalPoolUrlError? = null,
    val paypalPoolUrlSaved: Boolean = false,
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

private data class AdminFormState(
    val paypalPoolUrlOverride: String? = null,
    val paypalPoolUrlError: PayPalPoolUrlError? = null,
    val paypalPoolUrlSaved: Boolean = false,
    val linkingPersonId: String? = null,
    val linkResult: LinkResult? = null,
    val editingPerson: PersonEntity? = null,
    val showCreateDialog: Boolean = false,
    val showDeleteConfirm: PersonEntity? = null
)

private data class AdminData(
    val people: List<PersonEntity>,
    val pendingTopUps: List<TopUpRequestEntity>,
    val storedPoolUrl: String?
)

class AdminViewModel(
    private val repository: FridgeRepository,
    private val nfcManager: NfcManager
) : ViewModel() {

    private val form = MutableStateFlow(AdminFormState())
    private val data = combine(
        repository.observePeople(),
        repository.observePendingTopUpRequests(),
        repository.observePayPalPoolUrl()
    ) { people, pending, poolUrl ->
        AdminData(people, pending, poolUrl)
    }

    val uiState: StateFlow<AdminUiState> = combine(data, form) { data, form ->
        AdminUiState(
            people = data.people,
            pendingTopUps = data.pendingTopUps,
            paypalPoolUrl = form.paypalPoolUrlOverride ?: data.storedPoolUrl.orEmpty(),
            paypalPoolUrlError = form.paypalPoolUrlError,
            paypalPoolUrlSaved = form.paypalPoolUrlSaved,
            isLinkingCardForPersonId = form.linkingPersonId,
            linkResult = form.linkResult,
            editingPerson = form.editingPerson,
            showCreateDialog = form.showCreateDialog,
            showDeleteConfirm = form.showDeleteConfirm,
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
                val personId = form.value.linkingPersonId ?: return@collect
                val existing = repository.getPersonByNfcId(uid)
                if (existing != null && existing.id != personId) {
                    form.update { it.copy(linkResult = LinkResult.AlreadyAssigned, linkingPersonId = null) }
                    return@collect
                }
                repository.linkNfcCard(personId, uid)
                form.update { it.copy(linkResult = LinkResult.Success, linkingPersonId = null) }
            }
        }
    }

    fun onPayPalPoolUrlChange(value: String) {
        form.update {
            it.copy(
                paypalPoolUrlOverride = value,
                paypalPoolUrlError = null,
                paypalPoolUrlSaved = false
            )
        }
    }

    fun savePayPalPoolUrl() {
        val value = uiState.value.paypalPoolUrl.trim()
        val error = PayPalPoolUrlValidator.validate(value)
        if (error != null) {
            form.update { it.copy(paypalPoolUrlError = error, paypalPoolUrlSaved = false) }
            return
        }
        viewModelScope.launch {
            repository.updatePayPalPoolUrl(value)
            form.update {
                it.copy(
                    paypalPoolUrlOverride = null,
                    paypalPoolUrlError = null,
                    paypalPoolUrlSaved = true
                )
            }
        }
    }

    fun clearPayPalPoolUrl() {
        viewModelScope.launch {
            repository.updatePayPalPoolUrl(null)
            form.update {
                it.copy(
                    paypalPoolUrlOverride = null,
                    paypalPoolUrlError = null,
                    paypalPoolUrlSaved = false
                )
            }
        }
    }

    fun approveTopUp(requestId: String) {
        viewModelScope.launch { repository.approveTopUpRequest(requestId) }
    }

    fun rejectTopUp(requestId: String) {
        viewModelScope.launch { repository.rejectTopUpRequest(requestId) }
    }

    fun createPerson(name: String, balanceCents: Int) {
        viewModelScope.launch {
            repository.createPerson(name, balanceCents)
            form.update { it.copy(showCreateDialog = false) }
        }
    }

    fun updatePerson(personId: String, name: String, balanceCents: Int) {
        viewModelScope.launch {
            repository.updatePersonDetails(personId, name, balanceCents)
            form.update { it.copy(editingPerson = null) }
        }
    }

    fun deletePerson(personId: String) {
        viewModelScope.launch {
            repository.deletePerson(personId)
            form.update { it.copy(showDeleteConfirm = null) }
        }
    }

    fun startLinkingCard(personId: String) {
        form.update { it.copy(linkResult = null, linkingPersonId = personId) }
    }

    fun cancelLinkingCard() {
        form.update { it.copy(linkingPersonId = null) }
    }

    fun unlinkCard(personId: String) {
        viewModelScope.launch { repository.unlinkNfcCard(personId) }
    }

    fun showCreateDialog() {
        form.update { it.copy(showCreateDialog = true) }
    }

    fun dismissCreateDialog() {
        form.update { it.copy(showCreateDialog = false) }
    }

    fun startEditing(person: PersonEntity) {
        form.update { it.copy(editingPerson = person) }
    }

    fun dismissEditing() {
        form.update { it.copy(editingPerson = null) }
    }

    fun showDeleteConfirm(person: PersonEntity) {
        form.update { it.copy(showDeleteConfirm = person) }
    }

    fun dismissDeleteConfirm() {
        form.update { it.copy(showDeleteConfirm = null) }
    }

    fun consumeLinkResult() {
        form.update { it.copy(linkResult = null) }
    }
}

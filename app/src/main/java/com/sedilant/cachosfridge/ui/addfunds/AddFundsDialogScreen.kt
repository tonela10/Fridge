package com.sedilant.cachosfridge.ui.addfunds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R

@Composable
fun AddFundsDialogScreen(
    state: AddFundsUiState,
    onDismiss: () -> Unit,
    onPersonSelected: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.fondos_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val selectedName = state.people.firstOrNull { it.id == state.selectedPersonId }?.name.orEmpty()
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = { name ->
                        val person = state.people.firstOrNull { it.name == name }
                        person?.let { onPersonSelected(it.id) }
                    },
                    label = { Text(stringResource(id = R.string.fondos_persona_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = onAmountChange,
                    label = { Text(stringResource(id = R.string.importe_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { onQuickAdd(1) }, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(id = R.string.quick_1))
                    }
                    Button(
                        onClick = { onQuickAdd(2) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(stringResource(id = R.string.quick_2))
                    }
                    Button(
                        onClick = { onQuickAdd(5) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                    ) {
                        Text(stringResource(id = R.string.quick_5))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.confirmar_ingreso))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancelar))
            }
        }
    )
}


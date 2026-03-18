package com.sedilant.cachosfridge.ui.addfunds

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsDialogScreen(
    state: AddFundsUiState,
    onDismiss: () -> Unit,
    onPersonSelected: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = state.people.firstOrNull { it.id == state.selectedPersonId }?.name.orEmpty()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = stringResource(id = R.string.fondos_title))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.fondos_persona_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    state.people.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name) },
                            onClick = {
                                onPersonSelected(person.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChange,
                label = { Text(stringResource(id = R.string.importe_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { onQuickAdd(1) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.quick_1))
                }
                OutlinedButton(onClick = { onQuickAdd(2) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.quick_2))
                }
                OutlinedButton(onClick = { onQuickAdd(5) }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.quick_5))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.confirmar_ingreso))
                }
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text(stringResource(id = R.string.cancelar))
                }
            }
        }
    }
}


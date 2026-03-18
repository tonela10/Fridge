package com.sedilant.cachosfridge.ui.addbote

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
fun AddBoteDialogScreen(
    state: AddBoteUiState,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.bote_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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


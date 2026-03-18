package com.sedilant.cachosfridge.ui.addbote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBoteDialogScreen(
    state: AddBoteUiState,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onConfirm: () -> Unit
) {
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
            Text(text = stringResource(id = R.string.bote_title))

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


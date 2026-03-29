package com.sedilant.cachosfridge.ui.addfunds

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsDialogScreen(
    state: AddFundsUiState,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onStartNfcScan: () -> Unit,
    onCancelNfcScan: () -> Unit,
    onReset: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            onReset()
            onDismiss()
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.fondos_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            AnimatedVisibility(
                visible = state.step is AddFundsStep.EnterAmount,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = onAmountChange,
                        label = { Text(stringResource(id = R.string.importe_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { onQuickAdd(1) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.quick_1))
                        }
                        OutlinedButton(
                            onClick = { onQuickAdd(2) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.quick_2))
                        }
                        OutlinedButton(
                            onClick = { onQuickAdd(5) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.quick_5))
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onStartNfcScan,
                            modifier = Modifier.weight(1f),
                            enabled = state.isNfcAvailable
                        ) {
                            Text(stringResource(id = R.string.confirmar_ingreso))
                        }
                        OutlinedButton(
                            onClick = {
                                onReset()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                    }

                    if (!state.isNfcAvailable) {
                        Text(
                            text = stringResource(id = R.string.fondos_nfc_no_disponible),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = state.step is AddFundsStep.WaitingForCard,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Contactless,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(id = R.string.fondos_acerca_tarjeta),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    OutlinedButton(onClick = onCancelNfcScan) {
                        Text(stringResource(id = R.string.cancelar))
                    }
                }
            }

            AnimatedVisibility(
                visible = state.step is AddFundsStep.Success,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val personName = (state.step as? AddFundsStep.Success)?.personName.orEmpty()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(id = R.string.fondos_recarga_exitosa, personName),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = {
                        onReset()
                        onDismiss()
                    }) {
                        Text(stringResource(id = R.string.confirmar))
                    }
                }
            }

            AnimatedVisibility(
                visible = state.step is AddFundsStep.CardNotLinked,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.fondos_tarjeta_no_vinculada),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onCancelNfcScan) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                        Button(onClick = onStartNfcScan) {
                            Text(stringResource(id = R.string.fondos_acerca_tarjeta))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

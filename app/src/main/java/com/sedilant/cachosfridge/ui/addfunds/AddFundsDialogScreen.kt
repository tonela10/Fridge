package com.sedilant.cachosfridge.ui.addfunds

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R
import com.sedilant.cachosfridge.ui.toEuroString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFundsDialogScreen(
    state: AddFundsUiState,
    onDismiss: () -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onStartNfcScan: () -> Unit,
    onCancelNfcScan: () -> Unit,
    onSettleExactDebt: () -> Unit,
    onShowQr: () -> Unit,
    onBackToAmount: () -> Unit,
    onSubmitContribution: () -> Unit,
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
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.fondos_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            when (state.step) {
                AddFundsStep.PoolNotConfigured -> PoolNotConfigured(onDismiss)
                AddFundsStep.ReadyToScan -> ReadyToScan(state.isNfcAvailable, onStartNfcScan, onDismiss)
                AddFundsStep.WaitingForCard -> WaitingForCard(onCancelNfcScan)
                AddFundsStep.CardNotLinked -> CardNotLinked(onStartNfcScan, onCancelNfcScan)
                AddFundsStep.EnterAmount -> EnterAmount(
                    state = state,
                    onAmountChange = onAmountChange,
                    onQuickAdd = onQuickAdd,
                    onSettleExactDebt = onSettleExactDebt,
                    onContinue = onShowQr,
                    onCancel = onReset
                )
                AddFundsStep.ShowQr -> PayPalQr(
                    state = state,
                    onBack = onBackToAmount,
                    onSubmit = onSubmitContribution
                )
                AddFundsStep.Submitted -> PendingResult(
                    title = stringResource(R.string.fondos_paypal_submitted),
                    body = stringResource(R.string.fondos_paypal_submitted_description),
                    onClose = {
                        onReset()
                        onDismiss()
                    }
                )
                AddFundsStep.AlreadyPending -> PendingResult(
                    title = stringResource(R.string.fondos_paypal_already_pending),
                    body = state.pendingRequest?.amountCents?.let {
                        stringResource(R.string.fondos_paypal_pending_amount, it.toEuroString())
                    }.orEmpty(),
                    onClose = {
                        onReset()
                        onDismiss()
                    }
                )
                AddFundsStep.Error -> ErrorResult(
                    onRetry = onBackToAmount,
                    onClose = {
                        onReset()
                        onDismiss()
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PoolNotConfigured(onClose: () -> Unit) {
    StatusContent(
        icon = { Icon(Icons.Default.Warning, null, Modifier.size(56.dp), MaterialTheme.colorScheme.error) },
        title = stringResource(R.string.fondos_paypal_no_configurado),
        body = stringResource(R.string.fondos_paypal_no_configurado_description)
    ) {
        OutlinedButton(onClick = onClose) { Text(stringResource(R.string.cerrar)) }
    }
}

@Composable
private fun ReadyToScan(isNfcAvailable: Boolean, onScan: () -> Unit, onClose: () -> Unit) {
    StatusContent(
        icon = { Icon(Icons.Default.Contactless, null, Modifier.size(64.dp), MaterialTheme.colorScheme.primary) },
        title = stringResource(R.string.fondos_identificar_title),
        body = stringResource(R.string.fondos_identificar_description)
    ) {
        Button(onClick = onScan, enabled = isNfcAvailable, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.fondos_escanear_tarjeta))
        }
        OutlinedButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.cancelar))
        }
        if (!isNfcAvailable) {
            Text(
                stringResource(R.string.fondos_nfc_no_disponible),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun WaitingForCard(onCancel: () -> Unit) {
    StatusContent(
        icon = {
            Icon(Icons.Default.Contactless, null, Modifier.size(56.dp), MaterialTheme.colorScheme.primary)
            CircularProgressIndicator()
        },
        title = stringResource(R.string.fondos_acerca_tarjeta),
        body = stringResource(R.string.fondos_acerca_tarjeta_description)
    ) {
        OutlinedButton(onClick = onCancel) { Text(stringResource(R.string.cancelar)) }
    }
}

@Composable
private fun CardNotLinked(onRetry: () -> Unit, onCancel: () -> Unit) {
    StatusContent(
        icon = { Icon(Icons.Default.Warning, null, Modifier.size(56.dp), MaterialTheme.colorScheme.error) },
        title = stringResource(R.string.fondos_tarjeta_no_vinculada),
        body = stringResource(R.string.fondos_tarjeta_no_vinculada_description)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.cancelar))
            }
            Button(onClick = onRetry, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.reintentar))
            }
        }
    }
}

@Composable
private fun EnterAmount(
    state: AddFundsUiState,
    onAmountChange: (String) -> Unit,
    onQuickAdd: (Int) -> Unit,
    onSettleExactDebt: () -> Unit,
    onContinue: () -> Unit,
    onCancel: () -> Unit
) {
    val person = state.person ?: return
    PersonSummary(person.name, person.balanceCents)
    OutlinedTextField(
        value = state.amount,
        onValueChange = onAmountChange,
        label = { Text(stringResource(R.string.importe_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(1, 2, 5).forEach { euros ->
            OutlinedButton(onClick = { onQuickAdd(euros) }, modifier = Modifier.weight(1f)) {
                Text("+$euros EUR")
            }
        }
    }
    if (person.balanceCents < 0) {
        OutlinedButton(onClick = onSettleExactDebt, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.fondos_liquidar_deuda, (-person.balanceCents).toEuroString()))
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.cancelar))
        }
        Button(
            onClick = onContinue,
            enabled = state.amountCents > 0,
            modifier = Modifier.weight(1f)
        ) {
            Text(stringResource(R.string.continuar))
        }
    }
}

@Composable
private fun PayPalQr(state: AddFundsUiState, onBack: () -> Unit, onSubmit: () -> Unit) {
    val poolUrl = state.paypalPoolUrl ?: return
    val qr = remember(poolUrl) { QrCodeGenerator.create(poolUrl).asImageBitmap() }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(R.string.fondos_paypal_amount, state.amountCents.toEuroString()),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            stringResource(R.string.fondos_paypal_scan_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp)) {
            Image(
                bitmap = qr,
                contentDescription = stringResource(R.string.fondos_paypal_qr_description),
                modifier = Modifier
                    .size(260.dp)
                    .padding(12.dp)
            )
        }
        Text(
            stringResource(R.string.fondos_paypal_manual_amount),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.accion_volver))
            }
            Button(onClick = onSubmit, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.fondos_paypal_sent))
            }
        }
    }
}

@Composable
private fun PendingResult(title: String, body: String, onClose: () -> Unit) {
    StatusContent(
        icon = { Icon(Icons.Default.HourglassTop, null, Modifier.size(56.dp), MaterialTheme.colorScheme.primary) },
        title = title,
        body = body
    ) {
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.cerrar))
        }
    }
}

@Composable
private fun ErrorResult(onRetry: () -> Unit, onClose: () -> Unit) {
    StatusContent(
        icon = { Icon(Icons.Default.Warning, null, Modifier.size(56.dp), MaterialTheme.colorScheme.error) },
        title = stringResource(R.string.fondos_paypal_error),
        body = stringResource(R.string.fondos_paypal_error_description)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.cerrar))
            }
            Button(onClick = onRetry, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.reintentar))
            }
        }
    }
}

@Composable
private fun PersonSummary(name: String, balanceCents: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null)
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.fondos_saldo_actual, balanceCents.toEuroString()))
            }
        }
    }
}

@Composable
private fun StatusContent(
    icon: @Composable () -> Unit,
    title: String,
    body: String,
    actions: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        icon()
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text(body, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        actions()
    }
}

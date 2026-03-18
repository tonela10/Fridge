package com.sedilant.cachosfridge.ui.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sedilant.cachosfridge.R
import com.sedilant.cachosfridge.data.PurchaseResult
import com.sedilant.cachosfridge.ui.theme.CachosFridgeTheme
import com.sedilant.cachosfridge.ui.toEuroString

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PaymentScreen(
    state: PaymentUiState,
    onBack: () -> Unit,
    onPayNow: () -> Unit,
    onAddToAccount: () -> Unit,
    onPayWithBote: () -> Unit,
    onResultConsumed: () -> Unit,
    onPurchaseSuccess: () -> Unit
) {
    LaunchedEffect(state.purchaseResult) {
        if (state.purchaseResult == PurchaseResult.Success) {
            onResultConsumed()
            onPurchaseSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.pago_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.accion_volver)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.pago_subtitle, state.productName),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(id = R.string.pago_buyer, state.payerName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onPayNow,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(86.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null)
                        Text(
                            text = stringResource(id = R.string.pago_pagar_ya),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onAddToAccount,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(76.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                        Text(
                            text = stringResource(id = R.string.pago_anadir_cuenta),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onPayWithBote,
                    enabled = state.canPayWithBote,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(76.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null)
                        Text(
                            text = stringResource(id = R.string.pago_pagar_bote),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (!state.canPayWithBote) {
                Text(
                    text = stringResource(id = R.string.pago_bote_insuficiente),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (state.purchaseResult == PurchaseResult.ProductWithoutStock) {
                Text(
                    text = stringResource(id = R.string.error_sin_stock),
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryRow(
                        label = stringResource(id = R.string.pago_total_label),
                        value = state.totalCents.toEuroString(),
                        emphasize = true
                    )
                    SummaryRow(
                        label = stringResource(id = R.string.pago_bote_disponible),
                        value = state.boteCents.toEuroString()
                    )
                    SummaryRow(
                        label = stringResource(id = R.string.pago_cuenta_actual, state.payerName),
                        value = state.accountCents.toEuroString()
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    emphasize: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PaymentScreenPreview() {
    CachosFridgeTheme {
        PaymentScreen(
            state = PaymentUiState(
                productName = "Pepsi",
                payerName = "Mario",
                accountCents = -100,
                totalCents = 50,
                boteCents = 200,
                canPayWithBote = true,
                purchaseResult = null
            ),
            onBack = {},
            onPayNow = {},
            onAddToAccount = {},
            onPayWithBote = {},
            onResultConsumed = {},
            onPurchaseSuccess = {}
        )
    }
}


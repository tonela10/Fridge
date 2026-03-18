package com.sedilant.cachosfridge.ui.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R
import com.sedilant.cachosfridge.data.PurchaseResult
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.pago_subtitle, state.productName),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.pago_total, state.totalCents.toEuroString()),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onPayNow, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.pago_pagar_ya))
            }
            OutlinedButton(onClick = onAddToAccount, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(id = R.string.pago_anadir_cuenta))
            }
            OutlinedButton(
                onClick = onPayWithBote,
                enabled = state.canPayWithBote,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.pago_pagar_bote))
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
        }
    }
}


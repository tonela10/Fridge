package com.sedilant.cachosfridge.ui.history

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R
import com.sedilant.cachosfridge.data.TransactionEntity
import com.sedilant.cachosfridge.data.TransactionType
import com.sedilant.cachosfridge.ui.toEuroString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HistoryScreen(
    state: HistoryUiState,
    onBack: () -> Unit,
    onSelectPerson: (String?) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.historial_title)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.accion_volver)
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.selectedPersonId == null,
                    onClick = { onSelectPerson(null) },
                    label = { Text(stringResource(R.string.historial_todos)) }
                )
                state.people.forEach { person ->
                    FilterChip(
                        selected = state.selectedPersonId == person.id,
                        onClick = { onSelectPerson(person.id) },
                        label = { Text(person.name) }
                    )
                }
            }

            if (state.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.historial_sin_transacciones),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { Spacer(modifier = Modifier.padding(top = 4.dp)) }
                    items(state.transactions, key = { it.id }) { tx ->
                        TransactionRow(tx)
                    }
                    item { Spacer(modifier = Modifier.padding(bottom = 8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(tx: TransactionEntity) {
    val (icon, tint) = txIconAndTint(tx.type)
    val isCredit = tx.type == TransactionType.ADD_FUNDS ||
            tx.type == TransactionType.ADD_BOTE ||
            tx.type == TransactionType.SETTLE_DEBT
    val amountColor = if (isCredit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val amountSign = if (isCredit) "+" else "-"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = tint.copy(alpha = 0.12f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = txTitle(tx),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val subtitle = txSubtitle(tx)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = dateFormat.format(Date(tx.timestampMs)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "$amountSign${tx.amountCents.toEuroString()}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun txTitle(tx: TransactionEntity): String = when (tx.type) {
    TransactionType.PURCHASE_NOW -> if (tx.productName != null)
        stringResource(R.string.historial_compra_efectivo_producto, tx.productName)
    else stringResource(R.string.historial_compra_efectivo)
    TransactionType.PURCHASE_CARD -> if (tx.productName != null)
        stringResource(R.string.historial_compra_cuenta_producto, tx.productName)
    else stringResource(R.string.historial_compra_cuenta)
    TransactionType.PURCHASE_BOTE -> if (tx.productName != null)
        stringResource(R.string.historial_compra_bote_producto, tx.productName)
    else stringResource(R.string.historial_compra_bote)
    TransactionType.ADD_FUNDS -> stringResource(R.string.historial_recarga_saldo)
    TransactionType.ADD_BOTE -> stringResource(R.string.historial_recarga_bote)
    TransactionType.SETTLE_DEBT -> stringResource(R.string.historial_deuda_liquidada)
}

@Composable
private fun txSubtitle(tx: TransactionEntity): String? = when (tx.type) {
    TransactionType.PURCHASE_CARD, TransactionType.ADD_FUNDS, TransactionType.SETTLE_DEBT -> tx.personName
    else -> null
}

@Composable
private fun txIconAndTint(type: TransactionType): Pair<ImageVector, Color> {
    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val tertiary = MaterialTheme.colorScheme.tertiary
    return when (type) {
        TransactionType.PURCHASE_NOW,
        TransactionType.PURCHASE_CARD,
        TransactionType.PURCHASE_BOTE -> Icons.Default.ShoppingCart to error
        TransactionType.ADD_FUNDS -> Icons.Default.AddCircle to primary
        TransactionType.ADD_BOTE -> Icons.Default.MoveToInbox to tertiary
        TransactionType.SETTLE_DEBT -> Icons.Default.CheckCircle to primary
    }
}

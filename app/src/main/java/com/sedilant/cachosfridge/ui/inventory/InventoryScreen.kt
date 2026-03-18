package com.sedilant.cachosfridge.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R
import com.sedilant.cachosfridge.data.ProductEntity
import com.sedilant.cachosfridge.ui.toEuroString

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InventoryScreen(
    state: InventoryUiState,
    onBack: () -> Unit,
    onIncreaseStock: (ProductEntity) -> Unit,
    onDecreaseStock: (ProductEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.inventario_title)) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.products, key = { it.id }) { product ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(product.name, style = MaterialTheme.typography.titleMedium)
                            Text(product.priceCents.toEuroString())
                            Text(stringResource(id = R.string.inventario_stock_value, product.stock))
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onIncreaseStock(product) }) {
                                Text(stringResource(id = R.string.inventario_mas))
                            }
                            Button(onClick = { onDecreaseStock(product) }) {
                                Text(stringResource(id = R.string.inventario_menos))
                            }
                        }
                    }
                }
            }
        }
    }
}


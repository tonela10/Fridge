package com.sedilant.cachosfridge.ui.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    onDecreaseStock: (ProductEntity) -> Unit,
    onAddProduct: (String, Int, Int) -> Unit = { _, _, _ -> },
    onUpdatePrice: (ProductEntity, Int) -> Unit = { _, _ -> },
    onDeleteProduct: (ProductEntity) -> Unit = {}
) {
    val showAddForm = remember { mutableStateOf(false) }
    val newProductName = remember { mutableStateOf("") }
    val newProductPrice = remember { mutableStateOf("") }
    val newProductStock = remember { mutableStateOf("") }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            if (showAddForm.value) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(id = R.string.inventario_new_product), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newProductName.value,
                            onValueChange = { newProductName.value = it },
                            label = { Text(stringResource(id = R.string.inventario_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newProductPrice.value,
                            onValueChange = { newProductPrice.value = it },
                            label = { Text(stringResource(id = R.string.inventario_price_euro)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newProductStock.value,
                            onValueChange = { newProductStock.value = it },
                            label = { Text(stringResource(id = R.string.inventario_stock_input)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (newProductName.value.isNotBlank() && newProductPrice.value.isNotBlank()) {
                                        val price = (newProductPrice.value.toDoubleOrNull() ?: 0.0) * 100
                                        val stock = newProductStock.value.toIntOrNull() ?: 0
                                        onAddProduct(newProductName.value, price.toInt(), stock)
                                        newProductName.value = ""
                                        newProductPrice.value = ""
                                        newProductStock.value = ""
                                        showAddForm.value = false
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.confirmar))
                            }
                            Button(
                                onClick = { showAddForm.value = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(id = R.string.cancelar))
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
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
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(onClick = { onIncreaseStock(product) }) {
                                    Text(stringResource(id = R.string.inventario_mas))
                                }
                                Button(onClick = { onDecreaseStock(product) }) {
                                    Text(stringResource(id = R.string.inventario_menos))
                                }
                                if (!product.hasAsset) {
                                    Button(onClick = { onDeleteProduct(product) }) {
                                        Text(stringResource(id = R.string.inventario_delete))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { showAddForm.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(stringResource(id = R.string.inventario_add_product))
            }
        }
    }
}


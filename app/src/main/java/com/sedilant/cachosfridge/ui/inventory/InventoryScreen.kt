package com.sedilant.cachosfridge.ui.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    onSetStock: (ProductEntity, Int) -> Unit = { _, _ -> },
    onAddProduct: (String, Int, Int) -> Unit = { _, _, _ -> },
    onUpdatePrice: (ProductEntity, Int) -> Unit = { _, _ -> },
    onDeleteProduct: (ProductEntity) -> Unit = {}
) {
    var showAddSheet by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    var addName by remember { mutableStateOf("") }
    var addPrice by remember { mutableStateOf("") }
    var addStock by remember { mutableStateOf("") }

    var editPrice by remember { mutableStateOf("") }
    var editStock by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
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
                HorizontalDivider()
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.products, key = { it.id }) { product ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ProductThumb(product = product)

                            Column(modifier = Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = stringResource(
                                        id = R.string.inventario_row_stock_price,
                                        product.stock,
                                        product.priceCents.toEuroString()
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            OutlinedButton(onClick = {
                                editingProduct = product
                                editStock = product.stock.toString()
                                editPrice = "%.2f".format(product.priceCents / 100.0).replace('.', ',')
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(stringResource(id = R.string.inventario_editar))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { showAddSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(id = R.string.inventario_add_product))
            }
        }

        if (editingProduct != null) {
            ModalBottomSheet(onDismissRequest = { editingProduct = null }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(stringResource(id = R.string.inventario_editar_producto), style = MaterialTheme.typography.titleLarge)
                    Text(editingProduct?.name.orEmpty(), style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = editStock,
                        onValueChange = { editStock = it.filter(Char::isDigit) },
                        label = { Text(stringResource(id = R.string.inventario_stock_input)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text(stringResource(id = R.string.inventario_price_euro)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                val stockValue = editStock.toIntOrNull()
                                val priceValue = (editPrice.replace(',', '.').toDoubleOrNull() ?: 0.0) * 100
                                val current = editingProduct ?: return@Button
                                if (stockValue != null) onSetStock(current, stockValue)
                                onUpdatePrice(current, priceValue.toInt())
                                editingProduct = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.inventario_guardar_cambios))
                        }
                        OutlinedButton(
                            onClick = { editingProduct = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                    }
                }
            }
        }

        if (showAddSheet) {
            ModalBottomSheet(onDismissRequest = { showAddSheet = false }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(stringResource(id = R.string.inventario_new_product), style = MaterialTheme.typography.titleLarge)

                    OutlinedTextField(
                        value = addName,
                        onValueChange = { addName = it },
                        label = { Text(stringResource(id = R.string.inventario_name)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = addPrice,
                        onValueChange = { addPrice = it },
                        label = { Text(stringResource(id = R.string.inventario_price_euro)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = addStock,
                        onValueChange = { addStock = it.filter(Char::isDigit) },
                        label = { Text(stringResource(id = R.string.inventario_stock_input)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                val priceValue = (addPrice.replace(',', '.').toDoubleOrNull() ?: 0.0) * 100
                                val stockValue = addStock.toIntOrNull() ?: 0
                                if (addName.isNotBlank()) {
                                    onAddProduct(addName.trim(), priceValue.toInt(), stockValue)
                                    addName = ""
                                    addPrice = ""
                                    addStock = ""
                                    showAddSheet = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.confirmar))
                        }
                        OutlinedButton(
                            onClick = { showAddSheet = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(id = R.string.cancelar))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductThumb(product: ProductEntity) {
    val modifier = Modifier.size(56.dp)
    if (!product.hasAsset) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE5E7EB)
        ) {
            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color(0xFF94A3B8))
            }
        }
        return
    }

    val drawableId = when (product.id) {
        "pepsi" -> R.drawable.pepsi
        "kas_naranja" -> R.drawable.orange_kas
        "kas_limon" -> R.drawable.lemon_kas
        "cerveza" -> R.drawable.beer
        "agua" -> R.drawable.water
        "patatas" -> R.drawable.chips
        "palomitas" -> R.drawable.popcorn
        else -> R.drawable.pepsi
    }

    Surface(modifier = modifier, shape = RoundedCornerShape(8.dp)) {
        Image(
            painter = painterResource(id = drawableId),
            contentDescription = product.name,
            contentScale = ContentScale.Crop
        )
    }
}


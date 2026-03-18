package com.sedilant.cachosfridge.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R
import com.sedilant.cachosfridge.data.ProductEntity
import com.sedilant.cachosfridge.ui.toEuroString

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeScreen(
    state: HomeUiState,
    onOpenMenu: () -> Unit,
    onProductClick: (ProductEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.home_title))
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = stringResource(id = R.string.home_bote_label, state.boteCents.toEuroString()),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = stringResource(id = R.string.menu_abrir))
                    }
                }
            )
        }
    ) { padding ->
        HomeProductsContent(
            products = state.products,
            onProductClick = onProductClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        )
    }
}

@Composable
private fun HomeProductsContent(
    products: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
        Text(text = stringResource(id = R.string.home_subtitle), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
        }
        items(products, key = { it.id }) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductClick(product) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.priceCents.toEuroString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(id = R.string.inventario_stock_value, product.stock),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


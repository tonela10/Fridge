package com.sedilant.cachosfridge.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.home_title),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(
                                modifier = Modifier
                                    .size(1.dp)
                                    .weight(1f)
                            )
                            Text(
                                text = stringResource(
                                    id = R.string.home_bote_label,
                                    state.boteCents.toEuroString()
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onOpenMenu) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.menu_abrir)
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
            Surface() { }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.products, key = { it.id }) { product ->
                    ProductCard(product = product, onClick = { onProductClick(product) })
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: ProductEntity, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                ProductImage(product = product, modifier = Modifier.fillMaxSize())
            }
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.priceCents.toEuroString(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun Box(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Surface(modifier = modifier) {
        content()
    }
}

@Composable
private fun ProductImage(product: ProductEntity, modifier: Modifier = Modifier) {
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
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = product.name,
        modifier = modifier,
        contentScale = ContentScale.Inside
    )
}


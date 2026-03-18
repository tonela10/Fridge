package com.sedilant.cachosfridge.ui.selectbuyer

import androidx.compose.foundation.clickable
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
import com.sedilant.cachosfridge.data.PersonEntity
import com.sedilant.cachosfridge.ui.toEuroString

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SelectBuyerScreen(
    state: SelectBuyerUiState,
    onBack: () -> Unit,
    onBuyerClick: (PersonEntity) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.select_buyer_title)) },
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
            item {
                Text(
                    text = stringResource(id = R.string.select_buyer_subtitle, state.product?.name ?: ""),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(state.people, key = { it.id }) { person ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBuyerClick(person) }
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = person.name, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(
                            text = person.balanceCents.toEuroString(),
                            color = if (person.balanceCents < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}


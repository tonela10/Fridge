package com.sedilant.cachosfridge.ui.menu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sedilant.cachosfridge.R

@Composable
fun MenuRailScreen(
    drawerState: DrawerState,
    content: @Composable () -> Unit,
    onInventoryClick: () -> Unit,
    onDebtsClick: () -> Unit,
    onAddBoteClick: () -> Unit,
    onAddFundsClick: () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.menu_titulo),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Button(onClick = onInventoryClick, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(id = R.string.menu_inventario))
                    }
                    Button(
                        onClick = onDebtsClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.menu_deudas))
                    }
                    Button(
                        onClick = onAddBoteClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.menu_anadir_bote))
                    }
                    Button(
                        onClick = onAddFundsClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(stringResource(id = R.string.menu_anadir_fondos))
                    }
                }
            }
        },
        content = content
    )
}


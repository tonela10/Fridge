package com.sedilant.cachosfridge.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sedilant.cachosfridge.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.menu_titulo),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { /* Close handled by drawerState */ }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.menu_cerrar))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    MenuButton(
                        icon = Icons.Default.Edit,
                        label = stringResource(id = R.string.menu_inventario),
                        onClick = onInventoryClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuButton(
                        icon = Icons.Default.Info,
                        label = stringResource(id = R.string.menu_deudas),
                        onClick = onDebtsClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuButton(
                        icon = Icons.Default.Edit,
                        label = stringResource(id = R.string.menu_anadir_bote),
                        onClick = onAddBoteClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuButton(
                        icon = Icons.Default.Settings,
                        label = stringResource(id = R.string.menu_anadir_fondos),
                        onClick = onAddFundsClick
                    )
                }
            }
        },
        content = content
    )
}

@Composable
private fun MenuButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.width(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontWeight = FontWeight.SemiBold)
        }
    }
}


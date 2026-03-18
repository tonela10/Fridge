package com.sedilant.cachosfridge.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MoveToInbox
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        thickness = 2.dp
                    )

                    Spacer(modifier = Modifier.height(24.dp))


                    MenuButton(
                        icon = Icons.Default.Inventory,
                        label = stringResource(id = R.string.menu_inventario),
                        onClick = onInventoryClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuButton(
                        icon = Icons.Default.AccountBalance,
                        label = stringResource(id = R.string.menu_deudas),
                        onClick = onDebtsClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuButton(
                        icon = Icons.Default.MoveToInbox,
                        label = stringResource(id = R.string.menu_anadir_bote),
                        onClick = onAddBoteClick
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    MenuButton(
                        icon = Icons.Default.AttachMoney,
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
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.width(24.dp))
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .weight(1f)
            )
            Text(label, fontWeight = FontWeight.SemiBold)
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuButtonPreview() {
    MenuButton(
        icon = Icons.Default.Inventory,
        label = "Inventario",
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun MenuDrawerContentPreview() {
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
                    text = "MENU RAIL",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            MenuButton(
                icon = Icons.Default.Inventory,
                label = "Inventario",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))

            MenuButton(
                icon = Icons.Default.AccountBalance,
                label = "Deudas",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))

            MenuButton(
                icon = Icons.Default.MoveToInbox,
                label = "Añadir Bote",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))

            MenuButton(
                icon = Icons.Default.AttachMoney,
                label = "Añadir Fondos",
                onClick = {}
            )
        }
    }
}


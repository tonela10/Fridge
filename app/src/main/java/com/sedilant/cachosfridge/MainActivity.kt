package com.sedilant.cachosfridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sedilant.cachosfridge.ui.theme.CachosFridgeTheme
import com.sedilant.cachosfridge.ui.addbote.AddBoteDialogScreen
import com.sedilant.cachosfridge.ui.addbote.AddBoteViewModel
import com.sedilant.cachosfridge.ui.addfunds.AddFundsDialogScreen
import com.sedilant.cachosfridge.ui.addfunds.AddFundsViewModel
import com.sedilant.cachosfridge.ui.debts.DebtsScreen
import com.sedilant.cachosfridge.ui.debts.DebtsViewModel
import com.sedilant.cachosfridge.ui.home.HomeScreen
import com.sedilant.cachosfridge.ui.home.HomeViewModel
import com.sedilant.cachosfridge.ui.inventory.InventoryScreen
import com.sedilant.cachosfridge.ui.inventory.InventoryViewModel
import com.sedilant.cachosfridge.ui.menu.MenuRailScreen
import com.sedilant.cachosfridge.ui.payment.PaymentScreen
import com.sedilant.cachosfridge.ui.payment.PaymentViewModel
import com.sedilant.cachosfridge.ui.selectbuyer.SelectBuyerScreen
import com.sedilant.cachosfridge.ui.selectbuyer.SelectBuyerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CachosFridgeTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppNavigation() {
    val app = LocalContext.current.applicationContext as CachosFridgeApp
    val repository = app.appContainer.repository
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = Routes.Home) {
        composable(Routes.Home) {
            val vm: HomeViewModel = viewModel(factory = factory { HomeViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()

            MenuRailScreen(
                drawerState = drawerState,
                onInventoryClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.Inventory)
                },
                onDebtsClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.Debts)
                },
                onAddBoteClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.AddBote)
                },
                onAddFundsClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(Routes.AddFunds)
                },
                content = {
                    HomeScreen(
                        state = state,
                        onOpenMenu = { scope.launch { drawerState.open() } },
                        onProductClick = { product ->
                            navController.navigate("${Routes.SelectBuyer}/${product.id}")
                        }
                    )
                }
            )
        }

        composable(
            route = "${Routes.SelectBuyer}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId").orEmpty()
            val vm: SelectBuyerViewModel = viewModel(
                key = "select-$productId",
                factory = factory { SelectBuyerViewModel(repository, productId) }
            )
            val state by vm.uiState.collectAsStateWithLifecycle()
            SelectBuyerScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onBuyerClick = { person ->
                    val productId = state.product?.id ?: return@SelectBuyerScreen
                    navController.navigate("${Routes.Payment}/$productId/${person.id}")
                }
            )
        }

        composable(
            route = "${Routes.Payment}/{productId}/{personId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("personId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId").orEmpty()
            val personId = backStackEntry.arguments?.getString("personId").orEmpty()
            val vm: PaymentViewModel = viewModel(
                key = "payment-$productId-$personId",
                factory = factory { PaymentViewModel(repository, productId, personId) }
            )
            val state by vm.uiState.collectAsStateWithLifecycle()
            PaymentScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onPayNow = vm::payNow,
                onAddToAccount = vm::addToAccount,
                onPayWithBote = vm::payWithBote,
                onResultConsumed = vm::consumeResult,
                onPurchaseSuccess = {
                    navController.popBackStack(Routes.Home, inclusive = false)
                }
            )
        }

        composable(Routes.Debts) {
            val vm: DebtsViewModel = viewModel(factory = factory { DebtsViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            DebtsScreen(state = state, onBack = { navController.popBackStack() })
        }

        composable(Routes.Inventory) {
            val vm: InventoryViewModel = viewModel(factory = factory { InventoryViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            InventoryScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onIncreaseStock = vm::increaseStock,
                onDecreaseStock = vm::decreaseStock
            )
        }

        composable(Routes.AddBote) {
            val vm: AddBoteViewModel = viewModel(factory = factory { AddBoteViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            AddBoteDialogScreen(
                state = state,
                onDismiss = { navController.popBackStack() },
                onAmountChange = vm::onAmountChange,
                onQuickAdd = vm::addQuickAmount,
                onConfirm = {
                    vm.confirm {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Routes.AddFunds) {
            val vm: AddFundsViewModel = viewModel(factory = factory { AddFundsViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            AddFundsDialogScreen(
                state = state,
                onDismiss = { navController.popBackStack() },
                onPersonSelected = vm::onPersonSelected,
                onAmountChange = vm::onAmountChange,
                onQuickAdd = vm::addQuickAmount,
                onConfirm = {
                    vm.confirm {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

private object Routes {
    const val Home = "home"
    const val SelectBuyer = "select_buyer"
    const val Payment = "payment"
    const val Debts = "deudas"
    const val Inventory = "inventario"
    const val AddBote = "add_bote"
    const val AddFunds = "add_funds"
}

private fun <T : ViewModel> factory(create: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
            @Suppress("UNCHECKED_CAST")
            return create() as VM
        }
    }
}


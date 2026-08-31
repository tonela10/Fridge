package com.sedilant.cachosfridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sedilant.cachosfridge.nfc.NfcManager
import com.sedilant.cachosfridge.ui.theme.CachosFridgeTheme
import com.sedilant.cachosfridge.ui.admin.AdminScreen
import com.sedilant.cachosfridge.ui.admin.AdminViewModel
import com.sedilant.cachosfridge.ui.addbote.AddBoteDialogScreen
import com.sedilant.cachosfridge.ui.addbote.AddBoteViewModel
import com.sedilant.cachosfridge.ui.addfunds.AddFundsDialogScreen
import com.sedilant.cachosfridge.ui.addfunds.AddFundsViewModel
import com.sedilant.cachosfridge.ui.cart.CartDetailsScreen
import com.sedilant.cachosfridge.ui.cart.CartPaymentScreen
import com.sedilant.cachosfridge.ui.cart.CartPaymentViewModel
import com.sedilant.cachosfridge.ui.cart.CartViewModel
import com.sedilant.cachosfridge.ui.debts.DebtsScreen
import com.sedilant.cachosfridge.ui.debts.DebtsViewModel
import com.sedilant.cachosfridge.ui.home.HomeScreen
import com.sedilant.cachosfridge.ui.home.HomeViewModel
import com.sedilant.cachosfridge.ui.inventory.InventoryScreen
import com.sedilant.cachosfridge.ui.inventory.InventoryViewModel
import com.sedilant.cachosfridge.ui.history.HistoryScreen
import com.sedilant.cachosfridge.ui.history.HistoryViewModel
import com.sedilant.cachosfridge.ui.menu.MenuRailScreen
import com.sedilant.cachosfridge.ui.payment.PaymentScreen
import com.sedilant.cachosfridge.ui.payment.PaymentViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as CachosFridgeApp
        app.appContainer.nfcManager.init(this)

        enableEdgeToEdge()
        setContent {
            CachosFridgeTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
private fun NfcLifecycleHandler(nfcManager: NfcManager) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            val activity = context as? ComponentActivity ?: return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_RESUME -> nfcManager.enableReader(activity)
                Lifecycle.Event.ON_PAUSE -> nfcManager.disableReader(activity)
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
private fun AppNavigation() {
    val app = LocalContext.current.applicationContext as CachosFridgeApp
    val repository = app.appContainer.repository
    val nfcManager = app.appContainer.nfcManager
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cart ViewModel scoped to the whole navigation graph so it persists across screens
    val cartVm: CartViewModel = viewModel(factory = factory { CartViewModel() })
    val cartPaymentVm: CartPaymentViewModel = viewModel(
        factory = factory { CartPaymentViewModel(repository, nfcManager, cartVm) }
    )

    NfcLifecycleHandler(nfcManager)

    val tweenDuration = 350

    SharedTransitionLayout {
        val sharedTransScope = this

        NavHost(
            navController = navController,
            startDestination = Routes.Home,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(tweenDuration)
                ) + fadeIn(animationSpec = tween(tweenDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(tweenDuration)
                ) + fadeOut(animationSpec = tween(tweenDuration))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(tweenDuration)
                ) + fadeIn(animationSpec = tween(tweenDuration))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(tweenDuration)
                ) + fadeOut(animationSpec = tween(tweenDuration))
            }
        ) {
            composable(
            Routes.Home,
            exitTransition = {
                if (targetState.destination.route == Routes.Cart) ExitTransition.None
                else slideOutHorizontally(
                    targetOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(tweenDuration)
                ) + fadeOut(animationSpec = tween(tweenDuration))
            },
            popEnterTransition = {
                if (initialState.destination.route == Routes.Cart) EnterTransition.None
                else slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(tweenDuration)
                ) + fadeIn(animationSpec = tween(tweenDuration))
            }
        ) {
                val animScope = this
                val vm: HomeViewModel = viewModel(factory = factory { HomeViewModel(repository) })
                val state by vm.uiState.collectAsStateWithLifecycle()
                val cartState by cartVm.uiState.collectAsStateWithLifecycle()
                val addBoteVm: AddBoteViewModel = viewModel(factory = factory { AddBoteViewModel(repository) })
                val addBoteState by addBoteVm.uiState.collectAsStateWithLifecycle()
                val addFundsVm: AddFundsViewModel = viewModel(factory = factory { AddFundsViewModel(repository, nfcManager) })
                val addFundsState by addFundsVm.uiState.collectAsStateWithLifecycle()
                var showAddBoteSheet by remember { mutableStateOf(false) }
                var showAddFundsSheet by remember { mutableStateOf(false) }

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
                        showAddBoteSheet = true
                    },
                    onAddFundsClick = {
                        scope.launch { drawerState.close() }
                        showAddFundsSheet = true
                    },
                    onHistoryClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Routes.History)
                    },
                    content = {
                        HomeScreen(
                            state = state,
                            cartState = cartState,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onProductClick = { product ->
                                cartVm.addItem(product)
                            },
                            onCartClick = {
                                navController.navigate(Routes.Cart)
                            },
                            onAdminAccess = {
                                navController.navigate(Routes.Admin)
                            },
                            sharedTransitionScope = sharedTransScope,
                            animatedVisibilityScope = animScope
                        )

                        if (showAddBoteSheet) {
                            AddBoteDialogScreen(
                                state = addBoteState,
                                onDismiss = { showAddBoteSheet = false },
                                onAmountChange = addBoteVm::onAmountChange,
                                onQuickAdd = addBoteVm::addQuickAmount,
                                onConfirm = {
                                    addBoteVm.confirm {
                                        showAddBoteSheet = false
                                    }
                                }
                            )
                        }

                        if (showAddFundsSheet) {
                            AddFundsDialogScreen(
                                state = addFundsState,
                                onDismiss = { showAddFundsSheet = false },
                                onAmountChange = addFundsVm::onAmountChange,
                                onQuickAdd = addFundsVm::addQuickAmount,
                                onStartNfcScan = addFundsVm::startNfcScan,
                                onCancelNfcScan = addFundsVm::cancelNfcScan,
                                onSettleExactDebt = addFundsVm::settleExactDebt,
                                onShowQr = addFundsVm::showQr,
                                onBackToAmount = addFundsVm::backToAmount,
                                onSubmitContribution = addFundsVm::submitContribution,
                                onReset = addFundsVm::resetState
                            )
                        }
                    }
                )
            }

            composable(
                Routes.Cart,
                enterTransition = { fadeIn(tween(400)) },
                exitTransition = { fadeOut(tween(300)) },
                popEnterTransition = { fadeIn(tween(300)) },
                popExitTransition = { fadeOut(tween(350)) }
            ) {
                val cartState by cartVm.uiState.collectAsStateWithLifecycle()
                CartDetailsScreen(
                    state = cartState,
                    onBack = { navController.popBackStack() },
                    onIncrement = cartVm::incrementItem,
                    onDecrement = cartVm::decrementItem,
                    onRemove = cartVm::removeItem,
                    onPay = {
                        navController.navigate(Routes.CartPayment)
                    },
                    sharedTransitionScope = sharedTransScope,
                    animatedVisibilityScope = this
                )
            }

        composable(Routes.CartPayment) {
            val state by cartPaymentVm.uiState.collectAsStateWithLifecycle()
            CartPaymentScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onPayNow = cartPaymentVm::payNow,
                onPayWithBote = cartPaymentVm::payWithBote,
                onStartCardPayment = cartPaymentVm::startCardPayment,
                onCancelCardPayment = cartPaymentVm::cancelCardPayment,
                onResultConsumed = cartPaymentVm::consumeResult,
                onPurchaseSuccess = {
                    cartVm.clearCart()
                    navController.popBackStack(Routes.Home, inclusive = false)
                }
            )
        }

        composable(
            route = "${Routes.Payment}/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId").orEmpty()
            val vm: PaymentViewModel = viewModel(
                key = "payment-$productId",
                factory = factory { PaymentViewModel(repository, nfcManager, productId) }
            )
            val state by vm.uiState.collectAsStateWithLifecycle()
            PaymentScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onPayNow = vm::payNow,
                onPayWithBote = vm::payWithBote,
                onStartCardPayment = vm::startCardPayment,
                onCancelCardPayment = vm::cancelCardPayment,
                onResultConsumed = vm::consumeResult,
                onPurchaseSuccess = {
                    navController.popBackStack(Routes.Home, inclusive = false)
                }
            )
        }

        composable(Routes.Debts) {
            val vm: DebtsViewModel = viewModel(factory = factory { DebtsViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            DebtsScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onLiquidate = vm::liquidateDebt
            )
        }

        composable(Routes.Inventory) {
            val vm: InventoryViewModel = viewModel(factory = factory { InventoryViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            InventoryScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onIncreaseStock = vm::increaseStock,
                onDecreaseStock = vm::decreaseStock,
                onSetStock = vm::setStock,
                onAddProduct = vm::addNewProduct,
                onUpdatePrice = vm::updatePrice,
                onDeleteProduct = vm::deleteProduct
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
            val vm: AddFundsViewModel = viewModel(factory = factory { AddFundsViewModel(repository, nfcManager) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            AddFundsDialogScreen(
                state = state,
                onDismiss = { navController.popBackStack() },
                onAmountChange = vm::onAmountChange,
                onQuickAdd = vm::addQuickAmount,
                onStartNfcScan = vm::startNfcScan,
                onCancelNfcScan = vm::cancelNfcScan,
                onSettleExactDebt = vm::settleExactDebt,
                onShowQr = vm::showQr,
                onBackToAmount = vm::backToAmount,
                onSubmitContribution = vm::submitContribution,
                onReset = vm::resetState
            )
        }

        composable(Routes.History) {
            val vm: HistoryViewModel = viewModel(factory = factory { HistoryViewModel(repository) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            HistoryScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onSelectPerson = vm::selectPerson
            )
        }

        composable(Routes.Admin) {
            val vm: AdminViewModel = viewModel(factory = factory { AdminViewModel(repository, nfcManager) })
            val state by vm.uiState.collectAsStateWithLifecycle()
            AdminScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onCreatePerson = vm::createPerson,
                onUpdatePerson = vm::updatePerson,
                onDeletePerson = vm::deletePerson,
                onStartLinkingCard = vm::startLinkingCard,
                onCancelLinkingCard = vm::cancelLinkingCard,
                onUnlinkCard = vm::unlinkCard,
                onShowCreateDialog = vm::showCreateDialog,
                onDismissCreateDialog = vm::dismissCreateDialog,
                onStartEditing = vm::startEditing,
                onDismissEditing = vm::dismissEditing,
                onShowDeleteConfirm = vm::showDeleteConfirm,
                onDismissDeleteConfirm = vm::dismissDeleteConfirm,
                onConsumeLinkResult = vm::consumeLinkResult,
                onPayPalPoolUrlChange = vm::onPayPalPoolUrlChange,
                onSavePayPalPoolUrl = vm::savePayPalPoolUrl,
                onClearPayPalPoolUrl = vm::clearPayPalPoolUrl,
                onApproveTopUp = vm::approveTopUp,
                onRejectTopUp = vm::rejectTopUp
            )
        }
    }
    } // SharedTransitionLayout
}

private object Routes {
    const val Home = "home"
    const val Cart = "cart"
    const val CartPayment = "cart_payment"
    const val Payment = "payment"
    const val Debts = "deudas"
    const val Inventory = "inventario"
    const val AddBote = "add_bote"
    const val AddFunds = "add_funds"
    const val Admin = "admin"
    const val History = "historial"
}

private fun <T : ViewModel> factory(create: () -> T): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
            @Suppress("UNCHECKED_CAST")
            return create() as VM
        }
    }
}

package com.sedilant.cachosfridge.data

sealed interface PurchaseResult {
    data object Success : PurchaseResult
    data object ProductWithoutStock : PurchaseResult
    data object BoteInsufficient : PurchaseResult
    data object NotFound : PurchaseResult
}


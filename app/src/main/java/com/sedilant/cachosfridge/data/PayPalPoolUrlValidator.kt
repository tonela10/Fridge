package com.sedilant.cachosfridge.data

import java.net.URI

enum class PayPalPoolUrlError {
    EMPTY,
    INVALID,
    HTTPS_REQUIRED,
    INVALID_HOST,
    CREDENTIALS_NOT_ALLOWED
}

object PayPalPoolUrlValidator {
    fun validate(rawUrl: String): PayPalPoolUrlError? {
        val value = rawUrl.trim()
        if (value.isEmpty()) return PayPalPoolUrlError.EMPTY

        val uri = try {
            URI(value)
        } catch (_: Exception) {
            return PayPalPoolUrlError.INVALID
        }

        if (!uri.scheme.equals("https", ignoreCase = true)) {
            return PayPalPoolUrlError.HTTPS_REQUIRED
        }
        if (uri.userInfo != null) return PayPalPoolUrlError.CREDENTIALS_NOT_ALLOWED

        val host = uri.host?.lowercase() ?: return PayPalPoolUrlError.INVALID
        if (host != "paypal.com" && !host.endsWith(".paypal.com")) {
            return PayPalPoolUrlError.INVALID_HOST
        }
        return null
    }
}

package com.sedilant.cachosfridge.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PayPalPoolUrlValidatorTest {
    @Test
    fun acceptsPayPalHttpsUrls() {
        assertNull(PayPalPoolUrlValidator.validate("https://paypal.com/pool/example"))
        assertNull(PayPalPoolUrlValidator.validate("https://www.paypal.com/pool/example"))
        assertNull(PayPalPoolUrlValidator.validate(" https://paypal.com/pool/example "))
    }

    @Test
    fun rejectsEmptyInvalidOrInsecureUrls() {
        assertEquals(PayPalPoolUrlError.EMPTY, PayPalPoolUrlValidator.validate(""))
        assertEquals(PayPalPoolUrlError.INVALID, PayPalPoolUrlValidator.validate("not a url"))
        assertEquals(
            PayPalPoolUrlError.HTTPS_REQUIRED,
            PayPalPoolUrlValidator.validate("http://paypal.com/pool/example")
        )
    }

    @Test
    fun rejectsNonPayPalHostsAndCredentials() {
        assertEquals(
            PayPalPoolUrlError.INVALID_HOST,
            PayPalPoolUrlValidator.validate("https://paypal.com.example.org/pool/example")
        )
        assertEquals(
            PayPalPoolUrlError.CREDENTIALS_NOT_ALLOWED,
            PayPalPoolUrlValidator.validate("https://user:password@paypal.com/pool/example")
        )
    }
}

package com.sedilant.cachosfridge.ui.addfunds

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QrCodeGeneratorTest {
    @Test
    fun generatedQrDecodesToTheConfiguredPoolUrl() {
        val expected = "https://paypal.com/pool/cachos"
        val bitmap = QrCodeGenerator.create(expected, size = 320)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels)
        val decoded = QRCodeReader().decode(BinaryBitmap(HybridBinarizer(source)))

        assertEquals(expected, decoded.text)
    }
}

package com.sedilant.cachosfridge.nfc

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NfcManager {
    private val _tagUid = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val tagUid: SharedFlow<String> = _tagUid.asSharedFlow()

    private var nfcAdapter: NfcAdapter? = null

    val isNfcAvailable: Boolean get() = nfcAdapter != null

    fun init(activity: Activity) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
    }

    fun enableReader(activity: Activity) {
        nfcAdapter?.enableReaderMode(
            activity,
            { tag -> onTagDiscovered(tag) },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    fun disableReader(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }

    private fun onTagDiscovered(tag: Tag) {
        val uid = tag.id.joinToString("") { "%02X".format(it) }
        _tagUid.tryEmit(uid)
    }
}

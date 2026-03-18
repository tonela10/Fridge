package com.sedilant.cachosfridge

import android.app.Application
import com.sedilant.cachosfridge.data.AppContainer

class CachosFridgeApp : Application() {
	val appContainer: AppContainer by lazy { AppContainer(this) }
}


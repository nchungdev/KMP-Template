package com.chun.kmptemplate

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import platform.UIKit.UIDevice

actual val platform: String = UIDevice.currentDevice.let { "${it.systemName} ${it.systemVersion}" }

actual val platformCoroutineDispatcher: CoroutineDispatcher = Dispatchers.Default

actual fun runTest(block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}

actual fun isDebug() = Platform.isDebugBinary

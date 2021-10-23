package com.chun.kmptemplate

import kotlinx.coroutines.*

actual val platform: String = "Android ${android.os.Build.VERSION.SDK_INT}"

actual val platformCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO

actual fun runTest(block: suspend () -> Unit) = runBlocking { block() }

actual fun isDebug(): Boolean = BuildConfig.DEBUG

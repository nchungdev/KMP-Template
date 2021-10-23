package com.chun.kmptemplate

import kotlinx.coroutines.CoroutineDispatcher

expect val platform: String

expect val platformCoroutineDispatcher: CoroutineDispatcher

expect fun runTest(block: suspend () -> Unit)

expect fun isDebug(): Boolean

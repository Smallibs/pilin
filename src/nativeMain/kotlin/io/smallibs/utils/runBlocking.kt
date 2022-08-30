package io.smallibs.utils

actual fun <A> unsafeSyncRun(r: suspend () -> A): A = kotlinx.coroutines.runBlocking { r() }
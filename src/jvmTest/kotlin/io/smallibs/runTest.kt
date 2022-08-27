package io.smallibs

import kotlinx.coroutines.runBlocking

actual fun <A> runTest(r: suspend () -> A): A = runBlocking { r() }

package io.smallibs.pilin.core

import kotlin.coroutines.CoroutineContext

actual fun <B> execute(c: CoroutineContext, f: suspend () -> B): B {
    TODO("Not yet implemented")
}
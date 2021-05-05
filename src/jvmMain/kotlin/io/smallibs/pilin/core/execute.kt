package io.smallibs.pilin.core 

import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

actual fun <B> execute(c: CoroutineContext, f: suspend () -> B): B {
    return runBlocking(c) { f() }
}

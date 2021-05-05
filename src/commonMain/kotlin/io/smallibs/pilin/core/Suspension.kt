package io.smallibs.pilin.core

import kotlin.coroutines.CoroutineContext

expect fun <B> execute(c: CoroutineContext, f: suspend () -> B): B

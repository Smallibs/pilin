package io.smallibs.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.promise

@Suppress("UNCHECKED_CAST")
@OptIn(DelicateCoroutinesApi::class)
actual fun <A> runTest(r: suspend () -> A): A = GlobalScope.promise { r() } as A
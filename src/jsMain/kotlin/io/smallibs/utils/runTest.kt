package io.smallibs.utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asDeferred
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
actual fun <A> runTest(r: suspend () -> A): dynamic = GlobalScope.promise { r() }
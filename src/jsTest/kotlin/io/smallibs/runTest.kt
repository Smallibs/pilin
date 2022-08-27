package io.smallibs

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
actual fun <A> runTest(r: suspend () -> A): A = GlobalScope.async { r() }.getCompleted()
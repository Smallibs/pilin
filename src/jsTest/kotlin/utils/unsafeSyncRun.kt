package utils

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@Suppress("UNCHECKED_CAST")
@OptIn(DelicateCoroutinesApi::class)
actual fun <A> unsafeSyncRun(r: suspend () -> A): A = GlobalScope.promise { r() } as A

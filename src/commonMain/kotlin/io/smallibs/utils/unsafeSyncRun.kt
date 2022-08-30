package io.smallibs.utils

expect fun <A> unsafeSyncRun(r: suspend () -> A): A
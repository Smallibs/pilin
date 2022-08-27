package io.smallibs


actual fun <A> runTest(r: suspend () -> A): A = runTest { r() }
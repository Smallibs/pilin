package io.smallibs

expect fun <A> runTest(r:suspend () -> A) : A
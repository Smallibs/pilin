package io.smallibs.utils

expect fun <A> runTest(r:suspend () -> A) : A
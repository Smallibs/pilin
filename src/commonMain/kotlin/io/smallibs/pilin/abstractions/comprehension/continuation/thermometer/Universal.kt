package io.smallibs.pilin.abstractions.comprehension.continuation.thermometer

internal class Universal<A> {

    @Suppress("UNCHECKED_CAST")
    fun <B> from(a: B): A = a as A

    @Suppress("UNCHECKED_CAST")
    fun <B> to(a: A): B = a as B

}
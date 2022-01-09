package io.smallibs.pilin.abstractions.comprehension.continuation.thermometer

class Universal<A> {

    @Suppress("UNCHECKED_CAST")
    fun <B> fromU(a: B): A = a as A

    @Suppress("UNCHECKED_CAST")
    fun <B> toU(a: A): B = a as B

}
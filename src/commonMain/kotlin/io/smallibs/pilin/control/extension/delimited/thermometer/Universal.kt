package io.smallibs.pilin.control.extension.delimited.thermometer

class Universal<A> {

    @Suppress("UNCHECKED_CAST")
    fun <B> fromU(a: B): A = a as A

    @Suppress("UNCHECKED_CAST")
    fun <B> toU(a: A): B = a as B

}
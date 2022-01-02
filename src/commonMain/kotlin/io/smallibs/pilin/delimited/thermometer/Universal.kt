package io.smallibs.pilin.delimited.thermometer

object Universal {

    @Suppress("UNCHECKED_CAST")
    fun <B, A> from(a: B): A =
        a as A

}
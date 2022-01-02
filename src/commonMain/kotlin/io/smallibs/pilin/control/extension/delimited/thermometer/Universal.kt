package io.smallibs.pilin.control.extension.delimited.thermometer

object Universal {

    @Suppress("UNCHECKED_CAST")
    fun <B, A> from(a: B): A =
        try {
            a as A
        } catch (e: ClassCastException) {
            throw e
        }

}
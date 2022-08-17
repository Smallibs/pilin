package io.smallibs.pilin.abstractions

object Semigroup {

    interface Core<T> {
        suspend fun combine(l: T, r: T): T
    }

    open class Infix<T>(private val c: Core<T>) : Core<T> by c {
        suspend infix operator fun T.plus(t: T): T = combine(this, t)
    }

    interface API<T> : Core<T> {
        val infix: Infix<T> get() = Infix(this)
    }

}
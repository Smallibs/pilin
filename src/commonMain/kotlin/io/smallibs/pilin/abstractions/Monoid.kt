package io.smallibs.pilin.abstractions

object Monoid {

    interface WithNeutral<T> {
        val neutral: T
    }

    interface Core<T> : Semigroup.Core<T>, WithNeutral<T>

    class OverSemigroup<T>(private val s: Semigroup.Core<T>, private val n: WithNeutral<T>) :
        Semigroup.Core<T> by s, WithNeutral<T> by n, Core<T>

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Infix<T>(private val c: Core<T>) : Semigroup.Infix<T>(c), Core<T> by c

    interface API<T> : Core<T> {
        val infix: Infix<T> get() = Infix(this)
    }

}
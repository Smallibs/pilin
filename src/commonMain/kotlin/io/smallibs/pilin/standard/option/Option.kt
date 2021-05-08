package io.smallibs.pilin.standard.option

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Supplier

sealed class Option<A> : App<Option.TK, A> {
    data class None<A>(private val u: Unit = Unit) : Option<A>()
    data class Some<A>(val value: A) : Option<A>()

    // This code can be automatically generated
    class TK private constructor() {
        companion object {
            private val <A> App<TK, A>.fix: Option<A>
                get() = this as Option<A>

            suspend fun <A, B> App<TK, A>.fold(n: Supplier<B>, s: Fun<A, B>): B =
                when (val self = this.fix) {
                    is None -> n()
                    is Some -> s(self.value)
                }
        }
    }

    companion object {
        fun <A> none(): App<TK, A> = None()
        fun <A> some(a: A): App<TK, A> = Some(a)

        val functor = Functor.functor
        val applicative = Applicative.applicative
        val monad = Monad.monad

    }
}
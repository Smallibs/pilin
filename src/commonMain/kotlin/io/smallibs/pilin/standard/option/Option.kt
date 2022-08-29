package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Semigroup
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Supplier

sealed interface Option<out A> : App<Option.OptionK, A> {
    data class Some<out A>(val value: A) : Option<A>
    object None : Option<Nothing>

    // This code can be automatically generated
    object OptionK {
        private val <A> App<OptionK, A>.fix: Option<A>
            get() = this as Option<A>

        suspend fun <A, B> App<OptionK, A>.fold(n: Supplier<B>, s: Fun<A, B>): B = when (val self = this.fix) {
            is None -> n()
            is Some -> s(self.value)
        }
    }

    companion object {
        fun <A> none(): Option<A> = None
        fun <A> some(a: A): Option<A> = Some(a)

        fun <A> monoid(semigroup: Semigroup.API<A>) = Monoid.MonoidImp(semigroup)

        val functor = Functor.functor
        val applicative = Applicative.applicative
        val selective = Selective.selective
        val monad = Monad.monad
    }
}
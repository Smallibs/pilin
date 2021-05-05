package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.Option.TK.Companion.fold
import io.smallibs.pilin.standard.Option.TK.Companion.none
import io.smallibs.pilin.standard.Option.TK.Companion.some
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Supplier

object Option {

    sealed class T<A> : App<TK, A> {
        data class None<A>(private val u: Unit = Unit) : T<A>()
        data class Some<A>(val value: A) : T<A>()
    }

    // This code can be automatically generated
    class TK private constructor() {
        companion object {
            private val <A> App<TK, A>.fix: T<A>
                get() = this as T<A>

            fun <A> none(): App<TK, A> = T.None()
            fun <A> some(a: A): App<TK, A> = T.Some(a)

            suspend fun <A, B> App<TK, A>.fold(n: Supplier<B>, s: Fun<A, B>): B =
                when (val self = this.fix) {
                    is T.None -> n()
                    is T.Some -> s(self.value)
                }
        }
    }

    private class FunctorImpl : Functor.API<TK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TK, A>, App<TK, B>> =
            { ma -> ma.fold(::none) { a -> some(f(a)) } }
    }

    private class ApplicativeImpl :
        Applicative.API<TK>,
        Applicative.WithPureAndApply<TK> {
        override suspend fun <A> pure(a: A): App<TK, A> = some(a)
        override suspend fun <A, B> apply(mf: App<TK, Fun<A, B>>): Fun<App<TK, A>, App<TK, B>> =
            { ma -> mf.fold(::none) { f -> ma.fold(::none) { a -> pure(f(a)) } } }
    }

    private class MonadImpl(applicative: Applicative.API<TK>) :
        Monad.API<TK>,
        Monad.WithReturnsMapAndJoin<TK>,
        Monad.ViaApplicative<TK>(applicative) {
        override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> = mma.fold(::none) { it }
    }

    val functor: Functor.API<TK> = FunctorImpl()

    val applicative: Applicative.API<TK> = ApplicativeImpl()

    val monad: Monad.API<TK> = MonadImpl(applicative)

}
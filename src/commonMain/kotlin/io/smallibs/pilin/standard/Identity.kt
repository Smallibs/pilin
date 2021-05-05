package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.Identity.TK.Companion.fold
import io.smallibs.pilin.standard.Identity.TK.Companion.id
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Identity {

    data class Id<A>(val value: A) : App<TK, A>

    // This code can be automatically generated
    class TK private constructor() {
        companion object {
            private val <A> App<TK, A>.fix: Id<A> get() = this as Id<A>

            fun <A> id(a: A): App<TK, A> = Id(a)

            suspend fun <A, B> App<TK, A>.fold(f: Fun<A, B>): B = f(this.fix.value)
        }
    }

    private class FunctorImpl : Functor.API<TK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TK, A>, App<TK, B>> =
            { ma -> id(ma.fold(f)) }

    }

    private class ApplicativeImpl :
        Applicative.API<TK>,
        Applicative.WithPureAndApply<TK> {
        override suspend fun <A> pure(a: A): App<TK, A> = Id(a)
        override suspend fun <A, B> apply(mf: App<TK, Fun<A, B>>): Fun<App<TK, A>, App<TK, B>> =
            { ma -> mf.fold { f -> ma.fold { a -> pure(f(a)) } } }
    }

    private class MonadImpl(applicative: Applicative.API<TK>) :
        Monad.API<TK>,
        Monad.WithReturnsMapAndJoin<TK>,
        Monad.ViaApplicative<TK>(applicative) {
        override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
            mma.fold { it }

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TK, A>, App<TK, B>> {
            return applicative.map(f)
        }
    }

    val functor: Functor.API<TK> = FunctorImpl()

    val applicative: Applicative.API<TK> = ApplicativeImpl()

    val monad: Monad.API<TK> = MonadImpl(applicative)

}
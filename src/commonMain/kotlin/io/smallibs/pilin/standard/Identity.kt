package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Applicative
import io.smallibs.pilin.control.Functor
import io.smallibs.pilin.control.Monad
import io.smallibs.pilin.standard.Identity.TK.Companion.fix
import io.smallibs.pilin.type.App

object Identity {

    data class Id<A>(val value: A) : App<TK, A>

    class TK private constructor() {
        companion object {
            val <A> App<TK, A>.fix: Id<A>
                get() = this as Id<A>
        }
    }

    private class FunctorImpl : Functor.API<TK> {
        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<TK, A>) -> App<TK, B> =
            { ma -> Id(f(ma.fix.value)) }
    }

    private class ApplicativeImpl(override val functor: Functor.Core<TK>) :
        Applicative.API<TK>,
        Applicative.WithPureMapAndProduct<TK>,
        Applicative.ViaFunctor<TK> {
        override suspend fun <A> pure(a: A): App<TK, A> = Id(a)

        override suspend fun <A, B> product(ma: App<TK, A>): suspend (mb: App<TK, B>) -> App<TK, Pair<A, B>> =
            { mb -> Id(ma.fix.value to mb.fix.value) }
    }

    private class MonadImpl(override val applicative: Applicative.API<TK>) :
        Monad.API<TK>,
        Monad.WithReturnsMapAndJoin<TK>,
        Monad.ViaApplicative<TK>,
        Applicative.Core<TK> by applicative {
        override suspend fun <A> join(mma: App<TK, App<TK, A>>): App<TK, A> =
            mma.fix.value

        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<TK, A>) -> App<TK, B> {
            return applicative.map(f)
        }
    }

    val functor: Functor.API<TK> = FunctorImpl()

    val applicative: Applicative.API<TK> = ApplicativeImpl(functor)

    val monad: Monad.API<TK> = MonadImpl(applicative)

}
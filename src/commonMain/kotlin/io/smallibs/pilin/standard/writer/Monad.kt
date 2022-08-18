package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.standard.writer.Applicative.applicative
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {
    private class MonadImpl<F, T>(private val inner: Monad.Core<F>, private val tape: Monoid.Core<T>) :
        Monad.API<WriterK<F, T>>, Monad.WithReturnsAndBind<WriterK<F, T>>,
        Monad.ViaApplicative<WriterK<F, T>>(applicative(inner, tape)) {

        override suspend fun <A, B> bind(f: Fun<A, App<WriterK<F, T>, B>>): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, B>> =
            { ma ->
                Writer(inner.bind { (x, t): Pair<A, T> ->
                    inner.map { (y, u): Pair<B, T> ->
                        y to tape.combine(t, u)
                    }(f(x).run)
                }(ma.run))
            }

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, B>> =
            { ma -> Writer(inner.map { (a, t): Pair<A, T> -> f(a) to t }(ma.run)) }
    }

    fun <F, T> monad(inner: Monad.Core<F>, tape: Monoid.Core<T>): Monad.API<WriterK<F, T>> = MonadImpl(inner, tape)
}
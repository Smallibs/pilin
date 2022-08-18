package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<F, T>(val inner: Applicative.Core<F>, val tape: Monoid.Core<T>) :
        Applicative.API<WriterK<F, T>>,
        Applicative.WithPureAndApply<WriterK<F, T>> {

        private val innerOperation = Applicative.Operation(inner)

        override suspend fun <A> pure(a: A): App<WriterK<F, T>, A> = Writer(inner.pure(a to tape.neutral))

        override suspend fun <A, B> apply(mf: App<WriterK<F, T>, Fun<A, B>>): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, B>> =
            { ma ->
                val g: Fun<Pair<Fun<A, B>, T>, Fun<Pair<A, T>, Pair<B, T>>> =
                    { (f, t) -> { (g, u) -> f(g) to tape.combine(t, u) } }
                Writer(innerOperation.lift2(g)(mf.run)(ma.run))
            }
    }

    fun <F, T> applicative(inner: Applicative.Core<F>, tape: Monoid.Core<T>): Applicative.API<WriterK<F, T>> =
        ApplicativeImpl(inner, tape)
}
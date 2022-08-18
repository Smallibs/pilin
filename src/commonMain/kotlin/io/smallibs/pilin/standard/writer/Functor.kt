package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<F, T>(private val inner: Functor.Core<F>) : Functor.API<WriterK<F, T>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, B>> =
            { ma -> Writer(inner.map { (a, t): Pair<A, T> -> f(a) to t }(ma.run)) }
    }

    fun <F, E> functor(inner: Functor.Core<F>): Functor.API<WriterK<F, E>> = FunctorImpl(inner)
}
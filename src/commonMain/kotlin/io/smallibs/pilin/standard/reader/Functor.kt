package io.smallibs.pilin.standard.reader

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.standard.reader.Reader.ReaderK
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Functor {
    private class FunctorImpl<F, E>(private val inner: Functor.Core<F>) : Functor.API<ReaderK<F, E>> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ReaderK<F, E>, A>, App<ReaderK<F, E>, B>> =
            { ma -> Reader { inner.map(f)(ma(it)) } }
    }

    fun <F, E> functor(inner: Functor.Core<F>): Functor.API<ReaderK<F, E>> = FunctorImpl(inner)
}
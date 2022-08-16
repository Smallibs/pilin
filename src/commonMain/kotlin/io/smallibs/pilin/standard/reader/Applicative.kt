package io.smallibs.pilin.standard.reader

import io.smallibs.pilin.abstractions.Applicative
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.reader.Reader.ReaderK
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Applicative {
    private class ApplicativeImpl<F, E>(val inner: Applicative.Core<F>) :
        Applicative.API<ReaderK<F, E>>,
        Applicative.WithPureAndApply<ReaderK<F, E>> {
        override suspend fun <A> pure(a: A): App<ReaderK<F, E>, A> =
            Reader(Standard.const(inner.pure(a)))

        override suspend fun <A, B> apply(mf: App<ReaderK<F, E>, Fun<A, B>>): Fun<App<ReaderK<F, E>, A>, App<ReaderK<F, E>, B>> =
            { ma ->
                Reader { e -> inner.apply(mf(e))(ma(e)) }
            }
    }

    fun <F, E> applicative(inner: Applicative.Core<F>): Applicative.API<ReaderK<F, E>> =
        ApplicativeImpl(inner)
}
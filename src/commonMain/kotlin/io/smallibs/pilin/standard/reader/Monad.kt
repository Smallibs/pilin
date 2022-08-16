package io.smallibs.pilin.standard.reader

import io.smallibs.pilin.abstractions.Functor
import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.standard.reader.Applicative.applicative
import io.smallibs.pilin.standard.reader.Functor.functor
import io.smallibs.pilin.standard.reader.Reader.ReaderK
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {
    private class MonadImpl<F, E>(val inner: Monad.Core<F>) :
        Monad.API<ReaderK<F, E>>,
        Monad.WithReturnsAndBind<ReaderK<F, E>>,
        Monad.ViaApplicative<ReaderK<F, E>>(applicative(inner)) {

        val functor: Functor.Core<ReaderK<F, E>> = functor(inner)

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ReaderK<F, E>, A>, App<ReaderK<F, E>, B>> =
            functor.map(f)

        override suspend fun <A, B> bind(f: Fun<A, App<ReaderK<F, E>, B>>): Fun<App<ReaderK<F, E>, A>, App<ReaderK<F, E>, B>> =
            { reader -> Reader { e -> inner.bind<A, B> { f(it)(e) }(reader(e)) } }
    }

    fun <F, E> monad(inner: Monad.Core<F>): Monad.API<ReaderK<F, E>> = MonadImpl(inner)
}
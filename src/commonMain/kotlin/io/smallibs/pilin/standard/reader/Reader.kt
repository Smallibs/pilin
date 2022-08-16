package io.smallibs.pilin.standard.reader

import io.smallibs.pilin.abstractions.Monad.API
import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.reader.Reader.ReaderK
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.abstractions.Applicative.API as Applicative_API
import io.smallibs.pilin.abstractions.Functor.API as Functor_API
import io.smallibs.pilin.abstractions.Monad.API as Monad_API

class Reader<F, E, A>(val run: Fun<E, App<F, A>>) : App<ReaderK<F, E>, A> {

    // This code can be automatically generated
    class ReaderK<F, E> private constructor() {
        companion object {
            private val <F, E, A> App<ReaderK<F, E>, A>.fix: Reader<F, E, A>
                get() = this as Reader<F, E, A>

            operator fun <F, E, A> App<ReaderK<F, E>, A>.invoke(e: E): App<F, A> =
                this.fix(e)
        }
    }

    class OverMonad<F, E>(val monad: API<F>) : Transformer<F, ReaderK<F, E>> {
        fun <A> run(reader: App<ReaderK<F, E>, A>): Fun<E, App<F, A>> =
            { reader(it) }

        val ask: Reader<F, E, E> =
            Reader(monad::returns)

        fun <A> local(f: Fun<E, E>): Fun<Reader<F, E, A>, Reader<F, E, A>> =
            { r -> Reader { r.run(f(it)) } }

        fun <A> reader(f: Fun<E, App<F, A>>): Reader<F, E, A> =
            Reader(f)

        override suspend fun <A> upper(c: App<F, A>): App<ReaderK<F, E>, A> =
            Reader { c }
    }

    companion object {
        fun <F, E> functor(f: Functor_API<F>) = Functor.functor<F, E>(f)
        fun <F, E> applicative(a: Applicative_API<F>) = Applicative.applicative<F, E>(a)
        fun <F, E> monad(m: Monad_API<F>) = Monad.monad<F, E>(m)
        fun <F, E> selective(m: Monad_API<F>) = Selective.selective<F, E>(m)
    }
}
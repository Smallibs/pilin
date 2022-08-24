package io.smallibs.pilin.standard.reader

import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.reader.Reader.ReaderK
import io.smallibs.pilin.standard.reader.Reader.ReaderK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.abstractions.Monad.API as Monad_API
import io.smallibs.pilin.abstractions.Monad.Core as Monad_Core

class Reader<F, E, A>(val run: Fun<E, App<F, A>>) : App<ReaderK<F, E>, A> {

    // This code can be automatically generated
    class ReaderK<F, E> private constructor() {
        companion object {
            private val <F, E, A> App<ReaderK<F, E>, A>.fix: Reader<F, E, A>
                get() = this as Reader<F, E, A>

            suspend operator fun <F, E, A> App<ReaderK<F, E>, A>.invoke(e: E): App<F, A> = this.fix.run(e)
        }
    }

    open class OverMonad<F, E>(
        private val inner: Monad_Core<F>,
        private val api: Monad_API<ReaderK<F, E>> = Monad.monad(inner),
    ) : Monad_API<ReaderK<F, E>> by api {
        fun <A> reader(f: Fun<E, App<F, A>>): Reader<F, E, A> = Reader(f)

        fun <A> run(reader: App<ReaderK<F, E>, A>): Fun<E, App<F, A>> = { reader(it) }

        val ask: Reader<F, E, E> get() = Reader(inner::returns)

        fun <A> local(f: Fun<E, E>): Fun<Reader<F, E, A>, Reader<F, E, A>> = { r -> Reader { r.run(f(it)) } }

        fun transformer(): Transformer<F, ReaderK<F, E>> {
            return object : Transformer<F, ReaderK<F, E>> {
                override suspend fun <A> upper(ma: App<F, A>): App<ReaderK<F, E>, A> = Reader { ma }
            }
        }

        fun functor() = Functor.functor<F, E>(inner)
        fun applicative() = Applicative.applicative<F, E>(inner)
        fun monad() = api
        fun selective() = Selective.selective<F, E>(inner)
    }

    class Over<E> : OverMonad<IdentityK, E>(Identity.monad)
}
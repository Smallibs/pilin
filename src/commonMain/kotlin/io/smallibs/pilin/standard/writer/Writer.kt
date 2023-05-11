package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Id
import io.smallibs.pilin.abstractions.Monad.API as Monad_API
import io.smallibs.pilin.abstractions.Monad.Core as Monad_Core

class Writer<F, T, A>(val run: App<F, Pair<A, T>>) : App<WriterK<F, T>, A> {

    // This code can be automatically generated
    class WriterK<F, T> private constructor() {
        companion object {
            private val <F, T, A> App<WriterK<F, T>, A>.fix: Writer<F, T, A>
                get() = this as Writer<F, T, A>

            val <F, T, A> App<WriterK<F, T>, A>.run: App<F, Pair<A, T>>
                get() = this.fix.run
        }
    }

    open class OverMonad<F, T>(
        private val inner: Monad_Core<F>,
        private val tape: Monoid.Core<T>,
        private val api: Monad_API<WriterK<F, T>> = Monad.monad(inner, tape),
    ) : Monad_API<WriterK<F, T>> by api {

        suspend fun <A> writer(a: A, t: T): Writer<F, T, A> = Writer(inner.returns(a to t))

        fun <A> run(w: App<WriterK<F, T>, A>): App<F, Pair<A, T>> = w.run

        suspend fun <A> exec(w: App<WriterK<F, T>, A>): App<F, T> = inner.map { (_, t): Pair<A, T> -> t }(w.run)

        suspend fun tell(t: T): Writer<F, T, Unit> = writer(Unit, t)

        suspend fun <A> listen(w: App<WriterK<F, T>, A>): App<WriterK<F, T>, Pair<A, T>> =
            Writer(inner.map { (a, t): Pair<A, T> -> (a to t) to t }(w.run))

        suspend fun <A, B> listens(f: (T) -> B): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, Pair<A, B>>> = { w ->
            Writer(inner.map { (a, t): Pair<A, T> -> (a to f(t)) to t }(w.run))
        }

        suspend fun <A> pass(w: Writer<F, T, Pair<A, Id<T>>>): Writer<F, T, A> =
            Writer(inner.map { (a, t): Pair<Pair<A, Id<T>>, T> -> a.first to a.second(t) }(w.run))

        suspend fun <A> censor(f: Fun<T, T>): Fun<Writer<F, T, A>, Writer<F, T, A>> = { w ->
            Writer(inner.map { (a, t): Pair<A, T> -> a to f(t) }(w.run))
        }

        suspend fun transformer(tape: Monoid.Core<T>): Transformer<F, WriterK<F, T>> {
            return object : Transformer<F, WriterK<F, T>> {
                override suspend operator fun <A> invoke(ma: App<F, A>): App<WriterK<F, T>, A> =
                    Writer(inner.bind { a: A -> inner.returns(a to tape.neutral) }(ma))
            }
        }

        fun functor() = Functor.functor<F, T>(inner)
        fun applicative() = Applicative.applicative(inner, tape)
        fun monad() = api
        fun selective() = Selective.selective(inner, tape)
    }

    class OverMonoid<T>(tape: Monoid.Core<T>) : OverMonad<IdentityK, T>(Identity.monad, tape)
}
package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Id
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

    class OverMonad<F>(private val inner: Monad_Core<F>) {

        suspend fun <T, A> writer(a: A, t: T): Writer<F, T, A> = Writer(inner.returns(a to t))

        fun <T, A> run(w: App<WriterK<F, T>, A>): App<F, Pair<A, T>> = w.run

        suspend fun <T, A> exec(w: App<WriterK<F, T>, A>): App<F, T> =
            inner.map { (_, t): Pair<A, T> -> t }(w.run)

        suspend fun <T> tell(t: T): Writer<F, T, Unit> = writer(Unit, t)

        suspend fun <T, A> listen(w: App<WriterK<F, T>, A>): App<WriterK<F, T>, Pair<A, T>> =
            Writer(inner.map { (a, t): Pair<A, T> -> (a to t) to t }(w.run))

        suspend fun <T, A, B> listens(f: (T) -> B): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, Pair<A, B>>> = { w ->
            Writer(inner.map { (a, t): Pair<A, T> -> (a to f(t)) to t }(w.run))
        }

        suspend fun <T, A> pass(w: Writer<F, T, Pair<A, Id<T>>>): Writer<F, T, A> =
            Writer(inner.map { (a, t): Pair<Pair<A, Id<T>>, T> -> a.first to a.second(t) }(w.run))

        suspend fun <T, A> censor(f: Fun<T, T>): Fun<Writer<F, T, A>, Writer<F, T, A>> = { w ->
            Writer(inner.map { (a, t): Pair<A, T> -> a to f(t) }(w.run))
        }

        suspend fun <T> transformer(tape: Monoid.Core<T>): Transformer<F, WriterK<F, T>> {
            return object : Transformer<F, WriterK<F, T>> {
                override suspend fun <A> upper(ma: App<F, A>): App<WriterK<F, T>, A> =
                    Writer(inner.bind { a: A -> inner.returns(a to tape.neutral) }(ma))
            }
        }

        fun <T> functor() = Functor.functor<F, T>(inner)
        fun <T> applicative(tape: Monoid.Core<T>) = Applicative.applicative(inner, tape)
        fun <T> monad(tape: Monoid.Core<T>) = Monad.monad(inner, tape)
        fun <T> selective(tape: Monoid.Core<T>) = Selective.selective(inner, tape)
    }
}
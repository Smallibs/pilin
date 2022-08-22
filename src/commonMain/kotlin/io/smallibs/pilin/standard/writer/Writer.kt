package io.smallibs.pilin.standard.writer

import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.type.Id
import io.smallibs.pilin.abstractions.Applicative.Core as Applicative_Core
import io.smallibs.pilin.abstractions.Functor.Core as Functor_Core
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

    class OverMonad<F, T>(private val monad: Monad_Core<F>, private val tape: Monoid.Core<T>) :
        Transformer<F, WriterK<F, T>> {

        suspend fun <A> writer(a: A, t: T): Writer<F, T, A> = Writer(monad.returns(a to t))

        fun <F, T, A> run(w: App<WriterK<F, T>, A>): App<F, Pair<A, T>> = w.run

        suspend fun <A> exec(w: App<WriterK<F, T>, A>): App<F, T> =
            monad.map { (_, t): Pair<A, T> -> t }(w.run)

        suspend fun <A> tell(t: T): Writer<F, T, Unit> = writer(Unit, t)

        suspend fun <A> listen(w: App<WriterK<F, T>, A>): App<WriterK<F, T>, Pair<A, T>> =
            Writer(monad.map { (a, t): Pair<A, T> -> (a to t) to t }(w.run))

        suspend fun <A, B> listens(f: (T) -> B): Fun<App<WriterK<F, T>, A>, App<WriterK<F, T>, Pair<A, B>>> = { w ->
            Writer(monad.map { (a, t): Pair<A, T> -> (a to f(t)) to t }(w.run))
        }

        suspend fun <A> pass(w: Writer<F, T, Pair<A, Id<T>>>): Writer<F, T, A> =
            Writer(monad.map { (a, t): Pair<Pair<A, Id<T>>, T> -> a.first to a.second(t) }(w.run))

        suspend fun <A> censor(f: Fun<T, T>): Fun<Writer<F, T, A>, Writer<F, T, A>> = { w ->
            Writer(monad.map { (a, t): Pair<A, T> -> a to f(t) }(w.run))
        }

        override suspend fun <A> upper(ma: App<F, A>): App<WriterK<F, T>, A> =
            Writer(monad.bind { a: A -> monad.returns(a to tape.neutral) }(ma))
    }

    companion object {
        fun <F, T> functor(f: Functor_Core<F>) = Functor.functor<F, T>(f)
        fun <F, T> applicative(a: Applicative_Core<F>, t: Monoid.Core<T>) = Applicative.applicative(a, t)
        fun <F, T> monad(m: Monad_Core<F>, t: Monoid.Core<T>) = Monad.monad(m, t)
        fun <F, T> selective(m: Monad_Core<F>, t: Monoid.Core<T>) = Selective.selective(m, t)
    }
}
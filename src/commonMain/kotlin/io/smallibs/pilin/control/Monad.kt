package io.smallibs.pilin.control

import io.smallibs.pilin.core.Fun
import io.smallibs.pilin.type.App

object Monad {

    interface Core<F> : Applicative.Core<F> {
        suspend fun <A> returns(a: A): App<F, A>
        suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
        suspend fun <A, B> bind(f: suspend (A) -> App<F, B>): suspend (App<F, A>) -> App<F, B>
        suspend fun <A, B, C> leftToRight(f: suspend (A) -> App<F, B>): suspend (suspend (B) -> App<F, C>) -> suspend (A) -> App<F, C>
    }

    interface WithReturnsBindAndReturn<F> : Core<F> {
        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> =
            bind { a -> returns(f(a)) }

        override suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A> =
            bind<App<F, A>, A>(Fun::id)(mma)

        override suspend fun <A, B, C> leftToRight(f: suspend (A) -> App<F, B>): suspend (suspend (B) -> App<F, C>) -> suspend (A) -> App<F, C> =
            { g -> { x -> bind(g)(f(x)) } }
    }

    interface WithReturnsMapAndJoin<F> : Core<F> {
        override suspend fun <A, B> bind(f: suspend (A) -> App<F, B>): suspend (App<F, A>) -> App<F, B> =
            { x -> join(map(f)(x)) }

        override suspend fun <A, B, C> leftToRight(f: suspend (A) -> App<F, B>): suspend (suspend (B) -> App<F, C>) -> suspend (A) -> App<F, C> =
            { g -> { x -> bind(g)(f(x)) } }
    }

    interface WithReturnsAndKleisli<F> : Core<F> {
        override suspend fun <A, B> bind(f: suspend (A) -> App<F, B>): suspend (App<F, A>) -> App<F, B> =
            { m -> leftToRight<Unit, A, B> { m }(f)(Unit) }

        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> =
            bind { a -> returns(f(a)) }

        override suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A> =
            bind<App<F, A>, A>(Fun::id)(mma)
    }

    interface ViaApplicative<F> : Core<F> {
        val applicative: Applicative.Core<F>

        override suspend fun <A> returns(a: A): App<F, A> = applicative.pure(a)
        override suspend fun <A, B> map(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> = applicative.map(f)
    }

    class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> lift(f: suspend (A) -> B): suspend (App<F, A>) -> App<F, B> =
            map(f)

        suspend fun <A, B, C> lift2(f: suspend (A) -> suspend (B) -> C): suspend (App<F, A>) -> suspend (App<F, B>) -> App<F, C> =
            { ma -> { mb -> bind<A, C> { a -> bind<B, C> { b -> returns(f(a)(b)) }(mb) }(ma) } }

        suspend fun <A, B, C, D> lift3(f: suspend (A) -> suspend (B) -> suspend (C) -> D): suspend (App<F, A>) -> suspend (App<F, B>) -> suspend (App<F, C>) -> App<F, D> =
            { ma -> { mb -> { mc -> bind<A, D> { a -> bind<B, D> { b -> bind<C,D> { c -> returns(f(a)(b)(c)) }(mc) }(mb) }(ma) } }}
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Infix<F>(private val c: Core<F>) : Applicative.Infix<F>(c), Core<F> by c {
        suspend infix fun <A, B> App<F, A>.bind(f: suspend (A) -> App<F, B>): App<F, B> = c.bind(f)(this)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }

}


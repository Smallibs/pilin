package io.smallibs.pilin.control

import io.smallibs.pilin.control.extension.Comprehension
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.core.Standard.Infix.then
import io.smallibs.pilin.core.Standard.curry
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Monad {

    interface Core<F> : Applicative.Core<F> {
        suspend fun <A> returns(a: A): App<F, A>
        suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A>
        suspend fun <A, B> bind(f: Fun<A, App<F, B>>): Fun<App<F, A>, App<F, B>>
        suspend fun <A, B, C> leftToRight(f: Fun<A, App<F, B>>): Fun<Fun<B, App<F, C>>, Fun<A, App<F, C>>>
    }

    interface WithReturnsAndBind<F> : Core<F> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<F, A>, App<F, B>> =
            bind(f then ::returns)

        override suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A> =
            bind<App<F, A>, A>(Standard::id)(mma)

        override suspend fun <A, B, C> leftToRight(f: Fun<A, App<F, B>>): Fun<Fun<B, App<F, C>>, Fun<A, App<F, C>>> =
            { g -> f then bind(g) }
    }

    interface WithReturnsMapAndJoin<F> : Core<F> {
        override suspend fun <A, B> bind(f: Fun<A, App<F, B>>): Fun<App<F, A>, App<F, B>> =
            map(f) then ::join

        override suspend fun <A, B, C> leftToRight(f: Fun<A, App<F, B>>): Fun<Fun<B, App<F, C>>, Fun<A, App<F, C>>> =
            { g -> f then bind(g) }
    }

    interface WithReturnsAndKleisli<F> : Core<F> {
        override suspend fun <A, B> bind(f: Fun<A, App<F, B>>): Fun<App<F, A>, App<F, B>> =
            { m -> leftToRight<Unit, A, B> { m }(f)(Unit) }

        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<F, A>, App<F, B>> =
            bind(f then ::returns)

        override suspend fun <A> join(mma: App<F, App<F, A>>): App<F, A> =
            bind<App<F, A>, A>(Standard::id)(mma)
    }

    abstract class ViaApplicative<F>(private val applicative: Applicative.Core<F>) : Core<F>,
        Applicative.Core<F> by applicative {
        override suspend fun <A> returns(a: A): App<F, A> = pure(a)
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<F, A>, App<F, B>> = applicative.map(f)
    }

    class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> lift(f: Fun<A, B>): Fun<App<F, A>, App<F, B>> =
            map(f)

        suspend fun <A, B, C> lift2(f: Fun<A, Fun<B, C>>): Fun<App<F, A>, Fun<App<F, B>, App<F, C>>> =
            curry { ma, mb -> bind<A, C> { a -> bind(f(a) then ::returns)(mb) }(ma) }

        suspend fun <A, B, C, D> lift3(f: Fun<A, Fun<B, Fun<C, D>>>): Fun<App<F, A>, Fun<App<F, B>, Fun<App<F, C>, App<F, D>>>> =
            curry { ma, mb, mc ->
                bind<A, D> { a -> bind<B, D> { b -> bind(f(a)(b) then ::returns)(mc) }(mb) }(ma)
            }
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Infix<F>(private val c: Core<F>) : Applicative.Infix<F>(c), Core<F> by c {
        suspend infix fun <A, B> App<F, A>.bind(f: Fun<A, App<F, B>>): App<F, B> = c.bind(f)(this)
    }

    class Do<F>(private val c: API<F>) {
        suspend infix operator fun <A> invoke(f: suspend Comprehension<F>.() -> A): App<F, A> =
            Comprehension.run(c, f)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
        val `do`: Do<F> get() = Do(this)
    }

}


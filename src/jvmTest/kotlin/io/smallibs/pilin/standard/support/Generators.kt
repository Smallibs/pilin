package io.smallibs.pilin.standard.support

import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.EitherK
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.OptionK
import io.smallibs.pilin.standard.result.Result
import io.smallibs.pilin.standard.result.Result.ResultK
import io.smallibs.pilin.standard.`try`.Try
import io.smallibs.pilin.standard.`try`.Try.TryK
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import org.quicktheories.core.Gen
import org.quicktheories.generators.SourceDSL.integers

object Generators {

    fun <A> identity(gen: Gen<A>): Gen<App<IdentityK, A>> = gen.map { a ->
        Identity.id(a)
    }

    fun <A> identity(): Gen<Fun<A, App<IdentityK, A>>> = constant { a -> Identity.id(a) }

    fun <A> option(gen: Gen<A>): Gen<App<OptionK, A>> = integers().allPositive().flatMap { i ->
        gen.map { a ->
            if (i % 2 == 0) {
                Option.none()
            } else {
                Option.some(a)
            }
        }
    }

    fun <A> option(): Gen<Fun<A, App<OptionK, A>>> = integers().allPositive().map { i ->
        if (i % 2 == 0) {
            { Option.none() }
        } else {
            { a -> Option.some(a) }
        }
    }

    fun <A, B> either(lgen: Gen<A>): (Gen<B>) -> Gen<App<EitherK<A>, B>> = { rgen ->
        integers().allPositive().flatMap { i ->
            if (i % 2 == 0) {
                rgen.map { a -> Either.right(a) }
            } else {
                lgen.map { a -> Either.left(a) }
            }
        }
    }

    fun <A, B> either(l: A): Gen<Fun<B, App<EitherK<A>, B>>> = integers().allPositive().map { i ->
        if (i % 2 == 0) {
            { r -> Either.right(r) }
        } else {
            { Either.left(l) }
        }
    }

    fun <A, B> result(egen: Gen<B>): (Gen<A>) -> Gen<App<ResultK<B>, A>> = { rgen ->
        integers().allPositive().flatMap { i ->
            if (i % 2 == 0) {
                rgen.map { a -> Result.ok(a) }
            } else {
                egen.map { a -> Result.error(a) }
            }
        }
    }

    fun <A, B> result(e: B): Gen<Fun<A, App<ResultK<B>, A>>> = integers().allPositive().map { i ->
        if (i % 2 == 0) {
            { r -> Result.ok(r) }
        } else {
            { Result.error(e) }
        }
    }

    fun <A> `try`(egen: Gen<Throwable>): (Gen<A>) -> Gen<App<TryK, A>> = { rgen ->
        integers().allPositive().flatMap { i ->
            if (i % 2 == 0) {
                rgen.map { a -> Try.success(a) }
            } else {
                egen.map { a -> Try.failure(a) }
            }
        }
    }

    fun <A> `try`(e: Throwable): Gen<Fun<A, App<TryK, A>>> = integers().allPositive().map { i ->
        if (i % 2 == 0) {
            { r -> Try.success(r) }
        } else {
            { Try.failure(e) }
        }
    }

    private fun <I> continuation(a: I): Continuation<I> = object : Continuation<I> {
        override suspend fun <O> invoke(k: Fun<I, O>): O = k(a)
    }

    fun <A> continuation(gen: Gen<A>): Gen<App<Continuation.ContinuationK, A>> = gen.map(::continuation)


    fun <A> continuation(): Gen<Fun<A, App<Continuation.ContinuationK, A>>> =
        identity<Unit>().map { _ -> { a -> continuation(a) } }

    fun <A> constant(a: A): Gen<A> = Gen { a }

}
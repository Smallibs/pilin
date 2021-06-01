package io.smallibs.pilin.standard.support

import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import org.quicktheories.core.Gen
import org.quicktheories.generators.SourceDSL.integers

fun <A> identity(gen: Gen<A>): Gen<App<Identity.TK, A>> =
    gen.map { a ->
        Identity.id(a)
    }

fun <A> identity(): Gen<Fun<A, App<Identity.TK, A>>> =
    constant { a -> Identity.id(a) }

fun <A> option(gen: Gen<A>): Gen<App<Option.TK, A>> =
    integers().allPositive().flatMap { i ->
        gen.map { a ->
            if (i % 2 == 0) {
                Option.none()
            } else {
                Option.some(a)
            }
        }
    }

fun <A> option(): Gen<Fun<A, App<Option.TK, A>>> =
    integers().allPositive().map { i ->
        if (i % 2 == 0) {
            { _ -> Option.none() }
        } else {
            { a -> Option.some(a) }
        }
    }

fun <A, B> either(lgen: Gen<A>): (Gen<B>) -> Gen<App<Either.TK<A>, B>> =
    { rgen ->
        integers().allPositive().flatMap { i ->
            if (i % 2 == 0) {
                rgen.map { a -> Either.right(a) }
            } else {
                lgen.map { a -> Either.left(a) }
            }
        }
    }

fun <A> either(): Gen<Fun<A, App<Either.TK<Unit>, A>>> =
    integers().allPositive().map { i ->
        if (i % 2 == 0) {
            { r -> Either.right(r) }
        } else {
            { Either.left(Unit) }
        }
    }

fun <A> continuation(gen: Gen<A>): Gen<App<Continuation.TK<A>, A>> =
    gen.map { a ->
        Continuation.continuation { k -> k(a) }
    }

fun <A> constant(a: A): Gen<A> = Gen { a }


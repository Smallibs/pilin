package io.smallibs.pilin.standard.generator

import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.type.App
import org.quicktheories.core.Gen
import org.quicktheories.generators.SourceDSL.integers

fun <A> identity(gen: Gen<A>): Gen<App<Identity.TK, A>> =
    gen.map { a ->
        Identity.id(a)
    }

fun <A> either(gen: Gen<A>): Gen<App<Either.TK<A>, A>> =
    integers().allPositive().flatMap { i ->
        gen.map { a ->
            if (i % 2 == 0) {
                Either.right(a)
            } else {
                Either.left(a)
            }
        }
    }

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

fun <A> continuation(gen: Gen<A>): Gen<App<Continuation.TK<A>, A>> =
    gen.map { a ->
        Continuation.continuation { k -> k(a) }
    }

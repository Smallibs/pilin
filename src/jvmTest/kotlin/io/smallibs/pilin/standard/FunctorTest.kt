package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class FunctorTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }
    private val int: Fun<String, Int> = { i -> i.toInt() }

    @Test
    fun `(Identity) map id = id `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.functor.`map id = id`(id(a)) }
        }
    }

    @Test
    fun `(Option) map id = id `() {
        check(runBlocking { Option.functor.`map id = id`(none<Int>()) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.functor.`map id = id`(some(a)) }
        }
    }

    @Test
    fun `(Either) map id = id `() {
        check(runBlocking { Either.functor<Unit>().`map id = id`(left<Unit, Int>(Unit)) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.functor<Unit>().`map id = id`(right(a)) }
        }
    }

    @Test
    fun `(Identity) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.functor.`map (f compose g) = map f compose map g`(int, str, id(a)) }
        }
    }

    @Test
    fun `(Option) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.functor.`map (f compose g) = map f compose map g`(int, str, some(a)) }
        }
    }

    @Test
    fun `(Either) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.functor<Unit>().`map (f compose g) = map f compose map g`(int, str, right(a)) }
        }
    }
}
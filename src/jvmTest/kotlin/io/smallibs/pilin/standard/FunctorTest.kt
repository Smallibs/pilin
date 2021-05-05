package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.Either.T.Left
import io.smallibs.pilin.standard.Either.T.Right
import io.smallibs.pilin.standard.Identity.Id
import io.smallibs.pilin.standard.Option.T.None
import io.smallibs.pilin.standard.Option.T.Some
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
            runBlocking { Identity.functor.`map id = id`(Id(a)) }
        }
    }

    @Test
    fun `(Option) map id = id `() {
        runBlocking { Option.functor.`map id = id`(None<Int>()) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.functor.`map id = id`(Some(a)) }
        }
    }

    @Test
    fun `(Either) map id = id `() {
        runBlocking { Either.functor<Unit>().`map id = id`(Left<Unit, Int>(Unit)) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.functor<Unit>().`map id = id`(Right(a)) }
        }
    }

    @Test
    fun `(Identity) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.functor.`map (f compose g) = map f compose map g`(int, str, Id(a)) }
        }
    }

    @Test
    fun `(Option) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.functor.`map (f compose g) = map f compose map g`(int, str, Some(a)) }
        }
    }

    @Test
    fun `(Either) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.functor<Unit>().`map (f compose g) = map f compose map g`(int, str, Right(a)) }
        }
    }
}
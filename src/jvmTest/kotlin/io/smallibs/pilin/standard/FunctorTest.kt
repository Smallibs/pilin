package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.Either.T.Left
import io.smallibs.pilin.standard.Either.T.Right
import io.smallibs.pilin.standard.Identity.Id
import io.smallibs.pilin.standard.Option.T.None
import io.smallibs.pilin.standard.Option.T.Some
import kotlinx.coroutines.runBlocking
import org.junit.Test

// TODO Use a real PBT engine and not this ugly ad-hoc implementation

internal class FunctorTest {

    private val str: suspend (Int) -> String = { i -> i.toString() }
    private val int: suspend (String) -> Int = { i -> i.toInt() }

    @Test
    fun `(Identity) map id = id `() {
        for (a in -500..500) {
            runBlocking { `map id = id`(Identity.functor, Id(a)) }
        }
    }

    @Test
    fun `(Option) map id = id `() {
        runBlocking { `map id = id`(Option.functor, None<Int>()) }

        for (a in -500..500) {
            runBlocking { `map id = id`(Option.functor, Some(a)) }
        }
    }

    @Test
    fun `(Either) map id = id `() {
        runBlocking { `map id = id`(Either.functor(), Left<Unit, Int>(Unit)) }

        for (a in -500..500) {
            runBlocking { `map id = id`(Either.functor<Unit>(), Right(a)) }
        }
    }

    @Test
    fun `(Identity) map (incr compose toString) = (map incr) compose (map toString) `() {
        for (a in -500..500) {
            runBlocking { `map (f compose g) = map f compose map g`(Identity.functor, int, str, Id(a)) }
        }
    }

    @Test
    fun `(Option) map (incr compose toString) = (map incr) compose (map toString) `() {
        for (a in -500..500) {
            runBlocking { `map (f compose g) = map f compose map g`(Option.functor, int, str, Some(a)) }
        }
    }

    @Test
    fun `(Either) map (incr compose toString) = (map incr) compose (map toString) `() {
        for (a in -500..500) {
            runBlocking { `map (f compose g) = map f compose map g`(Either.functor<Unit>(), int, str, Right(a)) }
        }
    }
}
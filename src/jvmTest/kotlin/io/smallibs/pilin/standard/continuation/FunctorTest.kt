package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.standard.support.Functions
import io.smallibs.pilin.standard.support.continuation
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class FunctorTest : WithQuickTheories {

    @Test
    fun `map id = id `() {
        qt().forAll(continuation(integers().all())).check { a ->
            runBlocking { Continuation.functor.`map id = id`(a, Equatable.continuation()) }
        }
    }

    @Test
    fun `map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(continuation(integers().all())).check { a ->
            runBlocking {
                Continuation.functor.`map (f compose g) = map f compose map g`(Functions.int,
                    Functions.str, a, Equatable.continuation())
            }
        }
    }
}
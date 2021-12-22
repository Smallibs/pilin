package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.identity
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class FunctorTest : WithQuickTheories {

    @Test
    fun `map id = id `() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking { Identity.functor.`map id = id`(a) }
        }
    }

    @Test
    fun `map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking { Identity.functor.`map (f compose g) = map f compose map g`(int, str, a) }
        }
    }

}
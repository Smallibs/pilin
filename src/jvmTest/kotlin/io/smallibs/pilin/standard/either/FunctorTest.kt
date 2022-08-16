package io.smallibs.pilin.standard.either

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.either.Either.Companion.functor
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.either
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class FunctorTest : WithQuickTheories {

    @Test
    fun `map id = id `() {
        qt().forAll(either<Unit, Int>(constant(Unit))(integers().all())).check { a ->
            runBlocking { functor<Unit>().`map id = id`(a) }
        }
    }

    @Test
    fun `map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(either<Unit, Int>(constant(Unit))(integers().all())).check { a ->
            runBlocking { functor<Unit>().`map (f compose g) = map f compose map g`(int, str, a) }
        }
    }
}
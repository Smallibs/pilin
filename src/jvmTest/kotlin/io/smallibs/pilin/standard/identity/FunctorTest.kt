package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.support.constant
import io.smallibs.pilin.standard.support.either
import io.smallibs.pilin.standard.support.identity
import io.smallibs.pilin.standard.support.option
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class FunctorTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }
    private val int: Fun<String, Int> = { i -> i.toInt() }

    @Test
    fun `(Identity) map id = id `() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking { Identity.functor.`map id = id`(a) }
        }
    }

    @Test
    fun `(Identity) map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking { Identity.functor.`map (f compose g) = map f compose map g`(int, str, a) }
        }
    }

}
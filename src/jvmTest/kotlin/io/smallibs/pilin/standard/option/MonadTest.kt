package io.smallibs.pilin.standard.option

import io.smallibs.pilin.laws.Monad.`(a bind f) bind g = a bind {x in f x bind g}`
import io.smallibs.pilin.laws.Monad.`a bind returns = a`
import io.smallibs.pilin.laws.Monad.`returns a bind h = h a`
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.support.Functions.ret
import io.smallibs.pilin.standard.support.constant
import io.smallibs.pilin.standard.support.either
import io.smallibs.pilin.standard.support.identity
import io.smallibs.pilin.standard.support.option
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class MonadTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }
    private val int: Fun<String, Int> = { i -> i.toInt() }

    private suspend fun <F> retStr(r: Fun<String, App<F, String>>): Fun<Int, App<F, String>> =
        ret<F, Int, String>(str)(r)

    private suspend fun <F> retInt(r: Fun<Int, App<F, Int>>): Fun<String, App<F, Int>> =
        ret<F, String, Int>(int)(r)

    @Test
    fun `returns a bind h = h a`() {
        qt().forAll(integers().all(), option<String>()).check { a, r ->
            runBlocking {
                Option.monad.`returns a bind h = h a`(retStr(r), a)
            }
        }
    }

    @Test
    fun `a bind returns = a`() {
        qt().forAll(option(integers().all())).check { a ->
            runBlocking {
                Option.monad.`a bind returns = a`(a)
            }
        }
    }

    @Test
    fun `(a bind f) bind g = a bind {x in f x bind g}`() {
        qt().forAll(option(integers().all()), option<String>(), option<Int>()).check { a, rf, rg ->
            runBlocking {
                Option.monad.`(a bind f) bind g = a bind {x in f x bind g}`(retStr(rf), retInt(rg), a)
            }
        }
    }

}
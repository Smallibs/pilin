package io.smallibs.pilin.standard.identity

import io.smallibs.pilin.laws.Monad.`(a bind f) bind g = a bind {x in f x bind g}`
import io.smallibs.pilin.laws.Monad.`a bind returns = a`
import io.smallibs.pilin.laws.Monad.`returns a bind h = h a`
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.ret
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.identity
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class MonadTest : WithQuickTheories {

    private suspend fun <F> retStr(r: Fun<String, App<F, String>>): Fun<Int, App<F, String>> =
        ret<F, Int, String>(str)(r)

    private suspend fun <F> retInt(r: Fun<Int, App<F, Int>>): Fun<String, App<F, Int>> =
        ret<F, String, Int>(int)(r)

    @Test
    fun `returns a bind h = h a`() {
        qt().forAll(integers().all(), identity<String>()).check { a, r ->
            runBlocking {
                Identity.monad.`returns a bind h = h a`(retStr(r), a)
            }
        }
    }

    @Test
    fun `a bind returns = a`() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking {
                Identity.monad.`a bind returns = a`(a)
            }
        }
    }

    @Test
    fun `(a bind f) bind g = a bind {x in f x bind g}`() {
        qt().forAll(identity(integers().all()), identity<String>(), identity<Int>()).check { a, rf, rg ->
            runBlocking {
                Identity.monad.`(a bind f) bind g = a bind {x in f x bind g}`(retStr(rf), retInt(rg), a)
            }
        }
    }

}
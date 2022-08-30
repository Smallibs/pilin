package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.laws.Monad.`(a bind f) bind g = a bind {x in f x bind g}`
import io.smallibs.pilin.laws.Monad.`a bind returns = a`
import io.smallibs.pilin.laws.Monad.`returns a bind h = h a`
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.ret
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.continuation
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.utils.unsafeSyncRun

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class MonadTest : WithQuickTheories {

    private suspend fun <F> retStr(r: Fun<String, App<F, String>>): Fun<Int, App<F, String>> =
        ret<F, Int, String>(str)(r)

    private suspend fun <F> retInt(r: Fun<Int, App<F, Int>>): Fun<String, App<F, Int>> = ret<F, String, Int>(int)(r)

    @Test
    fun `returns a bind h = h a`() {
        qt().forAll(integers().all(), continuation<String>()).check { a, r ->
            unsafeSyncRun {
                monad.`returns a bind h = h a`(retStr(r), a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `a bind returns = a`() {
        qt().forAll(continuation(integers().all())).check { a ->
            unsafeSyncRun {
                monad.`a bind returns = a`(a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `(a bind f) bind g = a bind {x in f x bind g}`() {
        qt().forAll(continuation(integers().all()), continuation<String>(), continuation<Int>()).check { a, rf, rg ->
                unsafeSyncRun {
                    monad.`(a bind f) bind g = a bind {x in f x bind g}`(
                        retStr(rf), retInt(rg), a, Equatable.continuation()
                    )
                }
            }
    }

}
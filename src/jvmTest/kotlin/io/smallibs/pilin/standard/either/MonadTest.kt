package io.smallibs.pilin.standard.either

import io.smallibs.pilin.laws.Monad.`(a bind f) bind g = a bind {x in f x bind g}`
import io.smallibs.pilin.laws.Monad.`a bind returns = a`
import io.smallibs.pilin.laws.Monad.`returns a bind h = h a`
import io.smallibs.pilin.standard.either.Either.Companion.monad
import io.smallibs.pilin.standard.support.Functions.retInt
import io.smallibs.pilin.standard.support.Functions.retStr
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.either
import utils.unsafeSyncRun

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class MonadTest : WithQuickTheories {

    @Test
    fun `returns a bind h = h a`() {
        qt().forAll(integers().all(), either<Unit, String>(Unit)).check { a, r ->
            unsafeSyncRun {
                monad<Unit>().`returns a bind h = h a`(retStr(r), a)
            }
        }
    }

    @Test
    fun `a bind returns = a`() {
        qt().forAll(either<Unit, Int>(constant(Unit))(integers().all())).check { a ->
            unsafeSyncRun {
                monad<Unit>().`a bind returns = a`(a)
            }
        }
    }

    @Test
    fun `(a bind f) bind g = a bind {x in f x bind g}`() {
        qt().forAll(
            either<Unit, Int>(constant(Unit))(integers().all()), either<Unit, String>(Unit), either<Unit, Int>(Unit)
        ).check { a, rf, rg ->
                unsafeSyncRun {
                    monad<Unit>().`(a bind f) bind g = a bind {x in f x bind g}`(retStr(rf), retInt(rg), a)
                }
            }
    }

}
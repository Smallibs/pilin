package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.laws.Monad.`(a bind f) bind g = a bind {x in f x bind g}`
import io.smallibs.pilin.laws.Monad.`a bind returns = a`
import io.smallibs.pilin.laws.Monad.`returns a bind h = h a`
import io.smallibs.pilin.standard.support.Functions.retInt
import io.smallibs.pilin.standard.support.Functions.retStr
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.`try`
import io.smallibs.pilin.standard.`try`.Try.Companion.monad
import org.junit.Test
import org.quicktheories.WithQuickTheories
import utils.unsafeSyncRun

internal class MonadTest : WithQuickTheories {

    @Test
    fun `returns a bind h = h a`() {
        qt().forAll(integers().all(), `try`<String>(Exception())).check { a, r ->
            unsafeSyncRun {
                monad.`returns a bind h = h a`(retStr(r), a)
            }
        }
    }

    @Test
    fun `a bind returns = a`() {
        qt().forAll(`try`<Int>(constant(Exception()))(integers().all())).check { a ->
            unsafeSyncRun {
                monad.`a bind returns = a`(a)
            }
        }
    }

    @Test
    fun `(a bind f) bind g = a bind {x in f x bind g}`() {
        qt().forAll(
            `try`<Int>(constant(Exception()))(integers().all()), `try`<String>(Exception()), `try`<Int>(Exception())
        ).check { a, rf, rg ->
            unsafeSyncRun {
                monad.`(a bind f) bind g = a bind {x in f x bind g}`(retStr(rf), retInt(rg), a)
            }
        }
    }

}
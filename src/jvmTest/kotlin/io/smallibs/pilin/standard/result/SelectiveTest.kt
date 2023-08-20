package io.smallibs.pilin.standard.result

import io.smallibs.pilin.laws.Selective.`pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`
import io.smallibs.pilin.laws.Selective.`x select (pure id) = fold(id)(id) map x`
import io.smallibs.pilin.standard.result.Result.Companion.selective
import io.smallibs.pilin.standard.support.Functions.intToString
import io.smallibs.pilin.standard.support.Generators.either
import utils.unsafeSyncRun

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class SelectiveTest : WithQuickTheories {

    @Test
    fun `x select (pure id) = fold(id)(id) map x`() {
        qt().forAll(either<Int, Int>(integers().allPositive())(integers().allPositive())).check { a ->
            unsafeSyncRun { selective<Unit>().`x select (pure id) = fold(id)(id) map x`(a) }
        }
    }

    @Test
    fun `pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`() {
        qt().forAll(either<Int, Int>(integers().allPositive())(integers().allPositive())).check { a ->
            unsafeSyncRun {
                selective<Unit>().`pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`(
                    intToString, intToString, a
                )
            }
        }
    }
}

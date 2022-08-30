package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.laws.Selective.`pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`
import io.smallibs.pilin.laws.Selective.`x select (pure id) = fold(id)(id) map x`
import io.smallibs.pilin.standard.continuation.Continuation.Companion.selective
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.either
import io.smallibs.utils.unsafeSyncRun

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class SelectiveTest : WithQuickTheories {

    @Test
    fun `x select (pure id) = fold(id)(id) map x`() {
        qt().forAll(either<Int, Int>(integers().allPositive())(integers().allPositive())).check { a ->
            unsafeSyncRun {
                selective.`x select (pure id) = fold(id)(id) map x`(
                    a, Equatable.continuation()
                )
            }
        }
    }

    @Test
    fun `pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`() {
        qt().forAll(either<Int, String>(integers().allPositive())(strings().numeric())).check { a ->
            unsafeSyncRun {
                selective.`pure(x) select (y discardLeft z) = (pure(x) select y) discardLeft ((pure(x) select z)`(
                    str, str, a, Equatable.continuation()
                )
            }
        }
    }
}

package io.smallibs.pilin.standard.`try`

import io.smallibs.pilin.laws.Functor.`map (f compose g) = map f compose map g`
import io.smallibs.pilin.laws.Functor.`map id = id`
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.`try`
import io.smallibs.pilin.standard.`try`.Try.Companion.functor
import org.junit.Test
import org.quicktheories.WithQuickTheories
import utils.unsafeSyncRun

internal class FunctorTest : WithQuickTheories {

    @Test
    fun `map id = id `() {
        qt().forAll(`try`<Int>(constant(Exception()))(integers().all())).check { a ->
            unsafeSyncRun { functor.`map id = id`(a) }
        }
    }

    @Test
    fun `map (incr compose toString) = (map incr) compose (map toString) `() {
        qt().forAll(`try`<Int>(constant(Exception()))(integers().all())).check { a ->
            unsafeSyncRun { functor.`map (f compose g) = map f compose map g`(int, str, a) }
        }
    }

}
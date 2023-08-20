package io.smallibs.pilin.standard.result

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.result.Result.Companion.applicative
import io.smallibs.pilin.standard.support.Functions.stringToInt
import io.smallibs.pilin.standard.support.Functions.intToString
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.result
import io.smallibs.pilin.type.Fun
import org.junit.Test
import org.quicktheories.WithQuickTheories
import utils.unsafeSyncRun

internal class ApplicativeTest : WithQuickTheories {

    @Test
    fun `map f x = apply (pure f) x`() {
        qt().forAll(result<Int, Unit>(constant(Unit))(integers().all())).check { a ->
            unsafeSyncRun { applicative<Unit>().`map f x = apply (pure f) x`(intToString, a) }
        }
    }

    @Test
    fun `(pure id) apply v = v`() {
        qt().forAll(result<Int, Unit>(constant(Unit))(integers().all())).check { a ->
            unsafeSyncRun { applicative<Unit>().`(pure id) apply v = v`(a) }
        }
    }

    @Test
    fun `apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            unsafeSyncRun { applicative<Unit>().`apply (pure f) (pure x) = pure (f x)`(intToString, a) }
        }
    }

    @Test
    fun `apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all(), result<Fun<Int, String>, Unit>(constant(Unit))(constant(intToString))).check { a, f ->
            unsafeSyncRun { applicative<Unit>().`apply f (pure x) = apply (pure ($ y)) f`(f, a) }
        }
    }

    @Test
    fun `apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`() {
        qt().forAll(
            result<Int, Unit>(constant(Unit))(integers().all()),
            result<Fun<String, Int>, Unit>(constant(Unit))(constant(stringToInt)),
            result<Fun<Int, String>, Unit>(constant(Unit))(constant(intToString))
        ).check { a, f, g ->
            unsafeSyncRun {
                applicative<Unit>().`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`(f, g, a)
            }
        }
    }

}

package io.smallibs.pilin.standard.option

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.option.Option.Companion.applicative
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.option
import utils.unsafeSyncRun

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    @Test
    fun `map f x = apply (pure f) x`() {
        qt().forAll(/* values = */ option(integers().all())).check { a ->
            unsafeSyncRun { applicative.`map f x = apply (pure f) x`(str, a) }
        }
    }

    @Test
    fun `(pure id) apply v = v`() {
        qt().forAll(option(integers().all())).check { a ->
            unsafeSyncRun { applicative.`(pure id) apply v = v`(a) }
        }
    }

    @Test
    fun `apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            unsafeSyncRun { applicative.`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all(), option(constant(str))).check { a, f ->
            unsafeSyncRun { applicative.`apply f (pure x) = apply (pure ($ y)) f`(f, a) }
        }
    }

    @Test
    fun `apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`() {
        qt().forAll(option(integers().all()), option(constant(int)), option(constant(str))).check { a, f, g ->
            unsafeSyncRun {
                applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`(f, g, a)
            }
        }
    }

}

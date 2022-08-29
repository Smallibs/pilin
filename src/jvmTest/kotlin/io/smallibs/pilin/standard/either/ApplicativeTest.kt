package io.smallibs.pilin.standard.either

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.either.Either.Companion.applicative
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.either
import io.smallibs.pilin.type.Fun
import io.smallibs.utils.runTest

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    @Test
    fun `map f x = apply (pure f) x`() {
        qt().forAll(either<Unit, Int>(constant(Unit))(integers().all())).check { a ->
            runTest { applicative<Unit>().`map f x = apply (pure f) x`(str, a) }
        }
    }

    @Test
    fun `(pure id) apply v = v`() {
        qt().forAll(either<Unit, Int>(constant(Unit))(integers().all())).check { a ->
            runTest { applicative<Unit>().`(pure id) apply v = v`(a) }
        }
    }

    @Test
    fun `apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runTest { applicative<Unit>().`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all(), either<Unit, Fun<Int, String>>(constant(Unit))(constant(str))).check { a, f ->
            runTest { applicative<Unit>().`apply f (pure x) = apply (pure ($ y)) f`(f, a) }
        }
    }

    @Test
    fun `apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`() {
        qt().forAll(
            either<Unit, Int>(constant(Unit))(integers().all()),
            either<Unit, Fun<String, Int>>(constant(Unit))(constant(int)),
            either<Unit, Fun<Int, String>>(constant(Unit))(constant(str))
        ).check { a, f, g ->
                runTest {
                    applicative<Unit>().`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`(f, g, a)
                }
            }
    }

}

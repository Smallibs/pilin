package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.continuation.Continuation.Companion.applicative
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.standard.support.Functions.int
import io.smallibs.pilin.standard.support.Functions.str
import io.smallibs.pilin.standard.support.Generators.constant
import io.smallibs.pilin.standard.support.Generators.continuation
import io.smallibs.runTest

import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    @Test
    fun `map f x = apply (pure f) x`() {
        qt().forAll(continuation<Int>(integers().all())).check { a ->
            runTest {
                applicative.`map f x = apply (pure f) x`(str, a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `(pure id) apply v = v`() {
        qt().forAll(continuation<Int>(integers().all())).check { a ->
            runTest { applicative.`(pure id) apply v = v`(a, Equatable.continuation()) }
        }
    }

    @Test
    fun `apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runTest {
                applicative.`apply (pure f) (pure x) = pure (f x)`(str, a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all(), continuation(constant(str))).check { a, f ->
            runTest {
                applicative.`apply f (pure x) = apply (pure ($ y)) f`(f, a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`() {
        qt().forAll(
            continuation(integers().all()), continuation(constant(int)), continuation(constant(str))
        ).check { a, f, g ->
            runTest {
                applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`(
                    f, g, a, Equatable.continuation()
                )
            }
        }
    }

}

package io.smallibs.pilin.standard.continuation

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.standard.support.constant
import io.smallibs.pilin.standard.support.continuation
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }
    private val int: Fun<String, Int> = { i -> i.toInt() }

    @Test
    fun `map f x = apply (pure f) x`() {
        qt().forAll(continuation(integers().all())).check { a ->
            runBlocking {
                Continuation.applicative.`map f x = apply (pure f) x`(str, a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `(pure id) apply v = v`() {
        qt().forAll(continuation(integers().all())).check { a ->
            runBlocking { Continuation.applicative.`(pure id) apply v = v`(a, Equatable.continuation()) }
        }
    }

    @Test
    fun `apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking {
                Continuation.applicative
                    .`apply (pure f) (pure x) = pure (f x)`(str, a, Equatable.continuation())
            }
        }
    }

    @Test
    fun `apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all(), continuation(constant(str))).check { a, f ->
            runBlocking {
                Continuation.applicative.`apply f (pure x) = apply (pure ($ y)) f`(f,
                    a,
                    Equatable.continuation())
            }
        }
    }

    @Test
    fun `apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`() {
        qt().forAll(continuation(integers().all()), continuation(constant(int)), continuation(constant(str)))
            .check { a, f, g ->
                runBlocking {
                    Continuation.applicative
                        .`apply f (apply g x) == apply (apply (apply (pure compose) f) g) x`(f,
                            g,
                            a,
                            Equatable.continuation())
                }
            }
    }

}

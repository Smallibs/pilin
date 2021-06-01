package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.generator.either
import io.smallibs.pilin.standard.generator.identity
import io.smallibs.pilin.standard.generator.option
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }

    @Test
    fun `(Identity) map f x = apply (pure f) x`() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking { Identity.applicative.`map f x = apply (pure f) x`(str, a) }
        }
    }

    @Test
    fun `(Option) map f x = apply (pure f) x`() {
        qt().forAll(option(integers().all())).check { a ->
            runBlocking { Option.applicative.`map f x = apply (pure f) x`(str, a) }
        }
    }

    @Test
    fun `(Either) map f x = apply (pure f) x`() {
        qt().forAll(either(integers().all())).check { a ->
            runBlocking { Either.applicative<Int>().`map f x = apply (pure f) x`(str, a) }
        }
    }

    @Test
    fun `(Identity) (pure id) apply v = v`() {
        qt().forAll(identity(integers().all())).check { a ->
            runBlocking { Identity.applicative.`(pure id) apply v = v`(a) }
        }
    }

    @Test
    fun `(Option) (pure id) apply v = v`() {
        qt().forAll(option(integers().all())).check { a ->
            runBlocking { Option.applicative.`(pure id) apply v = v`(a) }
        }
    }

    @Test
    fun `(Either) (pure id) apply v = v`() {
        qt().forAll(either(integers().all())).check { a ->
            runBlocking { Either.applicative<Int>().`(pure id) apply v = v`(a) }
        }
    }

    @Test
    fun `(Identity) apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.applicative.`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `(Option) apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `(Either) apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `(Identity) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.applicative.`apply f (pure x) = apply (pure ($ y)) f`(id(str), a) }
        }
    }

    @Test
    fun `(Option) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`apply f (pure x) = apply (pure ($ y)) f`(some(str), a) }
        }
    }

    @Test
    fun `(Either) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`apply f (pure x) = apply (pure ($ y)) f`(right(str), a) }
        }
    }
}

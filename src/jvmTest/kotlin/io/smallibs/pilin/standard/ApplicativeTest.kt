package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.Either.T.Left
import io.smallibs.pilin.standard.Either.T.Right
import io.smallibs.pilin.standard.Either.TK.Companion.left
import io.smallibs.pilin.standard.Either.TK.Companion.right
import io.smallibs.pilin.standard.Identity.Id
import io.smallibs.pilin.standard.Identity.TK.Companion.id
import io.smallibs.pilin.standard.Option.T.None
import io.smallibs.pilin.standard.Option.T.Some
import io.smallibs.pilin.standard.Option.TK.Companion.none
import io.smallibs.pilin.standard.Option.TK.Companion.some
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }
    private val ten: Fun<String, String> = { s -> s + "0" }
    private val int: Fun<String, Int> = { s -> s.toInt() }

    // TODO(didier)
    // A dedicate generator per ADT should be provided

    @Test
    fun `(Identity) map f x = apply (pure f) x`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.applicative.`map f x = apply (pure f) x`(str, Id(a)) }
        }
    }

    @Test
    fun `(Option) map f x = apply (pure f) x`() {
        check(runBlocking { Option.applicative.`map f x = apply (pure f) x`(str, None()) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`map f x = apply (pure f) x`(str, Some(a)) }
        }
    }

    @Test
    fun `(Either) map f x = apply (pure f) x`() {
        check(runBlocking { Either.applicative<Unit>().`map f x = apply (pure f) x`(str, Left(Unit)) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`map f x = apply (pure f) x`(str, Right(a)) }
        }
    }

    @Test
    fun `(Identity) (pure id) apply v = v`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.applicative.`(pure id) apply v = v`(id(a)) }
        }
    }

    @Test
    fun `(Option) (pure id) apply v = v`() {
        check(runBlocking { Option.applicative.`(pure id) apply v = v`(none<Int>()) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`(pure id) apply v = v`(some(a)) }
        }
    }

    @Test
    fun `(Either) (pure id) apply v = v`() {
        check(runBlocking { Either.applicative<Unit>().`(pure id) apply v = v`(left<Unit, Int>(Unit)) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`(pure id) apply v = v`(right(a)) }
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

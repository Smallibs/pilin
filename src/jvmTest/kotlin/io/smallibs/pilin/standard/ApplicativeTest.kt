package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.Either.T.Left
import io.smallibs.pilin.standard.Either.T.Right
import io.smallibs.pilin.standard.Identity.Id
import io.smallibs.pilin.standard.Option.T.None
import io.smallibs.pilin.standard.Option.T.Some
import io.smallibs.pilin.standard.Validated.T.Invalid
import io.smallibs.pilin.standard.Validated.T.Valid
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class ApplicativeTest : WithQuickTheories {

    private val str: suspend (Int) -> String = { i -> i.toString() }
    private val ten: suspend (String) -> String = { s -> s + "0" }
    private val int: suspend (String) -> Int = { s -> s.toInt() }

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
        runBlocking { Option.applicative.`map f x = apply (pure f) x`(str, None()) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`map f x = apply (pure f) x`(str, Some(a)) }
        }
    }

    @Test
    fun `(Either) map f x = apply (pure f) x`() {
        runBlocking { Either.applicative<Unit>().`map f x = apply (pure f) x`(str, Left(Unit)) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`map f x = apply (pure f) x`(str, Right(a)) }
        }
    }

    @Test
    fun `(Identity) (pure id) apply v = v`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Identity.applicative.`(pure id) apply v = v`(Id(a)) }
        }
    }

    @Test
    fun `(Option) (pure id) apply v = v`() {
        runBlocking { Option.applicative.`(pure id) apply v = v`(None<Int>()) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`(pure id) apply v = v`(Some(a)) }
        }
    }

    @Test
    fun `(Either) (pure id) apply v = v`() {
        runBlocking { Either.applicative<Unit>().`(pure id) apply v = v`(Left<Unit, Int>(Unit)) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`(pure id) apply v = v`(Right(a)) }
        }
    }

    @Test
    fun `(Validated) (pure id) apply v = v`() {
        runBlocking { Validated.applicative<Unit>().`(pure id) apply v = v`(Invalid<Unit, Int>(Unit)) }

        qt().forAll(integers().all()).check { a ->
            runBlocking { Validated.applicative<Unit>().`(pure id) apply v = v`(Valid(a)) }
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
            runBlocking { Identity.applicative.`apply f (pure x) = apply (pure ($ y)) f`(Id(str), a) }
        }
    }

    @Test
    fun `(Option) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Option.applicative.`apply f (pure x) = apply (pure ($ y)) f`(Some(str), a) }
        }
    }

    @Test
    fun `(Either) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Either.applicative<Unit>().`apply f (pure x) = apply (pure ($ y)) f`(Right(str), a) }
        }
    }

    @Test
    fun `(Validated) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { Validated.applicative<Unit>().`apply f (pure x) = apply (pure ($ y)) f`(Valid(str), a) }
        }
    }
}

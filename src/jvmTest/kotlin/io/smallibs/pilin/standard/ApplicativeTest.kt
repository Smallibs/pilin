package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Applicative.`(pure id) apply v = v`
import io.smallibs.pilin.laws.Applicative.`apply (pure f) (pure x) = pure (f x)`
import io.smallibs.pilin.laws.Applicative.`apply f (pure x) = apply (pure ($ y)) f`
import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.either.Either.Left
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories
import io.smallibs.pilin.standard.either.Applicative.applicative as EitherApplicative
import io.smallibs.pilin.standard.identity.Applicative.applicative as IdentityApplicative
import io.smallibs.pilin.standard.option.Applicative.applicative as OptionApplicative

internal class ApplicativeTest : WithQuickTheories {

    private val str: Fun<Int, String> = { i -> i.toString() }
    private val ten: Fun<String, String> = { s -> s + "0" }
    private val int: Fun<String, Int> = { s -> s.toInt() }

    // TODO(didier)
    // A dedicate generator per ADT should be provided

    @Test
    fun `(Identity) map f x = apply (pure f) x`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { IdentityApplicative.`map f x = apply (pure f) x`(str, Identity(a)) }
        }
    }

    @Test
    fun `(Option) map f x = apply (pure f) x`() {
        check(runBlocking { OptionApplicative.`map f x = apply (pure f) x`(str, Option.None()) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { OptionApplicative.`map f x = apply (pure f) x`(str, Option.Some(a)) }
        }
    }

    @Test
    fun `(Either) map f x = apply (pure f) x`() {
        check(runBlocking { EitherApplicative<Unit>().`map f x = apply (pure f) x`(str, Left(Unit)) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { EitherApplicative<Unit>().`map f x = apply (pure f) x`(str, Either.Right(a)) }
        }
    }

    @Test
    fun `(Identity) (pure id) apply v = v`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { IdentityApplicative.`(pure id) apply v = v`(id(a)) }
        }
    }

    @Test
    fun `(Option) (pure id) apply v = v`() {
        check(runBlocking { OptionApplicative.`(pure id) apply v = v`(none<Int>()) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { OptionApplicative.`(pure id) apply v = v`(some(a)) }
        }
    }

    @Test
    fun `(Either) (pure id) apply v = v`() {
        check(runBlocking { EitherApplicative<Unit>().`(pure id) apply v = v`(left<Unit, Int>(Unit)) })

        qt().forAll(integers().all()).check { a ->
            runBlocking { EitherApplicative<Unit>().`(pure id) apply v = v`(right(a)) }
        }
    }

    @Test
    fun `(Identity) apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { IdentityApplicative.`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `(Option) apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { OptionApplicative.`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `(Either) apply (pure f) (pure x) = pure (f x)`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { EitherApplicative<Unit>().`apply (pure f) (pure x) = pure (f x)`(str, a) }
        }
    }

    @Test
    fun `(Identity) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { IdentityApplicative.`apply f (pure x) = apply (pure ($ y)) f`(id(str), a) }
        }
    }

    @Test
    fun `(Option) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { OptionApplicative.`apply f (pure x) = apply (pure ($ y)) f`(some(str), a) }
        }
    }

    @Test
    fun `(Either) apply f (pure x) = apply (pure ($ y)) f`() {
        qt().forAll(integers().all()).check { a ->
            runBlocking { EitherApplicative<Unit>().`apply f (pure x) = apply (pure ($ y)) f`(right(str), a) }
        }
    }
}

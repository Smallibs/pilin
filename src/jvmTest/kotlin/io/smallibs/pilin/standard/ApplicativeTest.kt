package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Applicative.`map f x = apply (pure f) x`
import io.smallibs.pilin.standard.Either.T.Left
import io.smallibs.pilin.standard.Either.T.Right
import io.smallibs.pilin.standard.Identity.Id
import io.smallibs.pilin.standard.Option.T.None
import io.smallibs.pilin.standard.Option.T.Some
import kotlinx.coroutines.runBlocking
import org.junit.Test


internal class ApplicativeTest {

    private val str: suspend (Int) -> String = { i -> i.toString() }
    private val ten: suspend (String) -> String = { s -> s + "0" }
    private val int: suspend (String) -> Int = { s -> s.toInt() }

    @Test
    fun `(Identity) map f x = apply (pure f) x`() {
        for (a in -500..500) {
            runBlocking { `map f x = apply (pure f) x`(Identity.applicative, str, Id(a)) }
        }
    }

    @Test
    fun `(Option) map f x = apply (pure f) x`() {
        runBlocking { `map f x = apply (pure f) x`(Option.applicative, str, None()) }

        for (a in -500..500) {
            runBlocking { `map f x = apply (pure f) x`(Option.applicative, str, Some(a)) }
        }
    }

    @Test
    fun `(Either) map f x = apply (pure f) x`() {
        runBlocking { `map f x = apply (pure f) x`(Either.applicative(), str, Left(Unit)) }

        for (a in -500..500) {
            runBlocking { `map f x = apply (pure f) x`(Either.applicative<Unit>(), str, Right(a)) }
        }
    }

    // TBC ...

}

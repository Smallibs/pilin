package io.smallibs.pilin.standard

import io.smallibs.pilin.laws.Selective.`x select (pure id) = fold(id)(id) map x`
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.generator.either
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.option.Option
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class SelectiveTest : WithQuickTheories {

    @Test
    fun `(Identity) x select (pure id) = fold(id)(id) map x`() {
        qt().forAll(either(integers().allPositive())).check { a ->
            runBlocking { Identity.selective.`x select (pure id) = fold(id)(id) map x`(a) }
        }
    }

    @Test
    fun `(Option) x select (pure id) = fold(id)(id) map x`() {
        qt().forAll(either(integers().allPositive())).check { a ->
            runBlocking { Option.selective.`x select (pure id) = fold(id)(id) map x`(a) }
        }
    }

    @Test
    fun `(Either) x select (pure id) = fold(id)(id) map x`() {
        qt().forAll(either(integers().allPositive())).check { a ->
            runBlocking { Either.selective<String>().`x select (pure id) = fold(id)(id) map x`(a) }
        }
    }

}

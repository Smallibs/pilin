package io.smallibs.pilin.standard

import io.smallibs.pilin.extension.Comprehension.Companion.`do`
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import io.smallibs.pilin.standard.either.Monad.monad as EitherMonad
import io.smallibs.pilin.standard.identity.Monad.monad as IdentityMonad
import io.smallibs.pilin.standard.option.Monad.monad as OptionMonad

internal class SyntaxTest {

    @Test
    fun `Should be able to chain Identity effects`() {
        assertEquals(id(42), runBlocking {
            IdentityMonad `do` {
                val (a) = returns(40)
                val (b) = returns(2)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Option effects`() {
        assertEquals(some(42), runBlocking {
            OptionMonad `do` {
                val (a) = returns(40)
                val (b) = returns(2)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Option effects`() {
        assertEquals(none<Int>(), runBlocking {
            OptionMonad `do` {
                val (a) = returns(2)
                val (b) = none<Int>()
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Either effects`() {
        assertEquals(right<String, Int>(42), runBlocking {
            EitherMonad<String>() `do` {
                val (a) = returns(2)
                val (b) = returns(40)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Either effects`() {
        assertEquals(left<String, Int>("Cannot compute A"), runBlocking {
            EitherMonad<String>() `do` {
                val (a) = left<String, Int>("Cannot compute A")
                val (b) = left<String, Int>("Cannot compute B")
                a + b
            }
        })
    }
}
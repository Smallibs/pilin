package io.smallibs.pilin.syntax

import io.smallibs.pilin.extension.Comprehension
import io.smallibs.pilin.extension.Comprehension.Companion.`do`
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals

internal class ComprehensionTest {

    @Test
    fun `Should be able to chain Identity effects`() {
        assertEquals(id(42), runBlocking {
            Identity.monad `do` {
                val a = returns(40).bind()
                delay(100)
                val b = returns(2).bind()
                delay(100)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Option effects`() {
        assertEquals(some(42), runBlocking {
            Option.monad `do` {
                val a = returns(40).bind()
                delay(100)
                val b = returns(2).bind()
                delay(100)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Option effects`() {
        assertEquals(none<Int>(), runBlocking {
            Option.monad `do` {
                val a = returns(2).bind()
                delay(100)
                val b = none<Int>().bind()
                delay(100)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Either effects`() {
        assertEquals(right<String, Int>(42), runBlocking {
            Either.monad<String>() `do` {
                val a = returns(2).bind()
                delay(100)
                val b = returns(40).bind()
                delay(100)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Either effects`() {
        assertEquals(left<String, Int>("Cannot compute A"), runBlocking {
            Either.monad<String>() `do` {
                val a = left<String, Int>("Cannot compute A").bind()
                val b = left<String, Int>("Cannot compute B").bind()
                a + b
            }
        })
    }

    @Test
    @Ignore
    fun `Should be able to Chain continuation effects`() {
        assertEquals(42, runBlocking {
            (Comprehension<Continuation.ContinuationK<Int>, Int>(Continuation.monad()) {
                val a = continuation<Int, Int> { k -> k(1) + k(1) }.bind()
                delay(100)
                val b = returns(38).bind()
                delay(100)
                a + b
            }) { it + 3 }
        })
    }

}
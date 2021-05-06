package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Comprehension
import io.smallibs.pilin.standard.Either.TK.Companion.left
import io.smallibs.pilin.standard.Either.TK.Companion.right
import io.smallibs.pilin.standard.Option.TK.Companion.none
import io.smallibs.pilin.standard.Option.TK.Companion.some
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class SyntaxTest {

    @Test
    fun `Should be able to chain Identity effects`() {
        assertEquals(some(42), runBlocking {
            Comprehension(Option.monad) {
                val (a) = returns(40)
                val (b) = returns(2)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Option effects`() {
        assertEquals(some(42), runBlocking {
            Comprehension(Option.monad) {
                val (a) = returns(40)
                val (b) = returns(2)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Option effects`() {
        assertEquals(none<Int>(), runBlocking {
            Comprehension(Option.monad) {
                val (a) = returns(2)
                val (b) = none<Int>()
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Either effects`() {
        assertEquals(right<String,Int>(42), runBlocking {
            Comprehension(Either.monad()) {
                val (a) = returns(2)
                val (b) = returns(40)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Either effects`() {
        assertEquals(left<String,Int>("Cannot compute A"), runBlocking {
            Comprehension(Either.monad()) {
                val (a) = left<String, Int>("Cannot compute A")
                val (b) = left<String, Int>("Cannot compute B")
                a + b
            }
        })
    }
}
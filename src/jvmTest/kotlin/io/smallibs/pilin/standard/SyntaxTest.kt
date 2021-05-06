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
    fun `Should be able to chain Option effects`() {
        assertEquals(some(42), runBlocking {
            Comprehension.run<Option.TK, Int>(Option.monad) {
                val (a) = returns(40)
                val (b) = returns(2)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Option effects`() {
        assertEquals(none(), runBlocking {
            Comprehension.run<Option.TK, Int>(Option.monad) {
                val (a) = returns(2)
                val (b) = none<Int>()
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Either effects`() {
        assertEquals(right(42), runBlocking {
            Comprehension.run<Either.TK<String>, Int>(Either.monad()) {
                val (a) = returns(2)
                val (b) = returns(40)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Either effects`() {
        assertEquals(left("Cannot compute B"), runBlocking {
            Comprehension.run<Either.TK<String>, Int>(Either.monad()) {
                val (a) = returns(42)
                val (b) = left<String, Int>("Cannot compute B")
                a + b
            }
        })
    }
}
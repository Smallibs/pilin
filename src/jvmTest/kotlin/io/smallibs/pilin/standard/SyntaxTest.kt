package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Comprehension
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class SyntaxTest : WithQuickTheories {

    @Test
    fun `Should be able to chain Identity effects`() {
        runBlocking {
            Comprehension.run<Identity.TK, Int>(Identity.monad) {
                println("First line ")
                val a = returns(1).exec()
                println("Second line with $a")
                val b = returns(2).exec()
                println("Third line with $b")
                a + b
            }
        }
    }

    @Test
    fun `Should be able to chain Option effects`() {
        runBlocking {
            with(Option.monad.syntax) {
                println("First line ")
                val (a) = returns(1)
                println("Second line with $a")
                val (b) = returns(2)
                println("Third line with $b")
                assert(a + b == 3)
            }
        }
    }

    @Test
    fun `Should be able to chain Either effects`() {
        runBlocking {
            with(Either.monad<Unit>().syntax) {
                println("First line ")
                val a = !returns(1)
                println("Second line with $a")
                val b = !returns(2)
                println("Third line with $b")
                assert(a + b == 3)
            }
        }
    }

}
package io.smallibs.pilin.standard

import io.smallibs.pilin.control.Comprehension
import io.smallibs.pilin.standard.Identity.TK
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.quicktheories.WithQuickTheories

internal class SyntaxTest : WithQuickTheories {

    @Ignore @Test
    fun `Should be able to chain Identity effects`() {
        runBlocking {
            Comprehension.run<TK, Int>(Identity.monad) {
                println("First line ")
                val a = returns(1).exec()
                println("Second line with $a")
                val b = returns(2).exec()
                println("Third line with $b")
                a + b
            }
        }
    }
}
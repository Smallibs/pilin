package io.smallibs.pilin.syntax

import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.invoke
import io.smallibs.pilin.standard.either.Either
import io.smallibs.pilin.standard.either.Either.Companion.left
import io.smallibs.pilin.standard.either.Either.Companion.right
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.Companion.id
import io.smallibs.pilin.standard.option.Option
import io.smallibs.pilin.standard.option.Option.Companion.none
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.utils.unsafeSyncRun
import kotlinx.coroutines.delay

import org.junit.Test
import kotlin.test.assertEquals

internal class ComprehensionTest {

    @Test
    fun `Should be able to chain Identity effects`() {
        assertEquals(id(42), unsafeSyncRun {
            Identity.monad `do` {
                val a = returns(40).bind()
                delay(10)
                val b = returns(2).bind()
                delay(10)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Option effects`() {
        assertEquals(some(42), unsafeSyncRun {
            Option.monad `do` {
                val a = returns(44).bind()
                delay(10)
                val b = returns(2).bind()
                delay(10)
                a - b
            }
        })
    }

    @Test
    fun `Should be able to stop chained Option effects`() {
        assertEquals(none(), unsafeSyncRun {
            Option.monad `do` {
                val a = returns(2).bind()
                delay(10)
                val b = none<Int>().bind()
                delay(10)
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Either effects`() {
        assertEquals(right(42), unsafeSyncRun {
            Either.monad<String>() `do` {
                val a = returns(2).bind()
                delay(10)
                val b = returns("40").bind()
                delay(10)
                a + b.toInt()
            }
        })
    }

    @Test
    fun `Should be able to stop chained Either effects`() {
        assertEquals(left("Cannot compute A"), unsafeSyncRun {
            Either.monad<String>() `do` {
                val a = left<String, Int>("Cannot compute A").bind()
                val b = left<String, Int>("Cannot compute B").bind()
                a + b
            }
        })
    }

    @Test
    fun `Should be able to Chain continuation effects`() {
        assertEquals(42, unsafeSyncRun {
            (Continuation.monad `do` {
                val a = returns(1).bind()
                delay(10)
                val b = returns(38).bind()
                delay(10)
                a + b
            }) { it + 3 }
        })
    }

}
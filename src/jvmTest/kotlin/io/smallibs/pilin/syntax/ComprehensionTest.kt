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
import io.smallibs.pilin.standard.result.Result
import io.smallibs.pilin.standard.result.Result.Companion.error
import io.smallibs.pilin.standard.result.Result.Companion.ok
import io.smallibs.pilin.standard.`try`.Try
import io.smallibs.pilin.standard.`try`.Try.Companion.failure
import io.smallibs.pilin.standard.`try`.Try.Companion.success
import kotlinx.coroutines.delay
import org.junit.Test
import utils.unsafeSyncRun
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
    fun `Should be able to chain Result effects`() {
        assertEquals(ok(42), unsafeSyncRun {
            Result.monad<String>() `do` {
                val a = returns(2).bind()
                delay(10)
                val b = returns("40").bind()
                delay(10)
                a + b.toInt()
            }
        })
    }

    @Test
    fun `Should be able to stop chained Result effects`() {
        assertEquals(error("Cannot compute A"), unsafeSyncRun {
            Result.monad<String>() `do` {
                val a = error<Int, String>("Cannot compute A").bind()
                val b = error<Int, String>("Cannot compute B").bind()
                a + b
            }
        })
    }

    @Test
    fun `Should be able to chain Try effects`() {
        assertEquals(success(42), unsafeSyncRun {
            Try.monad `do` {
                val a = returns(2).bind()
                delay(10)
                val b = returns("40").bind()
                delay(10)
                a + b.toInt()
            }
        })
    }

    @Test
    fun `Should be able to stop chained Try effects`() {
        Exception("Cannot compute A").let { error ->
            assertEquals(failure(error), unsafeSyncRun {
                Try.monad `do` {
                    val a = failure<Int>(error).bind()
                    val b = failure<Int>(Exception("Cannot compute B")).bind()
                    a + b
                }
            })
        }
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
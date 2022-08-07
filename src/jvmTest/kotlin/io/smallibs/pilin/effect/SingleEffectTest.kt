package io.smallibs.pilin.effect

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class SingleEffectTest {
    private class Console<F>(
        val printString: (String) -> App<F, Unit>,
        val readString: App<F, String>,
    ) : EffectHandler

    private fun <A> id(a: A): A {
        return a
    }

    private fun <F> effects(monad: Monad.API<F>): Effects<Console<F>, App<F, Unit>> =
        handle { console ->
            monad `do` {
                val value = id(console.readString.bind())
                id(console.printString("Hello $value").bind())
            }
        }

    private fun console(traces: MutableList<String>): Console<ContinuationK> =
        Console(
            printString = { text ->
                object : Continuation<Unit> {
                    override suspend fun <O> invoke(k: Fun<Unit, O>): O {
                        traces += listOf("printString($text)")
                        return k(Unit)
                    }
                }
            },
            readString = object : Continuation<String> {
                override suspend fun <O> invoke(k: Fun<String, O>): O {
                    traces += listOf("readStream(World)")
                    return k("World")
                }
            }
        )

    @Test
    fun shouldPerformEffect() {
        val traces = mutableListOf<String>()
        val handled = effects(monad) with console(traces)

        runBlocking { handled().invoke { } }

        assertEquals(listOf("readStream(World)", "printString(Hello World)"), traces)
    }
}
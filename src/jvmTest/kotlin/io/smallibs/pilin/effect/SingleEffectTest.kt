package io.smallibs.pilin.effect

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
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

    private fun console(): Console<ContinuationK<List<String>>> =
        Console(
            printString = { text ->
                continuation { k ->
                    listOf("printString($text)") + k(Unit)
                }
            },
            readString = continuation { k ->
                listOf("readStream(World)") + k("World")
            }
        )

    @Test
    fun shouldPerformEffect() {
        val handled = effects(monad<List<String>>()) with console()

        val traces = runBlocking { handled().invoke { listOf() } }

        assertEquals(listOf("readStream(World)", "printString(Hello World)"), traces)
    }
}
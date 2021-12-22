package io.smallibs.pilin.effect

import io.smallibs.pilin.control.Monad
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
    private class IOConsole<F>(
        val printString: (String) -> App<F, Unit>,
        val readString: App<F, String>,
    ) : Handler

    private fun <F> effects(monad: Monad.API<F>): Effects<IOConsole<F>, App<F, Unit>> = handle { console ->
        with(monad.infix) {
            console.readString bind { value ->
                console.printString("Hello $value")
            }
        }
    }

    private fun console(): IOConsole<ContinuationK<List<String>>> =
        IOConsole(
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
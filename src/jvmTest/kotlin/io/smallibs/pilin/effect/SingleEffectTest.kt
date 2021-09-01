package io.smallibs.pilin.effect

import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class SingleEffectTest {
    private class IOConsole(
        val printString: (String) -> Continuation<Unit>,
        val readString: Continuation<String>,
    ) : Handler

    private fun effects(): Effects<IOConsole, App<ContinuationK, Unit>> = handle { console ->
        with(monad.infix) {
            console.readString bind { value ->
                console.printString("Hello $value")
            }
        }
    }

    private fun console(): IOConsole =
        IOConsole(
            printString = { text ->
                continuation<Unit, List<String>> { k ->
                    listOf("printString($text)") + k(Unit)
                }
            },
            readString = continuation<String, List<String>> { k ->
                listOf("readStream(World)") + k("World")
            }
        )

    @Test
    fun shouldPerformEffect() {
        val handled: HandledEffects<App<ContinuationK, Unit>> = effects() with console()

        val traces = runBlocking { handled().invoke<Unit, List<String>> { listOf() } }

        assertEquals(listOf("readStream(World)", "printString(Hello World)"), traces)
    }
}
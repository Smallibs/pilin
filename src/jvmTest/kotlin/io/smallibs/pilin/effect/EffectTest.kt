package io.smallibs.pilin.effect

import io.smallibs.pilin.effect.Effects.Companion.handle
import io.smallibs.pilin.standard.continuation.Continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.continuation
import io.smallibs.pilin.standard.continuation.Continuation.Companion.monad
import io.smallibs.pilin.standard.continuation.Continuation.TK.Companion.invoke
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class EffectTest {
    class IOConsole(
        val printString: (String) -> Continuation<Unit, Unit>,
        val readString: Continuation<String, Unit>,
    ) : Handler

    @Test
    fun shouldPerformEffect() {
        val actions = mutableListOf<String>()

        val handled = handle<Unit, IOConsole> { console ->
            with(monad<Unit>().infix) {
                console.readString bind { value ->
                    console.printString("Hello $value")
                }
            }.invoke { }
        } with IOConsole(
            printString = { text ->
                continuation { k ->
                    actions += "printString($text)"
                    k(Unit)
                }
            },
            readString = continuation { k ->
                actions += "readStream(World!)"
                k("World!")
            }
        )

        runBlocking {
            handled()
        }

        assertEquals(
            listOf("readStream(World!)", "printString(Hello World!)"),
            actions
        )
    }


}
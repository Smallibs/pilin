package io.smallibs.pilin.examples

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.examples.ConsoleIOFreerTest.ConsoleIO.ConsoleIOK
import io.smallibs.pilin.examples.ConsoleIOFreerTest.ConsoleIO.ConsoleIOK.fix
import io.smallibs.pilin.standard.freer.Freer
import io.smallibs.pilin.standard.freer.Freer.Companion.run
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class ConsoleIOFreerTest {

    private sealed interface ConsoleIO<A> : App<ConsoleIOK, A> {
        object ConsoleIOK {
            val <A> App<ConsoleIOK, A>.fix get() = this as ConsoleIO<A>
        }

        data class Tell<A>(val statement: String, val k: Fun<Unit, A>) : ConsoleIO<A>
        data class Ask<A>(val question: String, val k: Fun<String, A>) : ConsoleIO<A>
    }

    private fun tell(statement: String): Freer<ConsoleIOK, Unit> =
        Freer.perform(ConsoleIO.Tell(statement, Standard::id))

    private fun ask(request: String): Freer<ConsoleIOK, String> = Freer.perform(ConsoleIO.Ask(request, Standard::id))

    private fun <A, B> runConsoleIO(trace: MutableList<String>): Fun<Fun<B, A>, Fun<App<ConsoleIOK, B>, A>> = { f ->
        { fa ->
            when (val fa = fa.fix) {
                is ConsoleIO.Ask -> {
                    trace += listOf("Ask ${fa.question}?")
                    f(fa.k("Hello"))
                }

                is ConsoleIO.Tell -> {
                    trace += listOf("Tell ${fa.statement}")
                    f(fa.k(Unit))
                }
            }
        }
    }

    private suspend fun <A> runConsole(trace: MutableList<String>): Freer.Handler<ConsoleIOK, A> =
        object : Freer.Handler<ConsoleIOK, A> {
            override suspend fun <B> handle(f: Fun<B, A>): Fun<App<ConsoleIOK, B>, A> = runConsoleIO<A, B>(trace)(f)
        }

    @Test
    fun `should tell Hello and tell World`() {
        // Given
        val output = mutableListOf<String>()

        // When
        runBlocking {
            with(Freer.monad<ConsoleIOK>().infix) {
                val program = tell("Hello") bind { tell("World") }
                run(runConsole(output), program)
            }
        }

        // Then
        val expected = listOf("Tell Hello", "Tell World")

        assertEquals(expected, output)
    }

    @Test
    fun `should Ask Name and tell Alice`() {
        // Given
        val output = mutableListOf<String>()

        // When
        runBlocking {
            with(Freer.monad<ConsoleIOK>()) {
                val program = `do` {
                    ask("Name").bind()
                    tell("Alice").bind()
                }
                run(runConsole(output), program)
            }
        }

        // Then
        val expected = listOf("Ask Name?", "Tell Alice")

        assertEquals(expected, output)
    }
}
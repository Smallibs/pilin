package io.smallibs.pilin.examples

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.core.Standard.id
import io.smallibs.pilin.examples.ConsoleIOFreeTest.ConsoleIO.Ask
import io.smallibs.pilin.examples.ConsoleIOFreeTest.ConsoleIO.ConsoleIOK
import io.smallibs.pilin.examples.ConsoleIOFreeTest.ConsoleIO.ConsoleIOK.fix
import io.smallibs.pilin.examples.ConsoleIOFreeTest.ConsoleIO.Tell
import io.smallibs.pilin.examples.ConsoleIOFreeTest.IO.ask
import io.smallibs.pilin.examples.ConsoleIOFreeTest.IO.tell
import io.smallibs.pilin.standard.free.monad.Free
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.utils.runTest
import org.junit.Test
import kotlin.test.assertEquals
import io.smallibs.pilin.abstractions.Functor.API as Functor_API

internal class ConsoleIOFreeTest {

    private sealed interface ConsoleIO<A> : App<ConsoleIOK, A> {
        object ConsoleIOK {
            val <A> App<ConsoleIOK, A>.fix get() = this as ConsoleIO<A>
        }

        data class Tell<A>(val statement: String, val k: Fun<Unit, A>) : ConsoleIO<A>
        data class Ask<A>(val question: String, val k: Fun<String, A>) : ConsoleIO<A>
    }

    private object Functor : Functor_API<ConsoleIOK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ConsoleIOK, A>, App<ConsoleIOK, B>> = {
            when (val self = it.fix) {
                is Tell -> Tell(self.statement) { s -> f(self.k(s)) }
                is Ask -> Ask(self.question) { s -> f(self.k(s)) }
            }
        }
    }

    private object IO : Free.OverFunctor<ConsoleIOK>(Functor) {
        suspend fun tell(statement: String): App<Free.FreeK<ConsoleIOK>, Unit> = perform(Tell(statement, ::id))

        suspend fun ask(statement: String): App<Free.FreeK<ConsoleIOK>, String> = perform(Ask(statement, ::id))
    }

    private suspend fun <A, B> runConsoleIO(traces: MutableList<String>, f: Fun<A, B>): Fun<App<ConsoleIOK, A>, B> = {
        when (val ma = it.fix) {
            is Ask -> {
                traces += listOf("Ask ${ma.question}?")
                f(ma.k(ma.question))
            }

            is Tell -> {
                traces += listOf("Tell ${ma.statement}")
                f(ma.k(Unit))
            }
        }
    }

    @Test
    fun `should Ask Name and tell Alice`() {
        // Given
        val output = mutableListOf<String>()
        // When
        runTest {
            val program = IO `do` {
                val name = ask("Name").bind()
                tell("$name Alice").bind()
            }

            IO.run(runConsoleIO<Unit, Unit>(output, Standard::id))(program)
        }

        // Then
        val expected = listOf("Ask Name?", "Tell Name Alice")

        assertEquals(expected, output)
    }

}
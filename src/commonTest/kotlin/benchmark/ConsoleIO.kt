package benchmark

import benchmark.ConsoleIO.ConsoleIO.Ask
import benchmark.ConsoleIO.ConsoleIO.ConsoleIOK
import benchmark.ConsoleIO.ConsoleIO.ConsoleIOK.fix
import benchmark.ConsoleIO.ConsoleIO.Tell
import io.smallibs.pilin.abstractions.Functor.API
import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.free.monad.Free
import io.smallibs.pilin.standard.freer.monad.Freer
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import utils.unsafeSyncRun

@State(Scope.Benchmark)
public class ConsoleIO {

    private sealed interface ConsoleIO<A> : App<ConsoleIOK, A> {
        object ConsoleIOK {
            val <A> App<ConsoleIOK, A>.fix get() = this as ConsoleIO<A>
        }

        data class Tell<A>(val statement: String, val k: Fun<Unit, A>) : ConsoleIO<A>
        data class Ask<A>(val question: String, val k: Fun<String, A>) : ConsoleIO<A>
    }

    companion object {
        private fun <A, B> runConsoleIO(trace: MutableList<String>, f: Fun<B, A>): Fun<App<ConsoleIOK, B>, A> = {
            when (val fa = it.fix) {
                is Ask -> {
                    trace += listOf("Ask ${fa.question}?")
                    f(fa.k("Hello"))
                }

                is Tell -> {
                    trace += listOf("Tell ${fa.statement}")
                    f(fa.k(Unit))
                }
            }
        }
    }

    // Freer smart constructors
    private object FreerIO : Freer.Over<ConsoleIOK>() {
        fun tell(statement: String): Freer<ConsoleIOK, Unit> = perform(Tell(statement, Standard::id))

        fun ask(request: String): Freer<ConsoleIOK, String> = perform(Ask(request, Standard::id))

        suspend fun <A> runConsole(trace: MutableList<String>): Freer.Handler<ConsoleIOK, A> =
            object : Freer.Handler<ConsoleIOK, A> {
                override suspend fun <B> handle(f: Fun<B, A>): Fun<App<ConsoleIOK, B>, A> = runConsoleIO(trace, f)
            }
    }

    // Free smart constructors
    private object FreeIO : Free.OverFunctor<ConsoleIOK>(Functor) {
        private object Functor : API<ConsoleIOK> {
            override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<ConsoleIOK, A>, App<ConsoleIOK, B>> = {
                when (val self = it.fix) {
                    is Tell -> Tell(self.statement) { s -> f(self.k(s)) }
                    is Ask -> Ask(self.question) { s -> f(self.k(s)) }
                }
            }
        }

        suspend fun tell(statement: String): App<Free.FreeK<ConsoleIOK>, Unit> = perform(Tell(statement, Standard::id))

        suspend fun ask(statement: String): App<Free.FreeK<ConsoleIOK>, String> = perform(Ask(statement, Standard::id))
    }

    @Benchmark
    fun withFree() = unsafeSyncRun {
        with(FreeIO) {
            val program: App<Free.FreeK<ConsoleIOK>, Unit> = this `do` {
                val name = ask("Name").bind()
                tell("$name Alice").bind()
            }

            run<Unit>(runConsoleIO(mutableListOf(), Standard::id))(program)
        }
    }

    @Benchmark
    fun withFreeAndDo() = unsafeSyncRun {
        with(FreeIO) {
            val program = `do` {
                val name = ask("Name").bind()
                tell("$name Alice").bind()
            }

            run<Unit>(runConsoleIO(mutableListOf(), Standard::id))(program)
        }
    }

    @Benchmark
    fun withFreer() = unsafeSyncRun {
        with(FreerIO) {
            val program = with(infix) {
                ask("Name") bind { name ->
                    tell("$name Alice")
                }
            }

            run(runConsole(mutableListOf()), program)
        }
    }

    @Benchmark
    fun withFreerAndDo() = unsafeSyncRun {
        with(FreerIO) {
            val program = `do` {
                val name = ask("Name").bind()
                tell("$name Alice").bind()
            }

            run(runConsole(mutableListOf()), program)
        }
    }
}

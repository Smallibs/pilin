package benchmarks

import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.list.List
import io.smallibs.pilin.standard.writer.Writer
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import kotlinx.benchmark.Benchmark
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@org.openjdk.jmh.annotations.State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 20, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
class XmlStax {

    private sealed interface Xml {
        data class Tag(val name: String, val content: Xml) : Xml
        data class Text(val text: String) : Xml
        data class Seq(val lhd: Xml, val rhd: Xml) : Xml
        object Empty : Xml
    }

    private sealed interface Stax {
        data class Open(val name: String) : Stax
        data class Text(val text: String) : Stax
        data class Close(val name: String) : Stax
    }

    private suspend fun direct(xml: Xml): kotlin.collections.List<Stax> =
        with(this) {
            when (xml) {
                Xml.Empty -> listOf()

                is Xml.Seq -> {
                    direct(xml.lhd) + direct(xml.rhd)
                }

                is Xml.Tag -> {
                    listOf(Stax.Open(xml.name)) + direct(xml.content) + Stax.Close(xml.name)
                }

                is Xml.Text -> {
                    listOf(Stax.Text(xml.text))
                }
            }
        }

    private suspend fun <F> Writer.OverMonad<F, List<Stax>>.executeWithWriter(xml: Xml): App<WriterK<F, List<Stax>>, Unit> =
        with(this.infix) {
            when (xml) {
                Xml.Empty -> {
                    returns(Unit)
                }

                is Xml.Seq -> {
                    executeWithWriter(xml.lhd).bind {
                        executeWithWriter(xml.rhd)
                    }
                }

                is Xml.Tag -> {
                    tell(List(Stax.Open(xml.name))).bind {
                        executeWithWriter(xml.content).bind {
                            tell(List(Stax.Close(xml.name)))
                        }
                    }
                }

                is Xml.Text -> {
                    tell(List(Stax.Text(xml.text)))
                }
            }
        }

    private suspend fun <F> Writer.OverMonad<F, List<Stax>>.executeWithWriterAndDo(xml: Xml): App<WriterK<F, List<Stax>>, Unit> =
        `do` {
            when (xml) {
                Xml.Empty -> {
                    // Nothing
                }

                is Xml.Seq -> {
                    executeWithWriterAndDo(xml.lhd).bind()
                    executeWithWriterAndDo(xml.rhd).bind()
                }

                is Xml.Tag -> {
                    tell(List(Stax.Open(xml.name))).bind()
                    executeWithWriterAndDo(xml.content).bind()
                    tell(List(Stax.Close(xml.name))).bind()
                }

                is Xml.Text -> {
                    tell(List(Stax.Text(xml.text))).bind()
                }
            }
        }

    @Benchmark
    fun direct() {
        val xml = Xml.Tag("A", Xml.Seq(Xml.Text("B"), Xml.Tag("C", Xml.Empty)))

        return runBlocking { direct(xml) }
    }

    @Benchmark
    fun withWriter() {
        val xml = Xml.Tag("A", Xml.Seq(Xml.Text("B"), Xml.Tag("C", Xml.Empty)))
        val monad = Writer.OverMonoid<List<Stax>>(List.monoid())

        return runBlocking { monad.executeWithWriter(xml).run.fold { it.second } }
    }

    @Benchmark
    fun withWriterAndDo() {
        val xml = Xml.Tag("A", Xml.Seq(Xml.Text("B"), Xml.Tag("C", Xml.Empty)))
        val monad = Writer.OverMonoid<List<Stax>>(List.monoid())

        return runBlocking { monad.executeWithWriterAndDo(xml).run.fold { it.second } }
    }
}

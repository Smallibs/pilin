package benchmark

import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.list.List
import io.smallibs.pilin.standard.writer.Writer
import io.smallibs.pilin.standard.writer.Writer.WriterK
import io.smallibs.pilin.standard.writer.Writer.WriterK.Companion.run
import io.smallibs.pilin.type.App
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import utils.unsafeSyncRun

@State(Scope.Benchmark)
class XmlStax {

    sealed interface Xml {
        data class Tag(val name: String, val content: Xml) : Xml
        data class Text(val text: String) : Xml
        data class Seq(val lhd: Xml, val rhd: Xml) : Xml
        object Empty : Xml
    }

    sealed interface Stax {
        data class Open(val name: String) : Stax
        data class Text(val text: String) : Stax
        data class Close(val name: String) : Stax
    }

    suspend fun direct(xml: Xml): kotlin.collections.List<Stax> = with(this) {
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

    suspend fun <F> Writer.OverMonad<F, List<Stax>>.executeWithWriter(xml: Xml): App<WriterK<F, List<Stax>>, Unit> =
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

    suspend fun <F> Writer.OverMonad<F, List<Stax>>.executeWithWriterAndDo(xml: Xml): App<WriterK<F, List<Stax>>, Unit> =
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

    companion object {
        val xml = Xml.Tag("A", Xml.Seq(Xml.Text("B"), Xml.Tag("C", Xml.Empty)))
    }

    @Benchmark
    fun direct() = unsafeSyncRun { direct(xml) }

    @Benchmark
    fun withWriter() = with(Writer.OverMonoid<List<Stax>>(List.monoid())) {
        unsafeSyncRun { executeWithWriter(xml).run.fold { it.second } }
    }

    @Benchmark
    fun withWriterAndDo() = with(Writer.OverMonoid<List<Stax>>(List.monoid())) {
        unsafeSyncRun { executeWithWriterAndDo(xml).run.fold { it.second } }
    }

}

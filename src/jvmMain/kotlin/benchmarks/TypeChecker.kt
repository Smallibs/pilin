package benchmarks

import benchmarks.TypeChecker.Type.Companion.integer
import benchmarks.TypeChecker.Type.Companion.string
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.state.State
import io.smallibs.pilin.standard.state.State.StateK
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
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
class TypeChecker {

    sealed interface Literal {
        data class LInt(val value: Int) : Literal
        data class LString(val value: String) : Literal
    }

    sealed interface Expr {
        data class ELiteral(val value: Literal) : Expr
        data class ELambda(val binding: String, val type: Type, val body: Expr) : Expr
        data class EApply(val abstraction: Expr, val parameter: Expr) : Expr
        data class EVar(val name: String) : Expr
    }

    sealed interface Type {
        data class TLiteral(val name: String) : Type
        data class TArrow(val lhd: Type, val rhd: Type) : Type


        companion object {
            val integer get() = TLiteral("int")
            val string get() = TLiteral("string")
        }
    }

    private suspend fun Map<String, Type>.typeCheck(expr: Expr): Type? =
        when (expr) {
            is Expr.EApply -> {
                val abstraction = typeCheck(expr.abstraction)
                when (abstraction) {
                    is Type.TArrow -> {
                        val parameter = typeCheck(expr.parameter)
                        if (abstraction.lhd == parameter) abstraction.rhd else null

                    }

                    else -> null
                }
            }


            is Expr.ELambda -> {
                (this + (expr.binding to expr.type)).typeCheck(expr.body)
            }

            is Expr.ELiteral -> when (expr.value) {
                is Literal.LInt -> integer
                is Literal.LString -> string
            }

            is Expr.EVar -> {
                this[expr.name]
            }
        }


    private suspend fun <F> State.OverMonad<F, Map<String, Type>>.typeCheckWithState(expr: Expr): App<StateK<F, Map<String, Type>>, Type?> =
        with(this.infix) {
            when (expr) {
                is Expr.EApply -> {
                    typeCheckWithState(expr.abstraction) bind { abstraction ->
                        when (abstraction) {
                            is Type.TArrow -> {
                                typeCheckWithState(expr.parameter).map { parameter ->
                                    if (abstraction.lhd == parameter) abstraction.rhd else null
                                }
                            }

                            else -> returns(null)
                        }
                    }
                }

                is Expr.ELambda -> {
                    modify { it + (expr.binding to expr.type) }.bind {
                        typeCheckWithState(expr.body)
                    }
                }

                is Expr.ELiteral -> when (expr.value) {
                    is Literal.LInt -> returns(integer)
                    is Literal.LString -> returns(string)
                }

                is Expr.EVar -> {
                    get().map { it[expr.name] }
                }
            }
        }

    private suspend fun <F> State.OverMonad<F, Map<String, Type>>.typeCheckWithStateAndComprehension(expr: Expr): App<StateK<F, Map<String, Type>>, Type?> =
        `do` {
            when (expr) {
                is Expr.EApply -> {
                    when (val abstraction = typeCheckWithState(expr.abstraction)
                        .bind()) {
                        is Type.TArrow -> {
                            val parameter = typeCheckWithState(expr.parameter).bind()
                            if (abstraction.lhd == parameter) abstraction.rhd else null
                        }

                        else -> null
                    }
                }

                is Expr.ELambda -> {
                    modify { it + (expr.binding to expr.type) }.bind()
                    typeCheckWithState(expr.body).bind()
                }

                is Expr.ELiteral -> when (expr.value) {
                    is Literal.LInt -> integer
                    is Literal.LString -> string
                }

                is Expr.EVar -> {
                    get().bind()[expr.name]
                }
            }
        }

    companion object {
        val gamma = mapOf("add" to Type.TArrow(integer, Type.TArrow(integer, integer)))
        val expr = Expr.ELambda("x", integer, Expr.EApply(Expr.EVar("add"), Expr.EVar("x")))
    }

    @Benchmark
    fun typeCheck() {
        runBlocking { mapOf<String,Type>().typeCheck(expr) }
    }

    @Benchmark
    fun typeCheckWithState() {
        val state = State.Over<Map<String, Type>>()

        runBlocking { state.typeCheckWithState(expr)(gamma).fold { it.first } }
    }

    @Benchmark
    fun typeCheckWithStateAndDo() {
        val state = State.Over<Map<String, Type>>()

        runBlocking { state.typeCheckWithStateAndComprehension(expr)(gamma).fold { it.first } }
    }
}
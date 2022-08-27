package io.smallibs.pilin.examples

import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.state.State
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

internal class TypeCheckerStateTest {

    private sealed interface Literal {
        data class LInt(val value: Int) : Literal
        data class LString(val value: String) : Literal
    }

    private sealed interface Expr {
        data class ELiteral(val value: Literal) : Expr
        data class ELambda(val binding: String, val type: Type, val body: Expr) : Expr
        data class EApply(val abstraction: Expr, val parameter: Expr) : Expr
        data class EVar(val name: String) : Expr
    }

    private sealed interface Type {
        data class TLiteral(val name: String) : Type
        data class TArrow(val lhd: Type, val rhd: Type) : Type
    }

    private suspend fun State.Over<Map<String, Type>>.typeCheck(expr: Expr): App<State.StateK<Identity.IdentityK, Map<String, Type>>, Optional<Type>> =
        `do` {
            when (expr) {
                is Expr.EApply -> {
                    val abstraction = typeCheck(expr.abstraction).bind()
                    val parameter = typeCheck(expr.parameter).bind()
                    abstraction.flatMap {
                        when (it) {
                            is Type.TArrow -> if (Optional.of(it.lhd) == parameter) Optional.of(it.rhd) else Optional.empty()

                            else -> Optional.empty()
                        }
                    }
                }

                is Expr.ELambda -> {
                    modify { it + (expr.binding to expr.type) }.bind()
                    typeCheck(expr.body).bind()
                }

                is Expr.ELiteral -> when (expr.value) {
                    is Literal.LInt -> Optional.of(Type.TLiteral("string"))
                    is Literal.LString -> Optional.of(Type.TLiteral("int"))
                }

                is Expr.EVar -> {
                    Optional.ofNullable(get().bind()[expr.name])
                }
            }
        }

    @Test
    fun `should type check the expression`() {
        // Given
        val expr = Expr.ELambda("x", Type.TLiteral("int"), Expr.EApply(Expr.EVar("add"), Expr.EVar("x")))
        val state = State.Over<Map<String, Type>>()
        val gamma =
            mapOf("add" to Type.TArrow(Type.TLiteral("int"), Type.TArrow(Type.TLiteral("int"), Type.TLiteral("int"))))

        // When
        val type = runBlocking {
            state.typeCheck(expr)(gamma).fold { it.first }
        }

        // Then
        val expected: Optional<Type> = Optional.of(Type.TArrow(Type.TLiteral("int"), Type.TLiteral("int")))

        assertEquals(expected, type)
    }

    @Test
    fun `should not type check the expression`() {
        // Given
        val expr = Expr.ELambda("x", Type.TLiteral("int"), Expr.EApply(Expr.EVar("add"), Expr.EVar("x")))
        val state = State.Over<Map<String, Type>>()
        val gamma = mapOf(
            "add" to Type.TArrow(
                Type.TLiteral("string"), Type.TArrow(Type.TLiteral("int"), Type.TLiteral("int"))
            )
        )

        // When
        val type = runBlocking {
            state.typeCheck(expr)(gamma).fold { it.first }
        }

        // Then
        val expected: Optional<Type> = Optional.empty()

        assertEquals(expected, type)
    }
}
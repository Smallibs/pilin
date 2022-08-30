package benchmark

import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.standard.state.State
import io.smallibs.pilin.standard.state.State.StateK.Companion.invoke
import io.smallibs.pilin.type.App
import utils.unsafeSyncRun
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope

@kotlinx.benchmark.State(Scope.Benchmark)
class DeBruijn {

    sealed interface Term {
        data class Var(val name: String) : Term
        data class App(val abstraction: Term, val parameter: Term) : Term
        data class Abs(val bind: String, val body: Term) : Term
    }

    sealed interface DBTerm {
        data class Ident(val name: String) : DBTerm
        data class Var(val index: Int) : DBTerm
        data class App(val abstraction: DBTerm, val parameter: DBTerm) : DBTerm
        data class Abs(val body: DBTerm) : DBTerm
    }

    suspend fun List<String>.execute(term: Term): DBTerm =
        when (term) {
            is Term.Abs -> {
                (this + listOf(term.bind)).execute(term.body)
            }

            is Term.App -> {
                DBTerm.App(execute(term.abstraction), execute(term.parameter))
            }

            is Term.Var -> {
                val index = indexOf(term.name)
                if (index > -1) {
                    DBTerm.Var(index)
                } else {
                    DBTerm.Ident(term.name)
                }
            }
        }

    suspend fun <F> State.OverMonad<F, List<String>>.executeWithState(term: Term): App<State.StateK<F, List<String>>, DBTerm> =
        with(infix) {
            when (term) {
                is Term.Abs -> {
                    modify { it + listOf(term.bind) } bind {
                        executeWithState(term.body) map { body ->
                            DBTerm.Abs(body)
                        }
                    }
                }

                is Term.App -> {
                    executeWithState(term.abstraction) bind { lhd ->
                        executeWithState(term.parameter).map { rhd ->
                            DBTerm.App(lhd, rhd)
                        }
                    }
                }

                is Term.Var -> {
                    get().map { env ->
                        val index = env.indexOf(term.name)
                        if (index > -1) {
                            DBTerm.Var(index)
                        } else {
                            DBTerm.Ident(term.name)
                        }
                    }
                }
            }
        }

    suspend fun <F> State.OverMonad<F, List<String>>.executeWithStateAndDo(term: Term): App<State.StateK<F, List<String>>, DBTerm> =
        `do` {
            when (term) {
                is Term.Abs -> {
                    modify { it + listOf(term.bind) }.bind()
                    DBTerm.Abs(executeWithStateAndDo(term.body).bind())
                }

                is Term.App -> {
                    val ldh = executeWithStateAndDo(term.abstraction).bind()
                    val rhd = executeWithStateAndDo(term.parameter).bind()
                    DBTerm.App(ldh, rhd)
                }

                is Term.Var -> {
                    val index = get().bind().indexOf(term.name)
                    if (index > -1) {
                        DBTerm.Var(index)
                    } else {
                        DBTerm.Ident(term.name)
                    }
                }
            }
        }

    companion object {
        val term = Term.App(Term.Abs("x", Term.Var("x")), Term.Var("y"))
    }

    @Benchmark
    fun direct() {
        return unsafeSyncRun { listOf<String>().execute(term) }
    }

    @Benchmark
    fun withState() {
        return unsafeSyncRun { State.Over<List<String>>().executeWithState(term)(listOf()).fold { it.first } }

    }

    @Benchmark
    fun withStateAndDo() {
        return unsafeSyncRun { State.Over<List<String>>().executeWithStateAndDo(term)(listOf()).fold { it.first } }
    }

}

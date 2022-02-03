package io.smallibs.pilin.abstractions

import io.smallibs.pilin.abstractions.Semigroupoid.FromMonad.FunK
import io.smallibs.pilin.abstractions.Semigroupoid.FromMonad.FunK.Companion.lift
import io.smallibs.pilin.type.App

object Category {

    /**
     * Laws:
     * - compose(f,id) = f
     * - compose(id,f) = f
     */

    interface Core<T> : Semigroupoid.Core<T> {
        suspend fun <A> id(): App<App<T, A>, A>
    }

    class FromMonad<F>(override val monad: Monad.Core<F>) : API<FunK<F>>, Semigroupoid.FromMonad<F>(monad) {
        override suspend fun <A> id(): App<App<FunK<F>, A>, A> {
            return lift { monad.returns(it) }
        }
    }

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Operation<T>(private val c: Core<T>) : Semigroupoid.Operation<T>(c), Core<T> by c

    @Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
    class Infix<T>(private val c: Core<T>) : Semigroupoid.Infix<T>(c), Core<T> by c

    interface API<T> : Core<T> {
        val operation: Operation<T> get() = Operation(this)
        val infix: Infix<T> get() = Infix(this)
    }

}
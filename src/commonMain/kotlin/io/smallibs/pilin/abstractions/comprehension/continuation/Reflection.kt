package io.smallibs.pilin.abstractions.comprehension.continuation

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.continuation.thermometer.Universal
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Supplier

interface Reflection<F> {

    suspend fun <A> reflect(x: App<F, A>): A

    suspend fun <A> reify(f: Supplier<A>): App<F, A>

    private class ReflectionMonad<F>(private val m: Monad.Core<F>) : Reflection<F> {

        private val cont: Control<App<F, Any>> = Control.new()

        override suspend fun <A> reflect(x: App<F, A>): A =
            cont.shift { k -> m.bind(k)(x) }

        override suspend fun <A> reify(f: Supplier<A>): App<F, A> =
            m.bind<Any, A> { m.returns(Universal<A>().fromU(it)) }(cont.reset { m.returns(Universal<A>().toU(f())) })
    }

    companion object {
        fun <F> represents(m: Monad.Core<F>): Reflection<F> = ReflectionMonad(m)
    }
}

package io.smallibs.pilin.standard.free.monad

import io.smallibs.pilin.abstractions.Transformer
import io.smallibs.pilin.standard.free.monad.Free.FreeK.Companion.fold
import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun
import io.smallibs.pilin.abstractions.Functor.Core as Functor_Core
import io.smallibs.pilin.abstractions.Monad.API as Monad_API

sealed interface Free<F, A> : App<Free.FreeK<F>, A> {
    data class Return<F, A>(val value: A) : Free<F, A>
    data class Bind<F, A>(val value: App<F, App<FreeK<F>, A>>) : Free<F, A>

    class FreeK<F> private constructor() {
        companion object {
            private val <F, A> App<FreeK<F>, A>.fix: Free<F, A> get() = this as Free<F, A>
            suspend fun <F, A, B> App<FreeK<F>, A>.fold(r: Fun<A, B>, b: Fun<App<F, App<FreeK<F>, A>>, B>): B =
                when (val self = this.fix) {
                    is Return -> r(self.value)
                    is Bind -> b(self.value)
                }
        }
    }

    open class OverFunctor<F>(val inner: Functor_Core<F>, val api: Monad_API<FreeK<F>> = Monad.monad(inner)) :
        Monad_API<FreeK<F>> by api {

        suspend fun <A> perform(f: App<F, A>): App<FreeK<F>, A> =
            Bind(inner.map<A, App<FreeK<F>, A>> { Return(it) }(f))

        suspend fun <A> run(f: Fun<App<F, A>, A>): Fun<App<FreeK<F>, A>, A> =
            { ma -> ma.fold({ it }, { f(inner.map(run(f))(it)) }) }

        suspend fun <A, G> Monad_API<G>.run(transformer: Transformer<F, G>): Fun<App<FreeK<F>, A>, App<G, A>> =
            { ma -> ma.fold({ this.returns(it) }) { bind(run<A, G>(transformer))(transformer(it)) } }

        fun functor() = Functor.functor(inner)
        fun applicative() = Applicative.applicative(inner)
        fun monad() = api
        fun selective() = Selective.selective(inner)
    }
}
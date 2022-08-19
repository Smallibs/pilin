package io.smallibs.pilin.standard.list

import io.smallibs.pilin.type.App

data class List<A>(val l: kotlin.collections.List<A>) : App<List.ListK, A> {

    class ListK private constructor() {
        companion object {
            val <A> App<ListK, A>.fix: List<A>
                get() = this as List<A>

            val <A> App<ListK, A>.run: kotlin.collections.List<A>
                get() = this.fix.run
        }
    }

    companion object {
        fun <A> monoid() = Monoid.monoid<A>()
        val functor = Functor.functor
        val applicative = Applicative.applicative
        val selective = Selective.selective
        val monad = Monad.monad
    }

}

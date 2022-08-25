package io.smallibs.pilin.standard.list

import io.smallibs.pilin.type.App

data class List<A> internal constructor(
    val list: kotlin.collections.List<A>,
) : App<List.ListK, A>, kotlin.collections.List<A> by list {

    constructor(vararg a: A) : this(a.toList())

    object ListK {
        val <A> App<ListK, A>.fix: List<A>
            get() = this as List<A>

        val <A> App<ListK, A>.inner: kotlin.collections.List<A>
            get() = this.fix.list
    }

    companion object {
        fun <A> monoid() = Monoid.monoid<A>()
        val functor = Functor.functor
        val applicative = Applicative.applicative
        val selective = Selective.selective
        val monad = Monad.monad
    }

}

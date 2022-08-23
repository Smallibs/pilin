package io.smallibs.pilin.standard.list

import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.standard.list.List.ListK.Companion.fix

object Monoid {
    class MonoidImp<A> : Monoid.API<List<A>> {
        override val neutral: List<A>
            get() = List(listOf())

        override suspend fun combine(l: List<A>, r: List<A>): List<A> {
            return List(l.fix + r.fix)
        }
    }

    fun <A> monoid(): Monoid.API<List<A>> = MonoidImp()
}
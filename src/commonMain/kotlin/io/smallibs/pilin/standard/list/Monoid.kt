package io.smallibs.pilin.standard.list

import io.smallibs.pilin.abstractions.Monoid

object Monoid {
    class MonoidImp<A> : Monoid.API<List<A>> {
        override val neutral: List<A>
            get() = List(listOf())

        override suspend fun combine(l: List<A>, r: List<A>): List<A> {
            return List(l.list + r.list)
        }
    }

    fun <A> monoid(): Monoid.API<List<A>> = MonoidImp()
}
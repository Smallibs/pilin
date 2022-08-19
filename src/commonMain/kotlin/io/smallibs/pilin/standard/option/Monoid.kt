package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.abstractions.Semigroup

object Monoid {
    class MonoidImp<A>(private val semigroup: Semigroup.API<A>) : Monoid.API<Option<A>> {
        override val neutral: Option<A>
            get() = Option.None

        override suspend fun combine(l: Option<A>, r: Option<A>): Option<A> = when (l) {
            Option.None -> r
            is Option.Some -> when (r) {
                Option.None -> l
                is Option.Some -> Option.Some(semigroup.combine(l.value, r.value))
            }
        }
    }

    fun <A> monoid(semigroup: Semigroup.API<A>): Monoid.API<Option<A>> = MonoidImp(semigroup)
}
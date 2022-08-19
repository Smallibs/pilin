package io.smallibs.pilin.standard.option

import io.smallibs.pilin.abstractions.Monoid
import io.smallibs.pilin.abstractions.Semigroup
import io.smallibs.pilin.standard.option.Option.Companion.some
import io.smallibs.pilin.standard.option.Option.OptionK.Companion.fold

object Monoid {
    class MonoidImp<A>(private val semigroup: Semigroup.API<A>) : Monoid.API<Option<A>> {
        override val neutral: Option<A>
            get() = Option.None

        override suspend fun combine(l: Option<A>, r: Option<A>): Option<A> =
            l.fold({ r }) { lv -> r.fold({ l }) { rv -> some(semigroup.combine(lv, rv)) } }
    }

    fun <A> monoid(semigroup: Semigroup.API<A>): Monoid.API<Option<A>> = MonoidImp(semigroup)
}
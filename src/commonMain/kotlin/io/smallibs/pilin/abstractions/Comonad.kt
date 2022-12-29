package io.smallibs.pilin.abstractions

import io.smallibs.pilin.type.App
import io.smallibs.pilin.type.Fun

object Comonad {

    interface WithMapAndDuplicate<F> : Functor.Core<F> {
        fun <A> extract(ma: App<F, A>): A

        fun <A> duplicate(ma: App<F, A>): App<F, App<F, A>>
    }

    interface WithExtend<F> {
        fun <A> extract(ma: App<F, A>): A

        fun <A, B> extend(f: Fun<App<F, A>, B>): B
    }

    interface WithCoKleisliComposition<F> {
        fun <A> extract(ma: App<F, A>): A

        fun <A, B, C> leftToRight(f: Fun<App<F, A>, B>): Fun<Fun<App<F, B>, C>, Fun<App<F, A>, C>>
    }

    interface Core<F> : WithMapAndDuplicate<F>, WithExtend<F>, WithCoKleisliComposition<F>

}
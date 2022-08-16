package io.smallibs.pilin.laws

import io.smallibs.pilin.abstractions.Semigroupoid
import io.smallibs.pilin.core.Standard.With
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.type.App

object Semigroupoid {

    suspend fun <F, A, B, C, D> Semigroupoid.API<F>.`f compose (g compose h) = (f compose g) compose h`(
        f: App<App<F, C>, D>,
        g: App<App<F, B>, C>,
        h: App<App<F, A>, B>,
        equatable: Equatable<App<App<F, A>, D>>,
    ): Boolean = With(this.infix, equatable) {
        {
            f composeRightToLeft (g composeRightToLeft h) isEqualTo ((f composeRightToLeft g) composeRightToLeft h)
        }
    }
}
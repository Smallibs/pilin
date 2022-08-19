package io.smallibs.pilin.laws

import io.smallibs.pilin.abstractions.PreCategory
import io.smallibs.pilin.core.Standard.With
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.type.App2

object PreCategory {

    suspend fun <F, A, B, C, D> PreCategory.API<F>.`f compose (g compose h) = (f compose g) compose h`(
        f: App2<F, C, D>,
        g: App2<F, B, C>,
        h: App2<F, A, B>,
        equatable: Equatable<App2<F, A, D>>,
    ): Boolean = With(this.infix, equatable) {
        {
            f composeRightToLeft (g composeRightToLeft h) isEqualTo ((f composeRightToLeft g) composeRightToLeft h)
        }
    }

}
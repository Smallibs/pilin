package io.smallibs.pilin.laws

import io.smallibs.pilin.abstractions.Category
import io.smallibs.pilin.core.Standard.With
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.type.App2

object Category {

    suspend fun <F, A, B> Category.API<F>.`f compose id = f`(
        f: App2<F, A, B>,
        equatable: Equatable<App2<F, A, B>>,
    ): Boolean = With(this.infix, equatable) {
        {
            f composeRightToLeft id() isEqualTo f
        }
    }

    suspend fun <F, A, B> Category.API<F>.`id compose f = f`(
        f: App2<F, A, B>,
        equatable: Equatable<App2<F, A, B>>,
    ): Boolean = With(this.infix, equatable) {
        {
            id<B>() composeRightToLeft f isEqualTo f
        }
    }
}
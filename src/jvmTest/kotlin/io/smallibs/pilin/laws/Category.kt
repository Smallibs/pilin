package io.smallibs.pilin.laws

import io.smallibs.pilin.abstractions.Category
import io.smallibs.pilin.standard.support.Equatable
import io.smallibs.pilin.type.App

object Category {

    suspend fun <F, A, B> Category.API<F>.`f compose id = f`(
        f: App<App<F, A>, B>,
        equatable: Equatable<App<App<F, A>, B>>,
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                f composeRightToLeft id() isEqualTo f
            }
        }

    suspend fun <F, A, B> Category.API<F>.`id compose f = f`(
        f: App<App<F, A>, B>,
        equatable: Equatable<App<App<F, A>, B>>,
    ): Boolean =
        with(this.infix) {
            with(equatable) {
                id<B>() composeRightToLeft f isEqualTo f
            }
        }
}
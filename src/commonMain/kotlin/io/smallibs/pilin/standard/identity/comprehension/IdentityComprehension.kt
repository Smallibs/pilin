package io.smallibs.pilin.standard.identity.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.abstractions.comprehension.Comprehension
import io.smallibs.pilin.standard.identity.Identity
import io.smallibs.pilin.standard.identity.Identity.IdentityK.fold
import io.smallibs.pilin.type.App

class IdentityComprehension(private val monad: Monad.API<Identity.IdentityK>) :
    Monad.API<Identity.IdentityK> by monad, Comprehension<Identity.IdentityK> {

    override suspend fun <B> App<Identity.IdentityK, B>.bind(): B = this.fold { it }

    companion object {
        suspend inline fun <A> run(
            monad: Monad.API<Identity.IdentityK>,
            crossinline f: suspend IdentityComprehension.() -> A,
        ): App<Identity.IdentityK, A> =
            IdentityComprehension(monad).let { comprehension ->
                monad.returns(comprehension.f())
            }
    }
}
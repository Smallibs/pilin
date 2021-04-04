package io.smallibs.pilin.control

import io.smallibs.pilin.type.App

object Functor {

    interface API<F> {
        suspend fun <A, B> map(ma: App<F, A>): suspend (suspend (A) -> B) -> App<F, B>
    }

}

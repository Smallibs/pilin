package io.smallibs.kategory.control

import io.smallibs.kategory.type.App

object Functor {

    interface API<F> {
        suspend fun <A, B> map(ma: App<F, A>): suspend (suspend (A) -> B) -> App<F, B>
    }

}

package io.smallibs.pilin.abstractions.comprehension

import io.smallibs.pilin.abstractions.Monad
import io.smallibs.pilin.type.App

interface Comprehension<F> : Monad.API<F> {

    suspend fun <B> App<F, B>.bind(): B

}
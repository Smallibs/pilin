package io.smallibs.pilin.standard.support

import io.smallibs.pilin.core.Standard
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK
import io.smallibs.pilin.standard.continuation.Continuation.ContinuationK.Companion.fix
import io.smallibs.pilin.type.App

interface Equatable<A> {
    suspend infix fun A.isEqualTo(a: A): Boolean

    companion object {
        fun <A> default(): Equatable<A> = object : Equatable<A> {
            override suspend fun A.isEqualTo(a: A): Boolean = this == a
        }

        fun <I> continuation(): Equatable<App<ContinuationK, I>> = object : Equatable<App<ContinuationK, I>> {
            override suspend fun App<ContinuationK, I>.isEqualTo(a: App<ContinuationK, I>): Boolean =
                this.fix(Standard::id) == a.fix(Standard::id)
        }
    }
}
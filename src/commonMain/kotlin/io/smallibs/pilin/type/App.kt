package io.smallibs.pilin.type

interface App<out F, out A>
typealias App2<F, A, B> = App<App<F, A>, B>

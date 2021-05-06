# Pilin

[![unstable](http://badges.github.io/stability-badges/dist/unstable.svg)](http://github.com/badges/stability-badges)

[pilin (pilind-, as in pl. pilindi) noun "arrow" (PÍLIM)](https://www.elfdict.com/w/pilin?include_old=1)

Pilin is a tiny library for [Kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) providing some functional programming constructions like:
- Functor,
- Applicative,
- Monad.

Some incarnations are available like Identity, Option and Either.

Since Kotlin has colored function, the design has been done with only suspended functions.
In this approach `suspend` does not mean functions interacting with the subsystem 
i.e. no Relationship with IO.

The main advantage of this approach is the capability to deliver a comprehension like
approach in order to simplify the code.

The construction is based on a highly modular system inspired by the [Preface](https://ocaml-preface.github.io/preface/index.html)
library and of course [Arrow.kt](https://arrow-kt.io) for the comprehension implementation.

## A taste of Pilin

### Functor internal design

```kotlin
object Functor {

    interface Core<F> {
        suspend fun <A, B> map(f: Fun<A, B>): Fun<App<F, A>, App<F, B>>
    }

    class Operation<F>(private val c: Core<F>) : Core<F> by c {
        suspend fun <A, B> replace(a: A): Fun<App<F, B>, App<F, A>> =
            map { a }

        suspend fun <A> void(ma: App<F, A>): App<F, Unit> =
            replace<Unit, A>(Unit)(ma)
    }

    open class Infix<F>(private val c: Core<F>) : Core<F> by c {
        suspend infix fun <A, B> (Fun<A, B>).map(ma: App<F, A>): App<F, B> = c.map(this)(ma)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }
}
```

### Option Functor

```kotlin
object Option {

    sealed class T<A> : App<TK, A> {
        data class None<A>(private val u: Unit = Unit) : T<A>()
        data class Some<A>(val value: A) : T<A>()
    }

    // This code can be automatically generated
    class TK private constructor() {
        companion object {
            private val <A> App<TK, A>.fix: T<A>
                get() = this as T<A>

            fun <A> none(): App<TK, A> = T.None()
            fun <A> some(a: A): App<TK, A> = T.Some(a)

            suspend fun <A, B> App<TK, A>.fold(n: Supplier<B>, s: Fun<A, B>): B =
                when (val self = this.fix) {
                    is T.None -> n()
                    is T.Some -> s(self.value)
                }
        }
    }

    private class FunctorImpl : Functor.API<TK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<TK, A>, App<TK, B>> =
            { ma -> ma.fold(::none) { a -> some(f(a)) } }
    }
    
    // ...
}
```

### Comprehension in action

```kotlin
Comprehension(Option.monad) {
    val (a) = returns(40)
    val (b) = returns(2)
    a + b
}
```

Of course this code can be generalized sine monad is injected:

```kotlin
suspend fun <T> doSomething(m: Monad.API<T>): App<T, Int> =
    Comprehension(m) {
        val (a) = returns(40)
        val (b) = returns(2)
        a + b
    }
```        


# License

MIT License

Copyright (c) 2021 Didier Plaindoux

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

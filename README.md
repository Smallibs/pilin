# Pilin

[![Build Status](https://travis-ci.org/d-plaindoux/pilin.svg?branch=main)](https://travis-ci.org/d-plaindoux/pilin.svg?branch=main)
[![unstable](http://badges.github.io/stability-badges/dist/unstable.svg)](http://github.com/badges/stability-badges)

[pilin (pilind-, as in pl. pilindi) noun "arrow" (PÍLIM)](https://www.elfdict.com/w/pilin?include_old=1)

Pilin is a library for [Kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) providing some functional programming constructions like:
- Functor,
- Applicative,
- Monad.

Some incarnations are available like Identity, Option and Either.

Since Kotlin has colored functions, the design has been done with only suspended functions.
In this approach `suspend` does not mean functions interacting with the subsystem 
i.e. no Relationship with IO.

The main advantage of this approach is the capability to deliver a comprehension like
approach in order to simplify the code.

The construction is based on a highly modular system inspired by the [Preface](https://ocaml-preface.github.io/preface/index.html)
library and of course [Arrow.kt](https://arrow-kt.io) for the comprehension implementation.

## A taste of Pilin

In this section we show how the `Functor` abstraction is designed. 

### Functor design

First we use `object` in order to have a string namespacing. Then a first interface named `Core` is proposed with the minimal set 
of function required. In addition two implementations are  proposed for `Operation` and `Infix` expressed thanks to the `Core`. 
The first one contains contains additional functions when the second proposes an infix version of `Core` functions using OOP 
capabilities.

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
        suspend infix fun <A, B> Fun<A, B>.map(ma: App<F, A>): App<F, B> = 
            c.map(this)(ma)
        suspend infix fun <A, B> App<F, A>.map(f: Fun<A, B>): App<F, B> = 
            c.map(f)(this)
    }

    interface API<F> : Core<F> {
        val operation: Operation<F> get() = Operation(this)
        val infix: Infix<F> get() = Infix(this)
    }
}
```

### Option

In this section we show how `Option` can be designed.

#### Data type definition

First at all, the data type should be specified. Of course, an optional value is `None` of `Some` value. In addition an internal class `TK` - for type kind - 
using a type defunctionalised as illustrated in [Lightweight higher-kinded polymorphism](https://www.cl.cam.ac.uk/~jdy22/papers/lightweight-higher-kinded-polymorphism.pdf).

In this `TK` class, a `fix` value is proposed when a downcast is required. This operation is of course dangerous, but to reduce this aspect the scope of the constructor is limited to `Option`. In addtion, the catamorphism `fold` function is proposed.

Finally, smart constructors and abstraction implementation references can be proposed.

```kotlin
sealed class Option<A> : App<Option.TK, A> {
    data class None<A>(private val u: Unit = Unit) : Option<A>()
    data class Some<A>(val value: A) : Option<A>()

    class OptionK private constructor() {
        companion object {
            val <A> App<OptionK, A>.fix: Option<A>
                get() = this as Option<A>

            suspend fun <A, B> App<OptionK, A>.fold(n: Supplier<B>, s: Fun<A, B>): B =
                when (val self = this.fix) {
                    is None -> n()
                    is Some -> s(self.value)
                }
        }
    }

    companion object {
        fun <A> none(): App<OptionK, A> = None()
        fun <A> some(a: A): App<OptionK, A> = Some(a)

        val functor = Functor.functor
        // ...
    }
}
```

#### Functor implementation for Option

The `Functor` abstraction implementation can then be proposed. This is done once again using object namespacing.

```kotlin
object Functor {
    private class FunctorImpl : Functor.API<OptionK> {
        override suspend fun <A, B> map(f: Fun<A, B>): Fun<App<OptionK, A>, App<OptionK, B>> =
            { ma -> ma.fold(::none) { a -> some(f(a)) } }
    }

    val functor: Functor.API<OptionK> = FunctorImpl()
}
```

## Comprehension in action

Using functional idioms like `Monad` can be painfull. For instance if we want to add to optional integers
we must write the following code:

```kotlin
with(Option.monad.infix) {
    returns(40) bind { a ->
        returns(2) map { b -> 
            a + b
        }
    } 
}
```

In order to have a more readable version a comprehension based formulation is provided. 
Then the previoux expression can be proposed using such comprehension facility:

```kotlin
Comprehension(Option.monad) {
    val (a) = returns(40)       // or val a = returns(40).bind()
    val (b) = returns(2)        // or val b = returns(2).bind()
    a + b
}
```

An infix version is also proposed with the Monad extension method `do`: 

```kotlin
Option.monad `do` {
    val (a) = returns(40)
    val (b) = returns(2)
    a + b
}
```

Finally, an generalized version can be proposed for any Monad and not only for `Option`.

```kotlin
suspend fun <T> doSomething(m: Monad.API<T>): App<T, Int> =
    m `do` {
        val (a) = returns(40)
        val (b) = returns(2)
        a + b
    }
```

Note: the `Comprehension` uses Kotlin continuation. Then each operation should be executed thanks to
the destructured operation or the explicit bind call. Otherwise the effect is not executed.

Of course the applicative can be used in this case:

```kotlin
suspend fun <T> doSomething(a: Applicative.API<T>): App<T, Int> =
    with(a.infix) {
        val plus = curry { a: Int, b: Int -> a + b }
        plus map pure(40) apply pure(2) // or pure(plus) apply pure(40) apply pure(2)
    }
```

## Onboarding user defined effects

In addition user defined effects can be proposed and seamlessly combined with predefined effects like
continuation, either option etc.

### IOConsole effect specification

We define a effect able to read and print strings. The resulting effect of each operation is defined 
using a parametric `F`.

```kotlin
class IOConsole<F>(
    val printString: (String) -> App<F, Unit>,
    val readString: App<F, String>,
) : Handler
```    

## Code using effect specification

Therefor we can write a naive program usign such effect specification.

```kotlin
private fun <F> program(monad: Monad.API<F>): Effects<IOConsole<F>, App<F, Unit>> =
    with(monad.infix) {
        console.readString bind { value ->
            console.printString("Hello $value")
        }
    }
}
```

## Defining my own console 

Of course, an implementation can be provided. In this example the effect if the 
`Continuation`.

```kotlin
private fun console(): IOConsole<ContinuationK<List<String>>> =
        IOConsole(
            printString = { text ->
                continuation { k ->
                    listOf("printString($text)") + k(Unit)
                }
            },
            readString = continuation { k ->
                listOf("readStream(World)") + k("World")
            }
        )
```

## Executing the program with a dedicated console

Finally the previous program can be executed with the `console()` uyser defined effect.

```kotlin
val handled = program(Continuation.monad<List<String>>()) with {
     console()
}

val traces = runBlocking { (handled()) { listOf() } }
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

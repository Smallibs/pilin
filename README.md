# Pilin

[![Build Status](https://travis-ci.org/d-plaindoux/pilin.svg?branch=main)](https://travis-ci.org/d-plaindoux/pilin.svg?branch=main)
[![unstable](http://badges.github.io/stability-badges/dist/stable.svg)](http://github.com/badges/stability-badges)

[pilin (pilind-, as in pl. pilindi) noun "arrow" (PÍLIM)](https://www.elfdict.com/w/pilin?include_old=1)

Pilin is a library for [Kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) providing some functional
programming constructions like:

- Semigroup,
- Monoid,
- PreCategory,
- Category,
- ProFunctor,
- Functor,
- Applicative,
- Selective and
- Monad

Some incarnations are available like:

- Identity,
- Option,
- Result,
- Try,
- Either,
- List,
- Continuation,
- Reader,
- Writer,
- State,
- Free and
- Freer.

Since Kotlin has colored functions, the design has been done with only suspended functions. In this approach `suspend`
does not mean functions interacting with the subsystem i.e. no relationship with IO for instance.

The construction is based on a highly modular system inspired by
the [Preface](https://ocaml-preface.github.io/preface/index.html)
library and [Thermometer Continuations](https://hal.inria.fr/hal-01929178/document) for the comprehension
implementation and dedicated implementation in order to have more efficient alghorithms.

## A taste of Pilin

In this section we show how the `Functor` abstraction can be designed.

### Functor design

First, we use `object` to have a simple namespacing. Then, a first interface named `Core` is proposed with the
the minimal set of required functions. Furthermore, two implementations are proposed for `Operation` and `Infix`, 
expressed through `Core`. The second one is expressed through `Core`. The first one contains additional functions, 
while the second one proposes an infix version of `Core` functions using the OOP capabilities.

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

Note: `Fun<A,B>` stands for `suspend (A) -> B`.

### Option

In this section we show how `Option` can be designed.

#### Data type definition

First at all, the data type should be specified. Of course, an optional value is `None` of `Some` value. In addition, an
internal class `OptionK` - for type kind - using a type defunctionalised as illustrated
in [Lightweight higher-kinded polymorphism](https://www.cl.cam.ac.uk/~jdy22/papers/lightweight-higher-kinded-polymorphism.pdf)
.

In this `OptionK` class, a `fix` value is proposed when a downcast is required. This operation is of course unsafe, but
to reduce this aspect the scope of the constructor is limited to `Option`. Finally, the catamorphism `fold` function is
suggested.

Smart constructors and abstraction implementation references can be proposed.

```kotlin
sealed class Option<A> : App<OptionK, A> {
    object None : Option<Nothing>()
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
        fun <A> none(): Option<A> = None
        fun <A> some(a: A): Option<A> = Some(a)

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
            { ma -> ma.fold(::none, f then ::some) }
    }

    val functor: Functor.API<OptionK> = FunctorImpl()
}
```

## Comprehension in action

Using functional idioms like `Monad` can be painful. For instance, if we want to add two optional integers we must write
the following code:

```kotlin
with(Option.monad.infix) {
    returns(40) * returns(2) map { (a, b) ->
        a + b
    }
}
```

In order to have a more readable version a comprehension based formulation is provided. Then the previous expression can
be proposed using such comprehension facility:

```kotlin
Option.monad `do` {
    returns(40).bind() + returns(2).bind()
}
```

Finally, a generalized version can be proposed for any Monad and not only for `Option`.

```kotlin
suspend fun <F> doSomething(m: Monad.API<F>): App<F, Int> =
    m `do` {
        returns(40).bind() + returns(2).bind()
    }
```

Each operation should be executed thanks to the `bind()` function. Otherwise, the effect is never executed.

Of course, the applicative can be used in this case:

```kotlin
suspend fun <T> doSomething(a: Applicative.API<T>): App<T, Int> =
    with(a.infix) {
        val plus = curry { a: Int, b: Int -> a + b }
        plus map pure(40) apply pure(2) // or pure(plus) apply pure(40) apply pure(2)
    }
```

## Onboard user defined effects

In addition, user defined effects can be proposed and seamlessly combined with predefined effects like continuation,
either, option etc.

### Console effect specification

We specify a user defined effect able to read and print strings. The resulting effect of each operation is defined using
a parametric `F`.

```kotlin
class Console<F>(
    val printString: (String) -> App<F, Unit>,
    val readString: App<F, String>,
) : Handler
```    

## Code using effect specification

Therefor we can write a naive program using such effect specification thanks to comprehension.

```kotlin
private fun <F> program(monad: Monad.API<F>): Effects<Console<F>, App<F, Unit>> =
    handle { console ->
        monad `do` {
            val value = console.readString.bind()
            console.printString("Hello $value").bind()
        }
    }
```

## Defining my own console

Of course, an implementation can be provided. In this example the effect used is `Continuation`.

```kotlin
fun console(traces: MutableList<String>) =
    Console(
        printString = { text ->
            object : Continuation<Unit> {
                override suspend fun <O> invoke(k: Fun<Unit, O>): O {
                    traces.add("printString($text)")
                    return k(Unit)
                }
            }
        },
        readString = object : Continuation<String> {
            override suspend fun <O> invoke(k: Fun<String, O>): O {
                traces.add("readStream(World)")
                return k("World")
            }
        }
    )
```

**Note**: we cannot use functional interface for continuation construction i.e. problem with existential type.

## Executing the program with a dedicated console

Then the previous program can be executed with the user defined effect implemented by `console`. Since all constructions
return suspended functions this execution should be performed thanks to the standard `runTest` function.

```kotlin
val traces = mutableListOf<String>()
val handled = program(Continuation.monad) with console(traces)

runTest { handled().invoke { } }
```

Finally, after the execution `traces` has the following value: `listOf("readString(World)", "printString(Hello World)")`

# Benchmarks

## JS 

```
Benchmark                    Mode  Cnt    Score    Error   Units
ConsoleIO.withFree          thrpt    5  109.507 ± 54.032  ops/ms
ConsoleIO.withFreeAndDo     thrpt    5  161.926 ± 43.818  ops/ms
ConsoleIO.withFreer         thrpt    5  169.727 ± 28.968  ops/ms
ConsoleIO.withFreerAndDo    thrpt    5  165.794 ± 39.267  ops/ms
DeBruijn.direct             thrpt    5  142.910 ± 19.566  ops/ms
DeBruijn.withState          thrpt    5  148.815 ±  9.436  ops/ms
DeBruijn.withStateAndDo     thrpt    5  130.154 ± 37.172  ops/ms
Template.direct             thrpt    5  153.322 ±  9.493  ops/ms
Template.withReader         thrpt    5  149.670 ± 23.897  ops/ms
Template.withReaderAndDo    thrpt    5  137.557 ± 26.141  ops/ms
TypeChecker.direct          thrpt    5  136.381 ± 32.328  ops/ms
TypeChecker.withState       thrpt    5  132.175 ± 15.194  ops/ms
TypeChecker.withStateAndDo  thrpt    5  138.713 ±  6.230  ops/ms
XmlStax.direct              thrpt    5  134.712 ± 19.905  ops/ms
XmlStax.withWriter          thrpt    5  111.659 ± 40.384  ops/ms
XmlStax.withWriterAndDo     thrpt    5  133.734 ± 19.931  ops/ms
```

## JVM

```
Benchmark                    Mode  Cnt      Score      Error  Units
ConsoleIO.withFree          thrpt   15   6442.289 ± 1258.576  ops/s
ConsoleIO.withFreeAndDo     thrpt   15   3112.459 ±  794.673  ops/s
ConsoleIO.withFreer         thrpt   15   9431.532 ± 3353.802  ops/s
ConsoleIO.withFreerAndDo    thrpt   15   3327.575 ±  683.984  ops/s
DeBruijn.direct             thrpt   15  14718.807 ± 4622.168  ops/s
DeBruijn.withState          thrpt   15   6316.542 ± 1116.652  ops/s
DeBruijn.withStateAndDo     thrpt   15   1433.421 ±  369.249  ops/s
Template.direct             thrpt   15  18539.666 ± 6732.296  ops/s
Template.withReader         thrpt   15  10677.396 ± 1924.346  ops/s
Template.withReaderAndDo    thrpt   15   2389.064 ±  426.447  ops/s
TypeChecker.direct          thrpt   15  12724.891 ± 5382.033  ops/s
TypeChecker.withState       thrpt   15   7472.074 ± 2166.757  ops/s
TypeChecker.withStateAndDo  thrpt   15   3155.082 ±  314.460  ops/s
XmlStax.direct              thrpt   15  12724.308 ± 2502.788  ops/s
XmlStax.withWriter          thrpt   15   4734.501 ±  701.159  ops/s
XmlStax.withWriterAndDo     thrpt   15    630.150 ±   79.681  ops/s
```

## Native (MaxOS / Intel)

```
Benchmark                    Mode  Cnt    Score    Error   Units
ConsoleIO.withFree          thrpt    5   66.124 ±  5.129  ops/ms
ConsoleIO.withFreeAndDo     thrpt    5   10.103 ±  4.670  ops/ms
ConsoleIO.withFreer         thrpt    5   97.557 ±  9.035  ops/ms
ConsoleIO.withFreerAndDo    thrpt    5   14.148 ±  1.316  ops/ms
DeBruijn.direct             thrpt    5  120.602 ± 21.918  ops/ms
DeBruijn.withState          thrpt    5   31.919 ±  1.704  ops/ms
DeBruijn.withStateAndDo     thrpt    5    2.511 ±  1.039  ops/ms
Template.direct             thrpt    5  163.160 ± 31.209  ops/ms
Template.withReader         thrpt    5   55.618 ±  5.617  ops/ms
Template.withReaderAndDo    thrpt    5    6.494 ±  0.153  ops/ms
TypeChecker.direct          thrpt    5  174.861 ± 24.807  ops/ms
TypeChecker.withState       thrpt    5   26.930 ± 12.186  ops/ms
TypeChecker.withStateAndDo  thrpt    5    7.407 ±  3.336  ops/ms
XmlStax.direct              thrpt    5  122.095 ± 10.856  ops/ms
XmlStax.withWriter          thrpt    5   25.770 ±  0.823  ops/ms
XmlStax.withWriterAndDo     thrpt    5    0.490 ±  0.213  ops/ms
```

# License

MIT License

Copyright (c) 2021-2022 Didier Plaindoux

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

[![Release](https://jitpack.io/v/io.github.portfoligno/stateful-cartesian-streams.svg)](
  https://jitpack.io/#io.github.portfoligno/stateful-cartesian-streams
)

Stateful Cartesian Streams
===
A poor man's for-comprehension (as in Scala) or do-notation (as in Haskell) for synchronous computation in Java.

For Kotlin users, [Λrrow](https://arrow-kt.io/docs/patterns/monad_comprehensions/) is a better alternative.

## Installation

```kts
repositories {
  mavenCentral()
  maven("https://jitpack.io")
}
dependencies {
  implementation("io.github.portfoligno", "stateful-cartesian-streams", VERSION)
}
```

## Usage

```java
List<Integer> combinations = StatefulCartesian
  .yieldAll(c -> {
    int i = c.pull(List.of(-1, 0, 1));
    int j = c.pull(List.of(700, 800, 900));
    int k = c.pull(List.of(1, 2, 3));

    return i * (j + k);
  })
  .collect(toList());

System.out.println(combinations);
// [-701, -702, -703, -801, -802, -803, -901, -902, -903, 0, 0, 0, 0, 0, 0, 0, 0, 0, 701, 702, 703, 801, 802, 803, 901, 902, 903]
```

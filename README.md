
# Overview

This repository contains the material for *Introduction to Functional Game Programming with Scala*, held at [LambdaConf 2014](http://degoesconsulting.com/lambdaconf/) in Boulder, Colorado.

This README file contains a walkthrough for the workshop, as well as the following source code:

 1. In the `src` directory, you'll find a skeleton for a functional game that uses Scalaz and Monocle. This application is intended as the basis for the final set of exercises in the workshop.
 2. In `snippets.txt`, you'll find a collection of snippets developed over the course of the workshop. You should be able to run this file from the command-line using the `scala` process.

# Introduction

Welcome to *Introduction to Functional Game Programming with Scala*! You're going to learn purely-functional programming by writing a fun little game.

Games are some of the most stateful applications in existence!

In fact, many games are really full-fledged simulations. Almost every piece of data changes at every iteration!

Can you *really* use pure functional programming to write games? The answer is yes, and this workshop is here to show you how.

We're going to focus on writing a text-based RPG (or adventure game, if you like). This way, you won't need to create or animate graphics, and you can focus on the essence of handling state in a functional way.

Ready? Good, let's begin!

# The Game Loop

At the center of every game is something called a *game loop*. The game loop handles input and updates the state of the game.

In an imperative style, we could write a game loop using a `while` loop:

```scala
var executing = true

while (executing) {
  executing = handleInput()
  
  updateGameWorld()
}
```

Since we're only focusing on text-based RPG, we can flesh this out a bit more.

Try dumping the following code into an empty file, and executing the file with `scala`:

```scala
var executing = true

while (executing) {
  val input = readLine()
  
  if (input == "quit") executing = false
  
  // Here's where we'd update the game world.... now just print out what they typed:
  println("So, you want to " + input + ", do you?")
}
```

This `while` loop prints out your commands until you enter the command "quit", at which point it stops executing.

What's the purely-functional alternative to loops? Recursion, of course!

Let's rewrite the game loop using a self-recursive function:

```scala
def loop(): Unit = {
  val input = readLine()
  
  if (input == "quit") return;
  
  println("So, you want to " + input + ", do you?")
  
  loop()
}
loop()
```

> **Note:** Unbounded self-recursion on the JVM will overflow the stack, but there's a robust way to work around this called *trampolining*, which basically involves stuffing steps of the recursion into data structures, and using an "interpreter" to execute the recursion step-by-step.

This is a good start, but the above game loop is far from purely-functional. Neither `readLine()` or `println()` are *pure functions* (deterministic functions of their input), which in a larger context, makes it harder to reason about code.

How can we make this game loop more functional and easier to understand?

There are lots of ways to do this, but the abstraction that's going to help you most as a beginning functional programmer is called a *monad*.

Bet you guessed that, didn't you?

### Exercises

 1. Write your own game loop using recursion. Hopefully, it's more interesting than the provided game loop!
 2. Add the `scala.annotation.@tailrec` annotation to your loop to see if Scala can compile it to a `while` loop. The compiler will give you an error if your function is not [tail-recursive](http://www.haskell.org/haskellwiki/Tail_recursion).

## Monads

As you've [probably heard](http://en.wikipedia.org/wiki/Monad_(functional_programming)), monads are a "functional design pattern" that come up a lot when you're writing purely-functional code.

There's a good reason for their ubiquity: monads encapsulate the essence of *sequential computation*.

In C, statements are executed one after another. Do this, then do that.

In purely-functional programming, there are no "statements". Instead, there are just declarations (which you can think of as rewrite rules) and expressions, which yield values.

In the purely-functional context, "do this, then do that" can be represented nicely using monads.

Monads have two functions that obey [some laws](http://en.wikipedia.org/wiki/Monad_(functional_programming)#Monad_laws):

 1. A `point` method to "lift" an ordinary value into the monad. For example, for the `List` monad, `point` is the singleton constructor `_ :: Nil`. It lifts an individual value of type `A` into a list of type `List[A]`. For the `Option` monad, `point` is the constructor `Some.apply()`, which lifts an individual value of type `A` into an option of type `Option[A]`.
 2. A `bind` method to produce the *next* computation from the value of the *current* computation (called `flatMap` in Scala). For the `List` monad, this is just the method `List.flatMap(f: A => List[B]): List[B]`, and it calls `f` on every element to obtain lists of a different type, then merges all those lists into one list.

The `bind` / `flatMap` function encodes sequential computation, and you can see this fact just by looking at the type signature:

```scala
def flatMap[A, B](value: M[A])(f: A => M[B]): M[B]
```

The first parameter is the monad. In Scala, if the `flatMap` method were defined on the monad itself (like it is for `Option` and `List`), then you wouldn't pass it explicitly, but I've included it in the type signature for clarity.

The second parameter is a function that takes a value of type `A`.

From looking at the type signature, you know that in order for `flatMap` to call the function you pass, `flatMap` first has to produce a value of type `A`.

Since `flatMap` works for all types (it's *polymorphic* in type parameter `A`), it can't just produce a value of type `A` out of thin air!

Instead, to get a value of type `A`, `flatMap` first has to "evaluate" `M[A]` (where the meaning of "evaluate" depends entirely on the monad). Thus, `M[A]` represents a computation that might yield a value of type `A`.

This is the essence of sequential computation: `flatMap` is prevented by its type signature from calling the function `f` before it first produces an `A`!

Monads make it easy to handle state, effects, and lots of other things in a purely-functional way. Next, we'll take a look at effectful monads to show how that's done.

### Exercises

```scala
sealed trait Errorful[+A] {
  def flatMap[B](f: A => Errorful[B]): Errorful[B] = ???
  def map[B](f: A => B): Errorful[B] = flatMap(f andThen (Continue.apply _))
}
case class Error(message: String) extends Errorful[Nothing]
case class Continue[+A](value: A) extends Errorful[A]
````

 1. Given the above code, implement the method `flatMap`. Can you call the function `f` without first having a value of type `A`?
 2. Scala's `for` notation just compiles down to sequences of `flatMap` and `map`. Write a `for` comprehension that uses the `Errorful` monad you just wrote.

## Effectful Monads

For some monads `M[A]`, you can extract the `A` out of `M[A]` (for a `List`, you can perform the extraction with `List.head`, though it's unsafe because the list might be empty).

If you can't write an extraction function for some monad in a purely-functional way, then the monad is called *effectful*.

Effectful monads can do anything from printing to the screen to launching nuclear missiles!

In Haskell, the mother of all effectful monads is called `IO`, and writing code in the `IO` monad is very similar to writing code in Java or C or any other imperative language.

The IO monad is basically a giant data structure that describes what effects to perform. You can create, compose, manipulate, and pass these `IO` values around in a purely-functional way, and nothing effectful happens until when and if you call the impure extraction function.

In Scala, we can write our own IO monad in just a few lines of code:

```scala
class IO[A] private (run0: => A) {
  def run = run0
  
  def flatMap[B](f: A => IO[B]): IO[B] = IO(f(run).run)
  
  def map[B](f: A => B): IO[B] = flatMap(a => IO(f(a)))
}
object IO {
  def apply[A](v: => A): IO[A] = new IO(v)
}
```

> **Note:** This definition of the `IO` monad will blow the stack for deeply recursive code, but we'll eventually switch to Scalaz, which has a trampolined implementation that won't blow the stack.

Notice how the constructor for `IO` simply captures the effect in a *call-by-name* parameter, so it isn't actually evaluated, it's just stored as data inside the `IO` class.

We can use `IO` to wrap ordinary Scala functions like `readLine()` and `println()` to make them purely-functional:

```scala
def getLine: IO[String] = IO(readLine())
def putStrLn(v: String): IO[Unit] = IO(println(v))
```

We can then compose `IO` actions together using `flatMap` or `map`, or using Scala's `for` notation:

```scala
val rez: IO[Unit] = for {
  line <- getLine
  _    <- putStrLn("You wrote: " + line)
} yield Unit
```

> **Note:** The underscore in the notation `_ <- putStrLn(...)` just means we don't care about the value of `Unit` that will be produced by `putStrLn` (what would we do with it?).

As you can tell from this implementation, creating an expression of `IO[Unit]` doesn't actually perform any effects &mdash; you need to call `run` to do that and extract the final value:

```scala
rez.run
```

Although every Scala application using `IO` will have to call `run` at some point to interact with the outside world, you can always push that out to the `main` function of your application, so that 99.9999% of your application code is purely-functional.

We now have enough tools to write a purely-functional game loop!

### Exercises

 1. Using the IO monad built in this section, write a simple program that asks the user for his or her name, then prints out, "Hello, [name]". Don't cheat and print out the text literal "[name]", either! :-)
 2. Try to add a conditional, so that if the name string is empty, the program spits out, "You must have a name!" Hint: If you get stuck, try using `map` and `flatMap` instead of Scala's `for` notation.
 
## The Super Simple, Pure FP Game Loop

Here's the full game loop, including the dependency on `IO` and all helpers:

```scala
class IO[A] private (run0: => A) {
  def run = run0
  
  def flatMap[B](f: A => IO[B]): IO[B] = IO(f(run).run)
  
  def map[B](f: A => B): IO[B] = flatMap(a => IO(f(a)))
}
object IO {
  def apply[A](v: => A): IO[A] = new IO(v)
}
def getLine: IO[String] = IO(readLine())
def putStrLn(v: String): IO[Unit] = IO(println(v))

def gameLoop: IO[Unit] = for {
  input <- getLine
  _     <- putStrLn("So, you want to " + input + ", do you?")
  _     <- if (input == "quit") IO(Unit) else gameLoop
} yield Unit

gameLoop.run
```

This version of the game loop looks a lot like the non-FP version &mdash; but except for `gameLoop.run` at the end, this code is purely-functional.

We're off to a good start, but our game is dead boring! To solve that, we're going to have to talk *game state*.

### Exercises

 1. Sketch out a game by creating a Scala file with a `main` function that calls the `run` method of the `IO` action representing the entire game.
 2. In addition to a game loop, add entrance and exit messages to your game (e.g. "Welcome to SimpleRPG v1.0!" and "Goodbye!").

# Game State

To make our game interesting, we need to add non-player characters (NPCs), a model of the geography of the game world, a model of the player character, and a way to move around the game world and perform other activities.

Every command the user enters into the game has the potential to modify NPCs, the geography, items in the game world, or player attributes.

If you think that sounds like a lot of state, you're right!

There are many approaches to handling all this state in a purely-functional way, including *functional reactive programming* and *event-oriented programming*.

The one we're going to look at involves the *State monad*.

 > **Note:** The `State` monad is not always the most elegant tool for the job. But it's a good fallback when other techniques don't pan out, so it's a good tool to learn first.

## The State Monad

The essence of the State monad is very simple: it's a function that takes the old state and produces the new state, together with some value.

To orient you, take a look at the following hypothetical function `pop`, which "pops" the head off a queue (represented as a `List`):

```scala
def pop[A](queue: List[A]): (List[A], A) = (queue.tail, queue.head)
```

This function takes the old state (`List[A]'), and returns both the new state (`List[A]`), together with a value representing the head of the queue (`A`), bundled in a tuple.

That's basically all there is to the `State` monad.

For a given type of state (call it `S`), we can define the `State` monad as follows:

```scala
case class State[S, A](run: S => (S, A)) {
  def flatMap[B](g: A => State[S, B]): State[S, B] = State { (s0: S) =>
    val (s1, a) = run(s0)
    
    g(a).run(s1)
  }
  
  def map[B](f: A => B): State[S, B] = flatMap(a => State.point(f(a)))
}
object State {
  def point[S, A](v: A): State[S, A] = State(run = s => (s, v))
}
```

Notice how in the definition of `flatMap`, the state is threaded through both state functions. So the final state you get has been "modified" by two functions in sequence.

To see how we can use `State`, let's invent a simple `GameState`:

```scala
case class PlayerState(health: Int)
case class GameState(player: PlayerState)
```

We can now write a little helper function to update the player's health and returnthe new health value:

```scala
def updateHealth(delta: Int): State[GameState, Int] = State { (s: GameState) => 
  val newHealth = s.player.health + delta
  
  (s.copy(player = s.player.copy(newHealth)), newHealth)
}
```

Let's use this helper function to inflict some serious damage on the player:

```
val stateWithNewHealth: State[GameState, Int] = for {
  _         <- updateHealth(-10)  
  newHealth <- updateHealth(-100)
} yield newHealth
```

The result is a `State` action. If we want to perform the action, we have to "run" the `State`. How do we do that?

Remember, `State` is just a function from one state to another state and some value. So to "run" `State`, all we have to do is pass it an initial game state:

```scala
val stateWithNewHealth: State[GameState, Int] = for {
  _         <- updateHealth(-10)  
  newHealth <- updateHealth(-100)
} yield newHealth

stateWithNewHealth.run(GameState(PlayerState(health = 100)))
```

If you print out the final health, you'll find it's -10. Yep, we succeeded in killing off our player!

### Exercises

 1. Flesh out `GameState` more by adding a `GameMap` and an `x: Int` and `y: Int` location. Define the `GameMap` as a rectangular grid, where each cell can contain things like a name, description, list of characters, list of items, etc.
 2. Add a few helper functions that return `State` actions. For example, one helper could update the player's position by moving in some direction.

# The Stateful Game Loop

We've written two separate pieces of a game: the game loop, and some miscellaneous code for updating game state.

We used monads to solve both problems, so we have some code written with the `IO` monad, and other code written with the `State` monad.

If you try to combine the code, you'll find you get type errors:

```scala
for {
  input     <- readLine
  newHealth <- if (input == "fight") updateHealth(-10) else State.point(-1)
} yield newHealth
```

The reason why you get these type errors will be clearer if you explicitly write out `flatMap` and all the type signatures.

If we think of `IO` as representing the effect of IO, and `State` as representing the "effect" of updating state, then what we really want is a way to combine them both together into a *single* monad that captures *both* effects.

This way, we can both perform IO and update state in the same game loop.

It turns out that for theoretical reasons, we can't just take any two monads `M1` and `M2` and combine them into another monad `M3`. It's not possible!

However, there are a number of ways to combine monadic effects, ranging from `Free` monads to monad zippers and views to monad coproducts (and lots more!).

The particular approach we're going to look at involves *monad transformers*.

 > **Note:** Like the `State` monad, monad transformers are ubiquitous, well-studied, well-supported, and do a very respectable job of composing effects.

## Monad Transformers

A monad transformer is a special version of a monad that can stack its own effects on those of another monad. If you stack a monad transformer on another monad, the result forms a monad, which combines the effects of both monads together.

Not all monads have monad transformers. For example, the `IO` monad doesn't have a transformer version.

Fortunately, we're in luck: while `IO` doesn't come in a transformer flavor, the `State` monad does!

It's convention to suffix monad transformers with the letter 'T', which of course is short for "transformer".

Before we can write a `StateT`, we first have to be more precise about what a monad is, because `StateT` can only be stacked on something that is a monad.

To do that, we can write a simple little trait called `Monad`:

```scala
trait Monad[M[_]] {
  def point[A](a: => A): M[A]
  
  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]
  
  def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(a => point[B](f(a)))
}
```

If some type is a monad, for example, `List` or `Option`, then you can write an implementation of this trait for that type. Here are the implementations for `Option` and `IO`:

```scala
implicit val OptionMonad = new Monad[Option] {
  def point[A](a: => A): Option[A] = Some(a)
  
  def flatMap[A, B](ma: Option[A])(f: A => Option[B]): Option[B] = ma.flatMap(f)
}
implicit val IOMonad = new Monad[IO] {
  def point[A](a: => A): IO[A] = IO(a)
  
  def flatMap[A, B](ma: IO[A])(f: A => IO[B]): IO[B] = ma.flatMap(f)
}
```

Now we're all ready to write the monad transformer version of `State`:

```scala
case class StateT[M[_], S, A](run: S => M[(S, A)]) {  
  def flatMap[B](g: A => StateT[M, S, B])(implicit M: Monad[M]): StateT[M, S, B] = StateT { (s0: S) =>
    M.flatMap(run(s0)) {
      case (s1, a) => g(a).run(s1)
    }
  }
  
  def map[B](f: A => B)(implicit M: Monad[M]): StateT[M, S, B] = flatMap(a => StateT.point(f(a)))
}
object StateT {
  def point[M[_], S, A](v: A)(implicit M: Monad[M]): StateT[M, S, A] = StateT(run = s => M.point((s, v)))
}
```

Notice how the implementations of `flatMap` and `map` require that `M` be a monad by passing along an implicit `Monad` trait for that type. 

We use the methods of `M`'s monad to implement the monad for `StateT`.

In the case of our game, we need the state transformer to stack on `IO`. We can define a type synonym called `Game` which describes the resulting monad:

```scala
type Game[A] = StateT[IO, GameState, A]
```

We now have a monad, aliased `Game` above, which allows us to both perform IO and update state!

But before we write a new game loop that combines state with IO, we need to tackle one more subject: *monadic lifting*.

### Exercises

 1. Just like `State` has a monad transformer variant, so also does `Option`. Write `map` and `flatMap` for the type, `case class OptionT[M[_], A](run: M[A])`.
 2. Use the `OptionT` you just wrote to define a new monad that combines the nullability effect of `Option` with the `IO` effect.
 
## Monadic Lifting

Using our new `Game` monad, it's very easy to access and modify state. Here's the new `updateHealth` function written for the `Game` monad:

```scala
def updateHealth(delta: Int): Game[Int] = StateT[IO, GameState, Int] { (s: GameState) => 
  val newHealth = s.player.health + delta
  IO((s.copy(player = s.player.copy(health = newHealth)), newHealth))
}
```

The only difference is that we have to wrap the return of the state function in `IO`, because `StateT` is stacked on `IO`.

Fortunately, it's easy to write a helper function that does this wrapping for us:

```scala
def state[A](f: GameState => (GameState, A)): Game[A] = StateT[IO, GameState, A](s => IO(f(s)))
```

This helper function makes it possible to define `updateHealth` in a way that looks just like the old definition:

```scala
def updateHealth(delta: Int): Game[Int] = state[Int] { (s: GameState) => 
  val newHealth = s.player.health + delta
  (s.copy(player = s.player.copy(health = newHealth)), newHealth)
}
```

What if we want to perform some IO in our game monad? The naive attempt will fail with type errors:

```scala
for {
  input  <- readLine
  health <- updateHealth(100)
} yield health
```

The code won't compile because `readLine` is still in the `IO` monad, not the `Game` monad.

Remember, `Game` is `StateT` stacked on top of `IO`. To execute an `IO` action inside `Game`, then we need some way of "lifting" that `IO` action into `Game`.

We know what the type signature should look like:

```scala
def liftIO[A](io: IO[A]): Game[A] = ???
```

How about the implementation? Turns out to be simple:

```scala
def liftIO[A](io: IO[A]): Game[A] = StateT[IO, GameState, A](s => io.map(a => (s, a)))
```

We just pass along the original state unmodified (since an `IO` action won't affect the game state), bundled with the value computed by the `IO` action.

How can we use this new function? Simply like so:

```scala
for {
  input  <- liftIO(getLine)
  health <- updateHealth(100)
} yield health
```

And there we have it! Two monadic effects combined into one monad.

We can now both perform IO and update state from the same game loop!

Since we're only using two IO functions, it makes sense to define versions of them for the `Game` monad, as follows:

```scala
def getLineG: Game[String] = liftIO(getLine)
def putStrLnG(v: String): Game[Unit] = liftIO(putStrLn(v))
```

Now we can define a non-trivial game loop that allows user input to change the game world:

```scala
def gameLoop: Game[Unit] = for {
  _     <- putStrLnG("What would you like to do?")
  input <- getLineG
  _     <- putStrLnG("So, you want to " + input + ", do you?")
  _     <- if (input == "fight") for {
             newHealth <- updateHealth(-10) 
             _         <- putStrLnG("Your new health is: " + newHealth)
             _         <- gameLoop
           } yield Unit
           else if (input == "quit") liftIO(IO(Unit))
           else for {
             _ <- putStrLnG("I'm sorry, I don't understand your command.")
             _ <- gameLoop
           } yield Unit
} yield Unit

gameLoop.run(GameState(PlayerState(100))).run
```

Pretty cool, huh? At this point, we're literally one step away from having all the tools necessary to write a clean, purely-functional game.

That one step involves making updating state simple, composable, and very powerful, in a way you can't match with procedural programming.

### Exercises

 1. Modify the previously introduced game loop to tell the user if and when they die (i.e. their health goes below 0). If the player dies, exit the game.
 2. Add the player location to the game state (if you haven't already), and allow the player to go north, east, west, and south (printing out the new location each time).

# Lenses

There's some ugly boilerplate hidden in the `updateHealth` function, which you probably had to recreate if you completed the exercises in the last section:

```scala
(s.copy(player = s.player.copy(newHealth)), newHealth)
```

Notice how to update the health, we have to copy the game state, and copy the player state, and then combine them together.

For just updating the player health, this isn't too awful. But it gets progressively worse as you dig deeper and deeper into `GameState` and have to update more and more data.

Fortunately, FP has an answer, and it's called *lenses*.

## A Simple Lens

A lens is a combination of a *functional getter* and a *functional setter*. Together, they give you a way of manipulating a subset of data inside a data structure (which I'll sloppily call a *field*).

For a data type `S`, and "field" type `A`, a simple lens can be defined as follows:

```scala
case class Lens[S, A](get: S => A, set: (S, A) => S)
```

The getter takes an instance of the data type, and returns the field value. The setter takes both an instance of the data type, and the new value, and returns a new instance of the data type.

Here's how we would define a `Lens` for the `health` field of `PlayerState`:

```scala
val health = Lens[PlayerState, Int](_.health, (s, a) => s.copy(a))
```

And here's another lens for the `player` field of `GameState`

```scala
val player = Lens[GameState, PlayerState](_.player, (s, a) => s.copy(a))
```

Using the lens is really simple:

```scala
val oldPlayer = PlayerState(100)
val health = health.get(oldPlayer)
val newPlayer = health.set(oldPlayer, health + 200)
```

They great thing about lenses is that they compose in lots of useful ways. For example, we can define a new method `|->` on lens that allows you to dig deeper into a structure:

```scala
case class Lens[S, A](get: S => A, set: (S, A) => S) {
  def |-> [B](that: Lens[A, B]): Lens[S, B] = Lens(
    get = that.get compose get,
    set = (s, b) => set(s, that.set(get(s), b))
  )
}
```

This method allows us to combine the previously defined `player` and `health` lens into another lens that can dig into the `player` field of a `GameState`, dig further into the `health` field of the `PlayerState`, and get or set the value of `health`!

Here's how we define the composite lens:

```scala
player |-> health
```

That's just as simple as `player.health`!

Look at how simple and clean our `updateHealth` function becomes:

```scala
def updateHealth(delta: Int): Game[Int] = state[Int] { (s: GameState) => 
  val newHealth = (player |-> health).get(s) + delta
  
  (player |-> health).set(s, newHealth) -> newHealth
}
```

We can further generalize this approach by creating a helper function that's aware of lenses and our `Game` monad:

```scala
def update[A](lens: Lens[GameState, A])(f: A => A): Game[A] = state[A] { (s: GameState) =>
  val newValue = f(lens.get(s))
  
  lens.set(s, newValue) -> newValue
}
```

Now updating health, *or any other field regardless of how deeply nested it is*, becomes as simple as follows:

```scala
def updateHealth(delta: Int): Game[Int] = update(player |-> health)(_ + delta)
```

That's powerful (at this point, we don't even need the helper function), and it just scratches the surface of what these abstractions are capable of.

We have all the tools we need to build a purely-functional game. So let's put them to use!

### Exercises

 1. Write a generic `get` function whose signature is `def get[A](lens: Lens[GameState, A]): Game[A]`.
 2. Define lenses for whatever other data you have in your `GameState` (as well as for all substructures thereof).

# Tying it all Together

You now have a choice: you can take the code you've built up to this point (you *have* been completing the exercises, right?), or you can jump ship and use the code in the `src` directory.

The code in the `src` repository builds on the material presented thus far (for example, adding basic command parsing so you don't have to worry about that), and uses the Scalaz and Monocle libraries for super-powered versions of some of the classes built here (`Monad`, `StateT`, `IO` AKA `Task`, `Lens`, etc.).

You can launch the existing game very simply for testing:

```
31 introgame % sbt                                                                                                                                     2014-04-15 21:23:29 John ttys002
Detected sbt version 0.13.0
Starting sbt: invoke with -help for other options
Using /Users/John/.sbt/0.13.0 as sbt dir, -sbt-dir to override.
[info] Loading project definition from /Users/John/Documents/github/lambdaconf/introgame/project
[info] Set current project to introfp (in build file:/Users/John/Documents/github/lambdaconf/introgame/)
[info] Defining */*:console::traceLevel, */*:consoleProject::traceLevel and 2 others.
[info] The new values will be used by no settings or tasks.
[info] 	Run `last` for details.
[info] Reapplying settings...
[info] Set current project to introfp (in build file:/Users/John/Documents/github/lambdaconf/introgame/)
> run
[info] Compiling 1 Scala source to /Users/John/Documents/github/lambdaconf/introgame/target/scala-2.10/classes...
[warn] there were 1 deprecation warning(s); re-run with -deprecation for details
[warn] one warning found
[info] Running introgame.Main 
What would you like to do now?
```

Now let's get to work building that game!

## Exercises

 1. Give the player the ability to pick up and put down items in any location (e.g. "pick up rusty sword", "drop rusty sword").
 2. Add an `NPC` class that models a non-player character. Extend `Cell` to hold a list of `NPC`s.
 3. Classify `NPC`s as "friendly" or "hostile" (you can be more elaborate but this is the bare minimum). If the player looks around, describe friendly and hostile `NPC`s differently.
 4. Allow the player to "fight" NPCs, exchanging damage in a deterministic (i.e. non-random) fashion.
 5. Allow items to modify the attributes of characters (player or non-player) as well as modify the outcome of a fight.
 6. BONUS - HARD: Add a deterministic random effect so you can incorporate randomness into fights. Note: You can do this by sticking the state information in `GameState`, or you can add a `RandomT` monad transformer in the stack (or if you want even more of a challenge, you can add a free algebra describing randomness, and use a coproduct functor to combine that with the existing IO free algebra).
 7. BONUS - HARDER: Generate events for all actions that happen, whether to player, the environment, an item, or a NPC, and store these events somewhere for later reference. Refactor the game so that player commands generate events, which in turn might generate other events, etc., and produce state updates through [monoid actions](http://en.wikipedia.org/wiki/Semigroup_action). You will find it much easier to encode game logic using this approach.

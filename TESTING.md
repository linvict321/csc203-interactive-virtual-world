# How to read the tests in [WorldTests.java](src/WorldTests.java)

This document explains how you should interpret the tests in `WorldTests.java`.
The tests are designed to capture the expected behaviour of the VirtualWorld in a way that should "survive" your refactoring changes.

**That is, the tests should continue to pass as you work through the refactoring steps.**

This document is meant to help you understand how to interpret the tests, so that you can use them as a guide for refactoring or debugging.

## Structure of the tests

The `makeSave` helper method in `WorldTests.java` is used to create a string representation of the world state—this world state is given to `VirtualWorld::headlessMain` to run the simulation for a few steps.

Each test case uses `makeSave` to create an initial world state, runs the simulation for a few steps, and then checks that the final world state matches the expected state.

Let's go through a couple of examples.

## Example 1: `testTreeAnimation`

This is the `testTreeAnimation` test case:

```java
@Test
public void testTreeAnimation() {
  String sav = makeSave(1, 1, "tree mytree 0 0 0.250 100.0 1");
  List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);

  assertEquals(1, entities.size(), "Expected the following entity to be present: mytree 0 0 20");
  assertEquals("mytree 0 0 20", entities.get(0));
}
```

Let's work through it line by line.

First, we create a world with a single tree entity:

```java
String sav = makeSave(1, 1, "tree mytree 0 0 0.250 100.0 1");
```

The line above is saying:

- The world has 1 row and 1 column (i.e., it's a 1x1 grid).
- There is one entity in the world: a tree named `mytree`, located at (0, 0).
- The tree has an animation period of 0.250, an action period of 100.0, and a health of 1.

Next, we run the simulation for 5 steps:

```java
List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 5);
```

The `entities` list now contains the string representations of all "id'd" entities in the world after 5 steps of simulation. (Only entities with IDs are included in this list.)

Finally, we check that the final state of the world contains exactly one entity, and that this entity is `mytree` at (0, 0), and the tree has advanced its animation frame to 20:

```java
assertEquals(1, entities.size(), "Expected the following entity to be present: mytree 0 0 20");
assertEquals("mytree 0 0 20", entities.get(0));
```

## Example 2: `testDudePathing`

This is the `testDudePathing` test case:

```java
@Test
public void testDudePathing() {
    String sav = makeSave(15, 20, "dude mydude 10 9 1.000 100.0 1", "obstacle  11 11 1.126", "obstacle  10 12 1.126", "obstacle  9 11 1.126", "tree  10 14 0.250 1.150 2", "tree  0 0 0.250 1.150 2", "house  10 8");
    List<String> entities = VirtualWorld.headlessMain(new String[]{sav}, 8);

    assertEquals(1, entities.size(), "Expected the following entity to be present: mydude 10 11 0");
    assertEquals("mydude 10 11 0", entities.get(0));
}
```

This test sets up a slightly more complex world:

- The world is a 15x20 grid.
- There is a "dude" entity named `mydude` at (10, 9) with an action period of 1.000, an animation period of 100.0, and a limit of 1.
- There are obstacles at (11, 11), (10, 12), and (9, 11), all with animation periods of 1.126.
- There are trees at (10, 14) and (0, 0), both with animation periods of 0.250, action periods of 1.150, and health of 2.
- There is a house at (10, 8).

We could visualize this world as follows (D = dude, X = obstacle, T = tree, H = house, . = empty space):

```txt
     0  1  2  3  4  5  6  7  8  9 10 11 12 13 14
 0   T  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 1   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 2   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 3   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 4   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 5   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 6   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 7   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
 8   .  .  .  .  .  .  .  .  .  .  H  .  .  .  .
 9   .  .  .  .  .  .  .  .  .  .  D  .  .  .  .
10   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
11   .  .  .  .  .  .  .  .  .  X  .  X  .  .  .
12   .  .  .  .  .  .  .  .  .  .  X  .  .  .  .
13   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
14   .  .  .  .  .  .  .  .  .  .  T  .  .  .  .
15   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
16   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
17   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
18   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
19   .  .  .  .  .  .  .  .  .  .  .  .  .  .  .
```

According to the rules of the simulation, the dude should try to move toward the nearest tree (at (10, 14)).
However, because the dude has a dumb pathing strategy, it will move down to (10, 10) first, then to (10, 11), and then get stuck because of the obstacles.

So that is what the test checks for: it checks that after 8 steps of simulation, the dude is at (10, 11).

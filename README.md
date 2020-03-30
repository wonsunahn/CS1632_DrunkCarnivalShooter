# Exercise 5 - Static Analysis

In this exercise, you will use three static analysis tools to test a program: a
bug finder named SpotBugs, a linter named CheckStyle, and a model checker named
Java Pathfinder (JPF).  SpotBugs and CheckStyle work in similar ways in that
both look for patterns that are either symptomatic of a bug (former) or are bad
coding style (latter).  So we will look at them together first.  Later we will
look at JPF which is much more rigorous in proving a program correct.

* IMPORTANT: You need Java 8 (1.8.0.231, preferably) to run JPF.  Make sure you have the correct Java version by doing "java -version" and "javac -version" before going into the JPF section.

## SpotBugs and CheckStyle

SpotBugs: https://spotbugs.github.io/  
CheckStyle: https://checkstyle.sourceforge.io/  

Try running both tools on a Sieve of Eratosthenes program, and then fix any
issues found.  This will allow you to see what kinds of bugs a static analysis
program can find (and which ones it cannot).

The Sieve of Eratosthenes is an ancient way of finding all prime numbers below
a specific value.  For details on the algorithm itself, please see
https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes.

This program accepts one integer value and will tell you all prime numbers up
to and including the passed-in value.  However, there are some problems hidden
in the code.  You are going to use SpotBugs and CheckStyle to find and fix
them.  Some problems are actual defects and some are just bad or confusing
code.

I have prepared scripts to run or test the program.  First cd into the Sieve
directory before executing the scripts.

To run the program (for Windows users):
```
$ run.bat [Integer]
```
To run SpotBugs:
```
$ runSpotbugs.bat
```
To run CheckStyle:
```
$ runCheckstyle.bat
```

For Mac or Linux users, please run the corresponding .sh scripts.

* There is a GUI for SpotBugs if that is what you prefer.  You can launch the GUI by using the following command:
```
$ java -jar spotbugs-4.0.0-beta4/lib/spotbugs.jar
```
The following link contains a short tutorial on how to use the GUI:
https://spotbugs.readthedocs.io/en/latest/gui.html

If all goes well you should see the following output:

```
$ java Sieve 100
Sieve of Eratosthenes
> 2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97

$ java Sieve 14
Sieve of Eratosthenes
> 2 3 5 7 11 13
```

Note that there is a bug in the logic of the code that is not caught by either
SpotBugs or CheckStyle that will prevent you from getting the above output.  For example, the argument 100 will show the following:
```
$ java Sieve 100
Sieve of Eratosthenes
> 2 3 5 7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89 97 99
```
Locate the problem by reviewing the code and fix the problem.

### Lessons on Pattern Matching

Both linters (CheckStyle) and bug finders (SpotBugs) work by pattern matching.  Pattern matching can be good at finding simple bugs that are recurrent across projects and can even catch errors in your documentation.  What they are not good for is finding problems in your program logic (as seen above).  For that, you need dynamic testing that actually executes the program to check program behavior.  Or, you can use model checking that the proves that certain properties hold for all inputs (see below).

## Java Pathfinder (JPF)

Java Pathfinder (JPF): 
https://github.com/javapathfinder/jpf-core/wiki, http://javapathfinder.sourceforge.net/

Java Pathfinder is a tool developed by NASA to model check Java programs.  It
works in exactly the same way we learned in class: it does an exhaustive and
systematic exploration of program state space to check for correctness.

### JPF on Rand

Let's first try out JPF on the example Rand program we saw on the Formal Verification lecture
slides:  

<img src="jpf.png" width="50%" height="50%">

First cd into the DrunkCarnivalShooter directory before executing the scripts.

To run the Rand program (for Windows users):
```
$ runRand.bat
```
To run JPF with Rand:
```
$ runJPFRand.bat
```

For Mac or Linux users, please run the corresponding .sh scripts.

When you run Rand with JPF, you can see from the screen output that it goes through all possible states, thereby finding the two states with division-by-0 exception errors (I configured JPF to find all possible errors).  Also, when you run JPF, you will get a file named jpf-state-space.dot that is a graph representation of the states that you have traversed.  The file is in DOT format that is viewable from the Graphviz viewer.  There is an online version here: http://graphviz.it/.  All you have to do is open the jpf-state-space.dot on a text editor and copy-paste it to the website.  Then, you should see a diagram very similar to the one shown above.  Don't pay attention to the source code line numbers.  There seems to be a bug in the JPF source code line calculation code.

So, now we know that there are two defective states, how do we debug?  You will see that JPF has generated a trace file named [Rand.trace](Rand.trace) of all the choices it had made to get to that state.  You will see two traces since there are two defective states.  Pay attention to "cur" value of each Random.nextInt invocation (that is the choice JPF has made for that invocation).  The first trace shows values of 0, 2 for a, b and the second trace shows cur values of 1, 1 for a, b.  These are exactly the values that would cause a division-by-0 exception at c = a/(b+a -2).  In this way, the trace file lets you easily trace through the code to get to the defective state.

### JPF on DrunkCarnivalShooter

Now let's try using JPF to debug and verify a real program.  DrunkCarnivalShooter is a simple text-based game where the player goes to a carnival shooting range and tries to win the prize by shooting all 4 provied targets.  The player can designate what target to shoot for pressing 0-3.  But since the player is drunk, there is an equal chance of the player shooting left or right as shooting straight.  Refer to the file [sample_run.txt](sample_run.txt) for an example game play session.

To run the DrunkCarnivalShooter program (for Windows users):
```
$ run.bat
```

For Mac or Linux users, please run the corresponding .sh scripts (for this one any following).

You may not notice the bug after playing the game once or twice due to the randomness of the shooting but it is definitely in there.  Now let's try running the JPF tool:
```
$ runJPF.bat
```

The JPF tool also doesn't show any errors but that is because DrunkCarnivalShooter takes user input and JPF does not know how to handle it.  Just like random numbers, we would like to have JPF to go over every possibility.  We will do that by using the Verify API.  But in order to be able to use that feature, we first have to import a library at the top of DrunkCarnivalShooter.java:
```
import gov.nasa.jpf.vm.Verify;
```
Now instead of scanning user input using the following statement:
```
int t = scanner.nextInt();
```
Exhaustively generate all possible inputs using the Verify API:
```
int t = Verify.getInt(0, 3);
```
* Invoke Verify instead of Scanner only when a commandline argument "test" is passed to program.  The "test" argument will put the program in test mode and not in play mode.  You can see "test" is already configured as the commandline argument in the target.args entry in [DrunkCarnivalShooter.jpf](DrunkCarnivalShooter/DrunkCarnivalShooter.jpf)), which is the JPF configuration file use when [runJPF.bat](DrunkCarnivalShooter/runJPF.bat) is invoked.

The above will direct JPF to generate 4 states each where t is set to 0, 1, 2, or 3 respectively.  Then it will systematically explore each state.  If you wish, you can test a larger set of numbers beyond 0-3.  You can even test strings.  It is just going to generate more states and take longer (the flipside being you will be able to model check your program against a larger set of inputs).

Now let's try running runJPF.bat one more time.  This will show an error state with an exception:
```
====================================================== error 1
gov.nasa.jpf.vm.NoUncaughtExceptionsProperty
java.lang.ArrayIndexOutOfBoundsException: -1
        at java.util.ArrayList.elementData(java/util/ArrayList.java:422)
        at java.util.ArrayList.get(java/util/ArrayList.java:435)
        at DrunkCarnivalShooter.isTargetStanding(DrunkCarnivalShooter.java:83)
        at DrunkCarnivalShooter.takeDownTarget(DrunkCarnivalShooter.java:75)
        at DrunkCarnivalShooter.shoot(DrunkCarnivalShooter.java:62)
        at DrunkCarnivalShooter.main(DrunkCarnivalShooter.java:97)
```
Use the generated DrunkCarnivalShooter.trace trace file in the same way you used Rand.trace to find the input value(s) and the random value(s) that led to the exception.

Once you fix these bugs, try running runJPF.bat one more time.  Not that you have fixed the buggy state JPF runs for much longer.  In fact, JPF is going to fall into an infinite loop and generate an infinite number of states (observed by the ever increasing Round number).
```
...
Round #20:
  ||    ||    ||
Choose your target (0-3):
You aimed at target #0 but the Force pulls your bullet to the left.
You miss! "Do or do not. There is no try.", Yoda chides.
Round #21:
        ||    ||    ||
Choose your target (0-3):
You miss! "Do or do not. There is no try.", Yoda chides.
... (to infinity)
```
There is no theoretical limit to the number of rounds a player can play, hence the state explosion.  How can I deal with this explosion and still verify my program?

We have to somehow narrow down the amount of state we test, or we will be forced to but JPF off after testing only a limited set of rounds.  Let's say the state that we are really interested in relation to the specifications is the state of the 4 targets.  Now if you think about it, the 4 targets can only be in a handful of states: 2 * 2 * 2 * 2 = 16 states (standing or toppled for each).  And this is true no matter how many rounds you go through.  The only thing that constantly changes every round is the round number --- and that is the culprit leading to the state explosion.  The round number is not something we are interested in verifying right now.  So, let's filter that state out!

Import the appropriate JPF library at the top of DrunkCarnivalShooter.java again:
```
import gov.nasa.jpf.annotation.FilterField;
```
And now, let's annotate roundNum such that it is filtered out:
```
@FilterField private static int roundNum;
```

Now if we run runJPF.bat again, JPF will only go up to Round #5 and stop and declare "no errors detected".
```
...
Round #5:

Choose your target (0-3):

====================================================== results
no errors detected
```

How is it able to do this?  This is because all 16 states can be covered in the space of 5 rounds.  Any further number of rounds will result in a match with an already visited state and therefore will not need to be explored.  You can again view the jpf-state-space.dot file on http://graphviz.it/ and see for yourself that a lot of transitions ended up in the same state (match).

Now, are we done?  Actually not.  JPF declares no errors but when you actually try playing the game using run.bat, you will notice that often the game will end prematurely or continue even when it should have finished.  That is because the model checker only checked no exceptions are thrown during the course of the game but did not check any other specification.  Looking at the defect, it seems the specification that the game should end when there are no remaining targets has been violated.  So let us check an invariant by inserting an assertion at the end of the shoot(int) method:

```
assert remainingTargetNum == targets.stream().filter(p -> p == true).count();
```

This checks at every round that remainingTargetNum is equal to the actual number of remaining targets.  It uses Java lambda expression to count the number of trues in the targets list, in order to fit it inside a single assert.  Also, don't forget to add the -ea (enable assert) option to the JVM inside the runJPF.bat file:

```
java -ea -jar jpf-core/build/RunJPF.jar +site=./jpf-core/site.properties DrunkCarnivalShooter.jpf
```

Now if we run runJPF.bat again, we should see the assertion fire.  Remove the defect, again with the help of the DrunkCarnivalShooter.trace trace file.  After debugging, you should see the "no errors detected" message again.  Now try playing the actual game using the run.bat command.  It should now run smoothly with no issues.

### Lessons on Model Checking

What have we learned?  We learned that a model checker such as JPF can guarantee correctness for the given set of inputs.  But in order to do that, you often need to limit the amount of state JPF monitors to prevent state explosion.  Also, the guarantee of correctness depends heavily on how much of the program specification you have encoded into the source code in the form of asserts.  If there are no asserts, JPF can only check only basic things such as no exceptions.

## Submission

Please do a Text Submission to Courseweb with a link to the GitHub repository where you stored it, along with names of all group members.

Example:

John Doe  
Jane Doe  
https://github.com/wonsunahn/CS1632_Fall2019/tree/master/exercises/5

Please submit by Friday (11/15) 11:59 PM to get timely feedback.

IMPORTANT: Please keep the github private and add the following users as collaborators: nikunjgoel95, wonsunahn

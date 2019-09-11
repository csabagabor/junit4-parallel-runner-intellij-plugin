# JUnit4 Parallel Runner - Intellij IDEA plugin

>Note: it is available on Intellij Marketplace: https://plugins.jetbrains.com/plugin/12959-junit4-parallel-runner

## About
Intellij IDEA plugin that allows you to run `JUnit4` tests in parallel (using **multiple CPU cores/threads**). Uses the **Fork/Join Framework** from Java 7 so it can fully utilize the power of the CPU.

## Implementation
This plugin was built with portability in mind, so it contains three different runners in case some of them fail. A *Runner* is a class/several classes that add additional functionality to JUnit tests, in this case, they run them in parallel. The problem was that the bundled JUnit plugin is not extensible enough to be able to develop a really simple solution which works in every case and with every Intellij IDEA version. For example, in the latest version, the package in which the class `JUnitStarter` resides is: `com.intellij.rt.execution.junit`, but it has already been changed to `com.intellij.rt.junit` in the official Github [repository](https://github.com/JetBrains/intellij-community/blob/master/plugins/junit_rt/src/com/intellij/rt/junit/JUnitStarter.java)....

>Note: even though the plugin contains several runners, I try to update the code of every runner which fails to work with a major Intellij IDEA version. For example, this happened in version 2019.3 where the first runner fails so please check out the `version/2019.3` branch.

#### First Runner
Just patches the JavaParameters, to use a custom runner instead of `JUnitStarter`. Also, I included the full source code of `JUnitStarter` class in the plugin(doesn't seem like a good idea, right?), just to prevent using reflection and not to depend on its implementation in different IDEA versions. This runner will be tried first, if it fails with any kind of error, the second runner will be started etc.
This runner is simple enough but sensitive to changes in the source code of the bundled junit plugin.

#### Second runner
Parses the `JUnitConfiguration` instance and builds a list with the selected tests and saves them to a text file named `jun_par_tes.tmp` in the OS specific temp directory. Also it modifies the JUnitConfiguration instance(patches the `ParametersList` from `JavaParameters`) and redirects the runnable test class to a custom class(`ParallelSuiteClasses`) that is annotated with `ParallelSuite`(see  [JUnit Toolbox](https://github.com/MichaelTamm/junit-toolbox)).

#### Third runner
Similar to the second runner, but instead of manually parsing the `JUnitConfiguration` instance and saving the selected tests in a file, uses the fact that the default JUnit runner creates a .tmp file itself to store the selected tests before calling `JUnitStarter`. So, using the `JUnit4TestRunnerUtil.buildRequest(..)` method, it gets the list with the selected tests and adds them to the `ParallelSuite`.

### Alternative solutions
A very good and simple alternative solution would be to use `ASM` or `ByteBuddy` to modify the class `JUnit4IdeaTestRunner` during runtime and add the following code to it after `Runner testRunner = request.getRunner()` in the method `startRunnerWithArgs(..)`:
```java
if (testRunner instanceof ParentRunner) {
     ParentRunner parentRunner = (ParentRunner) testRunner;
     parentRunner.setScheduler(new ParallelScheduler());
}
```

### Screenshots:


![image](https://user-images.githubusercontent.com/37183688/64076106-fe17aa00-ccc8-11e9-9c31-d4900f8b4f5c.png)



>Note: The more tests you have, the better the speedup will be.
Let's say you have 1000 tests which need 40 seconds to run and you have a 6 core CPU, 
using this plugin you can reduce the running time of tests by approx. 4x (down to 10 seconds).
Also, no parallelizing is done when running a single method from a single class(the single 
method is executed on a single thread) 

>Warning: Some code is not meant to be run in parallel (sometimes this is the case with integration tests).
Also, if you have tests which are not independent of each other, they might fail. 

### Credits:

- Icons for this plugin were made by [Smashicons](https://www.flaticon.com/authors/smashicons) from [flaticon](https://www.flaticon.com)
- Also, the plugin includes code from: [JUnit Toolbox](https://github.com/MichaelTamm/junit-toolbox)

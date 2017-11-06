# Spectrum Auction Test Suite: Optimization API (sats-opt)

### Getting Started
This getting started guide helps you to set up `sats-opt` to use it as a library in your own java project. 
`sats-opt` extends [`sats-core`](https://github.com/spectrumauctions/sats-core) and adds winner determination problem (WDP) solvers to the list of provided features.
As the WDP solvers require external libraries, we recommend to just use `sats-core`, should you not (yet) want to use the WDP solvers. An upgrade is easily possible any time.

Should you not want to use SATS it in your own java code, but want to create problem instances in a simpler way, you may want to use the command line tool (`sats-clt`) or the web application (`sats-web`) instead. See 
http://spectrumauctions.org for more information.

##### How to use sats-opt:
Since sats-opt is available on Maven Central, it can be directly included as a dependency:

```
<dependency>
    <groupId>org.spectrumauctions</groupId>
    <artifactId>sats-opt</artifactId>
    <version>0.5.2</version>
</dependency>
```

If you prefer to include sats-opt without maven, we provide a prepared jar coupled to the [release](https://github.com/spectrumauctions/sats-opt/releases).

##### Prerequisites

* `Java 1.8` (or later)
* Highly recommended: `CPLEX` 
  * When using `maven`, include your local `cplex.jar` in your maven repository by the following command:
    ```
    mvn install:install-file -Dfile=<path-to-cplex-jar> -DgroupId=cplex -DartifactId=cplex -Dversion=12.6 -Dpackaging=jar
    ```
  * Make sure to add your native CPLEX binaries to your `PATH` (Windows) / `LD_LIBRARY_PATH` (Unix) environment variable so sats-opt can find it! This may be done automatically when installing CPLEX. 
  * If you don't provide a CPLEX JAR, sats-opt will use LPSolve as a solver, which is considerably less performant.

### Code examples
Code examples can be found in the
[`org.spectrumauctions.sats.opt.examples`](https://github.com/spectrumauctions/sats-opt/tree/master/src/test/java/org/spectrumauctions/sats/opt/examples)
package in the test folder of this repository.

### Bug Reports, Feature Requests and Contribution Guidelines
We are grateful for bug reports and other feedback about SATS and are welcoming everyone to contribute to the project, too. 
If you do have a bug report or have code you want to add to SATS, please follow the following guidelines.
* To report bugs or to propose new features, please open a new issue in this repositories issue tracker. 
* To contribute code, please open a pull request to merge into develop. Small bugfixes will be accepted and merged very quickly. 
For larger contributions, we recommend to discuss and plan the contribution in the issue tracker beforehand.

A list of contributors can be found at 
https://github.com/spectrumauctions/sats-core/blob/master/CONTRIBUTORS.md

### Copyright
Copyright by
* Michael Weiss
* Sven Seuken and the University of Zurich
* Ben Lubin and the Boston University

SATS is licenced under AGPL. You find the license in this repository. 

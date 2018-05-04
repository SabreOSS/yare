<!--
  MIT License

  Copyright 2018 Sabre GLBL Inc.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 -->

# Contributing to _yare_

You have found a bug or you have an idea for a cool new feature? Contributing code is a great way to give something
back to the open source community. Before you dig right into the code there are a few guidelines that we need
contributors to follow so that we can have a chance of keeping on top of things.

## Getting Started

* Make sure you have a [GitHub account](https://github.com/signup/free).
* If you're planning to implement a bigger feature it makes sense to discuss your changes first.
  Please create an issue and explain the intended change.
  This way you can make sure you're not wasting your time on something that isn't considered to be in
  _yare_ scope.
* Submit a ticket for your issue, assuming one does not already exist.
  * Clearly describe the issue including steps to reproduce when it is a bug.
  * Make sure you fill in the earliest version that you know has the issue.
* Fork the repository on GitHub.

## Installation and setup

### PC setup

* Install latest [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Install [Maven 3.3+](https://maven.apache.org/download.cgi)
  * set system variable __MAVEN_HOME__ to location of Maven root directory
  * set system variable _MAVEN_ to _%MAVEN_HOME%\bin_
  * add _%MAVEN%_ variable to system's _PATH_
* Install [Git](https://git-scm.com/downloads) and add bin directory to system's path
* Install IDE of your choice

### Project setup

#### Clone

To clone Yet Another Rules Engine please use command below:

```bash
git clone https://github.com/SabreOSS/yare.git
```

#### Build

To build Yet Another Rules Engine please use command below:

```bash
 mvn clean install
```

## Making Changes

* Create a topic branch from where you want to base your work (this is usually the master/trunk branch).
* Make commits of logical units.
* Respect the original code style:
  * Only use spaces for indentation.
  * Create minimal diffs - disable on save actions like reformat source code or organize imports.
    If you feel the source code should be reformatted create a separate PR for this change.
  * Check for unnecessary whitespace with git diff --check before committing.
* Make sure your commit messages are in the proper format.
* Make sure you have added the necessary tests for your changes.
* Run all the tests with `mvn clean verify` to assure nothing else was accidentally broken.

## Submitting Changes

* Sign the [Contributor License Agreement][cla] if you haven't already.
* Push your changes to a topic branch in your fork of the repository.
* Submit a pull request to the repository.

## Additional Resources

+ [Contributor License Agreement][cla]
+ [General GitHub documentation](https://help.github.com/)
+ [GitHub pull request documentation](https://help.github.com/send-pull-requests/)

[cla]:https://cla-assistant.io/

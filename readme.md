JUnit Extensions
====

> With thanks and appreciation to to the authors of [JUnit5](https://github.com/junit-team/junit5/graphs/contributors).

| Branch  | Status | Coverage | Maven Central | Javadocs |
| --------| ------ | -------- | ------------- | -------- |
| Master  | [![Build Status](https://travis-ci.org/glytching/junit-extensions.svg?branch=master)](https://travis-ci.org/glytching/junit-extensions) | [![Coverage Status](https://coveralls.io/repos/github/glytching/junit-extensions/badge.svg?branch=master)](https://coveralls.io/github/glytching/junit-extensions?branch=master) | - | - |
| v1.1.0  | [![Build Status](https://travis-ci.org/glytching/junit-extensions.svg?branch=1.1.0)](https://travis-ci.org/glytching/junit-extensions/branches) | [![Coverage Status](https://coveralls.io/repos/github/glytching/junit-extensions/badge.svg?branch=1.1.0)](https://coveralls.io/github/glytching/junit-extensions?branch=1.1.0) | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.glytching/junit-extensions/badge.svg?style=flat)](http://repo1.maven.org/maven2/io/github/glytching/junit-extensions/1.1.0/) | [![Javadocs](http://www.javadoc.io/badge/io.github.glytching/junit-extensions.svg)](http://www.javadoc.io/doc/io.github.glytching/junit-extensions/1.1.0) |
    
There have been discussions amongst the JUnit community (see [this](https://github.com/junit-team/junit5/issues/169) and [this](https://github.com/junit-team/junit5-samples/issues/4)) about providing official [JUnit Jupiter Extensions](http://junit.org/junit5/docs/current/user-guide/#extensions) for the more popular [JUnit4 Rules](https://github.com/junit-team/junit4/wiki/Rules). The upshot of these discussions seems to be enabling [limited support for JUnit4 rules on JUnit5](http://junit.org/junit5/docs/snapshot/user-guide/#migrating-from-junit4-rule-support).

In a bid to fill a gap, this library provides implementations of some extensions which may prove useful until the suite of extensions provided by JUnit grows (or, in case it does not grow).

Detailed usage instructions for each of the extensions are available in [the docs](https://glytching.github.io/junit-extensions/) but briefly ...

###### ExpectedExceptionExtension

Allows you to run a test method with an expected exception and (optionally) exception message, delegating responsibility for making the assertion to the extension

###### RandomBeansExtension

Allows you to inject random instances of classes into your tests, useful when you need a class instance to test with but you don't care about its contents

###### SystemPropertyExtension

Allows you to set system properties before test execution and reverts these changes on test completion

###### TemporaryFolderExtension

Allows you to create temporary files and directories in your test, any such files or directories created in your tests are removed for you when the tests complete

###### TestNameExtension

Allows you to use the name of the currently executing test within your test cases

###### WatcherExtension

Logs test execution flow including entry, exit and elapsed time in milliseconds


Using JUnit-Extensions
-------

The `junit-extensions` library is available on [Maven Central](http://search.maven.org/#artifactdetails%7Cio.github.glytching%7Cjunit-extensions%7C1.1.0%7Cjar):

###### Maven 

```
<dependency>
    <groupId>io.github.glytching</groupId>
    <artifactId>junit-extensions</artifactId>
    <version>1.1.0</version>
    <scope>test</scope>
</dependency>
```

###### Gradle

```
testCompile 'io.github.glytching:junit-extensions:1.1.0'
```

Building JUnit-Extensions
-------

```
$ git clone https://github.com/glytching/junit-extensions.git
$ cd junit-extensions
$ mvn clean install
```

This will compile and run all automated tests and install the library in your local Maven repository. 

Note: the code is formatted using the [Google Code Formatter](https://github.com/google/google-java-format).

License
-------

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
JUnit Extensions
====

Provides [JUnit 5 Extensions](http://junit.org/junit5/docs/current/user-guide/#extensions) to add convenient behaviour to test cases.

Some of these have equivalents in [JUnit 4 Rules](https://github.com/junit-team/junit4/wiki/Rules) and, in time, there may be official JUnit 5 extensions for the more popular JUnit4 rules however that's not quite the case as yet.

There have been discussions on this topic amongst the JUnit team, see:

- See https://github.com/junit-team/junit5/issues/169
- See https://github.com/junit-team/junit5-samples/issues/4

But the upshot of these discussions seems to be:

- Enabling [limited support for JUnit4 rules on JUnit5](http://junit.org/junit5/docs/snapshot/user-guide/#migrating-from-junit4-rule-support)
- Create extensions for the _must haves_ e.g. [Spring](https://github.com/sbrannen/spring-test-junit5), [Mockito](https://github.com/junit-team/junit5-samples/blob/master/junit5-mockito-extension/src/main/java/com/example/mockito/MockitoExtension.java)

So, this library includes a few common extension implementations. These might prove useful until the suite of extensions provided by JUnit grows (or, in case it does not grow).

Building JUnit-Extensions
-------

```
$ git clone https://github.com/glytching/junit-extensions.git
$ cd junit-extensions
$ mvn clean install
```

This will compile and run all automated tests and install the library in your local Maven repository. 

Using JUnit-Extensions
-------

See [the docs](https://glytching.github.io/junit-extensions/).

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
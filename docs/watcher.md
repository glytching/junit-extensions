`WatcherExtension`
======

Similar to JUnit4's `TestWatcher` this extension watches the test invocation without modifying it or interfering with it in any way. Instead, it just logs:

1. Test method entry
1. Test method exit
1. Test elapsed time in milliseconds

This extension uses `java.util.logging.Logger` so it'll either play nicely with your current logging solution or there'll be a bridge to allow it to do so (e.g. [jul-to-slf4j](https://www.slf4j.org/legacy.html)).

#### Usage

This extension is engaged by adding the `@ExtendWith(WatcherExtension.class)` annotation to a test class.

#### Example

```
@ExtendWith(WatcherExtension.class)
public class MyTest {

    // entry, exit and elapsed time will be logged for every test in this test case
}
```

#### Output

Example output (after bridging to SLF4J):

```
2017-11-15 17:17:03,945|[main]|INFO |o.g.j.e.watcher.WatcherExtension|Starting test: [canGet]
2017-11-15 17:17:04,167|[main]|INFO |o.g.d.http.okhttp.OkHttpClient|Get from: http://host:1234/some/end/point
2017-11-15 17:17:04,201|[main]|INFO |o.g.j.e.watcher.WatcherExtension|Completed test [canGet] in 247 ms.

```
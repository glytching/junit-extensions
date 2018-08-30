BenchmarkExtension
======

This extension watches the test invocation without modifying it or interfering with it in any way. Instead, it just reports test elapsed time. The elapsed time is logged in milliseconds, by default, but you can also choose another unit from `java.util.concurrent.TimeUnit`. The elapsed time and test name are published to the test execution context so, by default, they will be written to console. Alternatively, you could provide your own implementation of `EngineExecutionListener` to report the output differently or to aggregate the output or to publish to a different target etc.

#### Usage

This extension is engaged by adding the `@ExtendWith(BenchmarkExtension.class)` annotation to a test class or - if you want to choose a non default `TimeUnit` - by using JUnit5's `@RegisterExtension`.

#### Example

```
@ExtendWith(BenchmarkExtension.class)
public class MyTest {

    // elapsed time will be logged for every test in this test case
    
}
```

```
public class MyTest {

    @RegisterExtension
    static BenchmarkExtension benchmarkExtension = new BenchmarkExtension(TimeUnit.MICROSECONDS);
    
    // elapsed time will be logged, in MICROSECONDS, for every test in this test case
    
}
```

#### Output

Example output

```
timestamp = 2018-08-30T16:47:07.352, Elapsed time in MILLISECONDS for canBenchmark = 6
```
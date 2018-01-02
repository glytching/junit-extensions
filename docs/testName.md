WatcherExtension
======

Similar to JUnit4's `TestName` Rule, this extension makes the current test name available inside test methods.

#### Usage

This extension is engaged by adding the `@ExtendWith(TestNameExtension.class)` annotation to a test class.

#### Example

```
@ExtendWith(TestNameExtension.class)
public class MyTest {

    @TestName
    private String testName;
    
    @Test
    public void someTest() {
        // use the populated testName
        // ...
    }
}
```
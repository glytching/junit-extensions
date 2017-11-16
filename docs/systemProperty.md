SystemPropertyExtension
======

If your test relies on system properties you could set them and unset them in 'before' and 'after' lifecycle methods. In Junit5, setting system properties for all tests in a test case might look like this:

```
@BeforeAll
public static void setSystemProperties() {
    // set the system properties
    // ...
}

@AfterAll
public static void unsetSystemProperties() {
    // unset the system properties
    // and reinstate the original value for any property which was overwritten
    // ...
}
```

Setting system properties for a single test in a test case might look like this:

```
@Test
public void aTest() {
    // set the system properties
  
    try {
        // code-under-test
    } finally {
        // unset the system properties
        // and reinstate the original value for any property which was overwritten
    }
}
```

These approaches will work but they are verbose and brittle. The `SystemPropertyExtension` allows you to _declare_ this behaviour by adding the `@SystemProperty` annotation (or its repeating equivalent: `@SystemProperties`) to a test case or a test method. This annotation allows you to declare:
                                                                       
- `name`: the system property name
- `value`: The system property value

#### Examples

###### Class Level System Property

```
@SystemProperty(name = "x", value = "y")
public class MyTest {

    @Test
    public void aTest() {
        assertThat(System.getProperty("x"), is("y"));
    }
}  
```

###### Class Level System Properties

```
@SystemProperties(
  properties = {
    @SystemProperty(name = "x", value = "y"),
    @SystemProperty(name = "p", value = "q")
  }
)
public class MyTest {

    @Test
    public void aTest() {
        assertThat(System.getProperty("x"), is("y"));
        assertThat(System.getProperty("p"), is("q"));
    }
}  
```

###### Method Level System Property

```
public class MyTest {

    @Test
    @SystemProperty(name = "x", value = "y")
    public void aTes() {
        assertThat(System.getProperty("x"), is("y"));
    }
}  
```

###### Method Level System Properties

```
public class MyTest {

    @Test
    @SystemProperties(
      properties = {
        @SystemProperty(name = "x", value = "y"),
        @SystemProperty(name = "p", value = "q")
      }
    )
    public void aTes() {
        assertThat(System.getProperty("x"), is("y"));
        assertThat(System.getProperty("p"), is("q"));
    }
}  
`ExpectedExceptionExtension`
======

Historically, assertions against an exception thrown by a JUnit test case involved one of the following:
 
 1. Wrap the test case code in a `try/fail/catch` idiom 
 1. Use the `expected` element of the `@Test` annotation
 1. Use JUnit4's `ExpectedException` rule
 
In JUnit5:
 
 1. The wrap-and-assert approach now has some lambda sugar, for example:
    
    ```
        RuntimeException actual = assertThrows(RuntimeException.class, () -> {
            // code-under-test
        });
        assertThat(actual.getMessage(), is("..."));
    ```
 1. The `@Test` annotation no longer has an `expected` element 
 1. The [limited support for JUnit4 rules on JUnit5](http://junit.org/junit5/docs/snapshot/user-guide/#migrating-from-junit4-rule-support) does support the `ExpectedException` rule but this is an experimental feature.

This extension offers much the same features as JUnit4's `ExpectedException` rule and is fully compatible with JUnit5.

#### Usage

This extension is engaged by adding the `@ExpectedException` annotation to a test method. This annotation allows you to declare:

- The type of the exception which you expect to be thrown by this test method
- A description of the message which you expect to be in the exception thrown by this test method. Unlike JUnit4's `ExpectedException` rule the message expectation does not accept a Hamcrest matcher so it is not quite a expressive as the equivalent rule however three different message matching strategies are supported:

- `messageIs`: asserts that the exception message exactly matches the value you supplied
- `messageStartsWith`: asserts that the exception message starts with the value you supplied
- `messageContains`: asserts that the exception message contains the value you supplied

Note: these are all **case sensitive**.

#### Examples

###### Expect a `Throwable` with an exact match on the exception message

```
    @Test
    @ExpectedException(type = Throwable.class, messageIs = "Boom!")
    public void canHandleAThrowable() throws Throwable {
        throw new Throwable("Boom!");
    }
```

###### Expect a `RuntimeException` with an match on the beginning of the exception message

```
    @Test
    @ExpectedException(type = RuntimeException.class, messageStartsWith = "Bye")
    public void canHandleAnExceptionWithAMessageWhichStartsWith() {
        throw new RuntimeException("Bye bye");
    }
```

###### Expect a custom exception type with an match on any part of the exception message 

```
    @Test
    @ExpectedException(type = MyDomainException.class, messageContains = "sorry")
    public void canHandleAnExceptionWithAMessageWhichContains() {
        throw new MyDomainException("Terribly sorry old chap");
    }
```
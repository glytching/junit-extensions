RandomBeansExtension
======

> With thanks to the author of [Random Beans](https://github.com/benas/random-beans).

Sometimes you don't care about the specific value of a class-under-test (or a class involved in a test) you just need it to be populated with _something_. The `RandomBeansExtension` wraps the [Random Beans](https://github.com/benas/random-beans) fake data generator in a JUnit Jupiter extension allowing you to inject 'fake' instances of classes, primitives, collections into your test cases. The extension supports injection of test class fields and test method parameters.

#### Usage

This extension is engaged by adding the `@Random` annotation to a test class field or a test method parameter. This annotation allows you to declare:

- `excludes`: fields to be excluded from the generated object, this supports _dot notation_ for fields in nested objects
- `size`: for collection types, the size of the generated collection
- `type`: for collection types, the underlying type of a generic collection 

You can use `@Random` annotation to a `static` field.  
From **v2.6.0** `static` field will be populated only once. Note, that in case of any default value (except `null`) the value will not be overridden by the extension.

#### Examples

###### Test Class Fields

```
@ExtendWith(RandomBeansExtension.class)
public class MyTest {

    // injected with a random String    
    @Random private String anyString;

    @Random private static String anyStaticString;
    
    // injected with a random, fully populated instance of DomainObject    
    @Random private DomainObject fullyPopulatedDomainObject;
    
    // injected with a random, partially populated instance of DomainObject    
    @Random(excludes = {"wotsits", "id", "nestedDomainObject.address"})
    private DomainObject partiallyPopulatedDomainObject;
    
    // injected with a List of random strings    
    @Random(type = String.class)
    private List<String> anyList;
    
    // injected with a List having 5 random strings    
    @Random(size = 5, type = String.class)
    private List<String> anyListOfSpecificSize;
    
    // injected with a Set of random strings
    @Random(type = String.class)
    private Set<String> anySet;
    
    // injected with a Stream of random strings
    @Random(type = String.class)
    private Stream<String> anyStream;
    
    // injected with a Collection of random strings
    @Random(type = String.class)
    private Collection<String> anyCollection;
    
    // injected with a List having 2 fully populated DomainObject instances
    @Random(size = 2, type = DomainObject.class)
    private List<DomainObject> anyFullyPopulatedDomainObjects;
    
    // injected with a List having 2 partially populated DomainObject instances
    @Random(
    size = 2,
    type = DomainObject.class,
    excludes = {"wotsits", "id", "nestedDomainObject.address"}
    )
    private List<DomainObject> anyPartiallyPopulatedDomainObjects;

    // ---- test methods which use these fields follow here ...  
} 
```

###### Test Method Parameters

```
@ExtendWith(RandomBeansExtension.class)
public class RandomBeansExtensionParameterTest {
    
    @Test
    @ExtendWith(RandomBeansExtension.class)
    public void canInjectARandomString(@Random String anyString) {
        // supplied with a random String 
    }
    
    @Test
    public void canInjectAFullyPopulatedRandomObject(@Random DomainObject domainObject) {
        // supplied with a random, fully populated DomainObject
    }
    
    @Test
    public void canInjectAPartiallyPopulatedRandomObject(
      @Random(excludes = {"wotsits", "id", "nestedDomainObject.address"})
          DomainObject domainObject) {
        // supplied with a random, partially populated DomainObject
    }
    
    @Test
    public void canInjectARandomListOfDefaultSize(@Random(type = String.class) List<String> anyList)
      throws Exception {
        // supplied with a List of random strings
    }
    
    @Test
    public void canInjectARandomListOfSpecificSize(
      @Random(size = 5, type = String.class) List<String> anyListOfSpecificSize) {
        // supplied with a List containing 5 random strings
    }
    
    @Test
    public void canInjectARandomSet(@Random(type = String.class) Set<String> anySet)
      throws Exception {
        // supplied with a Set of random strings
    }
    
    @Test
    public void canInjectARandomStream(@Random(type = String.class) Stream<String> anyStream)
      throws Exception {
        // supplied with a Stream of random strings
    }
    
    @Test
    public void canInjectARandomCollection(
      @Random(type = String.class) Collection<String> anyCollection) throws Exception {
        // supplied with a Collection of random strings
    }
    
    @Test
    public void canInjectRandomFullyPopulatedDomainObjects(
      @Random(size = 2, type = DomainObject.class)
          List<DomainObject> anyFullyPopulatedDomainObjects) {
        // supplied with a List of 2 fully populated instances of DomainObject
    }
    
    @Test
    public void canInjectRandomPartiallyPopulatedDomainObjects(
      @Random(
            size = 2,
            type = DomainObject.class,
            excludes = {"wotsits", "id", "nestedDomainObject.address"}
          )
          List<DomainObject> anyPartiallyPopulatedDomainObjects) {
        // supplied with a List of 2 partially populated instances of DomainObject
    }
}
```

##### Overriding the Default Randomization Parameters
 
In the event that the default [randomization parameters](https://github.com/j-easy/easy-random/wiki/Randomization-parameters) declared by this extension are not sufficient/appropriate for your use, you can override the defaults by using `@RegisterExtension` to pass in your own instance of RandomBeans' `EnhancedRandom`. For example:      

```
public class RandomBeansExtensionProgrammaticRegistrationTest {

    static EnhancedRandom enhancedRandom = EnhancedRandomBuilder
            .aNewEnhancedRandomBuilder()
            .exclude(FieldDefinitionBuilder
                    .field()
                    .named("wotsits")
                    .ofType(List.class)
                    .inClass(DomainObject.class)
                    .get())
            .randomize(Integer.class, IntegerRangeRandomizer.aNewIntegerRangeRandomizer(0, 10))
            .randomize(String.class, StringRandomizer.aNewStringRandomizer(5))
            .randomize(Double.class, DoubleRangeRandomizer.aNewDoubleRangeRandomizer(0.0, 10.0))
            .build();

    
    @RegisterExtension
    static RandomBeansExtension randomBeansExtension = new RandomBeansExtension(enhancedRandom);
   
    @Test
    public void canInjectIntegerInRangeOf(
        @Random(type = Integer.class) Integer anyInteger) throws Exception {
        assertThat(anyInteger, notNullValue());
        assertThat(anyInteger, lessThanOrEqualTo(10));
        assertThat(anyInteger, greaterThanOrEqualTo(0));
    }
      
    @Test
    public void canInjectDoubleInRangeOf(
        @Random(type = Double.class) Double anyDouble) throws Exception {
        assertThat(anyDouble, notNullValue());
        assertThat(anyDouble, lessThanOrEqualTo(10.0));
        assertThat(anyDouble, greaterThanOrEqualTo(0.0));
    }
}
```
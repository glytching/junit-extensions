/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.glytching.junit.extension.random;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.Charset.forName;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

/**
 * The random beans extension provides a test with randomly generated objects, including:
 *
 * <ul>
 *   <li>JDK types
 *   <li>Custom types
 *   <li>Collections
 *   <li>Generic collections
 *   <li>Partial population
 * </ul>
 *
 * <p>Usage examples:
 *
 * <p>Injecting random values as fields:
 *
 * <pre>
 * &#064;ExtendWith(RandomBeansExtension.class)
 * public class MyTest {
 *
 *     &#064;Random
 *     private String anyString;
 *
 *     &#064;Random(excluded = {"name", "value"})
 *     private List<DomainObject> anyPartiallyPopulatedDomainObject;
 *
 *     &#064;Random(type = DomainObject.class)
 *     private List<DomainObject> anyDomainObjects;
 *
 *     &#064;Test
 *     public void testUsingRandomString() {
 *         // use the injected anyString
 *         // ...
 *     }
 *
 *     &#064;Test
 *     public void testUsingRandomDomainObjects() {
 *         // use the injected anyDomainObjects
 *         // the anyDomainObjects will contain _N_ fully populated random instances of DomainObject
 *         // ...
 *     }
 *
 *     &#064;Test
 *     public void testUsingPartiallyPopulatedDomainObject() {
 *         // use the injected anyPartiallyPopulatedDomainObject
 *         // this object's "name" and "value" members will not be populated since this has been declared with
 *         //     excluded = {"name", "value"}
 *         // ...
 *     }
 * }
 * </pre>
 *
 * <p>Injecting random values as parameters:
 *
 * <pre>
 * &#064;ExtendWith(RandomBeansExtension.class)
 * public class MyTest {
 *
 *     &#064;Test
 *     &#064;ExtendWith(RandomBeansExtension.class)
 *     public void testUsingRandomString(&#064;Random String anyString) {
 *         // use the provided anyString
 *         // ...
 *     }
 *
 *     &#064;Test
 *     &#064;ExtendWith(RandomBeansExtension.class)
 *     public void testUsingRandomDomainObjects(&#064;Random(type = DomainObject.class) List<DomainObject> anyDomainObjects) {
 *         // use the injected anyDomainObjects
 *         // the anyDomainObjects will contain _N_ fully populated random instances of DomainObject
 *         // ...
 *     }
 *
 *     &#064;Test
 *     &#064;ExtendWith(RandomBeansExtension.class)
 *     public void testUsingPartiallyPopulatedDomainObject(&#064;Random(excluded = {"name", "value"}) List<DomainObject> anyPartiallyPopulatedDomainObject) {
 *         // use the injected anyPartiallyPopulatedDomainObject
 *         // this object's "name" and "value" members will not be populated since this has been declared with
 *         //     excluded = {"name", "value"}
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @see <a href="https://github.com/benas/random-beans">Random Beans</a>
 * @since 1.0.0
 */
public class RandomBeansExtension implements TestInstancePostProcessor, ParameterResolver {

  private final EnhancedRandom random;

  /**
   * Create the extension with a default {@link EnhancedRandom}.
   *
   * @see <a href="https://github.com/benas/random-beans/wiki/Randomization-parameters">Enhanced
   *     Random Configuration Parameters</a>
   */
  public RandomBeansExtension() {
    this(EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            // maximum number of instances of a given type, above this number requests will start to
            // reuse
            // previously generated instances
            .objectPoolSize(10)

            // how deep should we go when randomising an object graph?
            .randomizationDepth(5)

            // the charset used for all String and Character values
            .charset(forName("UTF-8"))

            // min, max bounds for the generated string length
            .stringLengthRange(5, 50)

            // min, max bounds for the generated collections size
            .collectionSizeRange(1, 10)

            // if a random values is declared as an abstract or interface type then the classpath
            // will be scanned
            // for a concrete type of that abstract or interface type
            .scanClasspathForConcreteTypes(true)

            // do not override any values which are already initialised in the target type
            .overrideDefaultInitialization(false)
            .build());
  }

  /**
   * Create the extension with the given {@link EnhancedRandom}. This is used, instead of the zero-arg alternative, when
   * the caller wants to override the default 'randomizer' configuration. This constructor will be called by using the
   * {@code RegisterExtension} annotation.
   *
   * @param enhancedRandom
   * @since 2.5.0
   */
  public RandomBeansExtension(EnhancedRandom enhancedRandom) {
    this.random = enhancedRandom;

  }

  /**
   * Does this extension support injection for parameters of the type described by the given {@code
   * parameterContext}?
   *
   * @param parameterContext the context for the parameter for which an argument should be resolved
   * @param extensionContext the extension context for the {@code Executable} about to be invoked
   * @return true if the given {@code parameterContext} is annotated with {@link Random}, false
   *     otherwise
   * @throws ParameterResolutionException
   */
  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return parameterContext.getParameter().getAnnotation(Random.class) != null;
  }

  /**
   * Provides a value for any parameter context which has passed the {@link
   * #supportsParameter(ParameterContext, ExtensionContext)} gate.
   *
   * @param parameterContext the context for the parameter for which an argument should be resolved
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return a randomly generated object
   * @throws ParameterResolutionException
   */
  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return resolve(
        parameterContext.getParameter().getType(),
        parameterContext.getParameter().getAnnotation(Random.class));
  }

  /**
   * Inject random values into any fields which are annotated with {@link Random}
   *
   * @param testInstance the instance to post-process
   * @param extensionContext the current extension context
   * @throws Exception
   */
  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext)
      throws Exception {
    for (Field field : testInstance.getClass().getDeclaredFields()) {
      if (isAnnotated(field, Random.class)) {
        Random annotation = field.getAnnotation(Random.class);
        Object randomObject = resolve(field.getType(), annotation);

        field.setAccessible(true);
        field.set(testInstance, randomObject);
      }
    }
  }

  /**
   * Maps the 'random requirements' expressed by the given {@code annotation} to invocations on
   * {@link #random}.
   *
   * @param targetType the type to be provided
   * @param annotation an instance of {@link Random} which describes how the user wishes to
   *     configure the 'random generation'
   * @return a randomly generated instance of {@code targetType}
   */
  private Object resolve(Class<?> targetType, Random annotation) {
    if (targetType.isAssignableFrom(List.class) || targetType.isAssignableFrom(Collection.class)) {
      return random
          .objects(annotation.type(), annotation.size(), annotation.excludes())
          .collect(Collectors.toList());
    } else if (targetType.isAssignableFrom(Set.class)) {
      return random
          .objects(annotation.type(), annotation.size(), annotation.excludes())
          .collect(Collectors.toSet());
    } else if (targetType.isAssignableFrom(Stream.class)) {
      return random.objects(annotation.type(), annotation.size(), annotation.excludes());
    } else {
      return random.nextObject(targetType, annotation.excludes());
    }
  }
}

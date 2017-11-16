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
package org.glytching.junit.extension.system;

import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.platform.commons.util.AnnotationUtils.isAnnotated;

/**
 * The system property extension sets system properties before test execution and unsets them on
 * completion. More specifically:
 *
 * <ul>
 *   <li>If a new system property was added then it is removed after test execution completes
 *   <li>If an existing system property was overwritten then its original value is reinstated after
 *       test execution completes
 * </ul>
 *
 * <p>System properties can be injected into your test or test case with either of the following
 * approaches:
 *
 * <ul>
 *   <li>Class level annotation. Note: you cannot annotate a base test case and expect the extension
 *       to be engaged for its children. So, if you want a system property to be available for all
 *       test methods in a Class {@code X} which extends {@code Y} then you must add the annotation
 *       to Class {@code X} not to Class {@code Y}. For example:
 *       <pre>
 *  &#064;SystemProperty(name = "nameA", value = "valueA")
 *  public class MyTest {
 *      // ...
 *  }
 * </pre>
 *   <li>Parameter injection into a {@code @Test} method. For example:
 *       <pre>
 *  &#064;Test
 *  &#064;SystemProperty(name = "nameA", value = "valueA")
 *  public void testUsingSystemProperty() {
 *      // ...
 *  }
 * </pre>
 * </ul>
 *
 * <p>Multiple system properties can be declared using the {@link SystemProperties} repeating
 * annotation.
 *
 * <p>Usage examples:
 *
 * <p>Declaring system properties at class level:
 *
 * <pre>
 *  &#064;SystemProperties(
 *      properties = {
 *          &#064;SystemProperty(name = "nameA", value = "valueA"),
 *          &#064;SystemProperty(name = "nameB", value = "valueB")
 *      }
 *  )
 * public class MyTest {
 *
 *     &#064;Test
 *     public void test() {
 *         // the system properties nameA:valueA, nameB:valueB have been set
 *         // ...
 *     }
 * }
 * </pre>
 *
 * <p>Declaring system properties at method level:
 *
 * <pre>
 * public class MyTest {
 *
 *     &#064;Test
 *     &#064;SystemProperties(
 *         properties = {
 *            &#064;SystemProperty(name = "nameA", value = "valueA"),
 *            &#064;SystemProperty(name = "nameB", value = "valueB")
 *         }
 *     )
 *     public void testUsingSystemProperties(TemporaryFolder temporaryFolder) {
 *         // the system properties nameA:valueA, nameB:valueB have been set
 *         // ...
 *     }
 *
 *     &#064;Test
 *     &#064;SystemProperty(name = "nameC", value = "valueC")
 *     public void testUsingSystemProperty(TemporaryFolder temporaryFolder) {
 *         // the system property nameC:valueC has been set
 *         // ...
 *     }
 *
 *     &#064;Test
 *     public void testWithoutSystemProperties() {
 *         // the system properties nameA:valueA, nameB:valueB, nameC:valueC have *not* been set
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @since 1.0.0
 */
public class SystemPropertyExtension
    implements AfterEachCallback, BeforeEachCallback, BeforeAllCallback, AfterAllCallback {

  private static final String KEY = "restoreContext";

  /**
   * If the current test class has a system property annotation(s) then create a {@link
   * RestoreContext} representing the annotation(s). This causes the requested system properties to
   * be set and retains a copy of pre-set values for reinstatement after test execution.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    List<SystemProperty> systemProperties =
        getSystemProperties(extensionContext.getRequiredTestClass());
    if (!systemProperties.isEmpty()) {
      RestoreContext.Builder builder = RestoreContext.createBuilder();
      for (SystemProperty systemProperty : systemProperties) {
        builder.addPropertyName(systemProperty.name());
        if (System.getProperty(systemProperty.name()) != null) {
          builder.addRestoreProperty(
              systemProperty.name(), System.getProperty(systemProperty.name()));
        }

        set(systemProperty);
      }
      writeRestoreContext(extensionContext, builder.build());
    }
  }

  /**
   * If a {@link RestoreContext} exists for the given {@code extensionContext} then restore it i.e.
   * unset any system properties which were set in {@link #beforeAll(ExtensionContext)} for this
   * {@code extensionContext} and reinstate original value, if applicable.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {
    RestoreContext restoreContext = readRestoreContext(extensionContext);
    if (restoreContext != null) {
      restoreContext.restore();
    }
  }

  /**
   * If the current test method has a system property annotation(s) then create a {@link
   * RestoreContext} representing the annotation(s). This causes the requested system properties to
   * be set and retains a copy of pre-set values for reinstatement after test execution.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void beforeEach(ExtensionContext extensionContext) throws Exception {
    List<SystemProperty> systemProperties =
        getSystemProperties(extensionContext.getRequiredTestMethod());
    if (!systemProperties.isEmpty()) {
      RestoreContext.Builder builder = RestoreContext.createBuilder();
      for (SystemProperty systemProperty : systemProperties) {
        builder.addPropertyName(systemProperty.name());
        if (System.getProperty(systemProperty.name()) != null) {
          builder.addRestoreProperty(
              systemProperty.name(), System.getProperty(systemProperty.name()));
        }

        set(systemProperty);
      }
      writeRestoreContext(extensionContext, builder.build());
    }
  }

  /**
   * If a {@link RestoreContext} exists for the given {@code extensionContext} then restore it i.e.
   * unset any system properties which were set in {@link #beforeEach(ExtensionContext)} for this
   * {@code extensionContext} and reinstate original value, if applicable.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void afterEach(ExtensionContext extensionContext) throws Exception {
    RestoreContext restoreContext = readRestoreContext(extensionContext);
    if (restoreContext != null) {
      restoreContext.restore();
    }
  }

  /**
   * Get a collection of {@link SystemProperty} for the given {@code annotatedElement}. If the given
   * {@code annotatedElement} has no such annotations then an empty list is returned, if the given
   * {@code annotatedElement} is annotated with {@link SystemProperty} then a list with one element
   * is returned, if the given {@code annotatedElement} is annotated with {@link SystemProperties}
   * then a list with one element for each of the repeated {@link SystemProperty} values is
   * returned.
   *
   * <p>This is essentially a shortcut for logic such as: 'does this element have the {@link
   * SystemProperty} annotation, if not does it have the {@link SystemProperties}' followed by
   * gathering these annotation values.
   *
   * @param annotatedElement either a test class or a test method which may be annotated with a
   *     system property annotation
   * @return 0..* {@link SystemProperty} elements
   */
  private List<SystemProperty> getSystemProperties(AnnotatedElement annotatedElement) {
    List<SystemProperty> systemProperties = new ArrayList<>();
    if (isAnnotated(annotatedElement, SystemProperties.class)) {
      // gather than repeating system property values
      systemProperties.addAll(
          Arrays.asList(annotatedElement.getAnnotation(SystemProperties.class).properties()));
    }
    if (isAnnotated(annotatedElement, SystemProperty.class)) {
      // add the single system property value
      systemProperties.add(annotatedElement.getAnnotation(SystemProperty.class));
    }
    return systemProperties;
  }

  private void set(SystemProperty systemProperty) {
    System.setProperty(systemProperty.name(), systemProperty.value());
  }

  private void writeRestoreContext(
      ExtensionContext extensionContext, RestoreContext restoreContext) {
    getStore(extensionContext).getOrComputeIfAbsent(KEY, key -> restoreContext);
  }

  private RestoreContext readRestoreContext(ExtensionContext extensionContext) {
    return getStore(extensionContext).get(KEY, RestoreContext.class);
  }

  /**
   * Creates a {@link Store} for a {@link RestoreContext} in the context of the given {@code
   * extensionContext}. A {@link Store} is bound to an {@link ExtensionContext} so different test
   * invocations do not share the same store. For example a test invocation on {@code
   * ClassA.testMethodA} will have a different {@link Store} instance to that associated with a test
   * invocation on {@code ClassA.testMethodB} or test invocation on {@code ClassC.testMethodC}.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return a {@link Store} for the given {@code extensionContext}
   */
  private Store getStore(ExtensionContext extensionContext) {
    return extensionContext.getRoot().getStore(namespace(extensionContext));
  }

  /**
   * Creates a {@link Namespace} in which {@link RestoreContext}s are stored on creation for post
   * execution restoration. Storing data in a custom namespace prevents accidental cross pollination
   * of data between extensions and between different invocations within the lifecycle of a single
   * extension.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return a {@link Namespace} describing the scope for a single {@link RestoreContext}
   */
  private Namespace namespace(ExtensionContext extensionContext) {
    return Namespace.create(this.getClass(), extensionContext);
  }
}

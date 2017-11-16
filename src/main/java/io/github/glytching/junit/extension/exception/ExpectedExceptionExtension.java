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
package io.github.glytching.junit.extension.exception;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;
import static org.junit.platform.commons.util.FunctionUtils.where;

/**
 * The expected exception extension allows the developer to express the expectations of an exception
 * to be thrown by the code-under-test. This extension is engaged by adding the {@link
 * ExpectedException} annotation to a test method.
 *
 * <p>Usage example:
 *
 * <pre>
 * public class MyTest {
 *
 *     &#064;Test
 *     &#064;ExpectedException(type = Exception.class, messageIs = "Boom!")
 *     public void canHandleAnException() throws Exception {
 *         // ...
 *         throw new Exception("Boom!");
 *     }
 *
 *
 *     &#064;Test
 *     &#064;ExpectedException(type = RuntimeException.class, messageStartsWith = "Bye")
 *     public void canHandleAnExceptionWithAMessageWhichStartsWith() {
 *         // ...
 *         throw new RuntimeException("Bye bye");
 *     }
 * }
 * </pre>
 *
 * @see <a href="https://github.com/junit-team/junit4/wiki/Rules#expectedexception-rules">JUnit 4
 *     ExpectedException Rule</a>
 * @since 1.0.0
 */
public class ExpectedExceptionExtension implements TestExecutionExceptionHandler {

  private final Function<Throwable, String> function;

  public ExpectedExceptionExtension() {
    this.function = Throwable::getMessage;
  }

  /**
   * Handle the supplied {@code Throwable throwable}. If the {@code extensionContext} is annotated
   * with {@link ExpectedException} and if the {@code throwable} matches the expectations expressed
   * in the {@link ExpectedException} annotation then the supplied {@code throwable} is swallowed
   * otherwise the supplied {@code throwable} is rethrown.
   *
   * @param extensionContext the current extension context
   * @param throwable the {@code Throwable} to handle
   * @throws Throwable
   */
  @Override
  public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable)
      throws Throwable {
    Optional<ExpectedException> optional =
        findAnnotation(extensionContext.getTestMethod(), ExpectedException.class);
    if (optional.isPresent()) {
      ExpectedException annotation = optional.get();
      if (throwable.getClass() == annotation.type()) {
        if (where(function, getPredicate(annotation)).test(throwable)) {
          return;
        }
      }
    }
    throw throwable;
  }

  /**
   * Maps the expectations expressed in the given {@code annotation} to a {@link Predicate}.
   *
   * @param annotation encapsulates the callers' expression of what's to be deemed an acceptable
   *     exception
   * @return a predicate which can be used to assess whether an exception matches the expectations
   *     expressed in the given {@code annotation}
   */
  private Predicate<String> getPredicate(ExpectedException annotation) {
    if (has(annotation.messageStartsWith())) {
      return s -> s.startsWith(annotation.messageStartsWith());
    } else if (has(annotation.messageContains())) {
      return s -> s.contains(annotation.messageContains());
    } else {
      // the default
      return s -> s.equals(annotation.messageIs());
    }
  }

  private boolean has(String incoming) {
    return incoming != null && incoming.length() > 0;
  }
}

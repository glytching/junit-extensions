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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * We cannot use the <em>normal</em> test flow to verify the path where a thrown exception does not
 * match the expected exception because this path causes the exception to be rethrown which would
 * then cause the test to fail. So, we have to test the <em>sad path</em> outside the normal test
 * flow by playing around with the {@link ExpectedExceptionExtension} directly.
 */
public class ExpectedExceptionExtensionMetaTest {

  private final ExpectedExceptionExtension sut = new ExpectedExceptionExtension();
  @Mock private ExtensionContext extensionContext;

  @BeforeEach
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void willRethrowIfTheExceptionTypeDoesNotMatchTheExpectedExceptionType() throws Throwable {
    givenExtensionContentWithMethod("canHandleAThrowable");

    RuntimeException expected = new RuntimeException("");

    RuntimeException actual =
        assertThrows(
            expected.getClass(),
            () -> sut.handleTestExecutionException(extensionContext, expected));
    assertThat(actual, is(expected));
  }

  @Test
  public void willRethrowIfTheExceptionMessageDoesNotMatchTheExpectedExceptionMessage()
      throws Throwable {
    givenExtensionContentWithMethod("canHandleARuntimeException");

    RuntimeException expected = new RuntimeException("");

    RuntimeException actual =
        assertThrows(
            expected.getClass(),
            () -> sut.handleTestExecutionException(extensionContext, expected));
    assertThat(actual, is(expected));
  }

  @Test
  public void willRethrowIfTheExceptionMessageDoesNotStartWithTheExpectedExceptionMessage()
      throws Throwable {
    givenExtensionContentWithMethod("canHandleARuntimeException");

    RuntimeException expected = new RuntimeException("Foo");

    RuntimeException actual =
        assertThrows(
            expected.getClass(),
            () -> sut.handleTestExecutionException(extensionContext, expected));
    assertThat(actual, is(expected));
  }

  @Test
  public void willRethrowIfTheExceptionMessageDoesNotContainTheExpectedExceptionMessage()
      throws Throwable {
    givenExtensionContentWithMethod("canHandleARuntimeException");

    RuntimeException expected = new RuntimeException("Bar");

    RuntimeException actual =
        assertThrows(
            expected.getClass(),
            () -> sut.handleTestExecutionException(extensionContext, expected));
    assertThat(actual, is(expected));
  }

  private void givenExtensionContentWithMethod(String methodName) throws NoSuchMethodException {
    when(extensionContext.getTestMethod()).thenReturn(Optional.of(getMethod(methodName)));
  }

  private Method getMethod(String methodName) throws NoSuchMethodException {
    return ExpectedExceptionExtensionTest.class.getMethod(methodName);
  }
}

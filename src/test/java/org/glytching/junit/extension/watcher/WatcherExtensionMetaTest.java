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
package org.glytching.junit.extension.watcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * We cannot use the <em>normal</em> test flow to verify the {@link WatcherExtension} because it has
 * no easily assertable side effects. So, we test its interaction with the logging subsystem by
 * playing around with the {@link WatcherExtension} directly.
 */
public class WatcherExtensionMetaTest {

  @Mock private ExtensionContext extensionContext;
  @Mock private Store store;
  @Mock private Logger logger;

  private WatcherExtension sut;

  @BeforeEach
  public void prepare() {
    MockitoAnnotations.initMocks(this);

    when(extensionContext.getStore(Namespace.create(WatcherExtension.class, extensionContext)))
        .thenReturn(store);

    sut = new WatcherExtension(logger);
  }

  @Test
  void willLogBeforeAndAfter() throws Exception {
    Method testMethod = getMethod("canExecuteATestWithTheWatcherEngaged");

    givenExtensionContentWithMethod(testMethod);
    when(store.remove(eq(testMethod), eq(long.class))).thenReturn(System.currentTimeMillis());

    sut.beforeTestExecution(extensionContext);
    sut.afterTestExecution(extensionContext);

    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(logger, times(2)).info(captor.capture());

    List<String> logEvents = captor.getAllValues();
    assertThat(logEvents.size(), is(2));
    assertThat(logEvents.get(0), is(String.format("Starting test [%s]", testMethod.getName())));
    assertThat(logEvents.get(1),
        startsWith(String.format("Completed test [%s] in ", testMethod.getName())));
  }

  private void givenExtensionContentWithMethod(Method method) throws NoSuchMethodException {
    when(extensionContext.getRequiredTestMethod()).thenReturn(method);
  }

  private Method getMethod(String methodName) throws NoSuchMethodException {
    return WatcherExtensionTest.class.getMethod(methodName);
  }
}
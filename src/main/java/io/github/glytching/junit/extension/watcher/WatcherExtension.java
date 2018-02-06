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
package io.github.glytching.junit.extension.watcher;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import static io.github.glytching.junit.extension.util.ExtensionUtil.getStore;

/**
 * The watcher extension logs test execution flow including:
 *
 * <ul>
 *   <li>Entry
 *   <li>Exit
 *   <li>Elapsed time in ms
 * </ul>
 *
 * <p>It produces output like so:
 *
 * <pre>
 * INFO: Starting test: [aTest]
 * INFO: Completed test [aTest] in 21 ms.
 * </pre>
 *
 * <p>Usage example:
 *
 * <pre>
 * &#064;ExtendWith(WatcherExtension.class)
 * public class MyTest {
 *
 *  &#064;Test
 *  public void aTest() {
 *      // ...
 *  }
 * }
 * </pre>
 *
 * @see <a
 *     href="https://github.com/junit-team/junit4/wiki/Rules#testwatchmantestwatcher-rules">JUnit 4
 *     TestWatchman/TestWatcher Rules</a>
 * @since 1.0.0
 */
public class WatcherExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
  private final Logger logger;

  WatcherExtension() {
    this(Logger.getLogger(WatcherExtension.class.getName()));
  }

  // erm, facilitates testing
  WatcherExtension(Logger logger) {
    this.logger = logger;
  }

  /**
   * Log test method entry and store its start time in the {@link Store} for use in {@link
   * #afterTestExecution(ExtensionContext)}.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    Method testMethod = extensionContext.getRequiredTestMethod();
    logger.info(String.format("Starting test [%s]", testMethod.getName()));
    getStore(extensionContext, this.getClass()).put(testMethod, System.currentTimeMillis());
  }

  /**
   * Log test method exit, using the start time stored by {@link
   * #beforeTestExecution(ExtensionContext)} to calculate a duration.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    Method testMethod = extensionContext.getRequiredTestMethod();
    long start = getStore(extensionContext, this.getClass()).remove(testMethod, long.class);
    long duration = System.currentTimeMillis() - start;

    logger.info(String.format("Completed test [%s] in %sms", testMethod.getName(), duration));
  }
}

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
package io.github.glytching.junit.extension.benchmark;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static io.github.glytching.junit.extension.util.ExtensionUtil.getStore;

/**
 * The benchmark extension publishes elapsed time to the execution listener. By default, this
 * produces output like so:
 *
 * <pre>
 * timestamp = 2018-08-30T16:28:47.701, Elapsed time in MICROSECONDS for canBenchmark = 13213
 * </pre>
 *
 * Your own implementation of the {@code EngineExecutionListener} could adopt a different template
 * for the event string or it could collect and aggregate results for all tests in a test case or it
 * could write results to somewhere other than the console etc.
 *
 * <p>By default, elapsed times are reported in {@link TimeUnit#MILLISECONDS} but you can use {@link
 * org.junit.jupiter.api.extension.RegisterExtension} to choose a different {@link TimeUnit}.
 *
 * <p>Usage example:
 *
 * <pre>
 * &#064;ExtendWith(BenchmarkExtension.class)
 * public class MyTest {
 *
 *  &#064;Test
 *  public void aTest() {
 *      // ...
 *  }
 * }
 * </pre>
 *
 * <pre>
 * public class MyTest {
 *
 *  // report elapsed times in a non default time unit
 *  &#064;@RegisterExtension
 *  static BenchmarkExtension benchmarkExtension = new BenchmarkExtension(TimeUnit.MICROSECONDS);
 *
 *  &#064;Test
 *  public void aTest() {
 *      // ...
 *  }
 * }
 * </pre>
 *
 * @since 2.4.0
 */
public class BenchmarkExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  public static final String REPORT_EVENT_FORMAT = "Elapsed time in %s for %s";

  private final TimeUnit timeUnit;

  /** Constructs an instance of this class which will report using the default time unit. */
  @SuppressWarnings("unused")
  public BenchmarkExtension() {
    this(TimeUnit.MILLISECONDS);
  }

  /**
   * Constructs an instance of this class which will report using the given {@code timeUnit}.
   *
   * @param timeUnit the time unit in which benchmarks will be reported
   */
  public BenchmarkExtension(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  /**
   * Store a {@link StopWatch} instance against the test method name for use in {@link
   * #afterTestExecution(ExtensionContext)}.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    // put a StopWatch in the context for the current test invocation
    getStore(extensionContext, this.getClass())
        .put(extensionContext.getRequiredTestMethod(), new StopWatch());
  }

  /**
   * Gather the elapsed time, using {@link StopWatch} stored by {@link
   * #beforeTestExecution(ExtensionContext)}.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @throws Exception
   */
  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    Method testMethod = extensionContext.getRequiredTestMethod();

    // get the StopWatch from the context for the current test invocation and report on it
    long duration =
        getStore(extensionContext, this.getClass())
            .get(testMethod, StopWatch.class)
            .duration(timeUnit);

    extensionContext.publishReportEntry(
        String.format(REPORT_EVENT_FORMAT, timeUnit.name(), testMethod.getName()),
        Long.toString(duration));
  }
}

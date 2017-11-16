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
package io.github.glytching.junit.extension.util;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.reporting.ReportEntry;

import java.util.Optional;
import java.util.function.Predicate;

import static java.util.function.Predicate.isEqual;
import static io.github.glytching.junit.extension.util.ExecutionEvent.Type.*;
import static org.junit.platform.commons.util.FunctionUtils.where;

/**
 * Represents an event collected by {@link RecordingExecutionListener}. The listener receives
 * callbacks from the engine during test execution, these callbacks represent stages in the test
 * execution lifecycle and are accompanied by the state which is relevant to each stage. The
 * intention here is to gather this state for use by assertions in the extension tests.
 *
 * <p><b>Note:</b> this is mostly lifted from JUnit5's own (unpublished) test utilities.
 *
 * @see <a
 *     href="https://github.com/junit-team/junit5/tree/master/junit-platform-engine/src/test/java/org/junit/platform/engine/test/event">JUnit5
 *     Test Utilities</a>
 */
public class ExecutionEvent {

  private final ExecutionEvent.Type type;
  private final TestDescriptor testDescriptor;
  private final Object payload;

  private ExecutionEvent(ExecutionEvent.Type type, TestDescriptor testDescriptor, Object payload) {
    this.type = type;
    this.testDescriptor = testDescriptor;
    this.payload = payload;
  }

  public static ExecutionEvent reportingEntryPublished(
      TestDescriptor testDescriptor, ReportEntry entry) {
    return new ExecutionEvent(REPORTING_ENTRY_PUBLISHED, testDescriptor, entry);
  }

  public static ExecutionEvent dynamicTestRegistered(TestDescriptor testDescriptor) {
    return new ExecutionEvent(DYNAMIC_TEST_REGISTERED, testDescriptor, null);
  }

  public static ExecutionEvent executionSkipped(TestDescriptor testDescriptor, String reason) {
    return new ExecutionEvent(SKIPPED, testDescriptor, reason);
  }

  public static ExecutionEvent executionStarted(TestDescriptor testDescriptor) {
    return new ExecutionEvent(STARTED, testDescriptor, null);
  }

  public static ExecutionEvent executionFinished(
      TestDescriptor testDescriptor, TestExecutionResult result) {
    return new ExecutionEvent(FINISHED, testDescriptor, result);
  }

  public static Predicate<ExecutionEvent> byType(ExecutionEvent.Type type) {
    return where(ExecutionEvent::getType, isEqual(type));
  }

  public static Predicate<ExecutionEvent> byTestDescriptor(
      Predicate<? super TestDescriptor> predicate) {
    return where(ExecutionEvent::getTestDescriptor, predicate);
  }

  public static <T> Predicate<ExecutionEvent> byPayload(
      Class<T> payloadClass, Predicate<? super T> predicate) {
    return event -> event.getPayload(payloadClass).filter(predicate).isPresent();
  }

  public ExecutionEvent.Type getType() {
    return type;
  }

  public TestDescriptor getTestDescriptor() {
    return testDescriptor;
  }

  public <T> Optional<T> getPayload(Class<T> payloadClass) {
    return Optional.ofNullable(payload).filter(payloadClass::isInstance).map(payloadClass::cast);
  }

  @Override
  public String toString() {
    return "ExecutionEvent{"
        + "type="
        + type
        + ", testDescriptor="
        + testDescriptor
        + ", payload="
        + payload
        + '}';
  }

  public enum Type {
    DYNAMIC_TEST_REGISTERED,
    SKIPPED,
    STARTED,
    FINISHED,
    REPORTING_ENTRY_PUBLISHED
  }
}

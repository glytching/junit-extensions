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

import io.github.glytching.junit.extension.util.ExecutionEvent.*;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.reporting.ReportEntry;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.function.Predicate.isEqual;
import static io.github.glytching.junit.extension.util.ExecutionEvent.Type.FINISHED;
import static io.github.glytching.junit.extension.util.ExecutionEvent.*;
import static org.junit.platform.commons.util.FunctionUtils.where;

/**
 * A {@link EngineExecutionListener} that records all events and makes them available for assertions
 * in the extension tests.
 *
 * <p><b>Note:</b> this is mostly lifted from JUnit5's own (unpublished) test utilities.
 *
 * @see <a
 *     href="https://github.com/junit-team/junit5/tree/master/junit-platform-engine/src/test/java/org/junit/platform/engine/test/event">JUnit5
 *     Test Utilities</a>
 */
public class RecordingExecutionListener implements EngineExecutionListener {

  private final List<ExecutionEvent> executionEvents = new CopyOnWriteArrayList<>();

  // ------------
  // listener callbacks
  // ------------

  @Override
  public void dynamicTestRegistered(TestDescriptor testDescriptor) {
    addEvent(ExecutionEvent.dynamicTestRegistered(testDescriptor));
  }

  @Override
  public void executionSkipped(TestDescriptor testDescriptor, String reason) {
    addEvent(ExecutionEvent.executionSkipped(testDescriptor, reason));
  }

  @Override
  public void executionStarted(TestDescriptor testDescriptor) {
    addEvent(ExecutionEvent.executionStarted(testDescriptor));
  }

  @Override
  public void executionFinished(TestDescriptor testDescriptor, TestExecutionResult result) {
    addEvent(ExecutionEvent.executionFinished(testDescriptor, result));
  }

  @Override
  public void reportingEntryPublished(TestDescriptor testDescriptor, ReportEntry entry) {
    addEvent(ExecutionEvent.reportingEntryPublished(testDescriptor, entry));
  }

  // ------------
  // convenience methods for accessing the events received via callbacks, used by test assertions
  // ------------

  public List<ExecutionEvent> getExecutionEvents() {
    return executionEvents;
  }

  public Stream<ExecutionEvent> getEventsByType(Type type) {
    return eventStream().filter(byType(type));
  }

  public Stream<ExecutionEvent> getTestEventsByType(Type type) {
    return getEventsByTypeAndTestDescriptor(type, TestDescriptor::isTest);
  }

  public Stream<ExecutionEvent> getEventsByTypeAndTestDescriptor(
      Type type, Predicate<? super TestDescriptor> predicate) {
    return eventStream().filter(byType(type).and(byTestDescriptor(predicate)));
  }

  public long countEventsByType(Type type) {
    return getEventsByType(type).count();
  }

  public Stream<ExecutionEvent> getFinishedEventsByStatus(Status status) {
    return getEventsByType(FINISHED)
        .filter(
            byPayload(
                TestExecutionResult.class, where(TestExecutionResult::getStatus, isEqual(status))));
  }

  private Stream<ExecutionEvent> eventStream() {
    return getExecutionEvents().stream();
  }

  private void addEvent(ExecutionEvent event) {
    executionEvents.add(event);
  }
}

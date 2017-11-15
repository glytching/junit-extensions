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
package org.glytching.junit.extension.folder;

import org.glytching.junit.extension.util.ExecutionEvent;
import org.glytching.junit.extension.util.RecordingExecutionListener;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;

import java.util.function.Predicate;

import static org.glytching.junit.extension.util.ExecutionEvent.Type.FINISHED;
import static org.glytching.junit.extension.util.ExecutionEvent.Type.STARTED;
import static org.glytching.junit.extension.util.ExtensionTester.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class TemporaryFolderExtensionMetaTest {

  @Test
  public void canExecuteASingleTestMethod() {
    String methodName = "canInjectATemporaryFolderAsAParameter";

    RecordingExecutionListener recorder =
        execute(
            selectMethod(
                TemporaryFolderExtensionParameterTest.class,
                methodName,
                TemporaryFolder.class.getName()));

    recorder
        .getExecutionEvents()
        .stream()
        .map(ExecutionEvent::toString)
        .forEach(System.out::println);

    Predicate<? super TestDescriptor> methodNamePredicate =
        (Predicate<TestDescriptor>)
            testDescriptor -> testDescriptor.getDisplayName().startsWith(methodName);

    assertThat(
        recorder.getEventsByTypeAndTestDescriptor(STARTED, methodNamePredicate).count(), is(1L));

    assertThat(recorder.getEventsByType(FINISHED).count(), is(3L));

    assertThat(recorder.getTestEventsByType(FINISHED).count(), is(1L));

    assertThat(recorder.getFinishedEventsByStatus(SUCCESSFUL).count(), is(3L));

    assertThat(
        recorder.getEventsByTypeAndTestDescriptor(FINISHED, methodNamePredicate).count(), is(1L));

    assertThat(recorder.countEventsByType(STARTED), is(3L));
  }
}
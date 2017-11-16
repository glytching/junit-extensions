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

import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

/**
 * A test utility which invokes the Jupiter engine for a given {@link DiscoverySelector} or {@link
 * DiscoverySelector}s. Typically, the {@code selectors} will isolate test class or test methods.
 *
 * <p>Why do we need this? can't we just tests extensions in the same way as we test any other
 * class? Many of the extension behaviours can be tested via side effects, for example:
 *
 * <ul>
 *   <li>An extension sets a system property: this can be asserted against in a test case
 *   <li>An extension provides a temporary folder; a test case can use this temporary folder and
 *       assert that it behaves correctly
 *   <li>An extension injects random values; a test case can assert that these values are populated
 *   <li>An extension catches expected exceptions; a test case can throw exceptions and assert that
 *       they are correctly handled by the extension
 *   <li>... etc
 * </ul>
 *
 * But ... some extension behaviours cannot be tested in the normal test execution flow. For
 * example:
 *
 * <ul>
 *   <li>An extension which rethrows (rather than handles and swallows) an exception
 *   <li>An extension which performs some {@link org.junit.jupiter.api.AfterAll} or {@link
 *       org.junit.jupiter.api.AfterEach} cleanup
 *   <li>An extension which has no discernible side effect e.g. one which only logs test execution
 * </ul>
 *
 * So, we'll need to execute some extensions within a JUnit container and then assert against what
 * the container did rather than only asserting against the side effects of the extension.
 */
public class ExtensionTester {

  /**
   * Instance an engine and execute the test resources identified by the given {@code selectors} and
   * wrap the response in a listener so that we can make sense of what happened. The listener
   * exposes information about the test execution flow which the extension tests can assert against.
   *
   * @param selectors {@link DiscoverySelector} instances which will isolate test class or test
   *     methods
   * @return a {@link RecordingExecutionListener} which encapsulates what the engine did
   */
  public static RecordingExecutionListener execute(DiscoverySelector... selectors) {
    // instance an engine
    JupiterTestEngine testEngine = new JupiterTestEngine();

    // discover the requested test resources
    LauncherDiscoveryRequest discoveryRequest = request().selectors(selectors).build();

    RecordingExecutionListener listener = new RecordingExecutionListener();

    // execute the discovered test resources
    TestDescriptor testDescriptor =
        testEngine.discover(discoveryRequest, UniqueId.forEngine(testEngine.getId()));
    testEngine.execute(
        new ExecutionRequest(
            testDescriptor, listener, discoveryRequest.getConfigurationParameters()));

    return listener;
  }
}

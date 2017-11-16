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
package io.github.glytching.junit.extension.system;

import org.junit.jupiter.api.Test;

import static io.github.glytching.junit.extension.util.ExtensionTester.execute;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

/**
 * Tests the {@link SystemPropertyExtension} <em>from the outside</em>. There are other tests which
 * verify that the 'set property' side effects of the {@link SystemPropertyExtension} are correct
 * but this extension is also responsible for reverting whatever properties it set and we cannot
 * test that behaviour in the standard test flow. Instead, we have to run the test and then assert
 * that the reversion is successful <b>after</b> the test engine has completed (including invoking
 * afterEach, afterAll).
 */
public class SystemPropertyExtensionMetaTest {

  @Test
  public void classLevelSystemPropertyWillBeResetBackToTheirPreTestValue() {
    System.setProperty("classPropertyKeyC", "no");

    execute(selectClass(SystemPropertyExtensionClassTest.class));

    assertThat(System.getProperty("classPropertyKeyC"), is("no"));
  }

  @Test
  public void classLevelSystemPropertiesWillBeResetBackToTheirPreTestValue() {
    System.setProperty("classPropertyKeyA", "no");

    execute(selectClass(SystemPropertyExtensionClassTest.class));

    assertThat(System.getProperty("classPropertyKeyA"), is("no"));
  }

  @Test
  public void classLevelSystemPropertiesWillBeUnsetIfTheyHadNoPreTestValue() {
    execute(selectClass(SystemPropertyExtensionClassTest.class));

    assertThat(System.getProperty("classPropertyKeyB"), nullValue());
  }

  @Test
  public void methodLevelSystemPropertyWillBeResetBackToItsPreTestValue() {
    System.setProperty("keyC", "no");

    execute(selectMethod(SystemPropertyExtensionMethodTest.class, "canSetSystemProperty"));

    assertThat(System.getProperty("keyC"), is("no"));
  }

  @Test
  public void methodLevelSystemPropertiesWillBeResetBackToTheirPreTestValues() {
    System.setProperty("keyA", "no");

    execute(selectMethod(SystemPropertyExtensionMethodTest.class, "canSetSystemProperties"));

    assertThat(System.getProperty("keyA"), is("no"));
  }

  @Test
  public void methodLevelSystemPropertiesWillBeUnsetIfTheyHadNoPreTestValue() {
    execute(selectMethod(SystemPropertyExtensionMethodTest.class, "canSetSystemProperties"));

    assertThat(System.getProperty("keyB"), nullValue());
  }
}

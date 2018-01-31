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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SystemProperty(name = "classPropertyKeyC", value = "classPropertyValueC")
@SystemProperty(name = "classPropertyKeyA", value = "classPropertyValueA")
@SystemProperty(name = "classPropertyKeyB", value = "classPropertyValueB")
public class SystemPropertyExtensionClassTest {

  @Test
  public void canSetSystemPropertyAtClassLevel() {
    assertThat(System.getProperty("classPropertyKeyC"), is("classPropertyValueC"));
  }

  @Test
  public void canSetSystemPropertiesAtClassLevel() {
    assertThat(System.getProperty("classPropertyKeyA"), is("classPropertyValueA"));
    assertThat(System.getProperty("classPropertyKeyB"), is("classPropertyValueB"));
  }

  @Test
  @SystemProperty(name = "keyA", value = "valueA")
  public void canHandleClassAndMethodLevelSystemProperties() {
    assertThat(System.getProperty("classPropertyKeyA"), is("classPropertyValueA"));
    assertThat(System.getProperty("classPropertyKeyB"), is("classPropertyValueB"));
    assertThat(System.getProperty("classPropertyKeyC"), is("classPropertyValueC"));

    assertThat(System.getProperty("keyA"), is("valueA"));
  }
}

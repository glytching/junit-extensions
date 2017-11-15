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
package org.glytching.junit.extension.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A context object which encapsulates what the system property extension did. This allows us to
 * reverse any changes made by the extension after test execution completes. For example:
 *
 * <ul>
 *   <li>If a new system property was added then we remove it
 *   <li>If an existing system property was overwritten then we reinstate its original vlaue
 * </ul>
 */
class RestoreContext {
  private Set<String> propertyNames;
  private Map<String, String> restoreProperties;

  /**
   * Created using the {@link Builder}.
   *
   * @param propertyNames
   * @param restoreProperties
   */
  private RestoreContext(Set<String> propertyNames, Map<String, String> restoreProperties) {
    this.propertyNames = propertyNames;
    this.restoreProperties = restoreProperties;
  }

  public static Builder createBuilder() {
    return new Builder();
  }

  /**
   * Reverse the system property 'sets' performed on behalf of this restore context.
   *
   * <p>For each entry in {@link #propertyNames}, if {@link #restoreProperties} contains an entry
   * then reset the system property with the value from {@link #restoreProperties} otherwise just
   * remove the system property for that property name.
   */
  public void restore() {
    for (String propertyName : propertyNames) {
      if (restoreProperties.containsKey(propertyName)) {
        // reinstate the original value
        System.setProperty(propertyName, restoreProperties.get(propertyName));
      } else {
        // remove the (previously unset) property
        System.clearProperty(propertyName);
      }
    }
  }

  /**
   * Simple builder implementation allowing a {@link RestoreContext} to be built up as we walk
   * through system property configuration.
   */
  static class Builder {
    private Set<String> properties;
    private Map<String, String> restoreProperties;

    private Builder() {
      properties = new HashSet<>();
      restoreProperties = new HashMap<>();
    }

    void addPropertyName(String propertyName) {
      properties.add(propertyName);
    }

    void addRestoreProperty(String propertyName, String propertyValue) {
      restoreProperties.put(propertyName, propertyValue);
    }

    RestoreContext build() {
      return new RestoreContext(properties, restoreProperties);
    }
  }
}

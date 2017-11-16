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

import io.github.glytching.junit.extension.random.DomainObject;
import io.github.glytching.junit.extension.random.Random;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AssertionUtil {

  public static void assertThatDomainObjectIsFullyPopulated(DomainObject domainObject) {
    assertThat(domainObject, notNullValue());

    assertThat(domainObject.getId(), notNullValue());
    assertThat(domainObject.getId(), not(is(0)));

    assertThat(domainObject.getName(), notNullValue());
    assertThat(domainObject.getName(), not(isEmptyString()));

    assertThat(domainObject.getNestedDomainObject(), notNullValue());
    assertThat(domainObject.getNestedDomainObject().getAddress(), notNullValue());
    assertThat(domainObject.getNestedDomainObject().getCategory(), notNullValue());

    assertThat(domainObject.getWotsits(), notNullValue());
    assertThat(domainObject.getWotsits(), not(empty()));

    assertThat(domainObject.getValue(), notNullValue());
    assertThat(domainObject.getValue(), not(is(0L)));

    assertThat(domainObject.getPrice(), notNullValue());
    assertThat(domainObject.getPrice(), not(is(0d)));
  }

  public static void assertThatDomainObjectIsPartiallyPopulated(DomainObject domainObject) {
    assertThat(domainObject, notNullValue());

    assertThat(domainObject.getId(), is(0));

    assertThat(domainObject.getName(), notNullValue());
    assertThat(domainObject.getName(), not(isEmptyString()));

    assertThat(domainObject.getNestedDomainObject(), notNullValue());
    assertThat(domainObject.getNestedDomainObject().getAddress(), nullValue());
    assertThat(domainObject.getNestedDomainObject().getCategory(), notNullValue());

    assertThat(domainObject.getWotsits(), nullValue());

    assertThat(domainObject.getValue(), notNullValue());
    assertThat(domainObject.getValue(), not(is(0L)));

    assertThat(domainObject.getPrice(), notNullValue());
    assertThat(domainObject.getPrice(), not(is(0d)));
  }

  public static int getDefaultSizeOfRandom() throws Exception {
    Class<?> clazz = Random.class;
    Method method = clazz.getDeclaredMethod("size");
    return (Integer) method.getDefaultValue();
  }
}

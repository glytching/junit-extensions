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
package org.glytching.junit.extension.random;

import org.glytching.junit.extension.util.AssertionUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.glytching.junit.extension.util.AssertionUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(RandomBeansExtension.class)
public class RandomBeansExtensionFieldTest {

  // gather the random values to facilitate assertions on the distinct-ness of the value supplied to
  // each test
  private final Set<String> anyStrings = new HashSet<>();

  @Random private String anyString;

  @Random private DomainObject fullyPopulatedDomainObject;

  @Random(excludes = {"wotsits", "id", "nestedDomainObject.address"})
  private DomainObject partiallyPopulatedDomainObject;

  @Random(type = String.class)
  private List<String> anyList;

  @Random(size = 5, type = String.class)
  private List<String> anyListOfSpecificSize;

  @Random(type = String.class)
  private Set<String> anySet;

  @Random(type = String.class)
  private Stream<String> anyStream;

  @Random(type = String.class)
  private Collection<String> anyCollection;

  @Random(size = 2, type = DomainObject.class)
  private List<DomainObject> anyFullyPopulatedDomainObjects;

  @Random(
    size = 2,
    type = DomainObject.class,
    excludes = {"wotsits", "id", "nestedDomainObject.address"}
  )
  private List<DomainObject> anyPartiallyPopulatedDomainObjects;

  @Test
  public void canInjectARandomString() {
    assertThat(anyString, notNullValue());
  }

  @Test
  public void canInjectAFullyPopulatedRandomObject() {
    assertThatDomainObjectIsFullyPopulated(fullyPopulatedDomainObject);
  }

  @Test
  public void canInjectAPartiallyPopulatedRandomObject() {
    assertThatDomainObjectIsPartiallyPopulated(partiallyPopulatedDomainObject);
  }

  @Test
  public void canInjectARandomListOfDefaultSize() throws Exception {
    assertThat(anyList, notNullValue());
    assertThat(anyList, not(empty()));
    assertThat(anyList.size(), is(getDefaultSizeOfRandom()));
  }

  @Test
  public void canInjectARandomListOfSpecificSize() {
    assertThat(anyListOfSpecificSize, notNullValue());
    assertThat(anyListOfSpecificSize.size(), is(5));
  }

  @Test
  public void canInjectARandomSet() throws Exception {
    assertThat(anySet, notNullValue());
    assertThat(anySet, not(empty()));
    assertThat(anySet.size(), is(getDefaultSizeOfRandom()));
  }

  @Test
  public void canInjectARandomStream() throws Exception {
    assertThat(anyStream, notNullValue());
    //noinspection UnnecessaryBoxing
    assertThat(anyStream.count(), is(Long.valueOf(getDefaultSizeOfRandom())));
  }

  @Test
  public void canInjectARandomCollection() throws Exception {
    assertThat(anyCollection, notNullValue());
    assertThat(anyCollection, not(empty()));
    assertThat(anyCollection.size(), is(getDefaultSizeOfRandom()));
  }

  @Test
  public void canInjectRandomFullyPopulatedDomainObjects() {
    assertThat(anyFullyPopulatedDomainObjects, notNullValue());
    assertThat(anyFullyPopulatedDomainObjects.size(), is(2));
    anyFullyPopulatedDomainObjects.forEach(AssertionUtil::assertThatDomainObjectIsFullyPopulated);
  }

  @Test
  public void canInjectRandomPartiallyPopulatedDomainObjects() {
    assertThat(anyPartiallyPopulatedDomainObjects, notNullValue());
    assertThat(anyPartiallyPopulatedDomainObjects.size(), is(2));
    anyPartiallyPopulatedDomainObjects.forEach(
        AssertionUtil::assertThatDomainObjectIsPartiallyPopulated);
  }

  @RepeatedTest(5)
  public void willInjectANewRandomValueEachTime() {
    assertThat(anyString, notNullValue());

    if (anyStrings.isEmpty()) {
      anyStrings.add(anyString);
    } else {
      assertThat(
          "Received the same value twice, expected each random value to be different!",
          anyStrings,
          not(hasItem(anyString)));
      anyStrings.add(anyString);
    }
  }
}

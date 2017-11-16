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
package io.github.glytching.junit.extension.random;

import io.github.glytching.junit.extension.util.AssertionUtil;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.glytching.junit.extension.util.AssertionUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(RandomBeansExtension.class)
public class RandomBeansExtensionParameterTest {

  // gather the random values to facilitate assertions on the distinct-ness of the value supplied to
  // each test
  private final Set<String> anyStrings = new HashSet<>();

  @Test
  @ExtendWith(RandomBeansExtension.class)
  public void canInjectARandomString(@Random String anyString) {
    assertThat(anyString, notNullValue());
  }

  @Test
  public void canInjectAFullyPopulatedRandomObject(@Random DomainObject domainObject) {
    assertThatDomainObjectIsFullyPopulated(domainObject);
  }

  @Test
  public void canInjectAPartiallyPopulatedRandomObject(
      @Random(excludes = {"wotsits", "id", "nestedDomainObject.address"})
          DomainObject domainObject) {
    assertThatDomainObjectIsPartiallyPopulated(domainObject);
  }

  @Test
  public void canInjectARandomListOfDefaultSize(@Random(type = String.class) List<String> anyList)
      throws Exception {
    assertThat(anyList, notNullValue());
    assertThat(anyList, not(empty()));
    assertThat(anyList.size(), is(getDefaultSizeOfRandom()));
  }

  @Test
  public void canInjectARandomListOfSpecificSize(
      @Random(size = 5, type = String.class) List<String> anyListOfSpecificSize) {
    assertThat(anyListOfSpecificSize, notNullValue());
    assertThat(anyListOfSpecificSize.size(), is(5));
  }

  @Test
  public void canInjectARandomSet(@Random(type = String.class) Set<String> anySet)
      throws Exception {
    assertThat(anySet, notNullValue());
    assertThat(anySet, not(empty()));
    assertThat(anySet.size(), is(getDefaultSizeOfRandom()));
  }

  @Test
  public void canInjectARandomStream(@Random(type = String.class) Stream<String> anyStream)
      throws Exception {
    assertThat(anyStream, notNullValue());
    //noinspection UnnecessaryBoxing
    assertThat(anyStream.count(), is(Long.valueOf(getDefaultSizeOfRandom())));
  }

  @Test
  public void canInjectARandomCollection(
      @Random(type = String.class) Collection<String> anyCollection) throws Exception {
    assertThat(anyCollection, notNullValue());
    assertThat(anyCollection, not(empty()));
    assertThat(anyCollection.size(), is(getDefaultSizeOfRandom()));
  }

  @Test
  public void canInjectRandomFullyPopulatedDomainObjects(
      @Random(size = 2, type = DomainObject.class)
          List<DomainObject> anyFullyPopulatedDomainObjects) {
    assertThat(anyFullyPopulatedDomainObjects, notNullValue());
    assertThat(anyFullyPopulatedDomainObjects.size(), is(2));
    anyFullyPopulatedDomainObjects.forEach(AssertionUtil::assertThatDomainObjectIsFullyPopulated);
  }

  @Test
  public void canInjectRandomPartiallyPopulatedDomainObjects(
      @Random(
            size = 2,
            type = DomainObject.class,
            excludes = {"wotsits", "id", "nestedDomainObject.address"}
          )
          List<DomainObject> anyPartiallyPopulatedDomainObjects) {
    assertThat(anyPartiallyPopulatedDomainObjects, notNullValue());
    assertThat(anyPartiallyPopulatedDomainObjects.size(), is(2));
    anyPartiallyPopulatedDomainObjects.forEach(
        AssertionUtil::assertThatDomainObjectIsPartiallyPopulated);
  }

  @RepeatedTest(5)
  @ExtendWith(RandomBeansExtension.class)
  public void willInjectANewRandomValueEachTime(@Random String anyString) {
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

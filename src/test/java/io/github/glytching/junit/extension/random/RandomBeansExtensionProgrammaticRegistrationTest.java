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

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.FieldDefinitionBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.DoubleRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.glytching.junit.extension.util.AssertionUtil.getDefaultSizeOfRandom;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RandomBeansExtensionProgrammaticRegistrationTest {


    static EnhancedRandom enhancedRandom = EnhancedRandomBuilder
            .aNewEnhancedRandomBuilder()
            .exclude(FieldDefinitionBuilder
                    .field()
                    .named("wotsits")
                    .ofType(List.class)
                    .inClass(DomainObject.class)
                    .get())
            .randomize(Integer.class, IntegerRangeRandomizer.aNewIntegerRangeRandomizer(0, 10))
            .randomize(String.class, StringRandomizer.aNewStringRandomizer(5))
            .randomize(Double.class, DoubleRangeRandomizer.aNewDoubleRangeRandomizer(0.0, 10.0))
            .build();

    @RegisterExtension
    static RandomBeansExtension randomBeansExtension = new RandomBeansExtension(enhancedRandom);

    // gather the random values to facilitate assertions on the distinct-ness of the value supplied to
    // each test
    private final Set<String> anyStrings = new HashSet<>();



    @Test
    @RepeatedTest(5)
    public void canOverrideDefaultIntegerRangeByProgrammaticExtensionRegistration(
            @Random(type = Integer.class) Integer anyInteger) throws Exception {
        assertThat(anyInteger, notNullValue());
        assertThat(anyInteger, lessThanOrEqualTo(10));
        assertThat(anyInteger, greaterThanOrEqualTo(0));
    }

    @Test
    @RepeatedTest(5)
    public void canOverrideDefaultDoubleRangeByProgrammaticExtensionRegistration(
            @Random(type = Double.class) Double anyDouble) throws Exception {
        assertThat(anyDouble, notNullValue());
        assertThat(anyDouble, lessThanOrEqualTo(10.0));
    }

    @Test
    @RepeatedTest(5)
    public void canOverrideDefaultMaxLengthOfStringByProgrammaticExtensionRegistration(
            @Random(type = String.class) String anySting) throws Exception {
        assertThat(anySting, notNullValue());
        assertThat(anySting.length(), lessThanOrEqualTo(5));
    }

    @Test
    @DisplayName("Should Inject Random values with random bean default behaviour if no Overrides are provided")
    public void canInjectAttributesIfNoOverridesAreProvided(@Random Long someRandomLong) throws Exception {
        assertThat(someRandomLong, notNullValue());
    }

    @Test
    public void canInjectAPartiallyPopulatedRandomObjectWithProgrammaticExtensionRegistration(@Random DomainObject domainObject) {
        assertThat(domainObject.getWotsits(), nullValue());
    }

    @Test
    public void canInjectARandomListOfDefaultSizeWithProgrammaticExtensionRegistration(@Random(type = String.class) List<String> anyList)
            throws Exception {
        assertThat(anyList, notNullValue());
        assertThat(anyList, not(empty()));
        assertThat(anyList.size(), is(getDefaultSizeOfRandom()));
    }

    @Test
    public void canInjectARandomListOfSpecificSizeWithProgrammaticExtensionRegistration(
            @Random(size = 5, type = String.class) List<String> anyListOfSpecificSize) {
        assertThat(anyListOfSpecificSize, notNullValue());
        assertThat(anyListOfSpecificSize.size(), is(5));
    }

    @Test
    public void canInjectARandomSetWithProgrammaticExtensionRegistration(@Random(type = String.class) Set<String> anySet)
            throws Exception {
        assertThat(anySet, notNullValue());
        assertThat(anySet, not(empty()));
        assertThat(anySet.size(), is(getDefaultSizeOfRandom()));
    }

    @Test
    public void canInjectARandomStreamWithProgrammaticExtensionRegistration(@Random(type = String.class) Stream<String> anyStream)
            throws Exception {
        assertThat(anyStream, notNullValue());
        //noinspection UnnecessaryBoxing
        assertThat(anyStream.count(), is(Long.valueOf(getDefaultSizeOfRandom())));
    }

    @Test
    public void canInjectARandomCollectionWithProgrammaticExtensionRegistration(
            @Random(type = String.class) Collection<String> anyCollection) throws Exception {
        assertThat(anyCollection, notNullValue());
        assertThat(anyCollection, not(empty()));
        assertThat(anyCollection.size(), is(getDefaultSizeOfRandom()));
    }


    @RepeatedTest(5)
    public void willInjectANewRandomValueEachTimeWithProgrammaticExtensionRegistration(@Random String anyString) {
        System.out.println(anyString);
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

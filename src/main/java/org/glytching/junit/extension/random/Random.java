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

import java.lang.annotation.*;

/**
 * Allows the caller to customise the random generaiton of a given type.
 *
 * <p>Usage example:
 *
 * <pre>
 *  // create a random instance of String
 *  &#064;Random String anyString;
 *
 *  // create a random, fully populated instance of MyDomainObject
 *  &#064;Random private DomainObject fullyPopulatedDomainObject;
 *
 *  // create a random, partially populated instance of MyDomainObject, ignoring these fields: "wotsits", "id", "nestedDomainObject.address"
 *  &#064;Random(excludes = {"wotsits", "id", "nestedDomainObject.address"}) MyDomainObject partiallyPopulatedDomainObject;
 *
 *  // create a List containing the default size of randomly generated instances of String
 *  &#064;Random(type = String.class) List<String> anyStrings;
 *
 *  // create a Stream containing two randomly generated instances of MyDomainObject
 *  &#064;Random(size = 2, type = MyDomainObject.class) Stream<MyDomainObject> anyStrings;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Random {

  /**
   * When generating a random type you may want to exclude some properties
   *
   * @return an array of property names to be excluded when generating a random instance of a given
   *     type
   */
  String[] excludes() default "[]";

  /**
   * When generating a collection of random type you may want to limit its size.
   *
   * @return the desired size of any collections within the randomly generated type
   */
  int size() default 10;

  /**
   * When generating a collection of random type you'll want to tell the generator what that type
   * <b>is</b>.
   *
   * @return the type of a randomly generated generic collection
   */
  Class<?> type() default Object.class;
}

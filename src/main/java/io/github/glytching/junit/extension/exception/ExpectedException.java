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
package io.github.glytching.junit.extension.exception;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

/**
 * Describes the expectations for an exception, including:
 *
 * <ul>
 *   <li>The exception class
 *   <li>Case sensitive matchers on the exception message in the form of:
 *       <ul>
 *         <li>Message matches exactly
 *         <li>Message starts with
 *         <li>Message contains
 *       </ul>
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>
 *  // match an exception of type Exception containing the message "Boom!"
 *  &#064;ExpectedException(type = RuntimeException.class, messageIs = "Boom!")
 *
 *  // match an exception of type RuntimeException containing a message which starts with "Bye"
 *  &#064;ExpectedException(type = RuntimeException.class, messageStartsWith = "Bye")
 *
 *  // match an exception of type MyDomainException having a message which contains "ouch"
 *  &#064;ExpectedException(type = MyDomainException.class, messageContains = "ouch")
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@ExtendWith(ExpectedExceptionExtension.class)
public @interface ExpectedException {

  Class<? extends Throwable> type();

  String messageIs() default "";

  String messageStartsWith() default "";

  String messageContains() default "";
}

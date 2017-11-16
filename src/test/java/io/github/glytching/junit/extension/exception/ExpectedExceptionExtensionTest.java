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

import org.junit.jupiter.api.Test;

public class ExpectedExceptionExtensionTest {

  @Test
  @ExpectedException(type = Throwable.class, messageIs = "Boom!")
  public void canHandleAThrowable() throws Throwable {
    throw new Throwable("Boom!");
  }

  @Test
  @ExpectedException(type = Exception.class, messageIs = "Boom!")
  public void canHandleAnException() throws Exception {
    throw new Exception("Boom!");
  }

  @Test
  @ExpectedException(type = RuntimeException.class, messageIs = "Boom!")
  public void canHandleARuntimeException() {
    throw new RuntimeException("Boom!");
  }

  @Test
  @ExpectedException(type = RuntimeException.class, messageStartsWith = "Bye")
  public void canHandleAnExceptionWithAMessageWhichStartsWith() {
    throw new RuntimeException("Bye bye");
  }

  @Test
  @ExpectedException(type = RuntimeException.class, messageContains = "sorry")
  public void canHandleAnExceptionWithAMessageWhichContains() {
    throw new RuntimeException("Terribly sorry old chap");
  }
}

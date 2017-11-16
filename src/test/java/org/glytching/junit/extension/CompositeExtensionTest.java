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
package org.glytching.junit.extension;

import org.glytching.junit.extension.exception.ExpectedException;
import org.glytching.junit.extension.folder.TemporaryFolder;
import org.glytching.junit.extension.folder.TemporaryFolderExtension;
import org.glytching.junit.extension.random.Random;
import org.glytching.junit.extension.random.RandomBeansExtension;
import org.glytching.junit.extension.system.SystemProperty;
import org.glytching.junit.extension.watcher.WatcherExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SystemProperty(name = "x", value = "y")
@ExtendWith({RandomBeansExtension.class, WatcherExtension.class})
public class CompositeExtensionTest {

  @Random private String anyString;

  @Test
  @ExtendWith(TemporaryFolderExtension.class)
  @ExpectedException(type = RuntimeException.class, messageIs = "Doh!")
  public void canHandleTheKitchenSink(TemporaryFolder temporaryFolder, @Random Long anyLong)
      throws IOException {
    // randomness
    assertThat(anyString, notNullValue());
    assertThat(anyLong, notNullValue());

    // system property
    assertThat(System.getProperty("x"), is("y"));

    // temporary folder
    File file = temporaryFolder.createFile("foo.txt");
    assertThat(file.exists(), is(true));

    // expected exception
    throw new RuntimeException("Doh!");
  }
}

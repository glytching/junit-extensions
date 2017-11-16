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
package io.github.glytching.junit.extension;

import io.github.glytching.junit.extension.exception.ExpectedException;
import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.github.glytching.junit.extension.system.SystemProperty;
import io.github.glytching.junit.extension.watcher.WatcherExtension;
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

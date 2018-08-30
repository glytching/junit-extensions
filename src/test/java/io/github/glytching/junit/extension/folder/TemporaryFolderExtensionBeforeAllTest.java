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
package io.github.glytching.junit.extension.folder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@ExtendWith(TemporaryFolderExtension.class)
public class TemporaryFolderExtensionBeforeAllTest {

  private static TemporaryFolder TEMPORARY_FOLDER;

  @BeforeAll
  public static void setUp(TemporaryFolder givenTemporaryFolder) {
    TEMPORARY_FOLDER = givenTemporaryFolder;
  }

  @AfterAll
  public static void cleanUp() throws IOException {
    try (Stream<Path> stream = Files.list(TEMPORARY_FOLDER.getRoot().toPath())) {
      Set<String> createdFileNames =
          stream.map(path -> path.toFile().getName()).collect(Collectors.toSet());

      // when using a static TemporaryFolder, every test gets the same instance so in AfterAll the
      // folder should contain all artifacts created by all tests in this test case
      assertThat(createdFileNames.size(), is(2));
      assertThat(createdFileNames, hasItem("foo.txt"));
      assertThat(createdFileNames, hasItem("bar"));
    }
  }

  @Test
  public void canCreateAFile() throws IOException {
    File file = TEMPORARY_FOLDER.createFile("foo.txt");
    assertThat(file.exists(), is(true));
  }

  @Test
  public void canCreateADirectory() throws IOException {
    File file = TEMPORARY_FOLDER.createDirectory("bar");
    assertThat(file.exists(), is(true));
  }
}

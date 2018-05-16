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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TemporaryFolderExtensionParameterTest {

  // gather the temporary file and directory paths to facilitate assertions on (a) the distinct-ness
  // of the temporary folder address supplied to each test and (b) the removal of each temporary
  // folder on test completion
  private static final Set<String> temporaryFilePaths = new HashSet<>();
  private static final Set<String> temporaryDirectoryPaths = new HashSet<>();

  @AfterAll
  public static void allTemporaryFilesAreDeleted() {
    List<String> existingFiles =
        temporaryFilePaths
            .stream()
            .filter(temporaryFilePath -> Files.exists(Paths.get(temporaryFilePath)))
            .collect(Collectors.toList());

    assertThat(existingFiles, empty());
  }

  @AfterAll
  public static void allTemporaryDirectoriesAreDeleted() {
    List<String> existingDirectories =
        temporaryDirectoryPaths
            .stream()
            .filter(temporaryFileDirectory -> Files.exists(Paths.get(temporaryFileDirectory)))
            .collect(Collectors.toList());

    assertThat(existingDirectories, empty());
  }

  @Test
  @ExtendWith(TemporaryFolderExtension.class)
  public void canInjectATemporaryFolderAsAParameter(TemporaryFolder temporaryFolder)
      throws IOException {
    File file = temporaryFolder.createFile("foo.txt");

    assertThat(file.exists(), is(true));

    File dir = temporaryFolder.createDirectory("bar");

    assertThat(dir.exists(), is(true));
  }

  @Test
  @ExtendWith(TemporaryFolderExtension.class)
  public void canGetTheRootFolderWhenATemporaryFolderIsInjectedAsAParameter(TemporaryFolder temporaryFolder)
      throws IOException {
    File root = temporaryFolder.getRoot();

    assertThat(root.exists(), is(true));

    File dir = temporaryFolder.createDirectory("bar");
    assertThat(dir.getParentFile(), is(root));
  }

  @RepeatedTest(5)
  @ExtendWith(TemporaryFolderExtension.class)
  public void willCreateANewTemporaryFileEveryTime(TemporaryFolder temporaryFolder)
      throws IOException {
    File file = temporaryFolder.createFile("foo.txt");

    assertThat(file.exists(), is(true));

    if (temporaryFilePaths.isEmpty()) {
      temporaryFilePaths.add(file.getAbsolutePath());
    } else {
      assertThat(
          "Received the same value twice, expected each random value to be different!",
          temporaryFilePaths,
          not(hasItem(file.getAbsolutePath())));
      temporaryFilePaths.add(file.getAbsolutePath());
    }
  }

  @RepeatedTest(5)
  @ExtendWith(TemporaryFolderExtension.class)
  public void willCreateANewTemporaryDirectoryEveryTime(TemporaryFolder temporaryFolder) {
    File dir = temporaryFolder.createDirectory("bar");

    assertThat(dir.exists(), is(true));

    if (temporaryDirectoryPaths.isEmpty()) {
      temporaryDirectoryPaths.add(dir.getAbsolutePath());
    } else {
      assertThat(
          "Received the same value twice, expected each random value to be different!",
          temporaryDirectoryPaths,
          not(hasItem(dir.getAbsolutePath())));
      temporaryDirectoryPaths.add(dir.getAbsolutePath());
    }
  }
}

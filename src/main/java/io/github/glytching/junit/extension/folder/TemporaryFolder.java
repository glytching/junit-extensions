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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Encapsulates the {@link #rootFolder} within which any files or directories will be created along
 * with the operations which a tester may wish to invoke ({@link #createFile(String)}, {@link
 * #createDirectory(String)}) and post test invocations which the associated extension will invoke.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class TemporaryFolder {
  private static final String FILE_PREFIX = "junit";
  private static final String FILE_SUFFIX = ".tmp";

  /**
   * The root folder within which any files or directories will be created, on {@link #destroy()}
   * this folder and all of its contents will be silently deleted.
   */
  private final File rootFolder;

  /**
   * Package protected since a {@link TemporaryFolder}'s lifecycle is expected to be controlled by
   * its associated extension.
   */
  TemporaryFolder() {
    try {
      rootFolder = File.createTempFile(FILE_PREFIX, FILE_SUFFIX);
      rootFolder.delete();
      rootFolder.mkdir();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Create a file within the temporary folder root.
   *
   * @param fileName the name of the file to be created
   * @return the newly created file instance
   * @throws IOException in case the file creation call fails
   */
  public File createFile(String fileName) throws IOException {
    File result = new File(rootFolder, fileName);

    result.createNewFile();

    return result;
  }

  /**
   * Create a directory within the temporary folder root.
   *
   * @param directoryName the name of the directory to be created
   * @return the directory instance
   */
  public File createDirectory(String directoryName) {
    File result = new File(rootFolder, directoryName);

    result.mkdirs();

    return result;
  }

  /**
   * Deletes the {@link #rootFolder} and all of its contents. This is package protected because a
   * {@link TemporaryFolder}'s lifecycle is expected to be controlled by its associated extension.
   *
   * <p><b>Note</b>: any exception encountered during deletion will be swallowed.
   */
  void destroy() throws IOException {
    if (rootFolder.exists()) {
      // walk the contents deleting each
      Files.walkFileTree(
          rootFolder.toPath(),
          new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
              return delete(file);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException exception)
                throws IOException {
              return delete(directory);
            }

            @SuppressWarnings("SameReturnValue")
            private FileVisitResult delete(Path file) throws IOException {
              Files.delete(file);
              return CONTINUE;
            }
          });

      // delete the parent
      rootFolder.delete();
    }
  }
}

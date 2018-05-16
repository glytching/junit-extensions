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

import org.junit.jupiter.api.extension.*;

import static io.github.glytching.junit.extension.util.ExtensionUtil.getStore;

/**
 * The temporary folder extension provides a test with access to temporary files and directories.
 * The temporary folder extension provides a {@link TemporaryFolder} which you can use to create a
 * temporary file or directory for use by your test. The {@link TemporaryFolder} can be injected
 * into your test or test case with either of the following approaches:
 *
 * <ul>
 *   <li>Parameter injection into a {@code @BeforeEach} method. For example:
 *       <pre>
 *  private TemporaryFolder temporaryFolder;
 *
 *  &#064;BeforeEach
 *  public void setUp(TemporaryFolder temporaryFolder) {
 *      this.temporaryFolder = temporaryFolder
 *      // ...
 *  }
 * </pre>
 *   <li>Parameter injection into a {@code @Test} method. For example:
 *       <pre>
 *  &#064;Test
 *  public void testUsingTemporaryFolder(TemporaryFolder temporaryFolder) {
 *      // ...
 *  }
 * </pre>
 * </ul>
 *
 * <p>In both approaches the {@link TemporaryFolder} will be destroyed during {@code @AfterEach} and
 * no exception will be thrown in cases where the deletion fails.
 *
 * <p>Usage examples:
 *
 * <p>Injecting a {@code TemporaryFolder} in a {@code @BeforeEach} method:
 *
 * <pre>
 * &#064;ExtendWith(TemporaryFolderExtension.class)
 * public class MyTest {
 *
 *     private TemporaryFolder temporaryFolder;
 *
 *     &#064;BeforeEach
 *     public void setUp(TemporaryFolder temporaryFolder) {
 *         this.temporaryFolder = temporaryFolder
 *         // ...
 *     }
 *
 *     &#064;Test
 *     public void testUsingTemporaryFile() {
 *         File file = temporaryFolder.createFile("foo.txt");
 *         // ...
 *     }
 *
 *     &#064;Test
 *     public void testUsingTemporaryDirectory() {
 *         // use the temporary folder itself
 *         File root = temporaryFolder.getRoot();
 *
 *         // create a sub directory within the temporary folder
 *         File file = temporaryFolder.createDirectory("foo");
 *         // ...
 *     }
 * }
 * </pre>
 *
 * <p>Injecting a {@code TemporaryFolder} in a {@code @Test} method:
 *
 * <pre>
 * public class MyTest {
 *
 *     &#064;Test
 *     &#064;ExtendWith(TemporaryFolderExtension.class)
 *     public void testUsingTemporaryFile(TemporaryFolder temporaryFolder) {
 *         File file = temporaryFolder.createFile("foo.txt");
 *         // ...
 *     }
 *
 *     &#064;Test
 *     &#064;ExtendWith(TemporaryFolderExtension.class)
 *     public void testUsingTemporaryDirectory(TemporaryFolder temporaryFolder) {
 *         // use the temporary folder itself
 *         File root = temporaryFolder.getRoot();
 *
 *         // create a sub directory within the temporary folder
 *         File file = temporaryFolder.createDirectory("foo");
 *         // ...
 *     }
 * }
 * </pre>
 *
 * @see <a href="https://github.com/junit-team/junit4/wiki/Rules#temporaryfolder-rule">JUnit 4
 *     TemporaryFolder Rule</a>
 * @since 1.0.0
 */
public class TemporaryFolderExtension implements AfterEachCallback, ParameterResolver {

  private static final String KEY = "temporaryFolder";

  /**
   * If there is a {@link TemporaryFolder} associated with the current {@code extensionContext} then
   * destroy it.
   *
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   */
  @Override
  public void afterEach(ExtensionContext extensionContext) {
    TemporaryFolder temporaryFolder =
        getStore(extensionContext, this.getClass()).get(KEY, TemporaryFolder.class);
    if (temporaryFolder != null) {
      try {
        temporaryFolder.destroy();
      } catch (Exception e) {
        // silent failures
      }
    }
  }

  /**
   * Does this extension support injection for parameters of the type described by the given {@code
   * parameterContext}?
   *
   * @param parameterContext the context for the parameter for which an argument should be resolved
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return true if the given {@code parameterContext} describes a parameter of type: {@link
   *     TemporaryFolder}, false otherwise
   * @throws ParameterResolutionException
   */
  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return appliesTo(parameterContext.getParameter().getType());
  }

  /**
   * Provides a value for any parameter context which has passed the {@link
   * #supportsParameter(ParameterContext, ExtensionContext)} gate.
   *
   * @param parameterContext the context for the parameter for which an argument should be resolved
   * @param extensionContext the <em>context</em> in which the current test or container is being
   *     executed
   * @return a new {@link TemporaryFolder}
   * @throws ParameterResolutionException
   */
  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return getStore(extensionContext, this.getClass())
        .getOrComputeIfAbsent(KEY, key -> new TemporaryFolder());
  }

  private boolean appliesTo(Class<?> clazz) {
    return clazz == TemporaryFolder.class;
  }
}

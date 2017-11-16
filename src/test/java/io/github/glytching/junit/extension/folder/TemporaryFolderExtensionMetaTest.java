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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * We cannot use the <em>normal</em> test flow to verify all aspects of the {@link
 * TemporaryFolderExtension}. Specifically:
 *
 * <ul>
 *   <li>The post test clean up occurs <b>after</b> test completion
 *   <li>The 'swallow exceptions during file/folder deletion' behaviour cannot be simulated from a
 *       test case which uses the extension since the internals are not exposed.
 * </ul>
 *
 * In addition, we cannot test these aspects using the {@link
 * io.github.glytching.junit.extension.util.ExtensionTester} because that has no access to the extension's
 * internals.
 *
 * <p>So, we test this extension behaviour by playing around with the {@link
 * TemporaryFolderExtension} directly.
 */
public class TemporaryFolderExtensionMetaTest {

  private final TemporaryFolderExtension sut = new TemporaryFolderExtension();
  @Mock private ExtensionContext extensionContext;
  @Mock private Store store;
  @Mock private TemporaryFolder temporaryFolder;

  @BeforeEach
  public void prepare() {
    MockitoAnnotations.initMocks(this);

    when(extensionContext.getStore(
            Namespace.create(TemporaryFolderExtension.class, extensionContext)))
        .thenReturn(store);
  }

  @Test
  void willDestroyTemporaryFolderAfterEach() throws IOException {
    when(store.get(any(String.class), eq(TemporaryFolder.class))).thenReturn(temporaryFolder);

    sut.afterEach(extensionContext);

    verify(temporaryFolder).destroy();
  }

  @Test
  void willSwallowAnyExceptionEncounteredWhenDestroyingTheTemporaryFolder() throws IOException {
    when(store.get(any(String.class), eq(TemporaryFolder.class))).thenReturn(temporaryFolder);

    doThrow(new RuntimeException("Boom!")).when(temporaryFolder).destroy();

    sut.afterEach(extensionContext);
  }
}

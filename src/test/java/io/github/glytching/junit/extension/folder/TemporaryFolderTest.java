package io.github.glytching.junit.extension.folder;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TemporaryFolderTest {

  @Test
  public void willThrowAnExceptionIfTheGivenDirectoryNameIsInvalid() throws IOException {
    TemporaryFolder temporaryFolder = new TemporaryFolder();

    // should cover invalid characters on all platforms
    String invalidDirectoryName = "\\\\/:*?\\\"<>|/:";
    assertThrows(
        TemporaryFolderException.class,
        () -> temporaryFolder.createDirectory(invalidDirectoryName));
  }
}

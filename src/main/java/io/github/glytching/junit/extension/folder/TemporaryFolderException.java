package io.github.glytching.junit.extension.folder;

public class TemporaryFolderException extends RuntimeException {

    public TemporaryFolderException(String message, Exception cause) {
        super(message, cause);
    }
}
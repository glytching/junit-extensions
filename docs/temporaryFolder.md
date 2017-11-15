`TemporaryFolderExtension`
======

The JUnit 4 `TemporaryFolder` rule allowed for the creation of files and folders that are deleted when the test method finished (whether the test method passed or not). By default no exception was thrown if the file system resources could not be deleted.

This extension offers the same features as JUnit4's `TemporaryFolder` rule and is fully compatible with JUnit5. 

#### Usage

This extension is engaged by adding the `@ExtendWith` annotation to a test class or a test method. This annotation results in a `TemporaryFolder` instance being injected into the test case or test method. You can then invoke methods on `TemporaryFolder` to create files or directories for use by your test(s).

#### Examples

###### Class Level `TemporaryFolder`

```
@ExtendWith(TemporaryFolderExtension.class)
public class MyTest {

    private TemporaryFolder temporaryFolder;
 
    @Test
    public void canUseTemporaryFolder() throws IOException {
        File file = temporaryFolder.createFile("foo.txt");
        assertThat(file.exists(), is(true));
 
        File dir = temporaryFolder.createDirectory("bar");
        assertThat(dir.exists(), is(true));
    } 
}
```

###### Method Level `TemporaryFolder`

```
public class MyTest {

    private TemporaryFolder temporaryFolder;
 
    @Test
    @ExtendWith(TemporaryFolderExtension.class)
    public void canUseTemporaryFolder(TemporaryFolder temporaryFolder) throws IOException {
        File file = temporaryFolder.createFile("foo.txt");
        assertThat(file.exists(), is(true));
    
        File dir = temporaryFolder.createDirectory("bar");
        assertThat(dir.exists(), is(true));
    }
}
```

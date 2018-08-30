TemporaryFolderExtension
======

The JUnit 4 `TemporaryFolder` rule allowed for the creation of files and folders that are deleted when the test method finished (whether the test method passed or not). By default no exception was thrown if the file system resources could not be deleted.

This extension offers the same features as JUnit4's `TemporaryFolder` rule and is fully compatible with JUnit Jupiter. 

#### Usage

This extension is engaged by adding the `@ExtendWith` annotation to a test class or a test method. This annotation results in a `TemporaryFolder` instance being injected into the test case or test method. You can then invoke methods on `TemporaryFolder` to create files or directories for use by your test(s).

#### Examples

###### Instance Variable TemporaryFolder

```
@ExtendWith(TemporaryFolderExtension.class)
public class MyTest {

    private TemporaryFolder temporaryFolder;
 
    @BeforeEach
    public void prepare(TemporaryFolder temporaryFolder) {
        this.temporaryFolder = temporaryFolder;
    }
    
    @Test
    public void canUseTemporaryFolder() throws IOException {
        // use the temporary folder itself
        File root = temporaryFolder.getRoot();

        // create a file within the temporary folder
        File file = temporaryFolder.createFile("foo.txt");
        assertThat(file.exists(), is(true));
 
        // create a directory within the temporary folder
        File dir = temporaryFolder.createDirectory("bar");
        assertThat(dir.exists(), is(true));
    } 
}
```

###### Method Level TemporaryFolder

```
public class MyTest {

    @Test
    @ExtendWith(TemporaryFolderExtension.class)
    public void canUseTemporaryFolder(TemporaryFolder temporaryFolder) throws IOException {
        // use the temporary folder itself
        File root = temporaryFolder.getRoot();

        // create a file within the temporary folder    
        File file = temporaryFolder.createFile("foo.txt");
        assertThat(file.exists(), is(true));
    
        // create a directory within the temporary folder
        File dir = temporaryFolder.createDirectory("bar");
        assertThat(dir.exists(), is(true));
    }
}
```

###### Class Variable TemporaryFolder

```
@ExtendWith(TemporaryFolderExtension.class)
public class MyTest {

    private static TemporaryFolder TEMPORARY_FOLDER;

    @BeforeAll
    public void prepare(TemporaryFolder givenTemporaryFolder) {
        this.TEMPORARY_FOLDER = givenTemporaryFolder;
    }
     
    @Test
    public void canUseTemporaryFolder() throws IOException {
        // use the temporary folder itself
        File root = TEMPORARY_FOLDER.getRoot();

        // create a file within the temporary folder
        File file = TEMPORARY_FOLDER.createFile("foo.txt");
        assertThat(file.exists(), is(true));
 
        // create a directory within the temporary folder
        File dir = TEMPORARY_FOLDER.createDirectory("bar");
        assertThat(dir.exists(), is(true));
    } 
}
```
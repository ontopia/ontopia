
package net.ontopia.infoset.content;

import java.io.*;
import java.util.*;
import java.net.URL;
import net.ontopia.utils.FileUtils;

public class FileContentStoreTest extends AbstractContentStoreTest {
  
  public FileContentStoreTest(String name) {
    super(name);
  }

  public void setUp() throws IOException, ContentStoreException {
    File tstdir = getTestDirectory("content");

    if (tstdir.exists()) {
      File[] files = tstdir.listFiles();
      for (int ix = 0; ix < files.length; ix++)
        FileUtils.delete(files[ix], true);
    } else
      tstdir.mkdir();

    store = new FileContentStore(tstdir);
  }

  // --- Specific tests

  public void testCloseAndReopen() throws IOException, ContentStoreException {
    // add one entry
    String CONTENT = "adding whatever entry";
    int key = store.add(getStream(CONTENT.getBytes()));

    for (int ix = 0; ix < 3; ix++) {
      // close and reopen
      store.close();
      File tstdir = getTestDirectory("content");
      store = new FileContentStore(tstdir);

      // is the old entry OK?
      assertTrue("Entry added before close/reopen is missing",
                 store.containsKey(key));
      compare(key, CONTENT.getBytes());

      // can we add new entries?
      CONTENT = "new entry";
      key = store.add(getStream(CONTENT.getBytes()));
      compare(key, CONTENT.getBytes());
    }
  }

  public File getTestDirectory() {
    String clsUri = getClass().getName().replace('.','/') + ".class";
    URL url = getClass().getClassLoader().getResource(clsUri);
    String clsPath = url.getPath().replaceAll("%20", " ");
    File root = new File(clsPath.substring(0, clsPath.length() - clsUri.length()));
    return new File(root.getParentFile(), "test-data");
  }
  public File getTestDirectory(String subDirectory) {
    File testDirectory = getTestDirectory();
    testDirectory.mkdir();
    return new File(testDirectory, subDirectory);
  }
	
}

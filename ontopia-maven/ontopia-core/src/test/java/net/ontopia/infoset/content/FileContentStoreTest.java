
// $Id: FileContentStoreTest.java,v 1.2 2004/01/28 13:36:50 larsga Exp $

package net.ontopia.infoset.content.test;

import java.io.*;
import java.util.*;
import net.ontopia.test.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.infoset.content.*;

public class FileContentStoreTest extends AbstractContentStoreTest {
  
  public FileContentStoreTest(String name) {
    super(name);
  }

  public void setUp() throws IOException, ContentStoreException {
    File tstdir = new File(getTestDirectory() + File.separator + "content");

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
      File tstdir = new File(getTestDirectory() + File.separator + "content");
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
}

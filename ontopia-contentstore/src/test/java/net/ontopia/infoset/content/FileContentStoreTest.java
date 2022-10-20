/*
 * #!
 * Ontopia Content Store
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.infoset.content;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileContentStoreTest extends AbstractContentStoreTest {
  
  @Before
  public void setUp() throws IOException, ContentStoreException {
    File tstdir = getTestDirectory("content");

    if (tstdir.exists()) {
      File[] files = tstdir.listFiles();
      for (int ix = 0; ix < files.length; ix++) {
        FileUtils.forceDelete(files[ix]);
      }
    } else {
      tstdir.mkdir();
    }

    store = new FileContentStore(tstdir);
  }

  // --- Specific tests

  @Test
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
      Assert.assertTrue("Entry added before close/reopen is missing",
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

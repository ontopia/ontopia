
package net.ontopia.topicmaps.utils;

import java.io.*;
import junit.framework.TestCase;

import net.ontopia.infoset.core.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.FileUtils;

public abstract class AbstractUtilsTestCase extends TestCase {

  private final static String testdataDirectory = "various";

  protected final static String FILE_SEPARATOR = System.getProperty("file.separator");

  protected LocatorIF baseAddress;
  protected TopicMapIF tm;


  public AbstractUtilsTestCase(String name) {
    super(name);
  }

  protected TopicMapIF getTopicMap() {
    return tm;
  }
  
  protected TopicIF getTopic(String fragId) {
    LocatorIF l = baseAddress.resolveAbsolute("#" + fragId);
    return (TopicIF) tm.getObjectByItemIdentifier(l);
  }

  protected void readFile(String fileName) {
    try {
      TopicMapReaderIF reader = ImportExportUtils.getReader(FileUtils.getTestInputFile(testdataDirectory, fileName));
      tm = reader.read();
      baseAddress = tm.getStore().getBaseAddress();
    } catch(IOException ex) {
      assertTrue("Topic map read failed!\n" + ex.getMessage(), false);
    }
  }
   
}


package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.*;
import java.util.*;
import org.xml.sax.SAXException;

import net.ontopia.utils.EncryptionUtils;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.impl.basic.*;
import net.ontopia.topicmaps.nav2.utils.*;
import net.ontopia.utils.FileUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptedModuleTest {

  private final static String testdataDirectory = "nav2";

  @Before
  public void setUp() throws IOException {
    // create encrypted module file
    File inFile = FileUtils.getTransferredTestInputFile(testdataDirectory, "functions", "plainTest.jsm");
    File outFile = FileUtils.getTestOutputFile(testdataDirectory, "out", "testEncWriterOut.jsm");
    EncryptionUtils.encrypt(inFile, outFile);
  }
   

  @Test
  public void testVerifyWritten() throws IOException, SAXException {
    File inFile = FileUtils.getTestOutputFile(testdataDirectory, "out", "testEncWriterOut.jsm");
    ModuleReaderIF modReader = new ModuleReader(true);
    Map funcs = modReader.read(new FileInputStream(inFile));

    Assert.assertTrue("Could not retrieve correct number of functions from encrypted module file.",
               funcs.size() == 1);
    
    Assert.assertTrue("Could not retrieve function 'names' from encrypted module file.",
               funcs.containsKey("names"));
    
    Assert.assertTrue("Object is not instance of FunctionIF.",
               funcs.get("names") instanceof FunctionIF); 
  }

}


package net.ontopia.topicmaps.nav2.utils;

import java.io.*;
import java.util.*;
import org.xml.sax.SAXException;

import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.utils.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.StreamUtils;

import org.junit.Assert;
import org.junit.Test;

public class ModuleReaderTest {

  private final static String testdataDirectory = "nav2";

  @Test
  public void testPlain() throws IOException, SAXException {
    String inpFile = TestFileUtils.getTestInputFile(testdataDirectory, "functions", "plainTest.jsm");
    ModuleReaderIF modReader = new ModuleReader(false);
    Map funcs = modReader.read(StreamUtils.getInputStream(inpFile));

    Assert.assertTrue("Could not retrieve correct number of functions from plain module file",
               funcs.size() == 1);
    
    Assert.assertTrue("Could not retrieve function 'names' from plain module file",
               funcs.containsKey("names"));
    
    Assert.assertTrue("Object is not instance of FunctionIF.",
               funcs.get("names") instanceof FunctionIF); 
  }
  
  @Test
  public void testEncrypted() throws IOException, SAXException {
    String inpFile = TestFileUtils.getTestInputFile(testdataDirectory, "functions", "encryptedTest.jsm");
    ModuleReaderIF modReader = new ModuleReader(true);
    Map funcs = modReader.read(StreamUtils.getInputStream(inpFile));

    Assert.assertTrue("Could not retrieve correct number of functions from encrypted module file",
               funcs.size() == 1);
    
    Assert.assertTrue("Could not retrieve function 'names' from encrypted module file",
               funcs.containsKey("names"));
    
    Assert.assertTrue("Object is not instance of FunctionIF.",
               funcs.get("names") instanceof FunctionIF); 
  }
  
}







// $Id: EncryptedModuleTest.java,v 1.5 2002/09/11 13:26:26 niko Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.*;
import java.util.*;
import org.xml.sax.SAXException;

import net.ontopia.utils.EncryptionUtils;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.impl.basic.*;
import net.ontopia.topicmaps.nav2.utils.*;

import net.ontopia.test.AbstractOntopiaTestCase;

public class EncryptedModuleTest extends AbstractOntopiaTestCase {

  String baseInDir;
  String baseOutDir;
  
  public EncryptedModuleTest(String name) {
    super(name);
  }

  public void setUp() throws IOException {
    String root = getTestDirectory();
    verifyDirectory(root, "nav2", "out");
    baseInDir = root + File.separator + "nav2" + File.separator + "functions";
    baseOutDir = root + File.separator + "nav2" + File.separator + "out";
    // create encrypted module file
    File inFile = new File(baseInDir, "plainTest.jsm");
    File outFile = new File(baseOutDir, "testEncWriterOut.jsm");
    EncryptionUtils.encrypt(inFile, outFile);
  }
   

  public void testVerifyWritten() throws IOException, SAXException {
    File inFile = new File(baseOutDir, "testEncWriterOut.jsm");
    ModuleReaderIF modReader = new ModuleReader(true);
    Map funcs = modReader.read(new FileInputStream(inFile));

    assertTrue("Could not retrieve correct number of functions from encrypted module file.",
               funcs.size() == 1);
    
    assertTrue("Could not retrieve function 'names' from encrypted module file.",
               funcs.containsKey("names"));
    
    assertTrue("Object is not instance of FunctionIF.",
               funcs.get("names") instanceof FunctionIF); 
  }

}

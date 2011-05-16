// $Id: ModuleReaderTest.java,v 1.1 2002/09/11 13:21:16 niko Exp $

package net.ontopia.topicmaps.nav2.utils;

import java.io.*;
import java.util.*;
import org.xml.sax.SAXException;

import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.utils.*;

import net.ontopia.test.AbstractOntopiaTestCase;

public class ModuleReaderTest extends AbstractOntopiaTestCase {

  String baseDir;
  
  public ModuleReaderTest(String name) {
    super(name);
  }

  public void setUp() {
    String root = getTestDirectory();
    baseDir = root + File.separator + "nav2" + File.separator + "functions";
  }

  public void testPlain() throws IOException, SAXException {
    File inpFile = new File(baseDir, "plainTest.jsm");
    ModuleReaderIF modReader = new ModuleReader(false);
    Map funcs = modReader.read(new FileInputStream(inpFile));

    assertTrue("Could not retrieve correct number of functions from plain module file",
               funcs.size() == 1);
    
    assertTrue("Could not retrieve function 'names' from plain module file",
               funcs.containsKey("names"));
    
    assertTrue("Object is not instance of FunctionIF.",
               funcs.get("names") instanceof FunctionIF); 
  }
  
  public void testEncrypted() throws IOException, SAXException {
    File inpFile = new File(baseDir, "encryptedTest.jsm");
    ModuleReaderIF modReader = new ModuleReader(true);
    Map funcs = modReader.read(new FileInputStream(inpFile));

    assertTrue("Could not retrieve correct number of functions from encrypted module file",
               funcs.size() == 1);
    
    assertTrue("Could not retrieve function 'names' from encrypted module file",
               funcs.containsKey("names"));
    
    assertTrue("Object is not instance of FunctionIF.",
               funcs.get("names") instanceof FunctionIF); 
  }
  
}






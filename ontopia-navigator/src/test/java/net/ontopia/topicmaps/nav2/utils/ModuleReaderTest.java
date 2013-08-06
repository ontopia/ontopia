/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.utils;

import java.io.IOException;
import java.util.Map;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.ModuleReaderIF;
import org.xml.sax.SAXException;
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






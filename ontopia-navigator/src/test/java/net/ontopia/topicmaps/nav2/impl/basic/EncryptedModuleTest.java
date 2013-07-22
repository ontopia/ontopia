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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.io.*;
import java.util.*;
import org.xml.sax.SAXException;

import net.ontopia.utils.EncryptionUtils;
import net.ontopia.topicmaps.nav2.core.*;
import net.ontopia.topicmaps.nav2.impl.basic.*;
import net.ontopia.topicmaps.nav2.utils.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptedModuleTest {

  private final static String testdataDirectory = "nav2";

  @Before
  public void setUp() throws IOException {
    // create encrypted module file
    File inFile = TestFileUtils.getTransferredTestInputFile(testdataDirectory, "functions", "plainTest.jsm");
    File outFile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "testEncWriterOut.jsm");
    EncryptionUtils.encrypt(inFile, outFile);
  }
   

  @Test
  public void testVerifyWritten() throws IOException, SAXException {
    File inFile = TestFileUtils.getTestOutputFile(testdataDirectory, "out", "testEncWriterOut.jsm");
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

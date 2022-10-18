/*
 * #!
 * Ontopia Engine
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

package net.ontopia.utils;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EncryptionUtilsTest {

  private final static String testdataDirectory = "various";

  private String baseDir;
  
  @Before
  public void setUp() throws IOException {
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, testdataDirectory);
    baseDir = root + File.separator + testdataDirectory;
  }

  @Test
  public void testVerifyWritten() throws IOException {
    createEncryptedFile("plainTest.jsm", "plainTestEncrypted.jsm");

    Assert.assertTrue("Read in file is not like encrypted base line file.",
               compareToBaseline("plainTestEncrypted.jsm", "baseline-plainTestEncrypted.jsm"));
  }

  // --- Internal helper methods

  protected void createEncryptedFile(String in_name, String out_name) throws IOException {
    // create encrypted dummy file
    File in_file = TestFileUtils.getTransferredTestInputFile(testdataDirectory, in_name);
    File out_file = TestFileUtils.getTestOutputFile(testdataDirectory, out_name);
    EncryptionUtils.encrypt(in_file, out_file);
  }

  protected boolean compareToBaseline(String out_name, String baseline_name) throws IOException {
    return TestFileUtils.compareFileToResource(new File(baseDir, out_name), TestFileUtils.getTestInputFile(testdataDirectory, baseline_name));
  }

}

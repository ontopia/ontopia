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

package net.ontopia.topicmaps.xml;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractCanonicalTests {

  // --- Canonicalization type methods

  /**
   * INTERNAL: Returns directory to search for files in.
   */
  protected String getFileDirectory() {
    return "in";
  }

  /**
   * INTERNAL: Makes the name of the outfile (without path) given the
   * name of the infile.
   */
  protected String getOutFilename(String infile) {
    return infile;
  }

  /**
   * INTERNAL: Performs the actual canonicalization.
   */
  protected abstract void canonicalize(String infile, String outfile)
    throws IOException;

  /**
   * INTERNAL: Returns the store factory to be used.
   */
  protected TopicMapStoreFactoryIF getStoreFactory() {
    return new InMemoryStoreFactory();
  }
  
  // --- Test case class

    protected String base;
    protected String filename;
    protected String _testdataDirectory;

    @Test
    public void testFile() throws IOException {
      TestFileUtils.verifyDirectory(base, "out");
      
      // setup canonicalization filenames
      String in = TestFileUtils.getTestInputFile(_testdataDirectory, getFileDirectory(), 
        filename);
      String out = base + File.separator + "out" + File.separator +
        getOutFilename(filename);
      // produce canonical output
      canonicalize(in, out);
      
      // compare results
      String baseline = TestFileUtils.getTestInputFile(_testdataDirectory, "baseline", 
        getOutFilename(filename));
      Assert.assertTrue("test file " + filename + " canonicalized wrongly",
              FileUtils.compareFileToResource(out, baseline));
    }

  // -- internal

  public static String file2URL(String filename) {
    if (filename.startsWith("classpath:")) {
      return net.ontopia.utils.URIUtils.getURI(filename).getExternalForm();
    } else try {
      return URIUtils.toURL(new File(filename)).toExternalForm();
    } catch (java.net.MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }
}

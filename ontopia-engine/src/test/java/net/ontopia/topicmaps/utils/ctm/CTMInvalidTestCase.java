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

package net.ontopia.topicmaps.utils.ctm;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.*;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.topicmaps.xml.InvalidTopicMapException;

import java.util.List;
import net.ontopia.utils.URIUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CTMInvalidTestCase {
  
  private final static String testdataDirectory = "ctm";

  @Parameters
  public static List generateTests() {
    return TestFileUtils.getTestInputFiles(testdataDirectory, "invalid", ".ctm");
  }

    private String filename;
        
    public CTMInvalidTestCase(String root, String filename) {
      this.filename = filename;
    }

    @Test
    public void testFile() throws IOException {
      // produce canonical output
      String in = TestFileUtils.getTestInputFile(testdataDirectory, "invalid", 
        filename);

      try {
        new CTMTopicMapReader(URIUtils.getURI(in)).read();
        Assert.fail("no error in reading " + filename);
      } catch (IOException e) {
      } catch (InvalidTopicMapException e) {
      } catch (Exception e) {
        throw new OntopiaRuntimeException("Error reading: " + in, e);
      }
    }
}

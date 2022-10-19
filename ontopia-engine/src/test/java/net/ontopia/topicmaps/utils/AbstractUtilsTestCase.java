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

package net.ontopia.topicmaps.utils;

import java.io.IOException;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;

public abstract class AbstractUtilsTestCase {

  private final static String testdataDirectory = "various";

  protected final static String FILE_SEPARATOR = System.getProperty("file.separator");

  protected LocatorIF baseAddress;
  protected TopicMapIF tm;

  protected TopicMapIF getTopicMap() {
    return tm;
  }
  
  protected TopicIF getTopic(String fragId) {
    LocatorIF l = baseAddress.resolveAbsolute("#" + fragId);
    return (TopicIF) tm.getObjectByItemIdentifier(l);
  }

  protected void readFile(String fileName) {
    try {
      TopicMapReaderIF reader = ImportExportUtils.getReader(TestFileUtils.getTestInputFile(testdataDirectory, fileName));
      tm = reader.read();
      baseAddress = tm.getStore().getBaseAddress();
    } catch(IOException ex) {
      Assert.assertTrue("Topic map read failed!\n" + ex.getMessage(), false);
    }
  }
   
}

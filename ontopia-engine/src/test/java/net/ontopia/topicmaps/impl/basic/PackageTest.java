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

package net.ontopia.topicmaps.impl.basic;

import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.TestFileUtils;

public class PackageTest extends TopicMapPackageTest {
  
  private final static String testdataDirectory = "various";

  @Override
  public void setUp() {
    if (tm == null) {
      try {
        TopicMapReaderIF reader =
          new XTMTopicMapReader(TestFileUtils.getTestInputURL(testdataDirectory, "package-test.xtm"));
        tm = reader.read();
        base = tm.getStore().getBaseAddress();
      }
      catch (java.io.IOException e) {
        throw new RuntimeException("IMPOSSIBLE ERROR! " + e.getMessage());
      }
    }
  }

  @Override
  public void tearDown() {
    //tm = null;
  }
  
}

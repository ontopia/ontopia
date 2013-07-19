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

package net.ontopia.topicmaps.cmdlineutils;

import java.io.IOException;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.FileUtils;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;

public class StatsTest extends CommandLineUtilsTest {
  
  public StatsTest(String name) {
    super(name);
  }

  protected void setUp() {

    XTMTopicMapReader reader  = null;

    String filename = TestFileUtils.getTestInputFile("various", "stats.xtm");

    try {
      reader = new XTMTopicMapReader(URIUtils.getURI(filename));
      tm = reader.read();
    } catch (IOException e) {
      fail("Error reading file\n" + e);
    }

  }

  protected void tearDown() {
    tm = null;
  }
}

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

import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;
import net.ontopia.utils.TestFileUtils;
import org.junit.Test;

public class XTMPathTopicMapSourceTest extends AbstractTopicMapSourceTest {

  private final static String testdataDirectory = "canonical";

  // --- Test cases

  @Test
  public void testSource() {
    XTMPathTopicMapSource source = new XTMPathTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPath("classpath:" + TestFileUtils.testdataInputRoot + testdataDirectory + "/" + "in");
    source.setSuffix(".xtm");
    
    // run abstract topic map source tests
    assertCompliesToAbstractTopicMapSource(source);
  }
  
}

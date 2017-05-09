/*
 * #!
 * Ontopia DB2TM
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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
package net.ontopia.topicmaps.db2tm;

import java.io.IOException;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.utils.TestFileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class Issue348Test {
  
  @BeforeClass
  public static void setup() throws IOException {
    TestFileUtils.transferTestInputDirectory("db2tm");
  }
  
  @Test(expected = DB2TMException.class)
  public void testIssue348() throws IOException {
    // set baseAddress to null
    TopicMapIF topicmap = new InMemoryTopicMapStore().getTopicMap();
    topicmap.getStore().setBaseAddress(null);

    DB2TM.add(TestFileUtils.getTransferredTestInputFile("db2tm", "issue348.xml").getPath(), topicmap);
  }
}

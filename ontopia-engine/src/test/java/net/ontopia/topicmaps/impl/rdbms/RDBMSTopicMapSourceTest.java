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

package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;
import org.junit.Before;
import org.junit.Test;

public class RDBMSTopicMapSourceTest extends AbstractTopicMapSourceTest {

  @Before
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  // --- Test cases

  @Test
  public void testSource() {
    RDBMSTopicMapSource source = new RDBMSTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setSupportsCreate(true);
    source.setSupportsDelete(true);
    
    // run abstract topic map source tests
    assertCompliesToAbstractTopicMapSource(source);
  }
  
}

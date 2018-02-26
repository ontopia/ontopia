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

public class RDBMSSingleTopicMapSourceTest extends AbstractTopicMapSourceTest {

  public RDBMSSingleTopicMapSourceTest(String name) {
    super(name);
  }

  @Override
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
    super.setUp();
  }

  // --- Test cases

  public void testSource() {
    RDBMSSingleTopicMapSource source = new RDBMSSingleTopicMapSource();
    source.setId("fooid");
    source.setTitle("footitle");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));

    // run abstract topic map source tests
    doAbstractTopicMapSourceTests(source);
  }
  
}

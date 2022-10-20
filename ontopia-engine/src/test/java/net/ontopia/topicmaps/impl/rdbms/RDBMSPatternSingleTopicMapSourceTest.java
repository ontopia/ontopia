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

import java.io.IOException;
import net.ontopia.topicmaps.entry.AbstractTopicMapSourceTest;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RDBMSPatternSingleTopicMapSourceTest
  extends AbstractTopicMapSourceTest {

  @Before
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  // --- Test cases

  @Test
  public void testSource() {
    // run abstract topic map source tests
    assertCompliesToAbstractTopicMapSource(makeSource());
  }

  @Test
  public void testRefresh() throws IOException {
    // constants
    final String title = "test-topic-map";
    final String propfile = System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile");

    RDBMSTopicMapStore store = null;
    RDBMSTopicMapStore store2 = null;
    try {
      // create a new topic map matching pattern
      store = new RDBMSTopicMapStore(propfile);
      ((TopicMap) store.getTopicMap()).setTitle(title);
      store.commit(); // make sure it's in the DB for real
      
      // verify that correct TM is being picked up
      RDBMSPatternSingleTopicMapSource source = makeSource();
      source.setMatch("title");
      source.setPattern(title);
      TopicMapReferenceIF ref = (TopicMapReferenceIF)
        source.getReferences().iterator().next();
      Assert.assertTrue("reference has wrong ID: " + ref,
                 ((RDBMSTopicMapReference) ref).getTopicMapId() == store.getLongId());

      // make another topic map matching pattern
      store2 = new RDBMSTopicMapStore(propfile);
      ((TopicMap) store2.getTopicMap()).setTitle(title);
      store2.commit(); // make sure it's in the DB for real

      // verify that new topic map is being picked up
      source.refresh();
      ref = (TopicMapReferenceIF) source.getReferences().iterator().next();
      Assert.assertTrue("reference has wrong ID: " + ref,
                 ((RDBMSTopicMapReference) ref).getTopicMapId() == store2.getLongId());
    } finally {
      // clean up
      if (store != null) {
        store.delete(true);
      }
      if (store2 != null) {
        store2.delete(true);
      }
    }
  }

  // --- Internal methods

  private RDBMSPatternSingleTopicMapSource makeSource() {
    RDBMSPatternSingleTopicMapSource source = new RDBMSPatternSingleTopicMapSource();
    source.setId("foosource");
    source.setTitle("footitle");
    source.setReferenceId("foo");
    source.setPropertyFile(System.getProperty("net.ontopia.topicmaps.impl.rdbms.PropertyFile"));
    source.setMatch("comments");
    source.setPattern("My comment");
    return source;
  }
  
}

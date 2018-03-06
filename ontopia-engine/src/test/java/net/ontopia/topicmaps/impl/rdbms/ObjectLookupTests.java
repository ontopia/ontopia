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
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * INTERNAL: Tests that verify that LocatorIF lookups work correctly
 * in a committed and reopened topic map store.
 */

public class ObjectLookupTests {
  
  private final static String testdataDirectory = "various";

  @Before
  public void setUp() throws Exception {
    RDBMSTestFactory.checkDatabasePresence();
  }

  @Test
  public void testLookups() throws IOException {
    
    URL file = TestFileUtils.getTestInputURL(testdataDirectory, "topicmap-object-lookup.xtm");
    LocatorIF base = new URILocator(file);

    // Load topic map, commit and close
    RDBMSTopicMapStore store = new RDBMSTopicMapStore();
    TopicMapIF tm = store.getTopicMap();
    TopicMapReaderIF importer = new XTMTopicMapReader(file);
    importer.importInto(tm);
    long topicmap_id = Long.parseLong(tm.getObjectId().substring(1));    
    store.commit();
    store.close();
    
    // Reopen store
    store = new RDBMSTopicMapStore(topicmap_id);
    tm = store.getTopicMap();

    // Test TopicMapIF.getTopicBySubjectIdentifier
    TopicIF topic = tm.getTopicBySubjectIdentifier(URILocator.create("http://test.ontopia.net/indicator1"));
    Assert.assertTrue("topic not found by indicator [1]", topic != null);
    topic = tm.getTopicBySubjectIdentifier(URILocator.create("http://test.ontopia.net/indicator2"));
    Assert.assertTrue("topic not found by indicator [2]", topic != null);
    
    // Test TopicMapIF.getObjectByItemIdentifier
    TMObjectIF tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicA"));
    Assert.assertTrue("tmobject not found by source locator [A]", tmobject != null);
    tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicB"));
    Assert.assertTrue("tmobject not found by source locator [B]", tmobject != null);
    tmobject = tm.getObjectByItemIdentifier(base.resolveAbsolute("#topicC"));
    Assert.assertTrue("tmobject not found by source locator [C]", tmobject != null);

    // Test TopicMapIF.getTopicBySubject
    topic = tm.getTopicBySubjectLocator(URILocator.create("http://test.ontopia.net/subject"));
    Assert.assertTrue("topic not found by subject", topic != null);

    store.delete(true);
    //! store.close();    
  }
  
}

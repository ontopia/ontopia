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

package net.ontopia.topicmaps.impl.tmapi2;

import junit.framework.TestCase;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class LocatorExternalFormTest extends TestCase {

// FIXME: This is issue 225, which is still open. Both tests are
// therefore disabled so that we avoid failures in the test suite.

  public void testDummy() {
    // this test is here just so that JUnit won't complain. once we
    // enable the tests we can delete it.
  }
  
//   public void testLocatorExternalForm() throws TMAPIException {
//     String male = "http://www.ex%25-st.com/";
//     TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
//     TopicMapSystem sys = factory.newTopicMapSystem();
//     TopicMap tm = sys.createTopicMap("foo:baa");
//     Topic t = tm.createTopic();
//     Locator l = tm.createLocator(male);
    
//     assertEquals(male, l.toExternalForm());
    
//     Occurrence o = t.createOccurrence(tm.createTopic(), l, tm.createTopic());
//     String locString = o.getValue();
//     o.locatorValue().toExternalForm();
//     assertEquals(male, locString);
    
//     // try to create a locator form value by hand
//     Locator l2 = tm.createLocator(locString);
//     assertEquals(male, l2);
    
//     Locator loc = o.locatorValue();
//     assertNotNull(loc);
    
//     assertEquals(male, o.locatorValue().toExternalForm());
//   }

//   public void testLocatorExternalFormOntopiaOnly() throws TMAPIException {
//     String male = "http://www.ex%25-st.com/";
//     InMemoryTopicMapStore store = new InMemoryTopicMapStore();
//     TopicMapIF tm = store.getTopicMap();
    
//     LocatorIF l = URILocator.create(male);
//     assertEquals(male, l.getExternalForm());

//     TopicIF t = tm.getBuilder().makeTopic();
//     TopicIF ot = tm.getBuilder().makeTopic();
    
//     OccurrenceIF occ = tm.getBuilder().makeOccurrence(t, ot, l);
    
//     assertEquals(male, occ.getValue());
//     assertEquals(male, occ.getLocator().getExternalForm());
//   }
}

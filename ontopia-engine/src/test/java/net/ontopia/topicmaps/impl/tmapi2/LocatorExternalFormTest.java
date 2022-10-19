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

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Test;
import org.tmapi.core.Locator;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIException;
import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;
import org.tmapi.core.TopicMapSystem;
import org.tmapi.core.TopicMapSystemFactory;

public class LocatorExternalFormTest {
  
   @Test
   public void testLocatorExternalForm() throws TMAPIException {
     String male = "http://www.ex%25-st.com/";
     TopicMapSystemFactory factory = TopicMapSystemFactory.newInstance();
     TopicMapSystem sys = factory.newTopicMapSystem();
     TopicMap tm = sys.createTopicMap("foo:baa");
     Topic t = tm.createTopic();
     Locator l = tm.createLocator(male);
    
     Assert.assertEquals(male, l.toExternalForm());
    
     Occurrence o = t.createOccurrence(tm.createTopic(), l, tm.createTopic());
     String locString = o.getValue();
     Assert.assertEquals(male, locString);
     Assert.assertEquals(male, o.locatorValue().toExternalForm());
    
     // try to create a locator form value by hand
     Locator l2 = tm.createLocator(locString);
     Assert.assertEquals(l, l2);
    
     Locator loc = o.locatorValue();
     Assert.assertNotNull(loc);
    
     Assert.assertEquals(male, o.locatorValue().toExternalForm());
     Assert.assertEquals(l, loc);
   }

   @Test
   public void testLocatorExternalFormOntopiaOnly() throws TMAPIException {
     String male = "http://www.ex%25-st.com/";
     InMemoryTopicMapStore store = new InMemoryTopicMapStore();
     TopicMapIF tm = store.getTopicMap();
    
     LocatorIF l = URILocator.create(male);
     Assert.assertEquals(male, l.getExternalForm());

     TopicIF t = tm.getBuilder().makeTopic();
     TopicIF ot = tm.getBuilder().makeTopic();
    
     OccurrenceIF occ = tm.getBuilder().makeOccurrence(t, ot, l);
    
     Assert.assertEquals(male, occ.getValue());
     Assert.assertEquals(male, occ.getLocator().getExternalForm());
   }
}

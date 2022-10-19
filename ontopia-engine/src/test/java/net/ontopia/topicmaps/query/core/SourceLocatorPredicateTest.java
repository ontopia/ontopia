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

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import org.junit.Test;

public class SourceLocatorPredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addSrclocsOf(matches, Collections.singleton(topicmap));
    addSrclocsOf(matches, topicmap.getAssociations());
    addSrclocsOf(matches, topicmap.getTopics());

    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      
      addSrclocsOf(matches, topic.getOccurrences());
      addSrclocsOf(matches, topic.getTopicNames());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext())
        addSrclocsOf(matches, ((TopicNameIF) it2.next()).getVariants());
    }

    it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();      
      addSrclocsOf(matches, assoc.getRoles());
    }
    
    assertQueryMatches(matches, "source-locator($OBJ, $LOCATOR)?");  
  }

  private void addSrclocsOf(List matches, Collection objects) {
    Iterator it = objects.iterator();
    while (it.hasNext()) {
      TMObjectIF object = (TMObjectIF) it.next();

      Iterator it2 = object.getItemIdentifiers().iterator();
      while (it2.hasNext())
        addMatch(matches, "OBJ", object, "LOCATOR", ((LocatorIF) it2.next()).getAddress());
    }
  }
  
  @Test
  public void testTopicToLocator() throws InvalidQueryException, IOException {
    load("jill.xtm");

    LocatorIF base = topicmap.getStore().getBaseAddress();
    List matches = new ArrayList();
    addMatch(matches, "LOCATOR", base.resolveAbsolute("#ontopia").getAddress());
    
    assertQueryMatches(matches, "source-locator(ontopia, $LOCATOR)?");
  }

  @Test
  public void testLocatorToTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");
    LocatorIF base = topicmap.getStore().getBaseAddress();

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("ontopia"));
    
    assertQueryMatches(matches, "source-locator($TOPIC, \"" + base.resolveAbsolute("#ontopia").getAddress() + "\")?");
  }

  @Test
  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    LocatorIF base = topicmap.getStore().getBaseAddress();

    List matches = new ArrayList();    
    assertQueryMatches(matches, "source-locator(type2, \"" + base.resolveAbsolute("#type1").getAddress() + "\")?");
  }

  @Test
  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    LocatorIF base = topicmap.getStore().getBaseAddress();

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "source-locator(type1, \"" + base.resolveAbsolute("#type1").getAddress() + "\")?");
  }
  
}

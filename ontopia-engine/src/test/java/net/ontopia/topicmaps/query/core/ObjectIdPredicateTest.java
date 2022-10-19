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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import org.junit.Test;

public class ObjectIdPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "OBJECT", topicmap, "ID", topicmap.getObjectId());
    
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      addMatch(matches, "OBJECT", topic, "ID", topic.getObjectId());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        addMatch(matches, "OBJECT", bn, "ID", bn.getObjectId());

        Iterator it3 = bn.getVariants().iterator();
        while (it3.hasNext()) {
          VariantNameIF vn = (VariantNameIF) it3.next();
          addMatch(matches, "OBJECT", vn, "ID", vn.getObjectId());
        }
      }

      it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        addMatch(matches, "OBJECT", occ, "ID", occ.getObjectId());
      }
    }

    it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();
      addMatch(matches, "OBJECT", assoc, "ID", assoc.getObjectId());

      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        addMatch(matches, "OBJECT", role, "ID", role.getObjectId());
      }
    }
    
    assertQueryMatches(matches, "object-id($OBJECT, $ID)?");
    closeStore();
  }

  @Test
  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    TopicIF horse = getTopicById("horse");

    List matches = new ArrayList();
    addMatch(matches, "ID", horse.getObjectId());
    
    assertQueryMatches(matches, "object-id(horse, $ID)?");
    closeStore();
  }

  @Test
  public void testWithSpecificId() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);
    
    assertQueryMatches(matches, "object-id($TOPIC, \"" + topic.getObjectId() + "\")?");
    closeStore();
  }

  @Test
  public void testWithTopicNames() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "object-id(horse, $BN), topic-name($T, $BN)?");
    closeStore();
  }
  
  @Test
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());

    TopicIF topic = getTopicById("thequeen");
    
    assertQueryMatches(matches, "object-id(thequeen, \"" + topic.getObjectId() +"\")?");
    closeStore();
  }
  
  @Test
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    
    assertQueryMatches(matches, "object-id(equation, \"" + topic.getObjectId() + "\")?");
    closeStore();
  }
  
}

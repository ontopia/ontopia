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
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Test;

public class AssociationRolePredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();

      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        addMatch(matches, "ASSOC", assoc, "ROLE", role);
      }
    }
    
    assertQueryMatches(matches, "association-role($ASSOC, $ROLE)?");
    
    closeStore();
  }

  @Test
  public void testSpecificAssoc() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    AssociationIF assoc = (AssociationIF) it.next();

    Iterator it2 = assoc.getRoles().iterator();
    while (it2.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it2.next();
      addMatch(matches, "ROLE", role);
    }
    
    assertQueryMatches(matches, "association-role(@" + assoc.getObjectId() + ", $ROLE)?");
    
    closeStore();
  }

  @Test
  public void testSpecificRole() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    AssociationIF assoc = role.getAssociation();
    addMatch(matches, "ASSOC", assoc);
    
    assertQueryMatches(matches, "association-role($ASSOC, @" + role.getObjectId() + ")?");
    
    closeStore();
  }

  @Test
  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    AssociationIF assoc = role.getAssociation();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "association-role(@" + assoc.getObjectId() + ", @" + role.getObjectId() + ")?");
    
    closeStore();
  }

  @Test
  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("gdm");
    Iterator it = teacher.getRoles().iterator();
    AssociationRoleIF role = (AssociationRoleIF) it.next();
    AssociationRoleIF role2 = (AssociationRoleIF) it.next();
    AssociationIF assoc = role2.getAssociation();
    
    assertQueryMatches(matches, "association-role(@" + assoc.getObjectId() + ", @" + role.getObjectId() + ")?");
    
    closeStore();
  } 

  @Test
  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches,
                OPT_TYPECHECK_OFF +
                "role-player($TOPIC, $ROLE), " +
                "association-role($ASSOC, $ROLE)?");
    
    closeStore();
  } 
  
  @Test
  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF horse = getTopicById("white-horse");
    TopicIF comment = getTopicById("comment-on");
    Iterator it = horse.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF assoc = role.getAssociation();
      if (assoc.getType().equals(comment)) {
        addMatch(matches, "ASSOC", assoc);
      }
    }
    
    assertQueryMatches(matches,
                "select $ASSOC from " +
                "  role-player($ROLE, white-horse), " +
                "  association-role($ASSOC, $ROLE), " +
                "  type($ASSOC, comment-on)?");
    
    closeStore();
  }
  
}

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
import org.junit.Test;

public class TypePredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();      
      addMatch(matches, "TYPED", assoc, "TOPIC", assoc.getType());

      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        addMatch(matches, "TYPED", role, "TOPIC", role.getType());
      }
    }

    it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();      

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF tn = (TopicNameIF) it2.next();
        addMatch(matches, "TYPED", tn, "TOPIC", tn.getType());
      }
      
      it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        addMatch(matches, "TYPED", occ, "TOPIC", occ.getType());
      }
    }
    
    assertQueryMatches(matches, "type($TYPED, $TOPIC)?");
  }  

  @Test
  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic-name($TOPIC, $TNAME), type($TNAME, $TYPE), " +
                "$TYPE /= i\"http://psi.topicmaps.org/iso13250/model/topic-name\"?");
  } 
  
  @Test
  public void testTopicType() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF type = builder.makeTopic();
    builder.makeTopic(type);

    List matches = new ArrayList();
 
    assertQueryMatches(matches, "type($THING, @" + type.getObjectId() + ")?");
  }

  @Test
  public void testTopicNameType() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF type1 = builder.makeTopic();
    TopicIF type2 = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    builder.makeTopicName(topic, type1, "");
    TopicNameIF bname2 = builder.makeTopicName(topic, type2, "");
    builder.makeTopicName(topic, (TopicIF)null, "");

    List matches = new ArrayList();
    addMatch(matches, "TYPE", type2);
 
    assertQueryMatches(matches, "type(@" + bname2.getObjectId() + ", $TYPE)?");
  }

  @Test
  public void testTopicNameType2() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF type1 = builder.makeTopic();
    TopicIF type2 = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    builder.makeTopicName(topic, type1, "");
    TopicNameIF bname2 = builder.makeTopicName(topic, type2, "");
    builder.makeTopicName(topic, (TopicIF)null, "");

    List matches = new ArrayList();
    addMatch(matches, "BNAME", bname2);
    
    assertQueryMatches(matches, "type($BNAME, @" + type2.getObjectId() + ")?");
  }

  @Test
  public void testTopicNameType3() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF type1 = builder.makeTopic();
    TopicIF type2 = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF bname1 = builder.makeTopicName(topic, type1, "");
    TopicNameIF bname2 = builder.makeTopicName(topic, type2, "");
    TopicNameIF bnameN = builder.makeTopicName(topic, (TopicIF)null, "");

    List matches = new ArrayList();
    addMatch(matches, "BNAME", bname1, "TYPE", type1);
    addMatch(matches, "BNAME", bname2, "TYPE", type2);
    addMatch(matches, "BNAME", bnameN, "TYPE", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"));
    
    assertQueryMatches(matches, "type($BNAME, $TYPE)?");
  }

  @Test
  public void testRoleType() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "TYPE", rtype);
 
    assertQueryMatches(matches, "type(@" + role.getObjectId() + ", $TYPE)?");
  }

  @Test
  public void testRoleType2() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);
    builder.makeAssociationRole(assoc, rtype, other);

    List matches = new ArrayList();
    addMatch(matches, "TYPE", rtype);
 
    assertQueryMatches(matches, "type(@" + role.getObjectId() + ", $TYPE)?");
  }

  @Test
  public void testTypeRole() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    assertQueryMatches(matches, "type($ROLE, @" + rtype.getObjectId() + ")?");
  }

  @Test
  public void testTypeRole2() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype, other);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
    addMatch(matches, "ROLE", role2);
 
    assertQueryMatches(matches, "type($ROLE, @" + rtype.getObjectId() + ")?");
  }

  @Test
  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);
    builder.makeAssociationRole(assoc, rtype, other);

    List matches = new ArrayList();
    matches.add(new HashMap());
 
    assertQueryMatches(matches, "type(@" + role.getObjectId() + ", @" + rtype.getObjectId() + ")?");
  }

  @Test
  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    builder.makeAssociationRole(assoc, rtype, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, player, player);

    List matches = new ArrayList();
 
    assertQueryMatches(matches, "type(@" + role2.getObjectId() + ", @" + rtype.getObjectId() + ")?");
  }

  // bug found by Stian Lavik
  @Test
  public void testTypeInNot() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF horse = getTopicById("horse");
    TopicIF role1 = getTopicById("role1");
    Iterator it = horse.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      if (role1.equals(role.getType())) {
        addMatch(matches, "ASSOC", role.getAssociation());
      }
    }
    
    assertQueryMatches(matches, "select $ASSOC from role-player($ROLE, horse), " +
                "association-role($ASSOC, $ROLE), " +
                "not(type($ASSOC, userownstopic)), " +
                "not(type($ASSOC, topicbelongstosubject)), " +
                "not(type($ASSOC, comment-on))?");
    
    closeStore();
  }

  // bug found by LMG by accident

  @Test
  public void testTypeWithOneArgument() throws InvalidQueryException, IOException {
    makeEmpty();
    assertGetParseError("select $ATYPE, $RTYPE from " +
                  "  role-player($ROLE, $ANY), type($RTYPE), " +
                  "  association-role($ASSOC, $ROLE), type($ATYPE)?");
  }
}

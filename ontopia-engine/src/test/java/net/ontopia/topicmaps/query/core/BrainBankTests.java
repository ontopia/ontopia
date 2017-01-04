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
import java.util.List;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import org.junit.Test;

/**
 * INTERNAL: A set of tests for the BrainBank project which exercises
 * the interaction between the new topic map introspection predicates.
 */
public class BrainBankTests extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testFindHorseInName() throws InvalidQueryException, IOException {
    load("bb-test.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById ("horse"),
                      "DESCR", "Nayyy",
                      "DATE",  "2003-06-02");
    addMatch(matches, "TOPIC", getTopicById ("white-horse"),
                      "DESCR", "Epic ballad by G.K. Chesterton.",
                      "DATE",  "2003-07-03");
    addMatch(matches, "TOPIC", getTopicById ("rider"),
                      "DESCR", "Person who rides a horse",
                      "DATE",  "2003-07-04");
    
    assertQueryOrder(matches,
                     "select $TOPIC, $DESCR, $DATE from " +
                     "  value-like($OBJ, \"horse\"), " +
                     "  { topic-name($TOPIC, $OBJ) | " +
                     "    occurrence($TOPIC, $OBJ), type($OBJ, beskrivelse) }, " +
                     "  topicbelongstosubject($TOPIC : bbtopic, k7ahistory : fag), " +
                     "  userownstopic($TOPIC : ownedtopic, gdm : bruker), " +
                     "  beskrivelse($TOPIC, $DESCR), " +
                     "  lastupdated($TOPIC, $DATE) " +
                     "order by $DATE?");
    closeStore();
  }

  @Test
  public void testFindHorseInAssocDesc() throws InvalidQueryException, IOException {
    load("bb-test.ltm", true);

    TopicIF isabout = getTopicById("is-about");
    TopicIF reifier = getTopicById("is-about-horse");
    AssociationIF assoc = getAssociationOfType(isabout);

    // make reifier reify assoc
		assoc.setReifier(reifier);
    
    List matches = new ArrayList();
    addMatch(matches, "ASSOC", assoc,
                      "DATE", "2003-08-14",
                      "DESCR", "The best association ever");
    assertQueryOrder(matches,
                     "select $ASSOC, $DATE, $DESCR from " +
                     "  value-like($OCC, \"association\"), " +
                     "  occurrence($TOPIC, $OCC), type($OCC, beskrivelse), " +
                     "  reifies($TOPIC, $ASSOC), association($ASSOC), " +

                     "  association-role($ASSOC, $ROLE), " +
                     "  type($ROLE, role1), " +
                     "  role-player($ROLE, $BBTOPIC), " +
                     "  topicbelongstosubject($BBTOPIC : bbtopic, k7ahistory : fag), " +
                     "  userownstopic($BBTOPIC : ownedtopic, gdm : bruker), " +
                     "  beskrivelse($TOPIC, $DESCR), " +
                     "  lastupdated($TOPIC, $DATE) " +
                     "order by $DATE?");
    closeStore();
  }

  @Test
  public void testFindHorseInComments() throws InvalidQueryException, IOException {
    load("bb-test.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "COMMENT", getTopicById("comment1"),
                      "DATE"   , "2003-06-03");
    addMatch(matches, "COMMENT", getTopicById("comment2"),
                      "DATE"   , "2003-06-04");
    
    assertQueryOrder(matches,
                     "select $COMMENT, $DATE from " +
                     "  value-like($CONTENT, \"horse\"), " +
                     "  occurrence($COMMENT, $CONTENT), type($CONTENT, content), " +
                     "  instance-of($COMMENT, comment), " +
                     "  comment-on($COMMENT : comment, $TOPIC : bbtopic), " +
                     
                     "  topicbelongstosubject($TOPIC : bbtopic, k7ahistory : fag), " +
                     "  userownstopic($TOPIC : ownedtopic, gdm : bruker), " +
                     "  beskrivelse($TOPIC, $DESCR), " +
                     "  lastupdated($COMMENT, $DATE) " +
                     "order by $DATE?");
    closeStore();
  }

//   public void testFindAssociation() throws InvalidQueryException, IOException {
//     load("bb-test.ltm");

//     List matches = new ArrayList();
//     TopicIF topic = getTopicById("gdm");
//     TopicIF elev =  getTopicById("elev");
//     TopicIF elevklasse =  getTopicById("elev-klasse");
//     TopicIF klasse =  getTopicById("klasse");
//     TopicIF k7amaths =  getTopicById("k7amaths");

    
//     addMatch(matches, "ASSOC", assoc);
    
//     verifyQuery(matches,
//                 "select $ASSOC from " +
//                 "  role-player($ROLE, gdm), type($ROLE, elev), " +
//                 "  association-role($ASSOC, $ROLE), type($ASSOC, elev-klasse), " +
//                 "  association-role($ASSOC, $ROLE2), type($ROLE2, klasse), " +
//                 "  role-player($ROLE2, k7amaths)?");
//     closeStore();
//   }

  // Helper methods

  private AssociationIF getAssociationOfType(TopicIF type) {
    TopicMapIF topicmap = type.getTopicMap();
    ClassInstanceIndexIF index = (ClassInstanceIndexIF) topicmap.getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");

    return index.getAssociations(type).iterator().next();
  }
  
}

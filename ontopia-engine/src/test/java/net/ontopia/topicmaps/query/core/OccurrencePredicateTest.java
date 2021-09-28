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
import java.util.Map;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;

public class OccurrencePredicateTest extends AbstractPredicateTest {
  
  public OccurrencePredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }

  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getOccurrences().iterator();
      while (it2.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it2.next();
        addMatch(matches, "OCC", occ, "TOPIC", topic);
      }
    }
    
    verifyQuery(matches, "occurrence($TOPIC, $OCC)?");
  }

  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addOccurrences(matches, "OCC", getTopicById("horse"));
    
    verifyQuery(matches, "occurrence(horse, $OCC)?");
  }

  public void testWithSpecificOccurrence() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    OccurrenceIF occ = (OccurrenceIF) topic.getOccurrences().iterator().next();

    addMatch(matches, "TOPIC", topic);
    
    verifyQuery(matches, "occurrence($TOPIC, @" + occ.getObjectId() + ")?");
  }

  public void testWithTopicNames() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    findNothing(OPT_TYPECHECK_OFF +
                "topic-name(horse, $BN), occurrence($T, $BN)?");
  }
  
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "occurrence(jill-ontopia-topic, jills-contract)?");
  }
  
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    OccurrenceIF occ = (OccurrenceIF) topic.getOccurrences().iterator().next();
    
    verifyQuery(matches, "occurrence(equation, @" + occ.getObjectId() + ")?");
  }

  public void testKnownProblem() throws InvalidQueryException, IOException {
    load("bb-test.ltm", true);
    List matches = new ArrayList();

    TopicIF content = getTopicById("content");
    TopicIF description = getTopicById("beskrivelse");
    TopicIF topic = getTopicById("comment2");
    addMatch(matches, "TOPIC", topic, "OBJ", getOccurrence(topic, content));
    topic = getTopicById("comment1");
    addMatch(matches, "TOPIC", topic, "OBJ", getOccurrence(topic, content));
    topic = getTopicById("rider");
    addMatch(matches, "TOPIC", topic, "OBJ", getOccurrence(topic, description));

    verifyQuery(matches, "value-like($OBJ, \"horse\"), occurrence($TOPIC, $OBJ)?");
  }    

  public void testNontopicVariable() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    List matches = new ArrayList();

    findNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), occurrence($TM, $OCC)?");
  }

  public void testNontopicParameter() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    List matches = new ArrayList();

    Map args = new HashMap();
    args.put("TM", topicmap);
             
    findNothing(OPT_TYPECHECK_OFF +
                "occurrence(%TM%, $OCC)?", args);
  }

  public void testBug1993() throws InvalidQueryException, IOException {
    load("ext-occs.ltm");

    TopicIF homepage = getTopicById("homepage");    
    List matches = new ArrayList();
    addMatch(matches, "A", homepage, "B", homepage);

    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "$A = homepage, $B = homepage, " +
                "{ occurrence($A, $B) | $A = $B }?");
  }
  
  /// helpers

  private OccurrenceIF getOccurrence(TopicIF topic, TopicIF type) {
    Iterator it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (type.equals(occ.getType()))
        return occ;
    }
    return null;
  }
  
  private void addOccurrences(List matches, String var, TopicIF topic) {
    Iterator it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      addMatch(matches, var, occ);
    }
  }
}

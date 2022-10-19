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
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import org.junit.Test;

public class TopicNamePredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    Iterator<TopicIF> it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = it.next();
      Iterator<TopicNameIF> it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = it2.next();
        addMatch(matches, "BNAME", bn, "TOPIC", topic);
      }
    }
    
    assertQueryMatches(matches, "topic-name($TOPIC, $BNAME)?");
  }

  @Test
  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addTopicNames(matches, "BNAME", getTopicById("marriage"));
    
    assertQueryMatches(matches, "topic-name(marriage, $BNAME)?");
  }

  @Test
  public void testWithOccurrences() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "occurrence(white-horse, $OCC), topic-name($T, $OCC)?");
  }

  @Test
  public void testWithSpecificTopicName() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("marriage");
    TopicNameIF bn = topic.getTopicNames().iterator().next();

    addMatch(matches, "TOPIC", topic);
    
    assertQueryMatches(matches, "topic-name($TOPIC, @" + bn.getObjectId() + ")?");
  }
  
  @Test
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("marriage");
    TopicNameIF bn = topic.getTopicNames().iterator().next();

    matches.add(new HashMap());
    
    assertQueryMatches(matches, "topic-name(marriage, @" + bn.getObjectId() + ")?");
  }
  
  @Test
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("marriage");
    TopicNameIF bn = topic.getTopicNames().iterator().next();
    
    assertQueryMatches(matches, "topic-name(parenthood, @" + bn.getObjectId() + ")?");
  }
  
  @Test
  public void testTypeConflict() throws InvalidQueryException, IOException {
    load("family2.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), topic-name($TM, $BN)?");
  }

  @Test
  public void testTypeConflict2() throws InvalidQueryException, IOException {
    load("family2.ltm");
    
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic-name(@" + topicmap.getObjectId() + ", $BN)?");
  }

  @Test
  public void testTypeConflict3() throws InvalidQueryException, IOException {
    load("family2.ltm");

    Map params = new HashMap();
    params.put("topic", topicmap);
    
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic-name(%topic%, $BN)?", params);
  }

  @Test
  public void testTypeConflict4() throws InvalidQueryException, IOException {
    load("family2.ltm");

    Map params = new HashMap();
    params.put("bn", topicmap);
    
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic-name($TOPIC, %bn%)?", params);
  }
  
  /// helpers

  private void addTopicNames(List matches, String var, TopicIF topic) {
    Iterator it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      addMatch(matches, var, bn);
    }
  }
  
}

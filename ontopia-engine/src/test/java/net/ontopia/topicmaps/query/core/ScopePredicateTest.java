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
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import org.junit.Test;

public class ScopePredicateTest extends AbstractPredicateTest {
  
  /// setup

  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addScopesOf(matches, topicmap.getAssociations());

    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      
      addScopesOf(matches, topic.getOccurrences());
      addScopesOf(matches, topic.getTopicNames());

      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        addScopesOf(matches, ((TopicNameIF) it2.next()).getVariants());
      }
    }
                
    assertQueryMatches(matches, "scope($SCOPED, $THEME)?");
  }

  @Test
  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches, OPT_TYPECHECK_OFF +
                "association-role($ASSOC, $ROLE), scope($ROLE, $THEME)?");
  } 
  
  private void addScopesOf(List matches, Collection scopeds) {
    Iterator it = scopeds.iterator();
    while (it.hasNext()) {
      ScopedIF object = (ScopedIF) it.next();
      Iterator it2 = object.getScope().iterator();
      while (it2.hasNext()) {
        addMatch(matches, "SCOPED", object, "THEME", it2.next());
      }
    }
  }
  
  @Test
  public void testTopicNameBothBound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches);
 
    assertQueryMatches(matches, "scope(@" + thing.getObjectId() + ", @" + theme.getObjectId() + ")?");
  }
  
  @Test
  public void testTopicNameBothUnbound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches, "THING", thing, "THEME", theme);

    // NOTE: using topic-name predicate here to avoid type cross product
    assertQueryMatches(matches, "topic-name(@" + topic.getObjectId() + ", $THING), scope($THING, $THEME)?");
  }
  
  @Test
  public void testTopicNameBound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches, "THEME", theme);
 
    assertQueryMatches(matches, "scope(@" + thing.getObjectId() + ", $THEME)?");
  }
  
  @Test
  public void testTopicNameUnbound() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF theme = builder.makeTopic();
    TopicIF topic = builder.makeTopic();
    TopicNameIF thing = builder.makeTopicName(topic, "mybasename");
    thing.addTheme(theme);
    
    List matches = new ArrayList();
    addMatch(matches, "THING", thing);
 
    // NOTE: using topic-name predicate here to avoid type cross product
    assertQueryMatches(matches, "topic-name(@" + topic.getObjectId() + ", $THING), scope($THING, @" + theme.getObjectId() + ")?");
  }
  
  @Test
  public void testSpecificTopicNameNoScope() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF type = builder.makeTopic();
    TopicIF topic = builder.makeTopic(type);
    TopicNameIF bn = builder.makeTopicName(topic, "name");

    List matches = new ArrayList();
    assertQueryMatches(matches, "scope(@" + bn.getObjectId() + ", $THEME)?");
  }
  
}

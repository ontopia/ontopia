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
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Test;

public class SubjectIdentifierPredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getSubjectIdentifiers().iterator();
      while (it2.hasNext())
        addMatch(matches, "TOPIC", topic,
                 "LOCATOR", ((LocatorIF) it2.next()).getAddress());
    }
    
    assertQueryMatches(matches, "subject-identifier($TOPIC, $LOCATOR)?");
    
    closeStore();    
  }
  
  @Test
  public void testTopicToLocator() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    addMatch(matches, "LOCATOR", "http://www.topicmaps.org/xtm/1.0/language.xtm#en");
    
    assertQueryMatches(matches, "subject-identifier(english, $LOCATOR)?");
    closeStore();
  }

  @Test
  public void testLocatorToTopic() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("english"));
    
    assertQueryMatches(matches, "subject-identifier($TOPIC, \"http://www.topicmaps.org/xtm/1.0/language.xtm#en\")?");
    closeStore();
  }

  @Test
  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches, "subject-identifier(user, \"http://www.topicmaps.org/xtm/1.0/language.xtm#en\")?");
    closeStore();
  }

  @Test
  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "subject-identifier(english, \"http://www.topicmaps.org/xtm/1.0/language.xtm#en\")?");
    closeStore();
  }
  
}

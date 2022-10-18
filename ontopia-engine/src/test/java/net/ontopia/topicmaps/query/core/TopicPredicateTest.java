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
import org.junit.Test;

public class TopicPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext())
      addMatch(matches, "TOPIC", it.next());
    
    assertQueryMatches(matches, "topic($TOPIC)?");
    closeStore();
  }

  @Test
  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "topic(marriage)?");
    closeStore();
  }

  @Test
  public void testWithSpecificNonTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList(); // should not match anything
    assertQueryMatches(matches, OPT_TYPECHECK_OFF +
                "topic(jill-ontopia-association)?");
    closeStore();
  }

  @Test
  public void testWithCrossJoin() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList(); // should not match anything
    assertQueryMatches(matches, OPT_TYPECHECK_OFF +
                "topic($NOTHING), association($NOTHING)?");
    closeStore();
  }

  @Test
  public void testFiltering() throws InvalidQueryException, IOException {
    load("family.ltm");

    assertFindNothing("/* #OPTION: optimizer.reorder = false */ " +
                "$A = 1, topic($A)?");
  }
}

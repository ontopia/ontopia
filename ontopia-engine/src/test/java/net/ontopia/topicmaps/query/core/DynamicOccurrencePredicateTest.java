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
import java.util.List;
import org.junit.Test;

public class DynamicOccurrencePredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testWithSpecificTopicInternal() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic1");
    
    assertQueryMatches(matches, "description(topic1, $DESC)?");
  }
  
  @Test
  public void testWithSpecificTopicExternal() throws InvalidQueryException, IOException {
    load("ext-occs.ltm"); // Note: this test case is for bug #1062

    List matches = new ArrayList();
    addMatch(matches, "HOMEPAGE", "http://example.org/topic1");
    
    assertQueryMatches(matches, "homepage(topic1, $HOMEPAGE)?");
  }
  
  @Test
  public void testWithAnyTopic() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"),
                      "DESC", "topic1");
    addMatch(matches, "TOPIC", getTopicById("topic2"),
                      "DESC", "topic2");
    addMatch(matches, "TOPIC", getTopicById("topic3"),
                      "DESC", "topic3");
    addMatch(matches, "TOPIC", getTopicById("topic4"),
                      "DESC", "topic4");
    
    assertQueryMatches(matches, "description($TOPIC, $DESC)?");
  }
  
  @Test
  public void testWithSpecificString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));
    
    assertQueryMatches(matches, "description($TOPIC, \"topic1\")?");
  }
  
  @Test
  public void testWithSpecificURL() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("horse"));
    
    assertQueryMatches(matches, "nettressurs($TOPIC, \"http://www.hest.no\")?");
  }

  @Test
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "description(topic1, \"topic1\")?");
  }

  @Test
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    assertFindNothing("description(topic2, \"topic1\")?");
  }

  @Test
  public void testWithStringForTopic() throws InvalidQueryException, IOException {
    load("opera.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "premiere-date(\"tosca\", $DATE)?");
  }

  @Test
  public void testWithTopicForString() throws InvalidQueryException, IOException {
    load("opera.ltm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "premiere-date($DATE, tosca)?");
  }

  @Test
  public void testWhenNoOccurrencesOfType() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");
    assertFindNothing("  kommentar($DOK, $DATE), " +
                "  $DATE > \"2005-04-23\" " +
                "order by $DATE desc?");
  }
  
  @Test
  public void testMultiDataType1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DOG", getTopicById("dog1"),
          		 			 "VAL", "voff:1");
    addMatch(matches, "DOG", getTopicById("dog2"),
          		 			 "VAL", "voff:1");
    
    assertQueryMatches(matches, "bark($DOG, $VAL)?");
  }
  
  @Test
  public void testMultiDataType2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CAT", getTopicById("cat1"));
    addMatch(matches, "CAT", getTopicById("cat2"));
    
    assertQueryMatches(matches, "beg($CAT, \"meow:1\")?");
  }

}

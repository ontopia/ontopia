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

public class DynamicOccurrencePredicateTest extends AbstractPredicateTest {
  
  public DynamicOccurrencePredicateTest(String name) {
    super(name);
  }

  @Override
  public void tearDown() {
    closeStore();
  }
  
  /// tests
  
  public void testWithSpecificTopicInternal() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic1");
    
    verifyQuery(matches, "description(topic1, $DESC)?");
  }
  
  public void testWithSpecificTopicExternal() throws InvalidQueryException, IOException {
    load("ext-occs.ltm"); // Note: this test case is for bug #1062

    List matches = new ArrayList();
    addMatch(matches, "HOMEPAGE", "http://example.org/topic1");
    
    verifyQuery(matches, "homepage(topic1, $HOMEPAGE)?");
  }
  
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
    
    verifyQuery(matches, "description($TOPIC, $DESC)?");
  }
  
  public void testWithSpecificString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));
    
    verifyQuery(matches, "description($TOPIC, \"topic1\")?");
  }
  
  public void testWithSpecificURL() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("horse"));
    
    verifyQuery(matches, "nettressurs($TOPIC, \"http://www.hest.no/\")?");
  }

  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "description(topic1, \"topic1\")?");
  }

  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    findNothing("description(topic2, \"topic1\")?");
  }

  public void testWithStringForTopic() throws InvalidQueryException, IOException {
    load("opera.ltm");

    findNothing(OPT_TYPECHECK_OFF +
                "premiere-date(\"tosca\", $DATE)?");
  }

  public void testWithTopicForString() throws InvalidQueryException, IOException {
    load("opera.ltm");

    findNothing(OPT_TYPECHECK_OFF +
                "premiere-date($DATE, tosca)?");
  }

  public void testWhenNoOccurrencesOfType() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");
    findNothing("  kommentar($DOK, $DATE), " +
                "  $DATE > \"2005-04-23\" " +
                "order by $DATE desc?");
  }
  
  public void testMultiDataType1() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DOG", getTopicById("dog1"),
          		 			 "VAL", "voff:1");
    addMatch(matches, "DOG", getTopicById("dog2"),
          		 			 "VAL", "voff:1");
    
    verifyQuery(matches, "bark($DOG, $VAL)?");
  }
  
  public void testMultiDataType2() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CAT", getTopicById("cat1"));
    addMatch(matches, "CAT", getTopicById("cat2"));
    
    verifyQuery(matches, "beg($CAT, \"meow:1\")?");
  }

}

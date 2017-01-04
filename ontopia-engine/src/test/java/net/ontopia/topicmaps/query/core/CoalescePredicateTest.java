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
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Test;

public class CoalescePredicateTest extends AbstractPredicateTest {
  
  /// tests 
  
  @Test
  public void testNotBoundTrueOne() throws IOException {
    load("bb-test.ltm");
    assertGetParseError("coalesce($TOPIC, thequeen)?");
  }
  
  @Test
  public void testNotBoundTrueFirst() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);

    assertQueryMatches(matches, "coalesce($TOPIC, thequeen, horse)?");
  }
  
  @Test
  public void testNotBoundTrueFirstVariable() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);

    assertQueryMatches(matches, "select $TOPIC from $QUEEN = thequeen, coalesce($TOPIC, $QUEEN, horse)?");
  }
  
  @Test
  public void testNotBoundTrueSecondVariable() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    getTopicById("horse");
    addMatch(matches, "DESC", "The queen of england");
    addMatch(matches, "DESC", "Foobar");

    assertQueryMatches(matches, "select $DESC from { $X = thequeen | $X = gdm}, { beskrivelse($X, $BESKRIVELSE) }, coalesce($DESC, $BESKRIVELSE, \"Foobar\")?");
  }
  
  @Test
  public void testBoundTrueFirst() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    assertQuery("coalesce(thequeen, thequeen, horse)?");
  }
  
  @Test
  public void testBoundTrueSecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    assertFindNothing("coalesce(thequeen, horse, thequeen)?");
  }  

  @Test
  public void testBoundVariables() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("thequeen"),
                      "B", getTopicById("horse"));

    assertQueryMatches(matches,
                "{ $A = thequeen, $B = horse | " +
                "  $A = horse, $B = thequeen }, " +
                "coalesce(thequeen, $A, $B)?");
  }

  @Test
  public void testIssue389() throws InvalidQueryException, IOException {
    makeEmpty();

    List matches = new ArrayList();
    addMatch(matches, "value", "default");

    assertQueryMatches(matches, "select $value from " +
                         "coalesce($value, $unknown, \"default\")?");
  }

  @Test
  public void testIssue389b() throws InvalidQueryException, IOException {
    makeEmpty();

    List matches = new ArrayList();
    addMatch(matches, "value", "default");

    assertQueryMatches(matches, "select $value from " +
                         "coalesce($value, \"default\", $unknown)?");
  }
}

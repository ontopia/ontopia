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

public class EqualsPredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testEqualsFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertFindNothing("topic1 = topic2?");
  }

  @Test
  public void testEqualsTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    assertQueryMatches(matches,"topic1 = topic1?");
  }

  @Test
  public void testEqualsString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic2"));

    assertQueryMatches(matches, 
		"select $TOPIC from occurrence($TOPIC, $O), " + 
		"type($O, description), value($O, $DESC), " +
		"$DESC = \"topic2\"?");
  }

  @Test
  public void testVariableEqualsTopic() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic4"));

    assertQueryMatches(matches, "$TOPIC = topic4?");
  }

  @Test
  public void testVariableEqualsTopic2() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic4"));

    assertQueryMatches(matches, "topic4 = $TOPIC?");
  }

  @Test
  public void testEqualsAssocDouble() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GC", getTopicById("trygve"),
                      "C1", getTopicById("petter"),
                      "C2", getTopicById("petter"),
                      "M", getTopicById("may"));
    addMatch(matches, "GC", getTopicById("tine"),
                      "C1", getTopicById("petter"),
                      "C2", getTopicById("petter"),
                      "M", getTopicById("may"));
    addMatch(matches, "GC", getTopicById("julie"),
                      "C1", getTopicById("petter"),
                      "C2", getTopicById("petter"),
                      "M", getTopicById("may"));
    addMatch(matches, "GC", getTopicById("astri"),
                      "C1", getTopicById("kfg"),
                      "C2", getTopicById("kfg"),
                      "M", getTopicById("bjorg"));
    addMatch(matches, "GC", getTopicById("lmg"),
                      "C1", getTopicById("kfg"),
                      "C2", getTopicById("kfg"),
                      "M", getTopicById("bjorg"));
    addMatch(matches, "GC", getTopicById("silje"),
                      "C1", getTopicById("kfg"),
                      "C2", getTopicById("kfg"),
                      "M", getTopicById("bjorg"));
    
    assertQueryMatches(matches,
                "parenthood(edvin : father, kjellaug : mother, $C1 : child)," +
                "$C1 = $C2, " +
                "parenthood($C2 : father, $M : mother, $GC : child)?");
  }  

  @Test
  public void testVariableEqualsTopic3() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic4"));

    assertQueryMatches(matches, "topic($TOPIC), $TOPIC = topic4?");
  }

  @Test
  public void testVariableEqualsTopic4() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic4"));

    assertQueryMatches(matches, "$TOPIC = topic4, topic($TOPIC)?");
  }

  @Test
  public void testUnboundVariable() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    assertGetParseError("$A = $B?");
  }
  
}

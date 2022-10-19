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

public class InstanceOfPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testEmptyInstanceOfAB() throws InvalidQueryException {
    makeEmpty();
    assertFindNothing("instance-of($A, $B)?");
  }

  @Test
  public void testInstanceOfAB() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    
    assertQueryMatches(matches, "instance-of($A, $B)?");
  }
  
  @Test
  public void testInstanceOfaB() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    
    assertQueryMatches(matches, "instance-of(topic1, $B)?");
  }

  @Test
  public void testInstanceOfAb() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    assertQueryMatches(matches, "instance-of($A, type1)?");
  }

  @Test
  public void testInstanceOfab() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "instance-of(topic1, type1)?");
  }

  @Test
  public void testInstanceOfWrong() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertFindNothing("instance-of(topic1, type2)?");
  }

  @Test
  public void testInstanceOfABSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type1"));
    
    assertQueryMatches(matches, "instance-of($A, $B)?");
  }
  
  @Test
  public void testInstanceOfaBSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    
    assertQueryMatches(matches, "instance-of(topic1, $B)?");
  }

  @Test
  public void testInstanceOfaBSub2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    addMatch(matches, "B", getTopicById("type2"));
    
    assertQueryMatches(matches, "instance-of(topic3, $B)?");
  }
  
  @Test
  public void testInstanceOfAbSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    addMatch(matches, "A", getTopicById("topic3"));
    addMatch(matches, "A", getTopicById("topic4"));
    
    assertQueryMatches(matches, "instance-of($A, type1)?");
  }

  @Test
  public void testInstanceOfAbSub2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic3"));
    addMatch(matches, "A", getTopicById("topic4"));
    
    assertQueryMatches(matches, "instance-of($A, type2)?");
  }
  
  @Test
  public void testInstanceOfabSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "instance-of(topic1, type1)?");
  }

  @Test
  public void testInstanceOfabSub2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "instance-of(topic2, type1)?");
  }

  @Test
  public void testInstanceOfWrongSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    assertFindNothing("instance-of(topic1, type2)?");
  }

  @Test
  public void testWrongType1() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of($TM, $TYPE)?");
  }

  @Test
  public void testWrongType2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of($INSTANCE, $TM)?");
  }

  @Test
  public void testWrongType3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of($TM, type1)?");
  }

  @Test
  public void testWrongType4() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    assertFindNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of(topic1, $TM)?");
  }
  
}

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
import java.util.Map;
import org.junit.Test;

public class ReifiesPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RR", getTopicById ("jill-ontopia-topic"),
                      "RD", getObjectById("jill-ontopia-association"));
    addMatch(matches, "RR", getTopicById ("jills-contract-topic"),
                      "RD", getObjectById("jills-contract"));
    addMatch(matches, "RR", getTopicById ("jillstm-topic"),
                      "RD", getObjectById("jillstm"));
    
    assertQueryMatches(matches, "reifies($RR, $RD)?");
    closeStore();
  }

  @Test
  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RD", getObjectById("jill-ontopia-association"));
    
    assertQueryMatches(matches, "reifies(jill-ontopia-topic, $RD)?");
    closeStore();
  }

  @Test
  public void testWithSpecificTopicParameter() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RD", getObjectById("jill-ontopia-association"));
    
    Map args = makeArguments("param", getObjectById("jill-ontopia-topic"));
    assertQueryMatches(matches, "reifies(%param%, $RD)?", args);
    closeStore();
  }

  @Test
  public void testWithSpecificTopicFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches, "reifies(jill, $RD)?");
    closeStore();
  }

  @Test
  public void testWithSpecificReified() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RR", getTopicById("jill-ontopia-topic"));
    
    assertQueryMatches(matches, "reifies($RR, jill-ontopia-association)?");
    closeStore();
  }
  
  @Test
  public void testWithSpecificReifiedParameter() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RR", getTopicById("jill-ontopia-topic"));
    
    Map args = makeArguments("param", getObjectById("jill-ontopia-association"));
    assertQueryMatches(matches, "reifies($RR, %param%)?", args);
    closeStore();
  }

  @Test
  public void testWithSpecificReifiedFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches, "reifies($RR, jills-name)?");
    closeStore();
  }
  
  @Test
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "reifies(jill-ontopia-topic, jill-ontopia-association)?");
    closeStore();
  }
  
  @Test
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    assertQueryMatches(matches, "reifies(jill-ontopia-topic, jills-contract)?");
    closeStore();
  }
  
}

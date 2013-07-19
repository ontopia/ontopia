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

public class ReifiesPredicateTest extends AbstractPredicateTest {
  
  public ReifiesPredicateTest(String name) {
    super(name);
  }

  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RR", getTopicById ("jill-ontopia-topic"),
                      "RD", getObjectById("jill-ontopia-association"));
    addMatch(matches, "RR", getTopicById ("jills-contract-topic"),
                      "RD", getObjectById("jills-contract"));
    addMatch(matches, "RR", getTopicById ("jillstm-topic"),
                      "RD", getObjectById("jillstm"));
    
    verifyQuery(matches, "reifies($RR, $RD)?");
    closeStore();
  }

  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RD", getObjectById("jill-ontopia-association"));
    
    verifyQuery(matches, "reifies(jill-ontopia-topic, $RD)?");
    closeStore();
  }

  public void testWithSpecificTopicParameter() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RD", getObjectById("jill-ontopia-association"));
    
    Map args = makeArguments("param", getObjectById("jill-ontopia-topic"));
    verifyQuery(matches, "reifies(%param%, $RD)?", args);
    closeStore();
  }

  public void testWithSpecificTopicFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    
    verifyQuery(matches, "reifies(jill, $RD)?");
    closeStore();
  }

  public void testWithSpecificReified() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RR", getTopicById("jill-ontopia-topic"));
    
    verifyQuery(matches, "reifies($RR, jill-ontopia-association)?");
    closeStore();
  }
  
  public void testWithSpecificReifiedParameter() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "RR", getTopicById("jill-ontopia-topic"));
    
    Map args = makeArguments("param", getObjectById("jill-ontopia-association"));
    verifyQuery(matches, "reifies($RR, %param%)?", args);
    closeStore();
  }

  public void testWithSpecificReifiedFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    
    verifyQuery(matches, "reifies($RR, jills-name)?");
    closeStore();
  }
  
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "reifies(jill-ontopia-topic, jill-ontopia-association)?");
    closeStore();
  }
  
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    verifyQuery(matches, "reifies(jill-ontopia-topic, jills-contract)?");
    closeStore();
  }
  
}

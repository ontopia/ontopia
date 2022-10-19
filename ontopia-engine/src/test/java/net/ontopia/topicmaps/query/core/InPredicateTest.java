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
import org.junit.Ignore;
import org.junit.Test;

@Ignore //disabled: EXPERIMENTAL predicate
public class InPredicateTest extends AbstractPredicateTest {
	
  protected final String IMPORT_EXPERIMENTAL = "import \"http://psi.ontopia.net/tolog/experimental/\" as exp ";
  
  /// tests

  @Test
  public void testHumanInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "HUMAN", getTopicById("dan"));
    addMatch(matches, "HUMAN", getTopicById("sharon"));
    addMatch(matches, "HUMAN", getTopicById("spencer"));
    
    assertQueryMatches(matches, IMPORT_EXPERIMENTAL + "instance-of($HUMAN, human), exp:in($HUMAN, dan, sharon, spencer)?");
    closeStore();
  }

  @Test
  public void testFemaleInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "FEMALE", getTopicById("sharon"));
    
    assertQueryMatches(matches, IMPORT_EXPERIMENTAL + "instance-of($FEMALE, female), exp:in($FEMALE, dan, sharon, spencer)?");
    closeStore();
  }

  @Test
  public void testMaleInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "MALE", getTopicById("dan"));
    addMatch(matches, "MALE", getTopicById("spencer"));
    
    assertQueryMatches(matches, IMPORT_EXPERIMENTAL + "instance-of($MALE, male), exp:in($MALE, dan, sharon, spencer)?");
    closeStore();
  }

  @Test
  public void testHumanNotInList() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "HUMAN", getTopicById("alan"));
    addMatch(matches, "HUMAN", getTopicById("peter"));
    addMatch(matches, "HUMAN", getTopicById("andy"));
    addMatch(matches, "HUMAN", getTopicById("philip"));
    addMatch(matches, "HUMAN", getTopicById("bruce"));
    addMatch(matches, "HUMAN", getTopicById("clyde"));
    addMatch(matches, "HUMAN", getTopicById("james"));
    
    assertQueryMatches(matches, IMPORT_EXPERIMENTAL + "instance-of($HUMAN, male), not(exp:in($HUMAN, dan, sharon, spencer))?");
    closeStore();
  }
  
}

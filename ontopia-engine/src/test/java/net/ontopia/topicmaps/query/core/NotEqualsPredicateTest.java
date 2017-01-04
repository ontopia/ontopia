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

public class NotEqualsPredicateTest extends AbstractPredicateTest {
  
  /// tests

  @Test
  public void testNotEqualsFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertFindNothing("topic1 /= topic1?");
  }

  @Test
  public void testNotEqualsTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    assertQueryMatches(matches, "topic1 /= topic2?");
  }

  @Test
  public void testNotEqualsString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));
    addMatch(matches, "TOPIC", getTopicById("topic3"));
    addMatch(matches, "TOPIC", getTopicById("topic4"));

    assertQueryMatches(matches, 
		"select $TOPIC from occurrence($TOPIC, $O), " + 
		"type($O, description), value($O, $DESC), " +
		"$DESC /= \"topic2\"?");
  }

  // bug caused by optimizer doing /= before all arguments bound (no number)
  @Test
  public void testNotEqualsReordering() throws InvalidQueryException, IOException {
    load("factbook.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));

    // if the bug is here we get a QueryException
    assertFindAny("borders-with($A : country, $B : country), " +
                      "borders-with($C : country, $D : country), " +
                      "$A /= $C?");
  }
}

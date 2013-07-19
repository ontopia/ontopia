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

public class NextPreviousOptimizerTest extends AbstractQueryTest {
  
  public NextPreviousOptimizerTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }
  
  /// tests
  
  public void testFindPrevious() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic23", "TOPIC", getTopicById("topic2"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "$DESC < \"topic3\" " +
                "order by $DESC desc limit 1?");
  }

  public void testFindPreviousInverted()
    throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic23", "TOPIC", getTopicById("topic2"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "\"topic3\" > $DESC " +
                "order by $DESC desc limit 1?");
  }
  
  public void testFindPreviousNonexistent()
    throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic3", "TOPIC", getTopicById("topic3"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "$DESC < \"topic333\" " +
                "order by $DESC desc limit 1?");
  }

  public void testFindPreviousSelf() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic3", "TOPIC", getTopicById("topic3"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "$DESC <= \"topic3\" " +
                "order by $DESC desc limit 1?");
  }

  public void testFindNext() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic3://woohoo/",
             "TOPIC", getTopicById("topic6"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "$DESC > \"topic3\" " +
                "order by $DESC limit 1?");
  }

  public void testFindNextInverted() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic3://woohoo/",
             "TOPIC", getTopicById("topic6"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "\"topic3\" < $DESC " +
                "order by $DESC limit 1?");
  }
  
  public void testFindNextNonexistent()
    throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic3://woohoo/",
             "TOPIC", getTopicById("topic6"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "$DESC > \"topic333\" " +
                "order by $DESC limit 1?");
  }
  
  public void testFindNextSelf() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DESC", "topic3",
             "TOPIC", getTopicById("topic3"));
    
    verifyQuery(matches,
                "description($TOPIC, $DESC), " +
                "$DESC >= \"topic3\" " +
                "order by $DESC limit 1?");
  }

  public void testFindNothing() throws InvalidQueryException, IOException {
    load("int-occs-2.ltm");
    
    findNothing("description($TOPIC, $DESC), " +
                "$DESC > \"topic4\" " +
                "order by $DESC limit 1?");
  }
}

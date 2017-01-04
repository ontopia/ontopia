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
import java.util.Map;
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Assert;
import org.junit.Test;

// FIXME: test merges of non-topics

public class MergeTest extends AbstractQueryTest {
  
  /// empty topic map
  
  @Test
  public void testEmptyMerge() throws InvalidQueryException {
    makeEmpty();
    assertUpdate("merge $A, $B from direct-instance-of($A, $B)");
  }

  /// instance-of topic map

  @Test
  public void testSelfMerge() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int before = topicmap.getTopics().size();
    
    assertUpdate("merge topic1, topic1");

    Assert.assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == before);
  }
  
  @Test
  public void testStaticMerge() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    int before = topicmap.getTopics().size();
    
    assertUpdate("merge topic1, topic2");

    Assert.assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    Assert.assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    Assert.assertTrue("topics not same after merge",
               getTopicById("topic2") == getTopicById("topic1"));

    Assert.assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }

  @Test
  public void testDynamicMerge() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    int before = topicmap.getTopics().size();
    
    assertUpdate("merge $A, $B from $A = topic1, $B = topic2");

    Assert.assertTrue("wrong number of topics after merge: " + topicmap.getTopics().size(),
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    Assert.assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    Assert.assertTrue("topics not same after merge",
               getTopicById("topic2") == getTopicById("topic1"));

    Assert.assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }

  @Test
  public void testDynamicMerge2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    int before = topicmap.getTopics().size();
    
    assertUpdate("merge $A, topic2 from $A = topic1");

    Assert.assertTrue("wrong number of topics after merge: " + topicmap.getTopics().size(),
               topicmap.getTopics().size() == (before - 1));
    Assert.assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    Assert.assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    Assert.assertTrue("topics not same after merge",
               getTopicById("topic2") == getTopicById("topic1"));

    Assert.assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }
  
  @Test
  public void testManyMerges() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    getTopicById("topic1");
    getTopicById("topic2");
    int before = topicmap.getTopics().size();

    // merges topic1, topic2, topic3, and topic4 into a single topic
    assertUpdate("merge $A, $B from instance-of($A, $C), instance-of($B, $D)");

    Assert.assertTrue("wrong number of topics after merge: " + topicmap.getTopics().size(),
               topicmap.getTopics().size() == (before - 3));
    Assert.assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    Assert.assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    Assert.assertTrue("topic3 not available after merge",
               getTopicById("topic3") != null);
    Assert.assertTrue("topic4 not available after merge",
               getTopicById("topic4") != null);

    Assert.assertTrue("topics not same after merge (1, 2)",
               getTopicById("topic1") == getTopicById("topic2"));
    Assert.assertTrue("topics not same after merge (1, 3)",
               getTopicById("topic1") == getTopicById("topic3"));
    Assert.assertTrue("topics not same after merge (1, 4)",
               getTopicById("topic1") == getTopicById("topic4"));
    Assert.assertTrue("topics not same after merge (2, 3)",
               getTopicById("topic2") == getTopicById("topic3"));
    Assert.assertTrue("topics not same after merge (2, 4)",
               getTopicById("topic2") == getTopicById("topic4"));
    Assert.assertTrue("topics not same after merge (3, 4)",
               getTopicById("topic3") == getTopicById("topic4"));
  }
  
  @Test
  public void testParam() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    assertUpdate("merge superclass, %topic%", params);

    Assert.assertTrue("topic still attached to TM after merge",
               subclass.getTopicMap() == null);
    Assert.assertTrue("name lost after merge: " + superclass.getTopicNames().size(),
               superclass.getTopicNames().size() == 2);
    Assert.assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (topics - 1));
  }  

  @Test
  public void testParam2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    assertUpdate("merge superclass, $A from $A = %topic%", params);

    Assert.assertTrue("topic still attached to TM after merge",
               subclass.getTopicMap() == null);
    Assert.assertTrue("name lost after merge: " + superclass.getTopicNames().size(),
               superclass.getTopicNames().size() == 2);
    Assert.assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (topics - 1));
  }

  @Test
  public void testParam3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    assertUpdate("merge $A, %topic% from $A = superclass", params);

    Assert.assertTrue("topic still attached to TM after merge",
               subclass.getTopicMap() == null);
    Assert.assertTrue("name lost after merge: " + superclass.getTopicNames().size(),
               superclass.getTopicNames().size() == 2);
    Assert.assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (topics - 1));
  }

  /// error tests
    
  @Test
  public void testVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("merge $A, topic1");
  }

  @Test
  public void testNoSuchParam() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("merge %A%, topic1");
  }

  @Test
  public void testTopicAndTopicMap() throws InvalidQueryException {
    makeEmpty();
    assertUpdateError("merge topic1, $TM from topicmap($TM)");
  }
}

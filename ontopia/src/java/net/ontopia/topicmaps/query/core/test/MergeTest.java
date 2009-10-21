
package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;

import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

// FIXME: test merges of non-topics

public class MergeTest extends AbstractQueryTest {
  
  public MergeTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
  }

  public void tearDown() {
    closeStore();
  }

  /// empty topic map
  
  public void testEmptyMerge() throws InvalidQueryException {
    makeEmpty();
    update("merge $A, $B from direct-instance-of($A, $B)!");
  }

  /// instance-of topic map

  public void testSelfMerge() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("merge topic1, topic1!");

    assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == 6);
  }
  
  public void testStaticMerge() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("merge topic1, topic2!");

    assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == 5);
    assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    assertTrue("topics not same after merge",
               getTopicById("topic2") == getTopicById("topic1"));

    assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }

  public void testDynamicMerge() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("merge $A, $B from $A = topic1, $B = topic2!");

    assertTrue("wrong number of topics after merge: " + topicmap.getTopics().size(),
               topicmap.getTopics().size() == 5);
    assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    assertTrue("topics not same after merge",
               getTopicById("topic2") == getTopicById("topic1"));

    assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }

  public void testDynamicMerge2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("merge $A, topic2 from $A = topic1!");

    assertTrue("wrong number of topics after merge: " + topicmap.getTopics().size(),
               topicmap.getTopics().size() == 5);
    assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    assertTrue("topics not same after merge",
               getTopicById("topic2") == getTopicById("topic1"));

    assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }
  
  public void testManyMerges() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicIF topic2 = getTopicById("topic2");
    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);

    // merges topic1, topic2, topic3, and topic4 into a single topic
    update("merge $A, $B from instance-of($A, $C), instance-of($B, $D)!");

    assertTrue("wrong number of topics after merge: " + topicmap.getTopics().size(),
               topicmap.getTopics().size() == 3);
    assertTrue("topic1 not available after merge",
               getTopicById("topic1") != null);
    assertTrue("topic2 not available after merge",
               getTopicById("topic2") != null);
    assertTrue("topic3 not available after merge",
               getTopicById("topic3") != null);
    assertTrue("topic4 not available after merge",
               getTopicById("topic4") != null);

    assertTrue("topics not same after merge (1, 2)",
               getTopicById("topic1") == getTopicById("topic2"));
    assertTrue("topics not same after merge (1, 3)",
               getTopicById("topic1") == getTopicById("topic3"));
    assertTrue("topics not same after merge (1, 4)",
               getTopicById("topic1") == getTopicById("topic4"));
    assertTrue("topics not same after merge (2, 3)",
               getTopicById("topic2") == getTopicById("topic3"));
    assertTrue("topics not same after merge (2, 4)",
               getTopicById("topic2") == getTopicById("topic4"));
    assertTrue("topics not same after merge (3, 4)",
               getTopicById("topic3") == getTopicById("topic4"));
  }
  
  public void testParam() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    update("merge superclass, %topic%!", params);

    assertTrue("topic still attached to TM after merge",
               subclass.getTopicMap() == null);
    assertTrue("name lost after merge: " + superclass.getTopicNames().size(),
               superclass.getTopicNames().size() == 2);
    assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (topics - 1));
  }  

  public void testParam2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    update("merge superclass, $A from $A = %topic%!", params);

    assertTrue("topic still attached to TM after merge",
               subclass.getTopicMap() == null);
    assertTrue("name lost after merge: " + superclass.getTopicNames().size(),
               superclass.getTopicNames().size() == 2);
    assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (topics - 1));
  }

  public void testParam3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();
    TopicIF subclass = getTopicById("subclass");
    TopicIF superclass = getTopicById("superclass");
    Map params = makeArguments("topic", subclass);

    update("merge $A, %topic% from $A = superclass!", params);

    assertTrue("topic still attached to TM after merge",
               subclass.getTopicMap() == null);
    assertTrue("name lost after merge: " + superclass.getTopicNames().size(),
               superclass.getTopicNames().size() == 2);
    assertTrue("wrong number of topics after merge",
               topicmap.getTopics().size() == (topics - 1));
  }

  /// error tests
    
  public void testVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    updateError("merge $A, topic1!");
  }

  public void testNoSuchParam() throws InvalidQueryException {
    makeEmpty();
    updateError("merge %A%, topic1!");
  }

  public void testTopicAndTopicMap() throws InvalidQueryException {
    makeEmpty();
    updateError("merge topic1, $TM from topicmap($TM)!");
  }
}

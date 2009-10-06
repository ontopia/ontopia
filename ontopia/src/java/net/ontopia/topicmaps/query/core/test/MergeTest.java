
package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

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

    assertTrue("both topics still attached to TM after merge",
               topic1.getTopicMap() == null || topic2.getTopicMap() == null);
  }
  
  /// error tests
    
  public void testVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    updateError("merge $A, topic1!");
  }

}

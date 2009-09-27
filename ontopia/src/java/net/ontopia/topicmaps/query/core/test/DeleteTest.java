
package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

// FIXME: test with parameters

public class DeleteTest extends AbstractQueryTest {
  
  public DeleteTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
  }

  public void tearDown() {
    closeStore();
  }

  /// empty topic map
  
  public void testEmptyDelete() throws InvalidQueryException {
    makeEmpty();
    update("delete $A, $B from direct-instance-of($A, $B)!");
  }

  /// instance-of topic map
  
  public void testStaticDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete topic4!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 5);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
  }

  public void testSimpleDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete $A from $A = topic4!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 5);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
  }

  public void testProjectedDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete $A from $A = topic4, $B = topic3!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 5);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    assertTrue("topic3 not still available after delete",
               getTopicById("topic3") != null);
  }
  
  public void testMixedDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete $A, topic3 from $A = topic4!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 4);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    assertTrue("topic3 still available after delete",
               getTopicById("topic3") == null);
  }

  public void testTopicTypeDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete type2!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 3);
    assertTrue("type2 still available after delete",
               getTopicById("type2") == null);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    assertTrue("topic3 still available after delete",
               getTopicById("topic3") == null);
  }  

  public void testDeleteTwice() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete $A, topic4 from $A = topic4!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 5);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
  }

  public void testBiggerDelete() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete $A from instance-of($A, type2)!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 4);
    assertTrue("topic4 still available after delete",
               getTopicById("topic4") == null);
    assertTrue("topic3 still available after delete",
               getTopicById("topic3") == null);
  }  

  public void testDeleteAll() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    assertTrue("wrong number of topics to start with",
               topicmap.getTopics().size() == 6);
    
    update("delete $A, $B from instance-of($A, $B)!");

    assertTrue("wrong number of topics after delete",
               topicmap.getTopics().size() == 0);
  }

  /// delete function tests

  public void testIIStatic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic4 = getTopicById("topic4");
    LocatorIF ii = (LocatorIF) topic4.getItemIdentifiers().iterator().next();
    
    update("delete item-identifier(topic4, \"" + ii.getAddress() + "\")!");

    assertTrue("topic retains item identifier after delete",
               topic4.getItemIdentifiers().isEmpty());
  }

  public void testIIDynamic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic4 = getTopicById("topic4");
    
    update("delete item-identifier(topic4, $II) from item-identifier(topic4, $II)!");

    assertTrue("topic retains item identifier after delete",
               topic4.getItemIdentifiers().isEmpty());
  }
  
  /// error tests
    
  public void testVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    updateError("delete $A!");
  }

  public void testNoSuchVariable() throws InvalidQueryException {
    makeEmpty();
    updateError("delete $A from $B = 1!");
  }

  public void testBadType() throws InvalidQueryException {
    makeEmpty();
    updateError("delete $A from $A = 1!");
  }

  public void testWrongArgNo() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    updateError("delete item-identifier(topic4)!");
  }

  public void testWrongArgNo2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    updateError("delete item-identifier(topic4, \"foo:bar\", topic3)!");
  }
  
  public void testFunctionVariableButNoFrom() throws InvalidQueryException {
    makeEmpty();
    updateError("delete item-identifier($A, $B)!");
  }
  
  public void testNoSuchFunction() throws InvalidQueryException {
    makeEmpty();
    updateError("delete gurble(topic4, \"http://example.org\")!");
  }
}

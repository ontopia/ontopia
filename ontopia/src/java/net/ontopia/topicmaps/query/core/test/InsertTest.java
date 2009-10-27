
package net.ontopia.topicmaps.query.core.test;

import java.util.Map;
import java.util.Iterator;
import java.io.IOException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;

public class InsertTest extends AbstractQueryTest {
  
  public InsertTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
  }

  public void tearDown() {
    closeStore();
  }

  /// empty topic map
  
  public void testEmptyInsert() throws InvalidQueryException {
    makeEmpty();
    update("insert topic . !");

    TopicIF topic = getTopicById("topic");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    assertTrue("topic not found after insert",
               topic != null);
  }

  public void testEmptyInsert2() throws InvalidQueryException {
    makeEmpty();
    update("insert http://example.com . !");

    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    assertTrue("topic not found after insert",
               topic != null);
    assertTrue("topic does not have subject identifier",
               topic.getSubjectIdentifiers().size() == 1);
  }

  public void testEmptyInsert3() throws InvalidQueryException {
    makeEmpty();
    update("insert = http://example.com . !");

    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    assertTrue("topic not found after insert",
               topic != null);
    assertTrue("topic does not have subject locator",
               topic.getSubjectLocators().size() == 1);
  }

  public void testEmptyInsert4() throws InvalidQueryException {
    makeEmpty();
    update("insert topic isa type . !");

    TopicIF topic = getTopicById("topic");
    TopicIF type = getTopicById("type");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 2);
    assertTrue("topic not found after insert",
               topic != null);
    assertTrue("type not found after insert",
               type != null);
    assertTrue("topic not instance of type",
               topic.getTypes().contains(type));
  }

  public void testEmptyInsert5() throws InvalidQueryException {
    makeEmpty();
    update("using foo for i\"http://example.com/\" " +
           "insert foo:bar . !");

    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    LocatorIF subjid = (LocatorIF) topic.getSubjectIdentifiers().iterator().next();
    assertTrue("topic has wrong subject identifier: " + subjid,
               subjid.getAddress().equals("http://example.com/bar"));
  }

  public void testEmptyWildcard() throws InvalidQueryException {
    makeEmpty();
    update("insert ?foo . !");

    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == 1);
    TopicIF topic = (TopicIF) topicmap.getTopics().iterator().next();
    assertTrue("topic has no item identifier",
               !topic.getItemIdentifiers().isEmpty());
  }

  /// instance-of topic map

  public void testAddOccurrence() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    update("insert $topic newocctype: \"hey\" . from $topic = topic1!");

    TopicIF topic = getTopicById("topic1");
    assertTrue("topic did not get new occurrence",
               topic.getOccurrences().size() == 1);
  }

  public void testAddOccurrence2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    update("insert $topic objid: $id . from " +
           "$topic = topic1, object-id($topic, $id)!");

    TopicIF topic = getTopicById("topic1");
    assertTrue("topic did not get new occurrence",
               topic.getOccurrences().size() == 1);
  }

  public void testWildcard() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int topicsbefore = topicmap.getTopics().size();
    update("insert $type isa ?newtype . from " +
           "$type = type1!");

    TopicIF topic = getTopicById("type1");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == topicsbefore + 1);
    assertTrue("topic does not have new type after insert",
               !topic.getTypes().isEmpty());
  }

  public void testWildcard2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    int topicsbefore = topicmap.getTopics().size();
    update("insert $type isa ?newtype . from " +
           "{ $type = type1 | $type = type2 }!");

    TopicIF topic1 = getTopicById("type1");
    TopicIF topic2 = getTopicById("type2");
    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == topicsbefore + 2);
    assertTrue("topic1 does not have new type after insert",
               !topic1.getTypes().isEmpty());
    assertTrue("topic2 does not have new type after insert",
               !topic2.getTypes().isEmpty());
    TopicIF type1 = (TopicIF) topic1.getTypes().iterator().next();
    TopicIF type2 = (TopicIF) topic2.getTypes().iterator().next();
    assertTrue("topics have the same type",
               type1 != type2);
  }

  public void testQName() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    int topics = topicmap.getTopics().size();

    update("using xtm for i\"http://www.topicmaps.org/xtm/1.0/core.xtm#\" " +
           "insert xtm:test . !");

    assertTrue("wrong number of topics after insert",
               topicmap.getTopics().size() == (topics + 1));

    TopicIF test = topicmap.getTopicBySubjectIdentifier(new URILocator("http://www.topicmaps.org/xtm/1.0/core.xtm#test"));
    assertTrue("no xtm:test after insert", test != null);
  }  

  public void testParam() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic = getTopicById("topic1");
    Map params = makeArguments("topic", topic);
    update("insert $topic newocctype: \"hey\" . from $topic = %topic%!", params);

    assertTrue("topic did not get new occurrence",
               topic.getOccurrences().size() == 1);
  }
}

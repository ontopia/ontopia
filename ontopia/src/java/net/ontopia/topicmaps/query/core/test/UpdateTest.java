
package net.ontopia.topicmaps.query.core.test;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

// FIXME: value() with three parameters
// FIXME: URLs which aren't really

public class UpdateTest extends AbstractQueryTest {
  
  public UpdateTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
  }

  public void tearDown() {
    closeStore();
  }

  /// empty topic map
  
  public void testEmptyUpdate() throws InvalidQueryException {
    makeEmpty();
    update("update value($TN, \"foo\") from topic-name($T, $TN)!");
  }

  /// instance-of topic map

  public void testStaticNameChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    TopicNameIF name = (TopicNameIF) getObjectById("jills-name");
    
    update("update value(jills-name, \"Jill R. Hacker\")!");

    assertTrue("name not changed after update",
               name.getValue().equals("Jill R. Hacker"));
  }
  
  public void testDynamicNameChange() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    TopicIF topic1 = getTopicById("topic1");
    TopicNameIF name = (TopicNameIF) topic1.getTopicNames().iterator().next();
    
    update("update value($N, \"TOPIC1\") from topic-name(topic1, $N)!");

    assertTrue("name not changed after update",
               name.getValue().equals("TOPIC1"));
  }

  public void testStaticOccChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update value(jills-contract, \"No such contract\")!");

    assertTrue("occurrence not changed after update",
               occ.getValue().equals("No such contract"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_STRING));
  }
  
  public void testDynamicOccChange() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update value($C, \"No such contract\") from type($C, contract)!");

    assertTrue("occurrence not changed after update",
               occ.getValue().equals("No such contract"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_STRING));
  }

  public void testStaticResource() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update resource(jills-contract, \"http://example.com\")!");

    assertTrue("occurrence not changed after update: " + occ.getLocator(),
               occ.getLocator().getAddress().equals("http://example.com/"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_URI));
  }

  public void testDynamicResource() throws InvalidQueryException, IOException {
    load("jill.xtm");

    OccurrenceIF occ = (OccurrenceIF) getObjectById("jills-contract");
    
    update("update resource($C, \"http://example.com\") " +
           "from type($C, contract)!");

    assertTrue("occurrence not changed after update: " + occ.getLocator(),
               occ.getLocator().getAddress().equals("http://example.com/"));
    assertTrue("incorrect datatype after update",
               occ.getDataType().equals(DataTypes.TYPE_URI));
  }

  public void testParam() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = (TopicNameIF) subclass.getTopicNames().iterator().next();
    Map params = makeArguments("name", name);

    update("update value(%name%, \"SUBCLASS\")!", params);

    assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }  

  public void testParam2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = (TopicNameIF) subclass.getTopicNames().iterator().next();
    Map params = new HashMap();
    params.put("v", "SUBCLASS");

    update("update value(@" + name.getObjectId() + ", %v%)!", params);

    assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }

  public void testParam3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    TopicIF subclass = getTopicById("subclass");
    TopicNameIF name = (TopicNameIF) subclass.getTopicNames().iterator().next();
    Map params = makeArguments("name", name);

    update("update value($N, \"SUBCLASS\") from $N = %name%!", params);

    assertTrue("name value not changed",
               name.getValue().equals("SUBCLASS"));
  }
  
  /// error tests

  public void testNotAString() throws InvalidQueryException, IOException {
    load("jill.xtm");
    updateError("update value(jills-contract, 5)!");
  }

  public void testNotAString2() throws InvalidQueryException, IOException {
    load("jill.xtm");
    updateError("update value(jills-contract, jill)!");
  }

  public void testHasNoValue() throws InvalidQueryException, IOException {
    load("jill.xtm");
    updateError("update value(jill, \"foo\")!");
  }
}

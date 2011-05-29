
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;

public class SubjectIdentifierPredicateTest extends AbstractPredicateTest {
  
  public SubjectIdentifierPredicateTest(String name) {
    super(name);
  }

  /// tests

  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getSubjectIdentifiers().iterator();
      while (it2.hasNext())
        addMatch(matches, "TOPIC", topic,
                 "LOCATOR", ((LocatorIF) it2.next()).getAddress());
    }
    
    verifyQuery(matches, "subject-identifier($TOPIC, $LOCATOR)?");
    
    closeStore();    
  }
  
  public void testTopicToLocator() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    addMatch(matches, "LOCATOR", "http://www.topicmaps.org/xtm/1.0/language.xtm#en");
    
    verifyQuery(matches, "subject-identifier(english, $LOCATOR)?");
    closeStore();
  }

  public void testLocatorToTopic() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("english"));
    
    verifyQuery(matches, "subject-identifier($TOPIC, \"http://www.topicmaps.org/xtm/1.0/language.xtm#en\")?");
    closeStore();
  }

  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    
    verifyQuery(matches, "subject-identifier(user, \"http://www.topicmaps.org/xtm/1.0/language.xtm#en\")?");
    closeStore();
  }

  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-ontologi.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "subject-identifier(english, \"http://www.topicmaps.org/xtm/1.0/language.xtm#en\")?");
    closeStore();
  }
  
}


// $Id: SubjectLocatorPredicateTest.java,v 1.6 2008/05/26 07:21:41 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class SubjectLocatorPredicateTest extends AbstractPredicateTest {
  
  public SubjectLocatorPredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();    
  }
  
  /// tests

  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getSubjectLocators().iterator();
      while (it2.hasNext())
        addMatch(matches, "TOPIC", topic,
                 "LOCATOR", ((LocatorIF) it2.next()).getAddress());
    }
    
    verifyQuery(matches, "subject-locator($TOPIC, $LOCATOR)?");
  }
  
  public void testTopicToLocator() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "LOCATOR", "http://psi.ontopia.net/test/#2");
    
    verifyQuery(matches, "subject-locator(type2, $LOCATOR)?");
  }
  
  public void testTopicToNoLocator() throws InvalidQueryException, IOException {
    // motivated by bug #1453
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("type2"),
             "LOCATOR", "http://psi.ontopia.net/test/#2");
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " + // don't reorder
                "select $TOPIC, $LOCATOR from " +
                "instance-of($INST, $TOPIC), " +
                "subject-locator($TOPIC, $LOCATOR)?");
  }
  
  public void testLocatorToTopic() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("type2"));
    
    verifyQuery(matches, "subject-locator($TOPIC, \"http://psi.ontopia.net/test/#2\")?");
  }

  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    
    verifyQuery(matches, "subject-locator(type1, \"http://psi.ontopia.net/test/#2\")?");
  }

  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "subject-locator(type2, \"http://psi.ontopia.net/test/#2\")?");
  }

  public void testBug1290() throws InvalidQueryException, IOException {
    load("opera.ltm");

    List matches = new ArrayList();
    addMatch(matches, "URL", "http://home.prcn.org/~pauld/opera/");

    verifyQuery(matches, "select $URL from " +
                "  resource($OCC, $URL), " +
                "  subject-locator($LOCATOR-OF, $URL), " +
                "  occurrence($OCCURRENCE-OF, $OCC) " +
                "order by $URL?");
  }
  
}

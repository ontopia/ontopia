
// $Id: TopicNamePredicateTest.java,v 1.7 2008/06/12 14:37:21 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class TopicNamePredicateTest extends AbstractPredicateTest {
  
  public TopicNamePredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }
  
  /// tests
  
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      Iterator it2 = topic.getTopicNames().iterator();
      while (it2.hasNext()) {
        TopicNameIF bn = (TopicNameIF) it2.next();
        addMatch(matches, "BNAME", bn, "TOPIC", topic);
      }
    }
    
    verifyQuery(matches, "topic-name($TOPIC, $BNAME)?");
  }

  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addTopicNames(matches, "BNAME", getTopicById("marriage"));
    
    verifyQuery(matches, "topic-name(marriage, $BNAME)?");
  }

  public void testWithOccurrences() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    findNothing(OPT_TYPECHECK_OFF +
                "occurrence(white-horse, $OCC), topic-name($T, $OCC)?");
  }

  public void testWithSpecificTopicName() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("marriage");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();

    addMatch(matches, "TOPIC", topic);
    
    verifyQuery(matches, "topic-name($TOPIC, @" + bn.getObjectId() + ")?");
  }
  
  public void testWithBothBoundTrue() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("marriage");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();

    matches.add(new HashMap());
    
    verifyQuery(matches, "topic-name(marriage, @" + bn.getObjectId() + ")?");
  }
  
  public void testWithBothBoundFalse() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("marriage");
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    
    verifyQuery(matches, "topic-name(parenthood, @" + bn.getObjectId() + ")?");
  }
  
  public void testTypeConflict() throws InvalidQueryException, IOException {
    load("family2.ltm");

    findNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), topic-name($TM, $BN)?");
  }

  public void testTypeConflict2() throws InvalidQueryException, IOException {
    load("family2.ltm");
    
    findNothing(OPT_TYPECHECK_OFF +
                "topic-name(@" + topicmap.getObjectId() + ", $BN)?");
  }

  public void testTypeConflict3() throws InvalidQueryException, IOException {
    load("family2.ltm");

    Map params = new HashMap();
    params.put("topic", topicmap);
    
    findNothing(OPT_TYPECHECK_OFF +
                "topic-name(%topic%, $BN)?", params);
  }

  public void testTypeConflict4() throws InvalidQueryException, IOException {
    load("family2.ltm");

    Map params = new HashMap();
    params.put("bn", topicmap);
    
    findNothing(OPT_TYPECHECK_OFF +
                "topic-name($TOPIC, %bn%)?", params);
  }
  
  /// helpers

  private void addTopicNames(List matches, String var, TopicIF topic) {
    Iterator it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      addMatch(matches, var, bn);
    }
  }
  
}

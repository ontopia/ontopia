
// $Id$

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class CoalescePredicateTest extends AbstractPredicateTest {
  
  public CoalescePredicateTest(String name) {
    super(name);
  }

  /// tests 
  
  public void testNotBoundTrueOne() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);

    try {
      verifyQuery(matches, "coalesce($TOPIC, thequeen)?");
      fail("Not valid, but still passed.");
    } catch (Exception e) {
      // ok
    }
    closeStore();
  }
  
  public void testNotBoundTrueFirst() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);

    verifyQuery(matches, "coalesce($TOPIC, thequeen, horse)?");
    closeStore();
  }
  
  public void testNotBoundTrueFirstVariable() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("thequeen");
    addMatch(matches, "TOPIC", topic);

    verifyQuery(matches, "select $TOPIC from $QUEEN = thequeen, coalesce($TOPIC, $QUEEN, horse)?");
    closeStore();
  }
  
  public void testNotBoundTrueSecondVariable() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF topic = getTopicById("horse");
    addMatch(matches, "TOPIC", topic);

    verifyQuery(matches, "select $TOPIC from $HORSE = horse, { $QUEEN = horse }, coalesce($TOPIC, $QUEEN, horse)?");
    closeStore();
  }
  
  public void testBoundTrueFirst() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    verifyQuery("coalesce(thequeen, thequeen, horse)?");
    closeStore();
  }
  
  public void testBoundTrueSecond() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    verifyQuery("coalesce(thequeen, horse, thequeen)?");
    closeStore();
  }
  
}


// $Id: NotEqualsPredicateTest.java,v 1.3 2005/07/13 08:56:48 grove Exp $

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotEqualsPredicateTest extends AbstractPredicateTest {
  
  public NotEqualsPredicateTest(String name) {
    super(name);
  }

  /// setup

  public void tearDown() {    
    closeStore();
  }
  
  /// tests

  public void testNotEqualsFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("topic1 /= topic1?");
  }

  public void testNotEqualsTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches, "topic1 /= topic2?");
  }

  public void testNotEqualsString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));
    addMatch(matches, "TOPIC", getTopicById("topic3"));
    addMatch(matches, "TOPIC", getTopicById("topic4"));

    verifyQuery(matches, 
		"select $TOPIC from occurrence($TOPIC, $O), " + 
		"type($O, description), value($O, $DESC), " +
		"$DESC /= \"topic2\"?");
  }

  // bug caused by optimizer doing /= before all arguments bound (no number)
  public void testNotEqualsReordering() throws InvalidQueryException, IOException {
    load("factbook.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));

    // if the bug is here we get a QueryException
    processor.execute("borders-with($A : country, $B : country), " +
                      "borders-with($C : country, $D : country), " +
                      "$A /= $C?");
  }
}


package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComparisonPredicatesTest extends AbstractPredicateTest {
  
  public ComparisonPredicatesTest(String name) {
    super(name);
  }

  /// setup

  public void tearDown() {    
    closeStore();
  }
  
  /// tests

  public void testLTFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("\"b\" < \"a\"?");
  }

  public void testLTFalse2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("\"a\" < \"a\"?");
  }
  
  public void testLTEFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("\"b\" <= \"a\"?");
  }
  
  public void testGTFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("\"a\" > \"b\"?");
  }
  
  public void testGTFalse2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("\"a\" > \"a\"?");
  }
  
  public void testGTEFalse() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("\"a\" >= \"b\"?");
  }
  
  public void testLTTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches,"\"a\" < \"b\"?");
  }
  
  public void testLTETrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches,"\"a\" <= \"b\"?");
  }
  
  public void testLTETrue2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches,"\"a\" <= \"a\"?");
  }
  
  public void testGTTrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches,"\"b\" > \"a\"?");
  }
  
  public void testGTETrue() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches,"\"b\" >= \"a\"?");
  }
  
  public void testGTETrue2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();
    matches.add(new HashMap());
    verifyQuery(matches,"\"a\" >= \"a\"?");
  }

  public void testLTVariable() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();

    addMatch(matches, "T", getTopicById("topic1"));
    addMatch(matches, "T", getTopicById("topic2"));
    
    verifyQuery(matches, "select $T from " +
                "topic-name($T, $N), value($N, $V), $V < \"Topic3\"?");
  } 

  public void testLTVariable2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    List matches = new ArrayList();

    addMatch(matches, "T", getTopicById("topic1"));
    addMatch(matches, "T", getTopicById("topic2"));
    
    verifyQuery(matches, "select $T from " +
                "topic-name($T, $N), $V < \"Topic3\", value($N, $V)?");
  } 

  public void testUnboundVariable() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    
    getParseError("topic-name($T, $N), $V < \"Topic3\"?"); // V never bound...
  }

  public void testTypeClash() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    findNothing("description($TOPIC, $VALUE), $VALUE < 1?");
  }

  public void testTypeClash2() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    findNothing("description($TOPIC, $VALUE), $VALUE <= 1?");
  }

  public void testTypeClash3() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    findNothing("description($TOPIC, $VALUE), 1 > $VALUE?");
  }

  public void testTypeClash4() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    findNothing("description($TOPIC, $VALUE), 1 >= $VALUE?");
  }
}

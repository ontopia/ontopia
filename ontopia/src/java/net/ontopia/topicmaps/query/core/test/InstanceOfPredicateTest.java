
// $Id: InstanceOfPredicateTest.java,v 1.4 2006/07/06 12:07:22 grove Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class InstanceOfPredicateTest extends AbstractPredicateTest {
  
  public InstanceOfPredicateTest(String name) {
    super(name);
  }

  /// setup

  public void tearDown() {    
    closeStore();
  }

  /// tests
  
  public void testEmptyInstanceOfAB() throws InvalidQueryException {
    makeEmpty();
    findNothing("instance-of($A, $B)?");
  }

  public void testInstanceOfAB() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    
    verifyQuery(matches, "instance-of($A, $B)?");
  }
  
  public void testInstanceOfaB() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    
    verifyQuery(matches, "instance-of(topic1, $B)?");
  }

  public void testInstanceOfAb() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    verifyQuery(matches, "instance-of($A, type1)?");
  }

  public void testInstanceOfab() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "instance-of(topic1, type1)?");
  }

  public void testInstanceOfWrong() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("instance-of(topic1, type2)?");
  }

  public void testInstanceOfABSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type1"));
    
    verifyQuery(matches, "instance-of($A, $B)?");
  }
  
  public void testInstanceOfaBSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    
    verifyQuery(matches, "instance-of(topic1, $B)?");
  }

  public void testInstanceOfaBSub2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    addMatch(matches, "B", getTopicById("type2"));
    
    verifyQuery(matches, "instance-of(topic3, $B)?");
  }
  
  public void testInstanceOfAbSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    addMatch(matches, "A", getTopicById("topic3"));
    addMatch(matches, "A", getTopicById("topic4"));
    
    verifyQuery(matches, "instance-of($A, type1)?");
  }

  public void testInstanceOfAbSub2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic3"));
    addMatch(matches, "A", getTopicById("topic4"));
    
    verifyQuery(matches, "instance-of($A, type2)?");
  }
  
  public void testInstanceOfabSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "instance-of(topic1, type1)?");
  }

  public void testInstanceOfabSub2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "instance-of(topic2, type1)?");
  }

  public void testInstanceOfWrongSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    findNothing("instance-of(topic1, type2)?");
  }

  public void testWrongType1() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    findNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of($TM, $TYPE)?");
  }

  public void testWrongType2() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    findNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of($INSTANCE, $TM)?");
  }

  public void testWrongType3() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    findNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of($TM, type1)?");
  }

  public void testWrongType4() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    findNothing(OPT_TYPECHECK_OFF +
                "topicmap($TM), instance-of(topic1, $TM)?");
  }
  
}

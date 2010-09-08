
// $Id: ValueLikePredicateTest.java,v 1.12 2006/05/05 12:18:16 grove Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class ValueLikePredicateTest extends AbstractPredicateTest {
  
  public ValueLikePredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }

  /// tests

  public void testWithSpecificOccurrenceAndString()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    TopicIF topic = getTopicById("topic1");
    OccurrenceIF oc = (OccurrenceIF) topic.getOccurrences().iterator().next();
    String oid = oc.getObjectId();

    // first argument must be unbound
    getParseError("value-like(@" + oid + ", \"topic1\")?");
  }

  public void testWithSpecificTopicAndVariable() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    // this predicate will fail because the first argument is of type TopicIF
    findNothing(OPT_TYPECHECK_OFF +
                "value-like(topic1, $VALUE)?");
  }

  public void testWithSpecificTopicAndString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");
    
    // this predicate will fail because the first argument is of type TopicIF
    findNothing(OPT_TYPECHECK_OFF +
                "value-like(topic1, \"topic1\")?");
  }

  public void testWithUnboundBoth1() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    // query parser will complain because the second argument is unbound
    getParseError("value-like($FOO, $VALUE)?");
  }

  public void testWithUnboundBoth2() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    // this test will fail because the $TOPIC variable is of type TopicIF
    findNothing(OPT_TYPECHECK_OFF +
                "topic($TOPIC), value-like($TOPIC, $VALUE)?");
  }

  public void testWithUnboundPatternValue() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    TopicIF topic = getTopicById("topic1");
    OccurrenceIF oc = (OccurrenceIF) topic.getOccurrences().iterator().next();
    String oid = oc.getObjectId();
    
    getParseError("value-like(@" + oid + ", $VALUE)?");
  }
  
  public void testWithAnyObject() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("lms"));
    addMatch(matches, "TOPIC", getTopicById("gerd"));
    addMatch(matches, "TOPIC", getTopicById("asle"));
    
    verifyQuery(matches, "select $TOPIC from " +
                         "  value-like($BNAME, \"skalle\"), " +
                         "  topic-name($TOPIC, $BNAME)?");
  }
  
  public void testWithScoreBound() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    getParseError("select $TOPIC from " +
                  "  value-like($BNAME, \"skalle\", 0.54), " +
                  "  topic-name($TOPIC, $BNAME)?");
  }
  
  public void testWithScoreAbove001() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("lms"));
    addMatch(matches, "TOPIC", getTopicById("gerd"));
    addMatch(matches, "TOPIC", getTopicById("asle"));
    
    verifyQuery(matches, "select $TOPIC from " +
                         "  value-like($BNAME, \"skalle\", $SCORE), " +
                         "  topic-name($TOPIC, $BNAME), $SCORE > 0.01?");
  }
  
  public void testWithScoreAbove001Ordered() throws InvalidQueryException, IOException {
    load("family.ltm");

    findAny("select $TOPIC, $SCORE from " +
            "  value-like($BNAME, \"skalle\", $SCORE), " +
            "  topic-name($TOPIC, $BNAME), $SCORE > 0.01 " +
            "  order by $SCORE, $TOPIC desc limit 2 offset 1?");
  } 
  
  public void testWithScoreAbove095() throws InvalidQueryException, IOException {
    load("family.ltm");

    if (topicmap.getStore().getImplementation() == TopicMapStoreIF.IN_MEMORY_IMPLEMENTATION) {      
      List matches = new ArrayList();
      addMatch(matches, "TOPIC", getTopicById("gerd"));
      addMatch(matches, "TOPIC", getTopicById("asle"));
      
      verifyQuery(matches, "select $TOPIC from " +
                  "  value-like($BNAME, \"skalle\", $SCORE), " +
                  "  topic-name($TOPIC, $BNAME), $SCORE > 0.95?");
    }
  }
  
  public void testWithScoreBetween006and007() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    findNothing("select $TOPIC from " +
                "  value-like($BNAME, \"skalle\", $SCORE), " +
                "  topic-name($TOPIC, $BNAME), $SCORE < 0.06, $SCORE > 0.07?");
  }
  
  public void testWithScoreBetween095and070() throws InvalidQueryException, IOException {
    load("family.ltm");

    if (topicmap.getStore().getImplementation() == TopicMapStoreIF.IN_MEMORY_IMPLEMENTATION) {      
      List matches = new ArrayList();
      addMatch(matches, "TOPIC", getTopicById("lms"));
      
      verifyQuery(matches, "select $TOPIC from " +
                  "  value-like($BNAME, \"skalle\", $SCORE), " +
                  "  topic-name($TOPIC, $BNAME), $SCORE < 0.95, $SCORE > 0.70?");
    }
  }
  
  public void testWithEscapedQuotes() throws InvalidQueryException, IOException {
    load("family.ltm");

    // WARNING: rdbms: this will cause a parse error with Oracle Text
    findNothing("select $TOPIC from " +
                "  value-like($BNAME, \"and \"\"ho\"\" ho\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  public void testWithSingleQuote() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    findNothing("select $TOPIC from " +
                "  value-like($BNAME, \"foo'bar\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  // see bug #955
  public void testWithEmptyString() throws InvalidQueryException, IOException {
    load("family.ltm");

    findNothing("select $TOPIC from " +
                "  value-like($BNAME, \"\"), " +
                "  topic-name($TOPIC, $BNAME)?");
  }

  public void testBug987() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));

    verifyQuery(matches,
                "select $TOPIC from " +
                "  type($OCC, description), " +
                "  occurrence($TOPIC, $OCC), " + 
                "  value-like($OCC, \"topic1\")?");
  }

  public void testIssue302() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"));

    verifyQuery(matches,
                "select $TOPIC from " +
                "  $query = \"topic1\", " +
                "  type($OCC, description), " +
                "  occurrence($TOPIC, $OCC), " + 
                "  value-like($OCC, $query)?");
  }
}

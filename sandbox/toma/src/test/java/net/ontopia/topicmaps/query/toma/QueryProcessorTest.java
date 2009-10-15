package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class QueryProcessorTest extends AbstractTomaQueryTestCase {
  
  public QueryProcessorTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// generic tests
  
  public void testTwoColumnsBound() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    TopicIF t = getTopicById("tn");
    addMatch(matches, "$T", t, "$T.NAME", t.getTopicNames().iterator().next());
    t = getTopicById("lmg");
    addMatch(matches, "$T", t, "$T.NAME", t.getTopicNames().iterator().next());
    
    verifyQuery(matches, "select $t, $t.name where $t.type = person;");
  }

  public void testTwoColumnsUnbound() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    TopicIF ltm = getTopicById("ltm");
    TopicIF xtm = getTopicById("xtm");
    Object tn = getTopicById("tn").getTopicNames().iterator().next();
    Object lmg = getTopicById("lmg").getTopicNames().iterator().next();
    
    addMatch(matches, "$T", ltm, "$P.NAME", tn);
    addMatch(matches, "$T", ltm, "$P.NAME", lmg);
    addMatch(matches, "$T", xtm, "$P.NAME", tn);
    addMatch(matches, "$T", xtm, "$P.NAME", lmg);
    
    verifyQuery(matches, "select $t, $p.name where $t.type = format and $p.type = person;");
  }
}

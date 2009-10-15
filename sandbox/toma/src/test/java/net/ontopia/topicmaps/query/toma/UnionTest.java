package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class UnionTest extends AbstractTomaQueryTestCase {
  
  public UnionTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// function tests
  
  public void testUnion() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("topic-maps"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(
        matches,
        "select $t where exists $t.oc(mass) union select $t where $t.type = standard;");
  }
  
  public void testUnionAll() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("topic-maps"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(
        matches,
        "select $t where exists $t.oc(mass) union all select $t where $t.type = standard;");
  }
  
  public void testExcept() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("topic-maps"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(
        matches,
        "select $t where $t.type = standard except select $t where $t.name ~* 'XTM';");
  }
  
  public void testIntersect() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(
        matches,
        "select $t where $t.type = standard intersect select $t where $t.name ~* 'XTM';");
  }
}

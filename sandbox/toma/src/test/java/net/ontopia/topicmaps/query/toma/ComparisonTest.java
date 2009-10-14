package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class ComparisonTest extends AbstractTomaQueryTestCase {
  
  public ComparisonTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// comparison tests
  
  public void testEquals() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm"));
    
    verifyQuery(matches, "select $t where $t.id = i'xtm';");
  }
  
  public void testNotEquals() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm"));
    
    verifyQuery(matches, "select $t where $t.type = format and $t != i'xtm';");
  }
  
  public void testLessThan() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(matches, "select $t where $t.oc(pages) < '100';");
  }

  public void testLessThanEquals() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(matches, "select $t where $t.oc(pages) <= '80';");
  }
  
  public void testGreaterThan() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(matches, "select $t where $t.oc(pages) > '100';");
  }
  
  public void testGreaterThanEquals() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(matches, "select $t where $t.oc(pages) >= '120';");
  }
  
  public void testRegExp() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(matches, "select $t where $t.name ~ '.*standard';");
  }
  
  public void testRegExpCaseInsensitive() throws InvalidQueryException,
      IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("standard"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(matches, "select $t where $t.name ~* '.*STANDARD';");
  }
  
  public void testNotRegExp() throws InvalidQueryException,
      IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("topic-maps"));

    verifyQuery(matches, "select $t where $t.type = standard and $t.name !~ 'XTM';");
  }

  public void testNotRegExpCaseInsensitive() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("topic-maps"));

    verifyQuery(matches,
        "select $t where $t.type = standard and $t.name !~* 'xtm';");
  }
}

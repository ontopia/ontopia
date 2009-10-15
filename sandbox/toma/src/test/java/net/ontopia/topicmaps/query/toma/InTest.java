package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class InTest extends AbstractTomaQueryTestCase {
  
  public InTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// IN tests
  
  public void testSimpleIN() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("format"));
    addMatch(matches, "$T", getTopicById("standard"));
    
    verifyQuery(
        matches,
        "select $t where $t IN (format, standard);");
  }
  
  public void testDifferentTypes() throws InvalidQueryException, IOException {
    load("full.ltm");
    
    String query = "select $T where $T IN (format.instance, 'XTM standard');";
    try {
      processor.execute(query);
      fail("accepted invalid IN query.");
    } catch (InvalidQueryException e) {
    }
  }
  
  public void testComplexIN() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm"));
    addMatch(matches, "$T", getTopicById("ltm"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(
        matches,
        "select $t where $s.name = 'XTM standard' and $t IN (format.instance, $s);");
  }
  
  public void testStringIN() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(
        matches,
        "select $t where $t.name IN ('XTM standard', 'LTM standard');");
  }
}

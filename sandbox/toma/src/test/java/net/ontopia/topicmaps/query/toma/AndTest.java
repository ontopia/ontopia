package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class AndTest extends AbstractTomaQueryTestCase {
  
  public AndTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// and tests
  
  public void testSimpleAnd() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("tn"));
    
    verifyQuery(matches, "select $t where $t.type = person and $t.name !~ 'Lars';");
  }

  public void testCombineTwoVariable() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    
    verifyQuery(matches, "select $t where $s = standard and $t.type = $s and not exists $t.name@fullname;");
  }
}

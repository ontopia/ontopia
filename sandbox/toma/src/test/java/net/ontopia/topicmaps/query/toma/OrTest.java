package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class OrTest extends AbstractTomaQueryTestCase {
  
  public OrTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// or tests
  
  public void testSimpleOr() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("ltm-standard"));
    addMatch(matches, "$T", getTopicById("xtm-standard"));
    
    verifyQuery(matches, "select $t where $t.oc(version) = '1.2' or $t.oc(pages) = '120';");
  }
}

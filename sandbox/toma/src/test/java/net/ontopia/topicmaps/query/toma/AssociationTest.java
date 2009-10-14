package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

@SuppressWarnings("unchecked")
public class AssociationTest extends AbstractTomaQueryTestCase {
  
  public AssociationTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// scope tests
  
  public void testSimpleScope() throws InvalidQueryException, IOException {
    load("full.ltm");

    List matches = new ArrayList();
    addMatch(matches, "$T", getTopicById("xtm"));
    addMatch(matches, "$T", getTopicById("ltm"));
    
    verifyQuery(matches, "select $t where $a(format-for)->(format) = $t;");
  }  
}

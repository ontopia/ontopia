
// $Id: QueryProcessorTest.java,v 1.75 2009/04/27 11:00:50 lars.garshol Exp $

package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

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

  /// association tests
  
  public void testEquals() throws InvalidQueryException, IOException {
    load("full.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T", getTopicById("xtm"));
    
    verifyQuery(matches, "select $t where $t.id = i'xtm';");
  }  
}

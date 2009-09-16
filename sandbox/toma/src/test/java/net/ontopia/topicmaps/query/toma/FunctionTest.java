
// $Id: QueryProcessorTest.java,v 1.75 2009/04/27 11:00:50 lars.garshol Exp $

package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

public class FunctionTest extends AbstractTomaQueryTestCase {
  
  public FunctionTest(String name) {
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
  
  public void testLowercase() throws InvalidQueryException, IOException {
    load("full.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T", getTopicById("format"));
    
    verifyQuery(matches, "select $t where lowercase($t.name) = 'format';");
  }
  
  public void testUppercase() throws InvalidQueryException, IOException {
    load("full.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T", getTopicById("format"));
    
    verifyQuery(matches, "select $t where uppercase($t.name) = 'FORMAT';");
  }  
  
  public void testTitlecase() throws InvalidQueryException, IOException {
    load("full.ltm");

    List<TopicIF> matches = new ArrayList<TopicIF>();
    addMatch(matches, "$T", getTopicById("format-for"));
    
    verifyQuery(matches, "select $t where titlecase($t.name) = 'Format For';");
  }  
}

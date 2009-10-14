package net.ontopia.topicmaps.query.toma;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

public class ParsedQueryTest extends AbstractTomaQueryTestCase {
  
  public ParsedQueryTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// checking query structure

  public void testSimpleQuery() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $T where exists $T;");
    List<String> vars = query.getSelectedVariables();
    
    assertTrue("bad number of variables in selected variables", vars.size() == 1);
    assertTrue("selected variables does not contain T: " + vars, vars.contains("T"));
    closeStore();
  }
 
  public void testSimpleQuery2() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, $B where exists $A and exists $B;");
    List<String> vars = query.getSelectedVariables();
    
    assertTrue("bad number of variables in selected variables", vars.size() == 2);
    assertTrue("selected variables does not contain A in first position",
           vars.get(0).equals("A"));
    assertTrue("selected variables does not contain B in second position",
           vars.get(1).equals("B"));
    closeStore();
  }
  
  public void testSimpleCount() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select count($B) where $A.instance(1) = $B;");
    Collection<String> vars = query.getCountedVariables();
    assertTrue("bad number of variables in counted variables", vars.size() == 1);
    assertTrue("selected variables does not contain B", vars.contains("B"));
    closeStore();
  }
  
  public void testNoCount() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, $B where $A.instance(1) = $B;");
    Collection<String> vars = query.getCountedVariables();
    assertTrue("bad number of variables in counted variables", vars.size() == 0);
    closeStore();
  }
  
  public void testAllVariables() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("select $A, $B where $A.instance(1) = $B;");
    Collection<String> vars = query.getAllVariables();
    assertTrue("bad number of variables in all variables", vars.size() == 2);
    assertTrue("all variables does not contain A", vars.contains("A"));
    assertTrue("all variables does not contain B", vars.contains("B"));
    closeStore();
  }
  
  public void testOrderBy() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("select $A, $B where $A.instance(1) = $B order by 2, 1;");
    List<String> vars = query.getOrderBy();
    assertTrue("bad number of variables in order by variables",
               vars.size() == 2);
    assertTrue("order by variables does not contain B in first position",
               vars.get(0).equals("B"));
    assertTrue("order by variables does not contain A in second position",
               vars.get(1).equals("A"));
    closeStore();
  }

  public void testOrderByAscending() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("select $A, $B where $A.instance(1) = $B order by 2 desc, 1;");
    assertTrue("B is ordered descending, not ascending",
           !query.isOrderedAscending("B"));
    assertTrue("A is ordered ascending, not descending",
           query.isOrderedAscending("A"));
    closeStore();
  }
}


// $Id: ParsedQueryTest.java,v 1.9 2005/07/13 08:56:48 grove Exp $

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ParsedQueryTest extends AbstractQueryTest {
  
  public ParsedQueryTest(String name) {
    super(name);
  }

  /// checking query structure

  public void testSimpleQuery() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("instance-of($A, $B)?");
    List vars = query.getSelectedVariables();
    assertTrue("bad number of variables in selected variables", vars.size() == 2);
    assertTrue("selected variables does not contain A: " + vars, vars.contains("A"));
    assertTrue("selected variables does not contain B: " + vars, vars.contains("B"));
    closeStore();
  }
 
  public void testProjectedQuery() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A from instance-of($A, $B)?");
    List vars = query.getSelectedVariables();
    assertTrue("bad number of variables in selected variables", vars.size() == 1);
    assertTrue("selected variables does not contain A", vars.contains("A"));
    closeStore();
  }

  public void testProjectedQuery2() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, $B from instance-of($A, $B)?");
    List vars = query.getSelectedVariables();
    assertTrue("bad number of variables in selected variables", vars.size() == 2);
    assertTrue("selected variables does not contain A in first position",
           vars.get(0).equals("A"));
    assertTrue("selected variables does not contain B in second position",
           vars.get(1).equals("B"));
    closeStore();
  }
  
  public void testSimpleCount() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, count($B) from instance-of($A, $B)?");
    Collection vars = query.getCountedVariables();
    assertTrue("bad number of variables in counted variables", vars.size() == 1);
    assertTrue("selected variables does not contain B", vars.contains("B"));
    closeStore();
  }
  
  public void testNoCount() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, $B from instance-of($A, $B)?");
    Collection vars = query.getCountedVariables();
    assertTrue("bad number of variables in counted variables", vars.size() == 0);
    closeStore();
  }
  
  public void testAllVariables() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("parenthood($A : mother, $B : child, $C : father)?");
    Collection vars = query.getAllVariables();
    assertTrue("bad number of variables in all variables", vars.size() == 3);
    assertTrue("all variables does not contain A", vars.contains("A"));
    assertTrue("all variables does not contain B", vars.contains("B"));
    assertTrue("all variables does not contain C", vars.contains("C"));
    closeStore();
  }
  
  public void testOrderBy() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("parenthood($A : mother, $B : child, $C : father) order by $B, $A?");
    List vars = query.getOrderBy();
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
    ParsedQueryIF query = parse("parenthood($A : mother, $B : child, $C : father) order by $B desc, $A?");
    assertTrue("B is ordered descending, not ascending",
           !query.isOrderedAscending("B"));
    assertTrue("A is ordered ascending, not descending",
           query.isOrderedAscending("A"));
    closeStore();
  }
  
}

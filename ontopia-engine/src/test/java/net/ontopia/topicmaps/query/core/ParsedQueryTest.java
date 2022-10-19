/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class ParsedQueryTest extends AbstractQueryTest {
  
  /// checking query structure

  @Test
  public void testSimpleQuery() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("instance-of($A, $B)?");
    List vars = query.getSelectedVariables();
    Assert.assertTrue("bad number of variables in selected variables", vars.size() == 2);
    Assert.assertTrue("selected variables does not contain A: " + vars, vars.contains("A"));
    Assert.assertTrue("selected variables does not contain B: " + vars, vars.contains("B"));
    closeStore();
  }
 
  @Test
  public void testProjectedQuery() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A from instance-of($A, $B)?");
    List vars = query.getSelectedVariables();
    Assert.assertTrue("bad number of variables in selected variables", vars.size() == 1);
    Assert.assertTrue("selected variables does not contain A", vars.contains("A"));
    closeStore();
  }

  @Test
  public void testProjectedQuery2() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, $B from instance-of($A, $B)?");
    List vars = query.getSelectedVariables();
    Assert.assertTrue("bad number of variables in selected variables", vars.size() == 2);
    Assert.assertTrue("selected variables does not contain A in first position",
           vars.get(0).equals("A"));
    Assert.assertTrue("selected variables does not contain B in second position",
           vars.get(1).equals("B"));
    closeStore();
  }
  
  @Test
  public void testSimpleCount() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, count($B) from instance-of($A, $B)?");
    Collection vars = query.getCountedVariables();
    Assert.assertTrue("bad number of variables in counted variables", vars.size() == 1);
    Assert.assertTrue("selected variables does not contain B", vars.contains("B"));
    closeStore();
  }
  
  @Test
  public void testNoCount() throws InvalidQueryException {
    makeEmpty();
    ParsedQueryIF query = parse("select $A, $B from instance-of($A, $B)?");
    Collection vars = query.getCountedVariables();
    Assert.assertTrue("bad number of variables in counted variables", vars.size() == 0);
    closeStore();
  }
  
  @Test
  public void testAllVariables() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("parenthood($A : mother, $B : child, $C : father)?");
    Collection vars = query.getAllVariables();
    Assert.assertTrue("bad number of variables in all variables", vars.size() == 3);
    Assert.assertTrue("all variables does not contain A", vars.contains("A"));
    Assert.assertTrue("all variables does not contain B", vars.contains("B"));
    Assert.assertTrue("all variables does not contain C", vars.contains("C"));
    closeStore();
  }
  
  @Test
  public void testOrderBy() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("parenthood($A : mother, $B : child, $C : father) order by $B, $A?");
    List vars = query.getOrderBy();
    Assert.assertTrue("bad number of variables in order by variables",
               vars.size() == 2);
    Assert.assertTrue("order by variables does not contain B in first position",
               vars.get(0).equals("B"));
    Assert.assertTrue("order by variables does not contain A in second position",
               vars.get(1).equals("A"));
    closeStore();
  }

  @Test
  public void testOrderByAscending() throws InvalidQueryException, IOException {
    load("family.ltm");
    ParsedQueryIF query = parse("parenthood($A : mother, $B : child, $C : father) order by $B desc, $A?");
    Assert.assertTrue("B is ordered descending, not ascending",
           !query.isOrderedAscending("B"));
    Assert.assertTrue("A is ordered ascending, not descending",
           query.isOrderedAscending("A"));
    closeStore();
  }
  
}

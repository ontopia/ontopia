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
import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

public class QueryResultTest extends AbstractQueryTest {
  
  public QueryResultTest(String name) {
    super(name);
  }

  /// setup

  @Override
  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  /// setup

  @Override
  public void tearDown() {    
    closeStore();
  }

  /// test cases
  
  public void testColumnNameNoSelect() throws InvalidQueryException, IOException {
    load("family.ltm");

    List vars = new ArrayList();
    vars.add("M");
    vars.add("F");
    vars.add("C");
    
    String query = "parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);

    for (int ix = 0; ix < 3; ix++)
      assertTrue("unknown variable " + result.getColumnName(ix) + " found",
                 vars.remove(result.getColumnName(ix)));

    assertTrue("not all variables found", vars.isEmpty());
    
    try {
      result.getColumnName(3);
      fail("accepted non-existent column");
    } catch (IndexOutOfBoundsException e) {
    }

    try {
      result.getColumnName(-1);
      fail("accepted non-existent column");
    } catch (IndexOutOfBoundsException e) {
    }
  }  

  public void testColumnNameSelect() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M, $F from parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);

    assertTrue("M not first column", result.getColumnName(0).equals("M"));
    assertTrue("F not second column", result.getColumnName(1).equals("F"));
    
    try {
      result.getColumnName(2);
      fail("accepted non-existent column");
    } catch (IndexOutOfBoundsException e) {
    }

    try {
      result.getColumnName(-1);
      fail("accepted non-existent column");
    } catch (IndexOutOfBoundsException e) {
    }
  }

  public void testColumnNamesNoSelect() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);
    String[] cols = result.getColumnNames();

    assertTrue("wrong length of column name array", cols.length == 3);

    List vars = new ArrayList();
    vars.add("M");
    vars.add("F");
    vars.add("C");

    for (int ix = 0; ix < 3; ix++)
      assertTrue("unknown variable " + cols[ix] + " found",
                 vars.remove(cols[ix]));

    assertTrue("not all variables found", vars.isEmpty());
  }  

  public void testColumnNamesSelect() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    String query = "select $M, $F from parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);
    String[] cols = result.getColumnNames();

    assertTrue("wrong length of column name array", cols.length == 2);
    assertTrue("M not first column", cols[0].equals("M"));
    assertTrue("F not second column", cols[1].equals("F"));
  }

  public void testGetIndexNoSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    
    String query = "parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);

    assertTrue("variable M had bad index",
               result.getIndex("M") >= 0 && result.getIndex("M") < 3);
    assertTrue("variable F had bad index",
               result.getIndex("F") >= 0 && result.getIndex("F") < 3);
    assertTrue("variable C had bad index",
               result.getIndex("C") >= 0 && result.getIndex("C") < 3);
    assertTrue("non-existent variable Q was found",
               result.getIndex("Q") == -1);
  }  

  public void testGetIndexSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    
    String query = "select $M, $F from parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);

    assertTrue("variable M had bad index",
               result.getIndex("M") == 0);
    assertTrue("variable F had bad index",
               result.getIndex("F") == 1);
    assertTrue("non-existent variable Q was found",
               result.getIndex("Q") == -1);
  }
  
  public void testGetWidthNoSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    
    String query = "parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);

    assertTrue("result had wrong width", result.getWidth() == 3);    
  }  
  
  public void testGetWidthSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    
    String query = "select $M, $F from parenthood($M : mother, $F : father, $C : child)?";
    QueryResultIF result = processor.execute(query);
    assertTrue("result had wrong width", result.getWidth() == 2);    
  }
  
}

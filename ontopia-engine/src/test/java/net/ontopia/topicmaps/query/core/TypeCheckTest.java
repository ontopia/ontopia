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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import org.junit.Before;
import org.junit.Test;

/**
 * Used to test that the type checking in the query processor is
 * performed correctly.
 */
public class TypeCheckTest extends AbstractQueryTest {
  
  /// context management

  @Before
  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  /// type checking on literals
  
  @Test
  public void testLiteralNontopic() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("instance-of(jill-ontopia-association, $B)?");
  }
  
  @Test
  public void testLiteralTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("type(jill, $TYPE)?");
  }

  @Test
  public void testLiteralString() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("value(\"huhei\", $OCC)?");
  }

  @Test
  public void testLiteralInPair() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("employment(jill-ontopia-association : employee, $T : employer)?");
  }

  @Test
  public void testLiteralOnUncaringPredicate() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertFindNothing("jill-ontopia-association = \"oisann\"?");
  }

  @Test
  public void testLiteralInRule() throws InvalidQueryException, IOException {
    load("family.ltm");

    assertGetParseError("parent($P, $C) :- " +
                  "  parenthood(\"oida\" : mother, $P : father, $C : child). " +
                     
                  "parent(petter, $C) " +
                  "order by $C desc?");    
  }
  
  /// type checking on variables

  @Test
  public void testSimpleConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic($A), association($A)?");
  }

  @Test
  public void testManyTypesConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic($A), type($A, $B)?");
  }

  @Test
  public void testOrTypesConflict() throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic($A), { $A = jill | association($A) }?");
    assertGetParseError("{ $A = jill | association($A) }, topic($A)?");
  }

  @Test
  public void testOrTypesConflict2() throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic($A), { topicmap($A) | association($A) }?");
    assertGetParseError("{ topicmap($A) | association($A) }, topic($A)?");
  }

  @Test
  public void testConditionalTypesConflict() 
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    assertGetParseError("topic($A), { association($A) }?");
    assertGetParseError("{ association($A) }, topic($A)?");
  }

  @Test
  public void testNotTypesConflict() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("topic($A), not(association($A))?");    
    assertGetParseError("not(association($A)), topic($A)?");
  }

  @Test
  public void testRuleTypeConflict() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("has-name($T, $V) :- topic-name($T, $N), value($V, $N). " +
                  "has-name(jill, $N)?");
  }

  /// type checking on parameters

  @Test
  public void testSimpleParameterConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic(%A%), association(%A%)?");
  }

  @Test
  public void testParameterValueConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");

    Map params = new HashMap();
    params.put("A", topicmap);
    assertGetParseError("topic(%A%)?", params);
  }
  
  @Test
  public void testManyParameterTypesConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic(%A%), type(%A%, %B%)?");
  }

  @Test
  public void testOrParameterTypesConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic(%A%), { %A% = jill | association(%A%) }?");
    assertGetParseError("{ %A% = jill | association(%A%) }, topic(%A%)?");
  }

  @Test
  public void testOrParameterTypesConflict2()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    assertGetParseError("topic(%A%), { topicmap(%A%) | association(%A%) }?");
    assertGetParseError("{ topicmap(%A%) | association(%A%) }, topic(%A%)?");
  }
  
  @Test
  public void testConditionalParameterTypesConflict() 
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    assertGetParseError("topic(%A%), { association(%A%) }?");
    assertGetParseError("{ association(%A) }, topic(%A%)?");
  }

  @Test
  public void testNotParameterTypesConflict()
    throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    assertGetParseError("topic(%A%), not(association(%A%))?");    
    assertGetParseError("not(association(%A%)), topic(%A%)?");
  }

  @Test
  public void testBug2102()
    throws InvalidQueryException, IOException {
    load("opera.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "PDATE", "1905-03-16");
    
    assertQueryMatches(matches, "using op for i\"http://psi.ontopia.net/opera/\" " +
                         "select $PDATE from " +
                         "$DATE = \"1905-01-01\", " +
                         "op:premiere-date($OPERA, $PDATE), " +
                         "$DATE < $PDATE " +
                         "order by $PDATE asc limit 1?");
  }

  @Test
  public void testIssue254() throws InvalidQueryException, IOException {
    load("jill.xtm");
    assertGetParseError("reifies($R, $T), " +
                  "subject-identifier($T, \"http://psi.ontopia.net/xtm/occurrence-type/description\")?");
  }
  
}


// $Id: TypeCheckTest.java,v 1.4 2008/01/09 10:07:33 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;

/**
 * Used to test that the type checking in the query processor is
 * performed correctly.
 */
public class TypeCheckTest extends AbstractQueryTest {
  
  public TypeCheckTest(String name) {
    super(name);
  }

  /// context management

  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  public void tearDown() {
    closeStore();
  }

  /// type checking on literals
  
  public void testLiteralNontopic() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("instance-of(jill-ontopia-association, $B)?");
  }
  
  public void testLiteralTopic() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("type(jill, $TYPE)?");
  }

  public void testLiteralString() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("value(\"huhei\", $OCC)?");
  }

  public void testLiteralInPair() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("employment(jill-ontopia-association : employee, $T : employer)?");
  }

  public void testLiteralOnUncaringPredicate() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    findNothing("jill-ontopia-association = \"oisann\"?");
  }

  public void testLiteralInRule() throws InvalidQueryException, IOException {
    load("family.ltm");

    getParseError("parent($P, $C) :- " +
                  "  parenthood(\"oida\" : mother, $P : father, $C : child). " +
                     
                  "parent(petter, $C) " +
                  "order by $C desc?");    
  }
  
  /// type checking on variables

  public void testSimpleConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic($A), association($A)?");
  }

  public void testManyTypesConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic($A), type($A, $B)?");
  }

  public void testOrTypesConflict() throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic($A), { $A = jill | association($A) }?");
    getParseError("{ $A = jill | association($A) }, topic($A)?");
  }

  public void testOrTypesConflict2() throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic($A), { topicmap($A) | association($A) }?");
    getParseError("{ topicmap($A) | association($A) }, topic($A)?");
  }

  public void testConditionalTypesConflict() 
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    getParseError("topic($A), { association($A) }?");
    getParseError("{ association($A) }, topic($A)?");
  }

  public void testNotTypesConflict() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("topic($A), not(association($A))?");    
    getParseError("not(association($A)), topic($A)?");
  }

  public void testRuleTypeConflict() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("has-name($T, $V) :- topic-name($T, $N), value($V, $N). " +
                  "has-name(jill, $N)?");
  }

  /// type checking on parameters

  public void testSimpleParameterConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic(%A%), association(%A%)?");
  }

  public void testParameterValueConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");

    Map params = new HashMap();
    params.put("A", topicmap);
    getParseError("topic(%A%)?", params);
  }
  
  public void testManyParameterTypesConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic(%A%), type(%A%, %B%)?");
  }

  public void testOrParameterTypesConflict()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic(%A%), { %A% = jill | association(%A%) }?");
    getParseError("{ %A% = jill | association(%A%) }, topic(%A%)?");
  }

  public void testOrParameterTypesConflict2()
    throws InvalidQueryException, IOException {

    load("jill.xtm");
    
    getParseError("topic(%A%), { topicmap(%A%) | association(%A%) }?");
    getParseError("{ topicmap(%A%) | association(%A%) }, topic(%A%)?");
  }
  
  public void testConditionalParameterTypesConflict() 
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    getParseError("topic(%A%), { association(%A%) }?");
    getParseError("{ association(%A) }, topic(%A%)?");
  }

  public void testNotParameterTypesConflict()
    throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("topic(%A%), not(association(%A%))?");    
    getParseError("not(association(%A%)), topic(%A%)?");
  }

  public void testBug2102()
    throws InvalidQueryException, IOException {
    load("opera.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "PDATE", "1905-03-16");
    
    verifyQuery(matches, "using op for i\"http://psi.ontopia.net/opera/\" " +
                         "select $PDATE from " +
                         "$DATE = \"1905-01-01\", " +
                         "op:premiere-date($OPERA, $PDATE), " +
                         "$DATE < $PDATE " +
                         "order by $PDATE asc limit 1?");
  }
  
}

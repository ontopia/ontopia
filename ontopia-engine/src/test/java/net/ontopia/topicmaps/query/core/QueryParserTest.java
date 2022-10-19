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
import net.ontopia.infoset.impl.basic.URILocator;
import org.junit.Assert;
import org.junit.Test;

public class QueryParserTest extends AbstractQueryTest {
  
  /// simple syntax errors

  @Test
  public void testBadFragmentRef() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, drit)?");
  }
  
  @Test
  public void testBadObjectIdRef() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, @1)?");
  }

  @Test
  public void testBadSourceLocRef() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, s\"http://www.ontopia.net\")?");
  }

  @Test
  public void testBadIndicatorRef() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, i\"http://www.ontopia.net\")?");
  }

  @Test
  public void testBadAddressRef() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, a\"http://www.ontopia.net\")?");
  }

  @Test
  public void testBadObjectIdRefPair1() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, $B : @1)?");
  }

  @Test
  public void testBadSourceLocRefPair1() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, $B : s\"http://www.ontopia.net\")?");
  }

  @Test
  public void testBadIndicatorRefPair1() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, $B : i\"http://www.ontopia.net\")?");
  }

  @Test
  public void testBadAddressRefPair1() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, $B : a\"http://www.ontopia.net\")?");
  }

  @Test
  public void testBadObjectIdRefPair2() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, @1 : $B)?");
  }

  @Test
  public void testBadSourceLocRefPair2() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, s\"http://www.ontopia.net\" : $B)?");
  }

  @Test
  public void testBadIndicatorRefPair2() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, i\"http://www.ontopia.net\" : $B)?");
  }

  @Test
  public void testBadAddressRefPair2() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, a\"http://www.ontopia.net\" : $B)?");
  }

  @Test
  public void testBadFragmentRefPredicate() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("drit($A, $B)?");
  }

  @Test
  public void testBadObjectIdRefPredicate() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("@1($A, $B)?");
  }

  @Test
  public void testBadSourceLocRefPredicate() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("s\"http://www.ontopia.net\"($A, $B)?");
  }

  @Test
  public void testBadIndicatorRefPredicate() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("i\"http://www.ontopia.net\"($A, $B)?");
  }

  @Test
  public void testBadAddressRefPredicate() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("a\"http://www.ontopia.net\"($A, $B)?");
  }

  @Test
  public void testRelativeSourceLocator() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    assertFindNothing("instance-of($A, s\"#country\")?");
  }

  @Test
  public void testRelativeIndicator() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    assertFindNothing("instance-of($A, i\"#country1\")?");
  }

  @Test
  public void testRelativeSubject() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    assertFindNothing("instance-of($A, a\"#country1\")?");
  }

  @Test
  public void testNotEqualsPredicate() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    assertGetParseError("/=(@1, @2)?");
  }

  @Test
  public void testColonInVarName() {
    makeEmpty();
    assertGetParseError("instance-of($A, $B:B)?");
  }

  @Test
  public void testColonInIdentifier() {
    makeEmpty();
    assertGetParseError("instance-of($A, B:B)?");
  }
  
  /// semantic errors

  @Test
  public void testInstanceOfABC() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, $B, $C)?");
  }

  @Test
  public void testDirectInstanceOfABC() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("direct-instance-of($A, $B, $C)?");
  }

  @Test
  public void testSelectNonExistentVariable() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("select $D from instance-of($A, $B)?");
  }

  @Test
  public void testCountNonExistentVariable() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("select count($D) from instance-of($A, $B)?");
  }

  @Test
  public void testOrderNonExistentVariable() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A, $B) order by $D?");
  }

  @Test
  public void testInstanceOfPair() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("instance-of($A : $B, $C : $D)?");
  }

  @Test
  public void testDirectInstanceOfPair() throws InvalidQueryException {
    makeEmpty();
    assertGetParseError("direct-instance-of($A : $B, $C : $D)?");
  }
  
  @Test
  public void testNotEqualsUnbound() {
    makeEmpty();
    assertGetParseError("$A /= $B?");
  }

  @Test
  public void testUnknownAssoc() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertGetParseError("child-of($A : mother, $B : child)?");
  }

  @Test
  public void testUnknownAssocRole() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertGetParseError("parenthood($A : mother, $B : child, $C : ftaher)?");
  }

  @Test
  public void testAssocNoRole() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertGetParseError("parenthood($A : mother, $B : child, $C)?");
  }

  @Test
  public void testAssocRoleVariable() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertGetParseError("parenthood($A : mother, $B : child, $C : $FATHER)?");
  }

  @Test
  public void testOrderByUnknown() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertGetParseError("parenthood($A : mother, $B : child, $C : father) " +
                  "order by $D?");
  }

  @Test
  public void testOrderByUnselected() throws InvalidQueryException,IOException{
    load("family.ltm");
    assertGetParseError("select $A from " +
                  "parenthood($A : mother, $B : child, $C : father) " +
                  "order by $B?");
  }

  /// special pair problems

  @Test
  public void testPairWithString() throws InvalidQueryException,IOException{
    load("family.ltm");
    assertGetParseError("select $A from " +
                  "parenthood($A : mother, $B : child, \"hey\" : father) " +
                  "order by $B?");
  }

  @Test
  public void testPairWithString2() throws InvalidQueryException,IOException{
    load("family.ltm");
    assertGetParseError("select $A from " +
                  "parenthood($A : mother, $B : child, $C : \"hey\") " +
                  "order by $B?");
  }

  /// special not equals problems
  
  @Test
  public void testNotEqualsPair() throws InvalidQueryException,IOException{
    load("family.ltm");
    assertGetParseError("kfg /= kfg : father?");
  }
  
  @Test
  public void testNotEqualsPair2() throws InvalidQueryException,IOException{
    load("family.ltm");
    assertGetParseError("kfg : father /= kfg?");
  }
  
  /// uppercase/lowercase problems
  
  @Test
  public void testOrderDescLC() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));

    assertQueryOrder(matches,
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C desc?");
  }

  @Test
  public void testOrderAscLC() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("trygve"));

    assertQueryOrder(matches,
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C asc?");
  }

  @Test
  public void testKeywordCase() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("trygve"));

    assertQueryOrder(matches,
      "seLect $C fROm parenthood(may : mother, petter : father, $C : child) " +
      "oRDer bY $C aSC?");
  }

  @Test
  public void testVariableCase() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("may"), "c", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("may"), "c", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("may"), "c", getTopicById("trygve"));

    assertQueryMatches(matches,
                "parenthood($C : mother, petter : father, $c : child)?");
  }

  @Test
  public void testIdentifierCase() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertFindNothing("parenthood(may : mother, petter : father, TRYGVE : child)?");
  }

  /// keyword conflicts

  @Test
  public void testCountCountry() throws InvalidQueryException, IOException{
    load("parser-misc.ltm");
    assertFindNothing("instance-of($A, country)?");
  }

  @Test
  public void testKeywordInString() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("select"));

    assertQueryMatches(matches,
                "select $TOPIC from " +
                "  value($TNAME, \"select\"), " +
                "  topic-name($TOPIC, $TNAME)?");
  }

  /// subtler errors

  @Test
  public void testDuplicateSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertGetParseError("select $F, $F from instance-of($F, father)?");
  }

  public void _testGarbageAfterEnd() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertGetParseError("instance-of($F, father)? order by $F");
  }

  @Test
  public void testUnusedRuleParameter() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertGetParseError("parent-of($P, $C) :- { " +
                  "  parenthood($M : mother, $C : child) | " +
                  "  parenthood($F : father, $C : child) " +
                  "}." +

                  "parent-of($A)?");
  }
  
  /// earlier parser bugs

  @Test
  public void testInfiniteLoop() throws InvalidQueryException, IOException{
    load("instance-of.ltm");
    assertGetParseError("instance-of($FAM, type1\")?");
  }

  /// LIMIT/OFFSET tests
  
  @Test
  public void testNegativeOffset() throws InvalidQueryException,IOException{
    load("family2.ltm");
    
    assertGetParseError("instance-of($A, human) order by $A offset -10?");
  }

  @Test
  public void testNegativeLimit() throws InvalidQueryException,IOException{
    load("family2.ltm");
    
    assertGetParseError("instance-of($A, human) order by $A limit -10?");
  }

  @Test
  public void testFloatingOffset() throws InvalidQueryException, IOException {
    load("family2.ltm");
    assertGetParseError("instance-of($A, human) order by $A offset 3.2?");
  }

  @Test
  public void testFloatingLimit() throws InvalidQueryException, IOException {
    load("family2.ltm");
    assertGetParseError("instance-of($A, human) order by $A limit 3.2?");
  }

  /// comment tests
  
  @Test
  public void testBasicComment() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));

    assertQueryOrder(matches,
                     "/* this is the same as testOrderDescLC, but with a comment */ "+
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C desc?");
  }
  
  @Test
  public void testNestedComment() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));

    assertQueryOrder(matches,
                     "/* this is /* a nested comment */, as you can tell */ "+
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C desc?");
  }
  
  @Test
  public void testBadComment1() throws InvalidQueryException, IOException{
    load("family.ltm");

    assertGetParseError("/* this is /* a nested comment, as you can tell */ "+
                  "parenthood(may : mother, petter : father, $C : child) " +
                  "order by $C desc?");
  }
  
  @Test
  public void testBadComment2() throws InvalidQueryException, IOException{
    load("family.ltm");

    assertGetParseError("/* incomplete comment "+
                  "parenthood(may : mother, petter : father, $C : child) " +
                  "order by $C desc?");
  }

  @Test
  public void testCommentWithNewline() throws InvalidQueryException {
    makeEmpty();
    List matches = new ArrayList();
    addMatch(matches, "A", "foo");
    assertQueryMatches(matches, "/* hey \n ho */ " +
                "$A = \"foo\"?"); 
  }
  
  /// prefix binding tests

  @Test
  public void testSubjectIndicatorBinding() throws InvalidQueryException, IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("equation"));
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("rider"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    assertQueryMatches(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " +
                "instance-of($TOPIC, bb:bbtopic)?");
  }

  @Test
  public void testSubjectIndicatorBinding2() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("thequeen"), "B", "The queen of england");
    addMatch(matches, "T", getTopicById("equation"), "B", "Mathematical Equation");
    addMatch(matches, "T", getTopicById("horse"), "B", "Nayyy");
    addMatch(matches, "T", getTopicById("rider"), "B", "Person who rides a horse");
    addMatch(matches, "T", getTopicById("white-horse"), "B", "Epic ballad by G.K. Chesterton.");
    
    assertQueryMatches(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " +
                "using ont for i\"http://psi.ontopia.net/xtm/occurrence-type/\" " +
                "instance-of($T, bb:bbtopic), ont:description($T, $B)?");
  }

  @Test
  public void testSubjectIndicatorBinding3() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7amaths"));
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7ahistory"));
    
    assertQueryMatches(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " +
                "bb:pupilinclass($P : bb:student, $C : bb:class)?");
  }

  @Test
  public void testSIBError() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    assertGetParseError("using bb for q\"http://psi.ontopia.net/brainbank/#\" " +
                  "bb:pupilinclass($P : bb:student, $C : bb:clasS)?");
  }

  @Test
  public void testSIBError2() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    assertGetParseError("using b for i\"http://psi.ontopia.net/brainbank/#\" " +
                  "bb:pupilinclass($P : bb:student, $C : bb:clasS)?");
  }

  @Test
  public void testSrclocBinding() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7amaths"));
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7ahistory"));
    
    assertQueryMatches(matches,
                "using bb for s\"#\" " +
                "bb:elev-klasse($P : bb:elev, $C : bb:klasse)?");
  }

  @Test
  public void testSubjlocBinding() throws InvalidQueryException,IOException{
    load("instance-of.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic3"));
    addMatch(matches, "T", getTopicById("topic4"));
    
    assertQueryMatches(matches,
                "using test for a\"http://psi.ontopia.net/test/#\" " +
                "instance-of($T, test:2)?");
  }

  @Test
  public void testBizarreError() throws InvalidQueryException, IOException {
    load("rdf-test-case.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TYPE", getTopicById("person"), "PROP", getTopicById("name"));

    assertQueryMatches(matches,
                "using tm for i\"http://psi.ontopia.net/tm2rdf/#\" " +
                "tm:name-property($TYPE : tm:type, $PROP : tm:property)?");
  }

  /// scope tests
 
  @Test
  public void testRuleInQuery() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    assertQueryMatches(matches,
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child). " +
                "grandchild(edvin, kjellaug, $GCC)?");
  }
 
  @Test
  public void testRuleLocalToQuery() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    assertQueryMatches(matches,
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child). " +
                "grandchild(edvin, kjellaug, $GCC)?");

    // definition from previous query should no longer be visible
    assertGetParseError("grandchild(edvin, kjellaug, $GCC)?");
  }
  
  @Test
  public void testRuleOverBuiltin() throws InvalidQueryException, IOException {
    load("family.ltm");

    assertGetParseError("instance-of($A, $B) :- topic-name($A, $B). \n" +
                  "instance-of($A, $B)?");
  }

  @Test
  public void testRuleOverBuiltin2() throws InvalidQueryException, IOException {
    load("shadow.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TM", topicmap);
    
    assertQueryMatches(matches, "topicmap($TM)?");
  }

  @Test
  public void testDuplicatePrefix1() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertGetParseError("using fam for i\"http://psi.ontopia.net/brainbank/#\" " +
                  "import \"grandchild.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testDuplicatePrefix2() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertGetParseError("import \"empty.tl\" as fam " +
                  "import \"grandchild.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testDuplicateRule() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertGetParseError("import \"duplicate.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testImportNonexistent() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertGetParseError("import \"nonexistent.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testImportLoop() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertGetParseError("import \"loop.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  /// topics not used as assoc/occ types

  @Test
  public void testNonPredicateTopicAsAssocPredicate()
    throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertFindNothing("lmg(edvin : father, $VALUE : mother)?");
  }


  @Test
  public void testNonPredicateTopicAsOccPredicate()
    throws InvalidQueryException, IOException {
    load("family.ltm");
    
    assertFindNothing("lmg(edvin, $VALUE)?");
  }

  /// type testing

  @Test
  public void testPairToNonAssocPredicate() throws IOException {
    load("family.ltm");

    assertGetParseError("instance-of($A, $B : mother)?");
  }

  @Test
  public void testTypeErrorInRule() throws IOException {
    load("family.ltm");
    
    assertGetParseError("etterkommer($A, $D) :- " +
"  { parenthood($A : father, $M : mother, $C : child), etterkommer($C, $D) | " +
"    parenthood($F : father, $A : mother, $C : child), etterkommer($C, $D) | " +
"    parenthood($A : father, $M : mother, $D : child) | " +
"    parenthood($F, $A : mother, $D : child) }." +
                  "etterkommer($AA, trygve)?");
  }

  /// predicate caching tests

//   public void testDoWeRunOutOfMemory() throws IOException, InvalidQueryException {
//     // this verifies that the predicate status caching does not cause us to run
//     // out of memory
//     for (int ix = 0; ix < 25; ix++) {
//       load("opera.ltm"); // the biggest topic map
//       getParseError("description($TOPIC, $DESC), topicmap($TOPIC)?");
//     }
//   }
  
  /// string literal tests
  
  @Test
  public void testStringQuoting() throws InvalidQueryException, IOException {
    load("string-with-quotes.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic2"));
    
    assertQueryMatches(matches, "occ($TOPIC, \"An \"\"unquoted\"\" string\")?");
  }

  @Test
  public void testNeverEndingString() throws InvalidQueryException, IOException {
    load("string-with-quotes.ltm");
    
    assertGetParseError("occ($TOPIC, \"An \"\"unquoted\"\" string)?");
  }
  
  /// locator tests

  public void _testRelativeLocator() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("family"));
    
    assertQueryMatches(matches, "item-identifier($TOPIC, \"#family\")?");
  }

  /// pre-parsed context tests

  @Test
  public void testPredecldUsing() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("equation"));
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("rider"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    assertQueryPre(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" ", // COMMA
                "instance-of($TOPIC, bb:bbtopic)?");
  }

  @Test
  public void testPredecldUsing2() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("thequeen"), "B", "The queen of england");
    addMatch(matches, "T", getTopicById("equation"), "B", "Mathematical Equation");
    addMatch(matches, "T", getTopicById("horse"), "B", "Nayyy");
    addMatch(matches, "T", getTopicById("rider"), "B", "Person who rides a horse");
    addMatch(matches, "T", getTopicById("white-horse"), "B", "Epic ballad by G.K. Chesterton.");
    
    assertQueryPre(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" ", // COMMA
                "using ont for i\"http://psi.ontopia.net/xtm/occurrence-type/\" " +
                "instance-of($T, bb:bbtopic), ont:description($T, $B)?");
  }
  
  @Test
  public void testPredeclOverride() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("equation"));
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("rider"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    assertQueryPre(matches,
                "using bb for i\"http://psi.ontopia.net/brainbunk/#\" ",  // COMMA
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " + // PLUS
                "instance-of($TOPIC, bb:bbtopic)?");
  }

  @Test
  public void testPredecldImport() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));

    assertQueryPre(matches,
                   "import \"grandchild.tl\" as fam ", // COMMA
                "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testPredecldRule() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    assertQueryPre(matches,
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child).",                
                "grandchild(edvin, kjellaug, $GCC)?");
  }

  /// tests for known bugs

  @Test
  public void testBug1143() throws InvalidQueryException, IOException {
    load("family.ltm");
    assertFindNothing("grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child). " +
                
                "grandchild($F, $F, $F), " +
                "instance-of($F, $F)?"); // avoiding results to make test simpler
  }

  /// tests for dots in names
  
  @Test
  public void testNoDotInVariables() throws IOException {
    load("family.ltm");
    assertGetParseError("topic($P.P)?");
  }
  
  @Test
  public void testNoDotInId() throws IOException {
    load("family.ltm");
    assertGetParseError("using foo.o for i\"http://foo.foo/foo\" topic($T)?");
  }
  
  @Test
  public void testNoDotInId2() throws IOException {
    load("family.ltm");
    assertGetParseError("topic($T), $T=t.t?");
  }
  
  @Test
  public void testNoDotInParameter() throws IOException, InvalidQueryException {
    load("family.ltm");
    Map argumentMap = new HashMap();
    QueryResultIF queryResult = processor.execute("topic($TOPIC)?", argumentMap);
    queryResult.next();
    argumentMap.put("T.T", queryResult.getValue(0));
    try {
      processor.execute("topic(%T.T%)?", argumentMap);
      Assert.fail("The query \"topic(%T.T%)?\", which has a '.' in a parameter"
          + " parsed without Assert.failure, but should have caused"
          + "InvalidQueryException");
    } catch (InvalidQueryException e) {
    }
  }

  @Test
  public void testURISyntaxError() throws IOException, InvalidQueryException {
    load("family.ltm");
    assertGetParseError("occurrence($T, $O), type($O, i\"http://psi.ontopia.net/#foo#\")?");
  }
  
  @Test
  public void testIssue424() throws IOException, InvalidQueryException {
    final String PREFIX = "http://psi.rijksoverheid.nl/";
    final String IDENTIFIER = "reden-be\u00ebindiging-ambtsbekleding";

    load("issue424.ltm");
    
    Assert.assertNotNull(topicmap.getTopicBySubjectIdentifier(URILocator.create(PREFIX + IDENTIFIER)));
    assertFindAny("using k for i\"" + PREFIX + "\" $t = k:" + IDENTIFIER + "?");
  }
}

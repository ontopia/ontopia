
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParserTest extends AbstractQueryTest {
  
  public QueryParserTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }

  /// simple syntax errors

  public void testBadFragmentRef() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, drit)?");
  }
  
  public void testBadObjectIdRef() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, @1)?");
  }

  public void testBadSourceLocRef() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, s\"http://www.ontopia.net\")?");
  }

  public void testBadIndicatorRef() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, i\"http://www.ontopia.net\")?");
  }

  public void testBadAddressRef() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, a\"http://www.ontopia.net\")?");
  }

  public void testBadObjectIdRefPair1() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, $B : @1)?");
  }

  public void testBadSourceLocRefPair1() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, $B : s\"http://www.ontopia.net\")?");
  }

  public void testBadIndicatorRefPair1() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, $B : i\"http://www.ontopia.net\")?");
  }

  public void testBadAddressRefPair1() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, $B : a\"http://www.ontopia.net\")?");
  }

  public void testBadObjectIdRefPair2() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, @1 : $B)?");
  }

  public void testBadSourceLocRefPair2() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, s\"http://www.ontopia.net\" : $B)?");
  }

  public void testBadIndicatorRefPair2() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, i\"http://www.ontopia.net\" : $B)?");
  }

  public void testBadAddressRefPair2() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, a\"http://www.ontopia.net\" : $B)?");
  }

  public void testBadFragmentRefPredicate() throws InvalidQueryException {
    makeEmpty();
    getParseError("drit($A, $B)?");
  }

  public void testBadObjectIdRefPredicate() throws InvalidQueryException {
    makeEmpty();
    getParseError("@1($A, $B)?");
  }

  public void testBadSourceLocRefPredicate() throws InvalidQueryException {
    makeEmpty();
    getParseError("s\"http://www.ontopia.net\"($A, $B)?");
  }

  public void testBadIndicatorRefPredicate() throws InvalidQueryException {
    makeEmpty();
    getParseError("i\"http://www.ontopia.net\"($A, $B)?");
  }

  public void testBadAddressRefPredicate() throws InvalidQueryException {
    makeEmpty();
    getParseError("a\"http://www.ontopia.net\"($A, $B)?");
  }

  public void testRelativeSourceLocator() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    findNothing("instance-of($A, s\"#country\")?");
  }

  public void testRelativeIndicator() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    findNothing("instance-of($A, i\"#country1\")?");
  }

  public void testRelativeSubject() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    findNothing("instance-of($A, a\"#country1\")?");
  }

  public void testNotEqualsPredicate() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    getParseError("/=(@1, @2)?");
  }

  public void testColonInVarName() {
    makeEmpty();
    getParseError("instance-of($A, $B:B)?");
  }

  public void testColonInIdentifier() {
    makeEmpty();
    getParseError("instance-of($A, B:B)?");
  }
  
  /// semantic errors

  public void testInstanceOfABC() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, $B, $C)?");
  }

  public void testDirectInstanceOfABC() throws InvalidQueryException {
    makeEmpty();
    getParseError("direct-instance-of($A, $B, $C)?");
  }

  public void testSelectNonExistentVariable() throws InvalidQueryException {
    makeEmpty();
    getParseError("select $D from instance-of($A, $B)?");
  }

  public void testCountNonExistentVariable() throws InvalidQueryException {
    makeEmpty();
    getParseError("select count($D) from instance-of($A, $B)?");
  }

  public void testOrderNonExistentVariable() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A, $B) order by $D?");
  }

  public void testInstanceOfPair() throws InvalidQueryException {
    makeEmpty();
    getParseError("instance-of($A : $B, $C : $D)?");
  }

  public void testDirectInstanceOfPair() throws InvalidQueryException {
    makeEmpty();
    getParseError("direct-instance-of($A : $B, $C : $D)?");
  }
  
  public void testNotEqualsUnbound() {
    makeEmpty();
    getParseError("$A /= $B?");
  }

  public void testUnknownAssoc() throws InvalidQueryException, IOException {
    load("family.ltm");
    getParseError("child-of($A : mother, $B : child)?");
  }

  public void testUnknownAssocRole() throws InvalidQueryException, IOException{
    load("family.ltm");
    getParseError("parenthood($A : mother, $B : child, $C : ftaher)?");
  }

  public void testAssocNoRole() throws InvalidQueryException, IOException{
    load("family.ltm");
    getParseError("parenthood($A : mother, $B : child, $C)?");
  }

  public void testAssocRoleVariable() throws InvalidQueryException, IOException{
    load("family.ltm");
    getParseError("parenthood($A : mother, $B : child, $C : $FATHER)?");
  }

  public void testOrderByUnknown() throws InvalidQueryException, IOException{
    load("family.ltm");
    getParseError("parenthood($A : mother, $B : child, $C : father) " +
                  "order by $D?");
  }

  public void testOrderByUnselected() throws InvalidQueryException,IOException{
    load("family.ltm");
    getParseError("select $A from " +
                  "parenthood($A : mother, $B : child, $C : father) " +
                  "order by $B?");
  }

  /// special pair problems

  public void testPairWithString() throws InvalidQueryException,IOException{
    load("family.ltm");
    getParseError("select $A from " +
                  "parenthood($A : mother, $B : child, \"hey\" : father) " +
                  "order by $B?");
  }

  public void testPairWithString2() throws InvalidQueryException,IOException{
    load("family.ltm");
    getParseError("select $A from " +
                  "parenthood($A : mother, $B : child, $C : \"hey\") " +
                  "order by $B?");
  }

  /// special not equals problems
  
  public void testNotEqualsPair() throws InvalidQueryException,IOException{
    load("family.ltm");
    getParseError("kfg /= kfg : father?");
  }
  
  public void testNotEqualsPair2() throws InvalidQueryException,IOException{
    load("family.ltm");
    getParseError("kfg : father /= kfg?");
  }
  
  /// uppercase/lowercase problems
  
  public void testOrderDescLC() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));

    verifyQueryOrder(matches,
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C desc?");
  }

  public void testOrderAscLC() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("trygve"));

    verifyQueryOrder(matches,
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C asc?");
  }

  public void testKeywordCase() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("trygve"));

    verifyQueryOrder(matches,
      "seLect $C fROm parenthood(may : mother, petter : father, $C : child) " +
      "oRDer bY $C aSC?");
  }

  public void testVariableCase() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("may"), "c", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("may"), "c", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("may"), "c", getTopicById("trygve"));

    verifyQuery(matches,
                "parenthood($C : mother, petter : father, $c : child)?");
  }

  public void testIdentifierCase() throws InvalidQueryException, IOException{
    load("family.ltm");
    findNothing("parenthood(may : mother, petter : father, TRYGVE : child)?");
  }

  /// keyword conflicts

  public void testCountCountry() throws InvalidQueryException, IOException{
    load("parser-misc.ltm");
    findNothing("instance-of($A, country)?");
  }

  public void testKeywordInString() throws InvalidQueryException, IOException {
    load("parser-misc.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("select"));

    verifyQuery(matches,
                "select $TOPIC from " +
                "  value($TNAME, \"select\"), " +
                "  topic-name($TOPIC, $TNAME)?");
  }

  /// subtler errors

  public void testDuplicateSelect() throws InvalidQueryException, IOException{
    load("family.ltm");
    getParseError("select $F, $F from instance-of($F, father)?");
  }

  public void _testGarbageAfterEnd() throws InvalidQueryException, IOException {
    load("family.ltm");
    getParseError("instance-of($F, father)? order by $F");
  }

  public void testUnusedRuleParameter() throws InvalidQueryException, IOException {
    load("family.ltm");
    getParseError("parent-of($P, $C) :- { " +
                  "  parenthood($M : mother, $C : child) | " +
                  "  parenthood($F : father, $C : child) " +
                  "}." +

                  "parent-of($A)?");
  }
  
  /// earlier parser bugs

  public void testInfiniteLoop() throws InvalidQueryException, IOException{
    load("instance-of.ltm");
    getParseError("instance-of($FAM, type1\")?");
  }

  /// LIMIT/OFFSET tests
  
  public void testNegativeOffset() throws InvalidQueryException,IOException{
    load("family2.ltm");
    
    getParseError("instance-of($A, human) order by $A offset -10?");
  }

  public void testNegativeLimit() throws InvalidQueryException,IOException{
    load("family2.ltm");
    
    getParseError("instance-of($A, human) order by $A limit -10?");
  }

  /// comment tests
  
  public void testBasicComment() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));

    verifyQueryOrder(matches,
                     "/* this is the same as testOrderDescLC, but with a comment */ "+
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C desc?");
  }
  
  public void testNestedComment() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));

    verifyQueryOrder(matches,
                     "/* this is /* a nested comment */, as you can tell */ "+
                     "parenthood(may : mother, petter : father, $C : child) " +
                     "order by $C desc?");
  }
  
  public void testBadComment1() throws InvalidQueryException, IOException{
    load("family.ltm");

    getParseError("/* this is /* a nested comment, as you can tell */ "+
                  "parenthood(may : mother, petter : father, $C : child) " +
                  "order by $C desc?");
  }
  
  public void testBadComment2() throws InvalidQueryException, IOException{
    load("family.ltm");

    getParseError("/* incomplete comment "+
                  "parenthood(may : mother, petter : father, $C : child) " +
                  "order by $C desc?");
  }

  public void testCommentWithNewline() throws InvalidQueryException {
    makeEmpty();
    List matches = new ArrayList();
    addMatch(matches, "A", "foo");
    verifyQuery(matches, "/* hey \n ho */ " +
                "$A = \"foo\"?"); 
  }
  
  /// prefix binding tests

  public void testSubjectIndicatorBinding() throws InvalidQueryException, IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("equation"));
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("rider"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    verifyQuery(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " +
                "instance-of($TOPIC, bb:bbtopic)?");
  }

  public void testSubjectIndicatorBinding2() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("thequeen"), "B", "The queen of england");
    addMatch(matches, "T", getTopicById("equation"), "B", "Mathematical Equation");
    addMatch(matches, "T", getTopicById("horse"), "B", "Nayyy");
    addMatch(matches, "T", getTopicById("rider"), "B", "Person who rides a horse");
    addMatch(matches, "T", getTopicById("white-horse"), "B", "Epic ballad by G.K. Chesterton.");
    
    verifyQuery(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " +
                "using ont for i\"http://psi.ontopia.net/xtm/occurrence-type/\" " +
                "instance-of($T, bb:bbtopic), ont:description($T, $B)?");
  }

  public void testSubjectIndicatorBinding3() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7amaths"));
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7ahistory"));
    
    verifyQuery(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " +
                "bb:pupilinclass($P : bb:student, $C : bb:class)?");
  }

  public void testSIBError() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    getParseError("using bb for q\"http://psi.ontopia.net/brainbank/#\" " +
                  "bb:pupilinclass($P : bb:student, $C : bb:clasS)?");
  }

  public void testSIBError2() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    getParseError("using b for i\"http://psi.ontopia.net/brainbank/#\" " +
                  "bb:pupilinclass($P : bb:student, $C : bb:clasS)?");
  }

  public void testSrclocBinding() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7amaths"));
    addMatch(matches, "P", getTopicById("gdm"), "C", getTopicById("k7ahistory"));
    
    verifyQuery(matches,
                "using bb for s\"#\" " +
                "bb:elev-klasse($P : bb:elev, $C : bb:klasse)?");
  }

  public void testSubjlocBinding() throws InvalidQueryException,IOException{
    load("instance-of.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("topic3"));
    addMatch(matches, "T", getTopicById("topic4"));
    
    verifyQuery(matches,
                "using test for a\"http://psi.ontopia.net/test/#\" " +
                "instance-of($T, test:2)?");
  }

  public void testBizarreError() throws InvalidQueryException, IOException {
    load("rdf-test-case.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TYPE", getTopicById("person"), "PROP", getTopicById("name"));

    verifyQuery(matches,
                "using tm for i\"http://psi.ontopia.net/tm2rdf/#\" " +
                "tm:name-property($TYPE : tm:type, $PROP : tm:property)?");
  }

  /// scope tests
 
  public void testRuleInQuery() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQuery(matches,
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child). " +
                "grandchild(edvin, kjellaug, $GCC)?");
  }
 
  public void testRuleLocalToQuery() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQuery(matches,
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child). " +
                "grandchild(edvin, kjellaug, $GCC)?");

    // definition from previous query should no longer be visible
    getParseError("grandchild(edvin, kjellaug, $GCC)?");
  }
  
  public void testRuleOverBuiltin() throws InvalidQueryException, IOException {
    load("family.ltm");

    getParseError("instance-of($A, $B) :- topic-name($A, $B). \n" +
                  "instance-of($A, $B)?");
  }

  public void testRuleOverBuiltin2() throws InvalidQueryException, IOException {
    load("shadow.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TM", topicmap);
    
    verifyQuery(matches, "topicmap($TM)?");
  }

  public void testDuplicatePrefix1() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    getParseError("using fam for i\"http://psi.ontopia.net/brainbank/#\" " +
                  "import \"grandchild.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  public void testDuplicatePrefix2() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    getParseError("import \"empty.tl\" as fam " +
                  "import \"grandchild.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  public void testDuplicateRule() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    getParseError("import \"duplicate.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  public void testImportNonexistent() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    getParseError("import \"nonexistent.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  public void testImportLoop() throws InvalidQueryException, IOException {
    load("family.ltm");
    
    getParseError("import \"loop.tl\" as fam " +
                  "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  /// topics not used as assoc/occ types

  public void testNonPredicateTopicAsAssocPredicate()
    throws InvalidQueryException, IOException {
    load("family.ltm");
    
    findNothing("lmg(edvin : father, $VALUE : mother)?");
  }


  public void testNonPredicateTopicAsOccPredicate()
    throws InvalidQueryException, IOException {
    load("family.ltm");
    
    findNothing("lmg(edvin, $VALUE)?");
  }

  /// type testing

  public void testPairToNonAssocPredicate() throws IOException {
    load("family.ltm");

    getParseError("instance-of($A, $B : mother)?");
  }

  public void testTypeErrorInRule() throws IOException {
    load("family.ltm");
    
    getParseError("etterkommer($A, $D) :- " +
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
  
  public void testStringQuoting() throws InvalidQueryException, IOException {
    load("string-with-quotes.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic2"));
    
    verifyQuery(matches, "occ($TOPIC, \"An \"\"unquoted\"\" string\")?");
  }

  public void testNeverEndingString() throws InvalidQueryException, IOException {
    load("string-with-quotes.ltm");
    
    getParseError("occ($TOPIC, \"An \"\"unquoted\"\" string)?");
  }
  
  /// locator tests

  public void _testRelativeLocator() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("family"));
    
    verifyQuery(matches, "item-identifier($TOPIC, \"#family\")?");
  }

  /// pre-parsed context tests

  public void testPredecldUsing() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("equation"));
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("rider"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    verifyQueryPre(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" ", // COMMA
                "instance-of($TOPIC, bb:bbtopic)?");
  }

  public void testPredecldUsing2() throws InvalidQueryException,IOException{
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("thequeen"), "B", "The queen of england");
    addMatch(matches, "T", getTopicById("equation"), "B", "Mathematical Equation");
    addMatch(matches, "T", getTopicById("horse"), "B", "Nayyy");
    addMatch(matches, "T", getTopicById("rider"), "B", "Person who rides a horse");
    addMatch(matches, "T", getTopicById("white-horse"), "B", "Epic ballad by G.K. Chesterton.");
    
    verifyQueryPre(matches,
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" ", // COMMA
                "using ont for i\"http://psi.ontopia.net/xtm/occurrence-type/\" " +
                "instance-of($T, bb:bbtopic), ont:description($T, $B)?");
  }
  
  public void testPredeclOverride() throws InvalidQueryException, IOException {
    load("bb-test.ltm");
    
    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("thequeen"));
    addMatch(matches, "TOPIC", getTopicById("equation"));
    addMatch(matches, "TOPIC", getTopicById("horse"));
    addMatch(matches, "TOPIC", getTopicById("rider"));
    addMatch(matches, "TOPIC", getTopicById("white-horse"));
    
    verifyQueryPre(matches,
                "using bb for i\"http://psi.ontopia.net/brainbunk/#\" ",  // COMMA
                "using bb for i\"http://psi.ontopia.net/brainbank/#\" " + // PLUS
                "instance-of($TOPIC, bb:bbtopic)?");
  }

  public void testPredecldImport() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));

    verifyQueryPre(matches,
                   "import \"grandchild.tl\" as fam ", // COMMA
                "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  public void testPredecldRule() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQueryPre(matches,
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child).",                
                "grandchild(edvin, kjellaug, $GCC)?");
  }

  /// tests for known bugs

  public void testBug1143() throws InvalidQueryException, IOException {
    load("family.ltm");
    findNothing("grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child). " +
                
                "grandchild($F, $F, $F), " +
                "instance-of($F, $F)?"); // avoiding results to make test simpler
  }

  /// tests for dots in names
  
  public void testNoDotInVariables() throws IOException {
    load("family.ltm");
    getParseError("topic($P.P)?");
  }
  
  public void testNoDotInId() throws IOException {
    load("family.ltm");
    getParseError("using foo.o for i\"http://foo.foo/foo\" topic($T)?");
  }
  
  public void testNoDotInId2() throws IOException {
    load("family.ltm");
    getParseError("topic($T), $T=t.t?");
  }
  
  public void testNoDotInParameter() throws IOException, InvalidQueryException {
    load("family.ltm");
    Map argumentMap = new HashMap();
    QueryResultIF queryResult = processor.execute("topic($TOPIC)?", argumentMap);
    queryResult.next();
    argumentMap.put("T.T", queryResult.getValue(0));
    try {
      processor.execute("topic(%T.T%)?", argumentMap);
      fail("The query \"topic(%T.T%)?\", which has a '.' in a parameter"
          + " parsed without failure, but should have caused"
          + "InvalidQueryException");
    } catch (InvalidQueryException e) {
    }
  }

  public void testURISyntaxError() throws IOException, InvalidQueryException {
    load("family.ltm");
    getParseError("occurrence($T, $O), type($O, i\"http://psi.ontopia.net/#foo#\")?");
  }
  
}

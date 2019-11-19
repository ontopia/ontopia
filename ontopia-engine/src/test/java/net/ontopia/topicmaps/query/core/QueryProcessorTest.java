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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.query.impl.basic.QueryMatches;
import net.ontopia.utils.TestFileUtils;
import net.ontopia.utils.URIUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// TODO:
//  - move tests that are really tests of specific predicates out
//    of here
//  - add test for sorting by topic

public class QueryProcessorTest extends AbstractQueryTest {
  
  /// constants for various options

  private static final String HIERARCHY_WALKER_ON =
    "/* #OPTION: optimizer.hierarchy-walker = true */ ";

  private static final String HIERARCHY_WALKER_OFF =
    "/* #OPTION: optimizer.hierarchy-walker = false */ ";

  /// context management

  @Before
  public void setUp() {
    QueryMatches.initialSize = 1;
  }

  /// empty topic map
  
  @Test
  public void testEmptyDirectInstanceOfAB() throws InvalidQueryException {
    makeEmpty();
    findNothing("direct-instance-of($A, $B)?");
  }

  /// instance-of topic map
  
  @Test
  public void testDirectInstanceOfAB() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    
    verifyQuery(matches, "direct-instance-of($A, $B)?");
  }
  
  @Test
  public void testDirectInstanceOfaB() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    
    verifyQuery(matches, "direct-instance-of(topic1, $B)?");
  }

  @Test
  public void testDirectInstanceOfAb() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    verifyQuery(matches, "direct-instance-of($A, type1)?");
  }

  @Test
  public void testDirectInstanceOfab() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "direct-instance-of(topic1, type1)?");
  }

  @Test
  public void testDirectInstanceOfWrong() throws InvalidQueryException, IOException {
    load("instance-of.ltm");
    findNothing("direct-instance-of(topic1, type2)?");
  }

  @Test
  public void testSubjectIndicatorRef() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    verifyQuery(matches, "direct-instance-of($A, i\"http://psi.ontopia.net/test/#1\")?");
  }

  @Test
  public void testSubjectAddressRef() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic3"));
    addMatch(matches, "A", getTopicById("topic4"));
    
    verifyQuery(matches, "direct-instance-of($A, a\"http://psi.ontopia.net/test/#2\")?");
  }  

  @Test
  public void testSimpleCount() throws InvalidQueryException, IOException{
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CLASS", getTopicById("type1"),
                      "INST", new Integer(2));
    addMatch(matches, "CLASS", getTopicById("type2"),
                      "INST", new Integer(2));
    
    verifyQuery(matches,
                "select $CLASS, count($INST) from " +
                "instance-of($INST, $CLASS)?");
  }

  @Test
  public void testZeroCount() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "INST", new Integer(0));
    
    // there should be no instance of topic4
    verifyQuery(matches,
                "select count($INST) from " +
                "instance-of($INST, topic4)?");
  }

  @Test
  public void testZeroCount2() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches,
             "TOPIC", getTopicById("type1"),
             "TYPE", new Integer(0));
    
    // there should be no types for type1
    verifyQuery(matches,
                "select $TOPIC, count($TYPE) from " +
                "$TOPIC = type1, " +
                "{ instance-of($TOPIC, $TYPE) }?");
  }
  
  /// subclasses topic map
  
  @Test
  public void testDirectInstanceOfABSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    
    verifyQuery(matches, "direct-instance-of($A, $B)?");
  }
  
  @Test
  public void testDirectInstanceOfaBSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));
    
    verifyQuery(matches, "direct-instance-of(topic1, $B)?");
  }

  @Test
  public void testDirectInstanceOfAbSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    verifyQuery(matches, "direct-instance-of($A, type1)?");
  }

  @Test
  public void testDirectInstanceOfabSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "direct-instance-of(topic1, type1)?");
  }

  @Test
  public void testDirectInstanceOfWrongSub() throws InvalidQueryException, IOException {
    load("subclasses.ltm");
    findNothing("direct-instance-of(topic1, type2)?");
  }
  
  @Test
  public void testSimpleSelect() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "F", getTopicById("kfg"));
    addMatch(matches, "F", getTopicById("edvin"));
    addMatch(matches, "F", getTopicById("petter"));
    addMatch(matches, "F", getTopicById("asle"));
    addMatch(matches, "F", getTopicById("magnus"));
    addMatch(matches, "F", getTopicById("unknown2"));
    
    verifyQuery(matches,
                "select $F from " +
                "parenthood($M : mother, $F : father, $C : child)?");
  }

  @Test
  public void testDoubleSelect() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "F", getTopicById("kfg"),  "M", getTopicById("bjorg"));
    addMatch(matches, "F", getTopicById("edvin"), "M", getTopicById("kjellaug"));
    addMatch(matches, "F", getTopicById("petter"), "M", getTopicById("may"));
    addMatch(matches, "F", getTopicById("asle"), "M", getTopicById("gerd"));
    addMatch(matches, "F", getTopicById("magnus"), "M", getTopicById("bertha"));
    addMatch(matches, "F", getTopicById("unknown2"), "M", getTopicById("unknown1"));
    
    verifyQuery(matches,
                "select $F, $M from " +
                "parenthood($M : mother, $F : father, $C : child)?");
  }
  
  @Test
  public void testRuleGrandchild() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQuery(matches, "grandchild(edvin, kjellaug, $GCC)?",
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child).");
  }

  @Test
  public void testRuleGrandchild2() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GC", getTopicById("trygve"));
    addMatch(matches, "GC", getTopicById("tine"));
    addMatch(matches, "GC", getTopicById("julie"));
    addMatch(matches, "GC", getTopicById("astri"));
    addMatch(matches, "GC", getTopicById("lmg"));
    addMatch(matches, "GC", getTopicById("silje"));
    
    verifyQuery(matches, "grandchild(edvin, kjellaug, $GC)?",
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child).");
  }

  @Test
  public void testRuleGrandchild3() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, "grandchild(edvin, kjellaug, trygve)?",
                "grandchild($GF, $GM, $GC) :- " +
                "parenthood($GF : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child).");
  }

  @Test
  public void testUsingTwoRules() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GC", getTopicById("trygve"),
             "C", getTopicById("petter"));
    addMatch(matches, "GC", getTopicById("tine"),
             "C", getTopicById("petter"));
    addMatch(matches, "GC", getTopicById("julie"),
             "C", getTopicById("petter"));
    addMatch(matches, "GC", getTopicById("astri"),
             "C", getTopicById("kfg"));
    addMatch(matches, "GC", getTopicById("lmg"),
             "C", getTopicById("kfg"));
    addMatch(matches, "GC", getTopicById("silje"),
             "C", getTopicById("kfg"));
    
    verifyQuery(matches, "is-father(edvin, $C), is-father($C, $GC)?",
                "is-father($F, $C) :- " +
                "parenthood($F : father, $M : mother, $C : child). ");
  }

  @Test
  public void testUsingTwoRules2() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GC", getTopicById("trygve"),
             "CHILD", getTopicById("petter"));
    addMatch(matches, "GC", getTopicById("tine"),
             "CHILD", getTopicById("petter"));
    addMatch(matches, "GC", getTopicById("julie"),
             "CHILD", getTopicById("petter"));
    addMatch(matches, "GC", getTopicById("astri"),
             "CHILD", getTopicById("kfg"));
    addMatch(matches, "GC", getTopicById("lmg"),
             "CHILD", getTopicById("kfg"));
    addMatch(matches, "GC", getTopicById("silje"),
             "CHILD", getTopicById("kfg"));
    
    verifyQuery(matches, "is-father(edvin, $CHILD), is-father($CHILD, $GC)?",
                "is-father($F, $C) :- " +
                "parenthood($F : father, $M : mother, $C : child). ");
  }
  
  @Test
  public void testTwoLevelsOfRule() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("astri"));
    addMatch(matches, "C", getTopicById("lmg"));
    addMatch(matches, "C", getTopicById("silje"));
    
    verifyQuery(matches, "grandfather(edvin, $C)?",
                "is-father($F, $C) :- " +
                "parenthood($F : father, $M : mother, $C : child). " +
                "grandfather($GF, $GC) :- " +
                "is-father($GF, $C)," +
                "is-father($C, $GC). ");
  }

  @Test
  public void testTwoLevelsOfRule2() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GC", getTopicById("trygve"));
    addMatch(matches, "GC", getTopicById("tine"));
    addMatch(matches, "GC", getTopicById("julie"));
    addMatch(matches, "GC", getTopicById("astri"));
    addMatch(matches, "GC", getTopicById("lmg"));
    addMatch(matches, "GC", getTopicById("silje"));
    
    verifyQuery(matches, "grandfather(edvin, $GC)?",
                "is-father($F, $C) :- " +
                "parenthood($F : father, $M : mother, $C : child). " +
                "grandfather($GF, $GC) :- " +
                "is-father($GF, $C)," +
                "is-father($C, $GC). ");
  }

  // motivated by the bug LMG discovered when doing the wumpus agent
  // never reported
  @Test
  public void testRuleWithConstant() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQuery(matches, "edvins-grandchild($GCC)?",
                "edvins-grandchild($GC) :- " +
                "parenthood(edvin : father, $GM : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child).");
  }
  
  // motivated by the bug LMG discovered when doing the wumpus agent
  // never reported
  @Test
  public void testRuleWithConstantTwoLevels()
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQuery(matches, "edvins-grandchild($GCC)?",
                "father-of($F, $C) :- " +
                "parenthood($F : father, $M : mother, $C : child)." +
                "edvins-grandchild($GC) :- " +
                "father-of($CC, $GC), " +
                "parenthood(edvin : father, $M : mother, $CC : child).");
  }

  @Test
  public void testRuleWithConstantArguments()
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());

    // this should be true
    verifyQuery(matches,
                "father-of($F, $C) :- " +
                "  parenthood($F : father, $M : mother, $C : child). " +
                "father-of(kfg, lmg)?");

    // and this should be false
    findNothing("father-of($F, $C) :- " +
                "  parenthood($F : father, $M : mother, $C : child). " +
                "father-of(lmg, kfg)?");
  }

  @Test
  public void testOrQuery() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("petter"));
    addMatch(matches, "P", getTopicById("may"));
    
    verifyQuery(matches, "select $P from " +
                " { parenthood($P : father, $M : mother, trygve : child) | " +
                "   parenthood($F : father, $P : mother, trygve : child) }? ");
  }

  @Test
  public void testOrOneFails() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("petter"));
    
    verifyQuery(matches, "select $P from " +
                " { parenthood($P : father, $M : mother, trygve : child) | " +
                "   trygve /= trygve }? ");
  }
  
  @Test
  public void testOrRule() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("petter"));
    addMatch(matches, "P", getTopicById("may"));
    
    verifyQuery(matches, "parent($P, trygve)?",
                "parent($P, $C) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }. ");
  }

  @Test
  public void testRuleCount() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("petter"),   "C", new Integer(3));
    addMatch(matches, "P", getTopicById("may"),      "C", new Integer(3));
    addMatch(matches, "P", getTopicById("kfg"),      "C", new Integer(3));
    addMatch(matches, "P", getTopicById("bjorg"),    "C", new Integer(3));
    addMatch(matches, "P", getTopicById("kjellaug"), "C", new Integer(2));
    addMatch(matches, "P", getTopicById("edvin"),    "C", new Integer(2));
    addMatch(matches, "P", getTopicById("bertha"),   "C", new Integer(2));
    addMatch(matches, "P", getTopicById("magnus"),   "C", new Integer(2));
    addMatch(matches, "P", getTopicById("gerd"),     "C", new Integer(1));
    addMatch(matches, "P", getTopicById("asle"),     "C", new Integer(1));
    addMatch(matches, "P", getTopicById("unknown1"), "C", new Integer(1));
    addMatch(matches, "P", getTopicById("unknown2"), "C", new Integer(1));
    
    verifyQuery(matches, "select $P, count($C) from parent($P, $C)?",
                "parent($P, $C) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }. ");
  }

  @Test
  public void testRecursiveOr() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "AA", getTopicById("edvin"));
    addMatch(matches, "AA", getTopicById("kjellaug"));
    addMatch(matches, "AA", getTopicById("may"));
    addMatch(matches, "AA", getTopicById("petter"));
    
    verifyQuery(matches, "etterkommer($AA, trygve)?",
                "etterkommer($A, $D) :- " +
                "  { parenthood($A : father, $M : mother, $C : child), " +
                "    etterkommer($C, $D) | " +
                "    parenthood($F : father, $A : mother, $C : child), " +
                "    etterkommer($C, $D) | " +
                "    parenthood($A : father, $M : mother, $D : child) | " +
                "    parenthood($F : father, $A : mother, $D : child) }.");
  }

  @Test
  public void testRecursiveOr2() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "AA", getTopicById("edvin"));
    addMatch(matches, "AA", getTopicById("kjellaug"));
    addMatch(matches, "AA", getTopicById("may"));
    addMatch(matches, "AA", getTopicById("petter"));
    
    verifyQuery(matches, "etterkommer($AA, trygve)?",
                "parent($P, $C) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }. " +
                "etterkommer($A, $D) :- " +
                "  { parent($A, $C), etterkommer($C, $D) | " +
                "    parent($A, $D) }.");
  }

  // switches rule parameter order around, to test HierarchyWalkerRulePredicate
  @Test
  public void testRecursiveOr3() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "AA", getTopicById("edvin"));
    addMatch(matches, "AA", getTopicById("kjellaug"));
    addMatch(matches, "AA", getTopicById("may"));
    addMatch(matches, "AA", getTopicById("petter"));
    
    verifyQuery(matches, HIERARCHY_WALKER_ON + "etterkommer($AA, trygve)?",
                "parent($C, $P) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }. " +
                "etterkommer($A, $D) :- " +
                "  { parent($MID, $A), etterkommer($MID, $D) | " +
                "    parent($D, $A) }.");
  }

  // adds an extra, superfluous, argument, to stress HierarchyWalkerRulePredicate
  @Test
  public void testRecursiveOr4() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "AA", getTopicById("edvin"));
    addMatch(matches, "AA", getTopicById("kjellaug"));
    addMatch(matches, "AA", getTopicById("may"));
    addMatch(matches, "AA", getTopicById("petter"));
    
    verifyQuery(matches, HIERARCHY_WALKER_ON + "etterkommer($AA, trygve, tm)?",
                "parent($C, $P, $UNUSED) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }, " +
                "  $UNUSED /= edvin . " +
                /* edvin can't be the TM, and /= gives good error messages */
                "etterkommer($A, $D, $UNUSED) :- " +
                "  { parent($MID, $A, $UNUSED), etterkommer($MID, $D, $UNUSED) | " +
                "    parent($D, $A, $UNUSED) }.");
  }

  // checks what happens with a simple fact test
  @Test
  public void testRecursiveFacts() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    matches.add(new HashMap());
    
    verifyQuery(matches, HIERARCHY_WALKER_ON + "etterkommer(edvin, trygve)?",
                "parent($P, $C) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }. " +
                "etterkommer($A, $D) :- " +
                "  { parent($A, $C), etterkommer($C, $D) | " +
                "    parent($A, $D) }.");
  }

  // checks what happens with a simple fact test that is false
  @Test
  public void testRecursiveMyths() throws InvalidQueryException, IOException {
    load("family.ltm");

    findNothing(HIERARCHY_WALKER_ON + 
                "parent($P, $C) :- " +
                "  { parenthood($P : father, $M : mother, $C : child) | " +
                "    parenthood($F : father, $P : mother, $C : child) }. " +
                "etterkommer($A, $D) :- " +
                "  { parent($A, $C), etterkommer($C, $D) | " +
                "    parent($A, $D) }. " +
                "etterkommer(trygve, edvin)?");                
  }

  // checks what happens when the order in the recursive rule is wrong
  @Test
  public void testRecursiveBug1229() throws InvalidQueryException, IOException {
    load("opera.ltm");

    List matches = new ArrayList();
    addMatch(matches, "COUNTRY", getTopicById("italy"), "OPERA", new Integer(30));
    addMatch(matches, "COUNTRY", getTopicById("france"), "OPERA", new Integer(24));
    addMatch(matches, "COUNTRY", getTopicById("germany"), "OPERA", new Integer(8));
    addMatch(matches, "COUNTRY", getTopicById("spain"), "OPERA", new Integer(5));

    // deliberately *not* using hierarchy walker, as that would obscure problem
    verifyQuery(matches, HIERARCHY_WALKER_OFF + 
                "ext-located-in($CONTAINEE, $CONTAINER) :- " +
                "{ " +
                " located-in($CONTAINEE : containee, $CONTAINER : container) | " +
                " ext-located-in($MID, $CONTAINER), " +
                " located-in($CONTAINEE : containee, $MID : container) " +
                "}. " +

                "select $COUNTRY, count($OPERA) from " +
                " instance-of($COUNTRY, country), " +
                " { takes-place-in($OPERA : opera, $COUNTRY : place) | " +
                "   takes-place-in($OPERA : opera, $PLACE : place), " +
                "   ext-located-in($PLACE, $COUNTRY) } " +
                "order by $OPERA desc " +
                "limit 4?");
  }
  
  @Test
  public void testNotWithNotEquals() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("lmg"));
    
    verifyQuery(matches,
               "select $C from " +
               "parenthood($F : father, $M : mother, $C : child), " +
               "not($C /= lmg)?");
  }

  @Test
  public void testNotWithInstanceOf() throws InvalidQueryException,IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("bjorg"));
    addMatch(matches, "C", getTopicById("lmg"));
    addMatch(matches, "C", getTopicById("astri"));
    addMatch(matches, "C", getTopicById("silje"));
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("lms"));
    addMatch(matches, "C", getTopicById("gerd"));
    
    verifyQuery(matches,
               "select $C from " +
               "parenthood($F : father, $M : mother, $C : child), " +
               "not(instance-of($C, father))?");
  }
  
  @Test
  public void testNotWithOr() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("lmg"));
    addMatch(matches, "C", getTopicById("astri"));
    addMatch(matches, "C", getTopicById("silje"));
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("lms"));
    
    verifyQuery(matches,
               "select $C from " +
               "parenthood($F : father, $M : mother, $C : child), " +
               "not({ parenthood($C : father, $MM : mother, $CC : child) | "+
               "      parenthood($FF : father, $C : mother, $CC : child) })?");
  }

  // motivated by bug found writing TM converter
  @Test
  public void testOrWithNots() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "PERSON", getTopicById("bjorg"));
    
    verifyQuery(matches,
                "parenthood(lmg : child, $PERSON : mother, kfg : father), " +
                "{ not(parenthood($PERSON : child, bertha : mother, magnus : father)) | " +
                "  not(instance-of($PERSON, father)) }?");
  }
  
  @Test
  public void testNotWithUnboundVariable() throws InvalidQueryException, IOException{
    load("potato.ltm");

    List matches = new ArrayList();
    addMatch(matches, "DIED-IN", getTopicById("milan"),
                      "PERSON", getTopicById("catalani"));

    addMatch(matches, "DIED-IN", getTopicById("milan"),
                      "PERSON", getTopicById("puccini"));

    // Note: Mr. Potato Head should not occur in the result.
    
    verifyQuery(matches,
               "select $DIED-IN, $PERSON from " +
                "born-in($PERSON : person, lucca : place), " +
                "died-in($PERSON : person, $DIED-IN : place), " +
                "not(direct-instance-of($DIED-IN, country))?");
  }

  /// bug #662

  @Test
  public void testTwoLevelRules() throws InvalidQueryException, IOException {
    load("bug662.xtm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("text-sign"));
    addMatch(matches, "B", getTopicById("tabular-sign"));
    addMatch(matches, "B", getTopicById("graph-sign"));
    addMatch(matches, "B", getTopicById("visual-sign"));

    verifyQuery(matches, "descendant-of($B, sign-type)?",
                "child-of ($A, $B) :- " +
                "  subclass-of($A : subclass, $B : superclass). " +
                "descendant-of($A, $B) :- " +
                "  {child-of($A, $C), descendant-of($C, $B) | " +
                "   child-of($A, $B) }. ");
  }

  @Test
  public void testOtherTwoLevelRule() throws InvalidQueryException,IOException{
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "AUNT", getTopicById("anita"));
    addMatch(matches, "AUNT", getTopicById("carolyn"));
    
    verifyQuery(matches, "aunt-of($AUNT, lana)?",
                "parent-of($A, $B) :- " +
                "    { parenthood($B : child, $A : mother, $F : father) | " +
                "      parenthood($B : child, $M : mother, $A : father) }. " +
                "aunt-of($A, $B) :- " +
                "    parent-of($ASIBLING, $B), " +
                "    parent-of($GP, $ASIBLING), " +
                "    parent-of($GP, $A), " +
                "    instance-of($A, female), " +
                "    not(parent-of($A, $B)).");
                

  }

  @Test
  public void testOtherTwoLevelRule2()throws InvalidQueryException,IOException{
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "NEPHEW", getTopicById("philip"));
    
    verifyQuery(matches, "aunt-of(lana, $NEPHEW)?",
                "parent-of($A, $B) :- " +
                "    { parenthood($B : child, $A : mother, $F : father) | " +
                "      parenthood($B : child, $M : mother, $A : father) }. " +
                "aunt-of($A, $B) :- " +
                "    parent-of($ASIBLING, $B), " +
                "    parent-of($GP, $ASIBLING), " +
                "    parent-of($GP, $A), " +
                "    instance-of($A, female), " +
                "    not(parent-of($A, $B)).");                

  }

  /// sorting tests

  @Test
  public void testOrderByNonTopics() throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "REIFIER", getTopicById("jillstm-topic"),
             "REIFIED", getObjectById("jillstm"));
    addMatch(matches, "REIFIER", getTopicById("jills-contract-topic"),
             "REIFIED", getObjectById("jills-contract"));
    addMatch(matches, "REIFIER", getTopicById("jill-ontopia-topic"),
             "REIFIED", getObjectById("jill-ontopia-association"));
    
    verifyQuery(matches, "reifies($REIFIER, $REIFIED) order by $REIFIED?");
  }

  @Test
  public void testOrderingOfNullTopics() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "F", getTopicById("unknown2"),
             "GF", null);
    addMatch(matches, "F", getTopicById("magnus"),
             "GF", null);
    addMatch(matches, "F", getTopicById("edvin"),
             "GF", null);
    addMatch(matches, "F", getTopicById("asle"),
             "GF", getObjectById("unknown2"));
    addMatch(matches, "F", getTopicById("petter"),
             "GF", getObjectById("edvin"));
    addMatch(matches, "F", getTopicById("kfg"),
             "GF", getObjectById("edvin"));

    verifyQueryOrder(matches,
                     "select $F, $GF from " + // simplify results
                     "instance-of($F, father), " +
                     "{ parenthood($F : child, $GM : mother, $GF : father) } " +
                     "order by $GF, $F?");
  }

  @Test
  public void testOrderingOfNullStrings() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("equation"),
             "D", null);
    addMatch(matches, "T", getTopicById("thequeen"),
             "D", null);
    addMatch(matches, "T", getTopicById("rider"),
             "D", null);
    addMatch(matches, "T", getTopicById("white-horse"),
             "D", null);
    addMatch(matches, "T", getTopicById("horse"),
             "D", "http://www.hest.no/");

    verifyQueryOrder(matches,
                     "instance-of($T, bbtopic), " +
                     "{ nettressurs($T, $D) } " +
                     "order by $D, $T?");
  }

  @Test
  public void testOrderingOfNullTMObjects() throws InvalidQueryException, IOException{
    load("jill.xtm");

    List matches = new ArrayList();
    addMatch(matches, "REIFIER", getTopicById("jill"),
             "REIFIED", null);
    addMatch(matches, "REIFIER", getTopicById("ontopia"),
             "REIFIED", null);
    addMatch(matches, "REIFIER", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"),
             "REIFIED", null);
    addMatch(matches, "REIFIER", getTopicById("company"),
             "REIFIED", null);
    addMatch(matches, "REIFIER", getTopicById("contract"),
             "REIFIED", null);

    verifyQueryOrder(matches,
                     "topic($REIFIER), " +
                     "{ reifies($REIFIER, $REIFIED) } " +
                     "order by $REIFIED, $REIFIER " +
                     "limit 5?");
  }
  
  public void _testOrderingOfNullIntegers() throws InvalidQueryException, IOException{
    load("family.ltm");

    // NOTE: do not run in rdbms tolog, because query is slow
    if (isRDBMSTolog()) return;

    List matches = new ArrayList();
    addMatch(matches, "F", getTopicById("unknown2"), "C", new Integer(1));
    addMatch(matches, "F", getTopicById("asle"), "C", new Integer(1));
    addMatch(matches, "F", getTopicById("lmg"), "C", new Integer(1));
    addMatch(matches, "F", getTopicById("magnus"), "C", new Integer(2));
    addMatch(matches, "F", getTopicById("edvin"), "C", new Integer(2));
    addMatch(matches, "F", getTopicById("petter"), "C", new Integer(3));
    addMatch(matches, "F", getTopicById("kfg"), "C", new Integer(3));

    verifyQueryOrder(matches,
                     "select $F, count($C) from topic($F), " +
                     "{ instance-of($F, father) | topic-name($F, $TN), value($TN, \"Lars Marius Garshol\") }, " +
                     "{ parenthood($F : father, $C : child) } " +
                     "order by $C, $F?");
  }
  
  /// parameter reference tests

  @Test
  public void testInstanceOfaBParameter() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));

    Map args = makeArguments("param", "topic1");
    verifyQuery(matches, "instance-of(%param%, $B)?", args);
  }
  
  @Test
  public void testDirectInstanceOfaBParameter() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("type1"));

    Map args = makeArguments("param", "topic1");
    verifyQuery(matches, "direct-instance-of(%param%, $B)?", args);
  }

  @Test
  public void testInstanceOfAbParameter() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    Map args = makeArguments("param", "type1");
    verifyQuery(matches, "instance-of($A, %param%)?", args);
  }
  
  @Test
  public void testDirectInstanceOfAbParameter() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"));
    addMatch(matches, "A", getTopicById("topic2"));
    
    Map args = makeArguments("param", "type1");
    verifyQuery(matches, "direct-instance-of($A, %param%)?", args);
  }

  @Test
  public void testChildrenOfAParameter() throws InvalidQueryException,IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("lmg"));
    addMatch(matches, "C", getTopicById("silje"));
    addMatch(matches, "C", getTopicById("astri"));

    Map args = makeArguments("param", "bjorg");
    
    verifyQuery(matches,
               "select $C from " +
               "parenthood(%param% : mother, $C : child)?", args);
  }

  @Test
  public void testParamAsThirdArg() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();

    Map args = makeArguments("param", "kfg");
    
    verifyQuery(matches,
                "related($A, $ASSOC, $B) :- " +
                "  role-player($ROLE1, $A), association-role($ASSOC, $ROLE1), " +
                "  association-role($ASSOC, $ROLE2), $ROLE1 /= $ROLE2, " +
                "  role-player($ROLE2, $B). " +
                
                "related(asle, $ASSOC, %param%)?", args);
  }

  @Test
  public void testUnspecifiedParam() throws InvalidQueryException, IOException {
    load("family.ltm");

    getParseError("topicmap(%NOSUCHPARAMETER%)?");
  }
  
  /// other tests

  @Test
  public void testParamIsWrongType()
    throws InvalidQueryException, IOException {
    makeEmpty();

    try {
      Map params = new HashMap();
      params.put("str", new Integer(5));
      processor.execute("resource($R, %str%)?", params);      
      Assert.fail("Successfully passed integer parameter to predicate requiring string");
    } catch (InvalidQueryException e) {
      // this is what we were testing for
    }
  }
  
  @Test
  public void testRuleWithWrongNumberOfArgs()
    throws InvalidQueryException, IOException {
    
    load("family.ltm");

    processor.load("grandchild($GF, $GM, $GC) :- " +
                   "parenthood($GF : father, $GM : mother, $C : child)," +
                   "parenthood($C : father, $M : mother, $GC : child).");

    try {
      processor.execute("grandchild(edvin, kjellaug)?");
      Assert.fail("Successfully called 3-argument rule with 2 arguments");
    } catch (InvalidQueryException e) {
      // this is what we were testing for
    }
  }

  /// LIMIT/OFFSET tests

  @Test
  public void testLimitWithOrder() throws InvalidQueryException,IOException{
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("alan"));
    addMatch(matches, "A", getTopicById("andy"));
    addMatch(matches, "A", getTopicById("anita"));
    addMatch(matches, "A", getTopicById("bruce"));
    
    verifyQuery(matches,
               "instance-of($A, human) order by $A limit 4?");
  }

  @Test
  public void testOffsetWithOrder() throws InvalidQueryException,IOException{
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("sharon"));
    addMatch(matches, "A", getTopicById("spencer"));
    
    verifyQuery(matches,
               "instance-of($A, human) order by $A offset 15?");
  }

  @Test
  public void testOffsetWithOrderAndLimit1() throws InvalidQueryException,IOException{
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("andy"));
    addMatch(matches, "A", getTopicById("anita"));
    addMatch(matches, "A", getTopicById("bruce"));
    addMatch(matches, "A", getTopicById("carolyn"));
    addMatch(matches, "A", getTopicById("clyde"));
    
    verifyQuery(matches,
               "instance-of($A, human) order by $A limit 5 offset 1?");
  }

  @Test
  public void testOffsetWithOrderAndLimit2() throws InvalidQueryException,IOException{
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("clyde"));
    
    verifyQuery(matches,
               "instance-of($A, human) order by $A limit 1 offset 5?");
  }

  @Test
  public void testOffsetZero() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("topic1"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic2"), "B", getTopicById("type1"));
    addMatch(matches, "A", getTopicById("topic3"), "B", getTopicById("type2"));
    addMatch(matches, "A", getTopicById("topic4"), "B", getTopicById("type2"));
    
    verifyQuery(matches, "instance-of($A, $B) offset 0?");
  }

  @Test
  public void testLimitZero() throws InvalidQueryException, IOException {
    load("instance-of.ltm");

    List matches = new ArrayList();  
    verifyQuery(matches, "instance-of($A, $B) limit 0?");
  }

  /// count and duplicate removal tests

  @Test
  public void testCountWithDuplicates1() throws InvalidQueryException, IOException {
    load("synonyms.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T2", getTopicById("daat"));
    addMatch(matches, "T2", getTopicById("daau"));
    addMatch(matches, "T2", getTopicById("alcohol-testing"));
    addMatch(matches, "T2", getTopicById("drug-abuse"));
    addMatch(matches, "T2", getTopicById("drug-testing"));
    addMatch(matches, "T2", getTopicById("alcohol-abuse"));
    addMatch(matches, "T2", getTopicById("addiction"));

    verifyQuery(matches,
                "select $T2 from synonym2(drug-abuse, $T2)?",
                "synonym($A,$B) :- { use-for($A: used-term, $B: unused-term) " +
                " | use-for($B: used-term, $A: unused-term) }. " +

                "synonym2($A,$B) :- { synonym($A,$B) | " +
                "                     synonym($A,$C), synonym($C,$B) }.");
  }

  @Test
  public void testCountWithDuplicates2() throws InvalidQueryException, IOException {
    load("synonyms.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T2", new Integer(7));

    verifyQuery(matches,
                "select count($T2) from synonym2(drug-abuse, $T2)?",
                "synonym($A,$B) :- { use-for($A: used-term, $B: unused-term) " +
                " | use-for($B: used-term, $A: unused-term) }. " +

                "synonym2($A,$B) :- { synonym($A,$B) | " +
                "                     synonym($A,$C), synonym($C,$B) }.");
  }

  @Test
  public void testCountWithDuplicates3() throws InvalidQueryException, IOException {
    load("synonyms.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T1", getTopicById("daat"), "T2", new Integer(5));
    addMatch(matches, "T1", getTopicById("daau"), "T2", new Integer(5));
    addMatch(matches, "T1", getTopicById("alcohol-testing"), "T2", new Integer(4));
    addMatch(matches, "T1", getTopicById("drug-abuse"), "T2", new Integer(7));
    addMatch(matches, "T1", getTopicById("drug-testing"), "T2", new Integer(4));
    addMatch(matches, "T1", getTopicById("alcohol-abuse"), "T2", new Integer(4));
    addMatch(matches, "T1", getTopicById("addiction"), "T2", new Integer(4));

    verifyQuery(matches,
                "select $T1, count($T2) from synonym2($T1, $T2)?",
                "synonym($A,$B) :- { use-for($A: used-term, $B: unused-term) " +
                " | use-for($B: used-term, $A: unused-term) }. " +

                "synonym2($A,$B) :- { synonym($A,$B) | " +
                "                     synonym($A,$C), synonym($C,$B) }.");
  }

  /// test optional clause

  @Test
  public void testOptionalClause() throws InvalidQueryException, IOException {
    load("family2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "P", getTopicById("nancy"), "F", getTopicById("clyde"));
    addMatch(matches, "P", getTopicById("eileen"), "F", getTopicById("andy"));
    addMatch(matches, "P", getTopicById("janet"), "F", getTopicById("andy"));
    addMatch(matches, "P", getTopicById("sharon"), "F", getTopicById("andy"));
    addMatch(matches, "P", getTopicById("lana"), "F", getTopicById("andy"));
    addMatch(matches, "P", getTopicById("norma"), "F", null);
    addMatch(matches, "P", getTopicById("anita"), "F", getTopicById("clyde"));
    addMatch(matches, "P", getTopicById("carolyn"), "F", getTopicById("clyde"));
    
    verifyQuery(matches,
                "  instance-of($P, female), " +
                "  { parenthood($P : child, $F : father) }?");
  }

  @Test
  public void testOptionalClauseEmpty() throws InvalidQueryException, IOException {
    load("family2.ltm");

    getParseError("  instance-of($P, female), { }?");
  }

  @Test
  public void testOptionalClauseOrdering() throws InvalidQueryException, IOException {
    load("opera.ltm");

    List matches = new ArrayList();
    QueryResultIF result = processor.execute("{ premiere-date($OPERA, $DATE) }, " +
                                             "date-of-birth($PERSON, $DATE)?");
    while (result.next())
      addMatch(matches,
               "OPERA",  result.getValue("OPERA"),
               "DATE",   result.getValue("DATE"),
               "PERSON", result.getValue("PERSON"));
    result.close();

    verifyQuery(matches, "date-of-birth($PERSON, $DATE), " +
                "{ premiere-date($OPERA, $DATE) }?");
  }
  
  /// test modules

  @Test
  public void testImportModuleAbsolute() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    String url = URIUtils.toURL(TestFileUtils.getTransferredTestInputFile("query", "grandchild.tl")).toString();

    verifyQuery(matches,
                "import \"" + url + "\" as fam " +
                "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testImportModuleRelative() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GCC", getTopicById("trygve"));
    addMatch(matches, "GCC", getTopicById("tine"));
    addMatch(matches, "GCC", getTopicById("julie"));
    addMatch(matches, "GCC", getTopicById("astri"));
    addMatch(matches, "GCC", getTopicById("lmg"));
    addMatch(matches, "GCC", getTopicById("silje"));
    
    verifyQuery(matches,
                "import \"grandchild.tl\" as fam " +
                "fam:grandchild(edvin, kjellaug, $GCC)?");
  }

  @Test
  public void testImportModuleBang() throws InvalidQueryException, IOException {
    makeEmpty(false);

    getParseError("import \"empty.tl\" as fam " +
                "instance-of($A, $B)?");
  }

  /// ordering tests


  @Test
  public void testCountSortDesc() throws InvalidQueryException, IOException{
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CLASS", getTopicById("type1"),
                      "INST", new Integer(4));
    addMatch(matches, "CLASS", getTopicById("type2"),
                      "INST", new Integer(2));
    
    verifyQueryOrder(matches,
                     "select $CLASS, count($INST) from " +
                     "instance-of($INST, $CLASS)" +
                     "order by $INST desc?");
  }

  @Test
  public void testCountSortAsc() throws InvalidQueryException, IOException {
    load("subclasses.ltm");

    List matches = new ArrayList();
    addMatch(matches, "CLASS", getTopicById("type2"),
                      "INST", new Integer(2));
    addMatch(matches, "CLASS", getTopicById("type1"),
                      "INST", new Integer(4));
    
    verifyQueryOrder(matches,
                     "select $CLASS, count($INST) from " +
                     "instance-of($INST, $CLASS)" +
                     "order by $INST asc?");
  }

  @Test
  public void testSortByString() throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"),
                      "DESC", "topic1");
    addMatch(matches, "TOPIC", getTopicById("topic2"),
                      "DESC", "topic2");
    addMatch(matches, "TOPIC", getTopicById("topic3"),
                      "DESC", "topic3");
    addMatch(matches, "TOPIC", getTopicById("topic4"),
                      "DESC", "topic4");
    
    verifyQueryOrder(matches,
                     "description($TOPIC, $DESC)" +
                     "order by $DESC asc?");
  }  

  @Test
  public void testSortSortName() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "FATHER", getTopicById("unknown2"));
    addMatch(matches, "FATHER", getTopicById("petter"));
    addMatch(matches, "FATHER", getTopicById("magnus"));
    addMatch(matches, "FATHER", getTopicById("kfg"));
    addMatch(matches, "FATHER", getTopicById("edvin"));
    addMatch(matches, "FATHER", getTopicById("asle"));
    
    verifyQueryOrder(matches,
                     "instance-of($FATHER, father)" +
                     "order by $FATHER?");
  }

  @Test
  public void testSortSortName1() throws InvalidQueryException, IOException {
    load("sort1.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("t1"));
    addMatch(matches, "T", getTopicById("t2"));
    addMatch(matches, "T", getTopicById("t3"));
    addMatch(matches, "T", getTopicById("t4"));
    addMatch(matches, "T", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"));
    
    verifyQueryOrder(matches, "topic($T) order by $T?");
  }
  
  @Test
  public void testSortSortName2() throws InvalidQueryException, IOException {
    load("sort2.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("scope"));
    addMatch(matches, "T", getTopicById("t1"));
    addMatch(matches, "T", getTopicById("t2"));
    addMatch(matches, "T", getTopicById("t3"));
    addMatch(matches, "T", getTopicById("t4"));
    addMatch(matches, "T", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"));
    
    verifyQueryOrder(matches, "topic($T) order by $T?");
  }

  @Test
  public void testSortSortName3() throws InvalidQueryException, IOException {
    load("sort3.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("sort"));
    addMatch(matches, "T", getTopicById("t1"));
    addMatch(matches, "T", getTopicById("t2"));
    addMatch(matches, "T", getTopicById("t3"));
    addMatch(matches, "T", getTopicById("t4"));
    addMatch(matches, "T", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"));
    
    verifyQueryOrder(matches, "topic($T) order by $T?");
  }

  @Test
  public void testSortSortName4() throws InvalidQueryException, IOException {
    load("sort4.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("sort"));
    addMatch(matches, "T", getTopicById("t1"));
    addMatch(matches, "T", getTopicById("t2"));
    addMatch(matches, "T", getTopicById("t3"));
    addMatch(matches, "T", getTopicById("t4"));
    addMatch(matches, "T", getTopicById("scope"));
    addMatch(matches, "T", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"));
    
    verifyQueryOrder(matches, "topic($T) order by $T?");
  }

  @Test
  public void testSortTypedNames() throws InvalidQueryException, IOException {
    load("typed-names.xtm"); // see bug #1893

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("antheil"));
    addMatch(matches, "T", getTopicById("dvorak"));
    addMatch(matches, "T", getTopicById("gounod"));
    addMatch(matches, "T", getTopicById("kverndokk"));
    addMatch(matches, "T", getTopicById("mozart"));
    addMatch(matches, "T", getTopicById("rachmaninov"));
    
    verifyQueryOrder(matches, "instance-of($T, composer) order by $T?");
  }
  
  /// type-related problems

  @Test
  public void testMixingStringsAndObjects() throws InvalidQueryException, IOException {
    load("jill.xtm");
    
    getParseError("{ topicmap($A) | resource(jills-contract, $A) }?");
  }

  /// bugs

  public void _testLostCombinations() throws InvalidQueryException, IOException {
    load("opera.ltm");

    // NOTE: do not run in rdbms tolog, because query is slow
    if (isRDBMSTolog()) return;

    List matches = new ArrayList();
    addMatch(matches, "CITY", getTopicById("milano"),       "OPERA", new Integer(49));
    addMatch(matches, "CITY", getTopicById("roma"),         "OPERA", new Integer(18));
    addMatch(matches, "CITY", getTopicById("venezia"),       "OPERA", new Integer(7));
    addMatch(matches, "CITY", getTopicById("new-york"),      "OPERA", new Integer(6));
    addMatch(matches, "CITY", getTopicById("napoli"),        "OPERA", new Integer(6));
    addMatch(matches, "CITY", getTopicById("cremona"),       "OPERA", new Integer(3));
    addMatch(matches, "CITY", getTopicById("firenze"),       "OPERA", new Integer(3));
    addMatch(matches, "CITY", getTopicById("monte-carlo"),   "OPERA", new Integer(3));
    addMatch(matches, "CITY", getTopicById("paris"),         "OPERA", new Integer(3));
    addMatch(matches, "CITY", getTopicById("london"),        "OPERA", new Integer(2));
    addMatch(matches, "CITY", getTopicById("genova"),        "OPERA", new Integer(2));
    addMatch(matches, "CITY", getTopicById("trieste"),       "OPERA", new Integer(2));
    addMatch(matches, "CITY", getTopicById("pesaro"),        "OPERA", new Integer(2));
    addMatch(matches, "CITY", getTopicById("rimini"),        "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("st-petersburg"), "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("chicago"),       "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("buenos-aires"),  "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("rovereto"),      "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("verona"),        "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("san-remo"),      "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("piacenza"),      "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("palermo"),       "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("berlin"),        "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("lecco"),         "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("cairo"),         "OPERA", new Integer(1));
    addMatch(matches, "CITY", getTopicById("torino"),        "OPERA", new Integer(1));
  
    //! verifyQuery(matches,
    //!             "select $CITY, count($OPERA) from " +
    //!             "  instance-of($CITY, city), " +
    //!             "  { premiere($OPERA : opera, $CITY : place) | " +
    //!             "    premiere($OPERA : opera, $THEATRE : place), "+
    //!             "    located-in($THEATRE : containee, $CITY : container)} " +
    //!             "order by $OPERA desc?");
    verifyQuery(matches,
                "select $CITY, count($OPERA) from " +
                "  instance-of($CITY, city), instance-of($OPERA, opera), " +
                "  { premiere($OPERA : opera, $CITY : place) | " +
                "    premiere($OPERA : opera, $THEATRE : place), "+
                "    located-in($THEATRE : containee, $CITY : container)} " +
                "order by $OPERA desc?");
  }  

//   public void testOptimizer() throws InvalidQueryException, IOException {
//     load("opera.ltm");

//     List matches = new ArrayList();

//     verifyQuery(matches,
//                 "select $TYPE from " +
//                 "  direct-instance-of($TOPIC, $TYPE), " +
//                 "  type($ROLE, $TYPE), association-role($ASSOC, $ROLE)?");
//   }

  @Test
  public void testFailurePredicate() throws InvalidQueryException, IOException {
    // motivated by bug #903
    load("family.ltm");
    findNothing("trygve($T, $V) order by $T?");
  }

  @Test
  public void testRuleTypeInferencing() throws InvalidQueryException, IOException {
    // motivated by bug #998
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("silje"));
    addMatch(matches, "C", getTopicById("lmg"));
    addMatch(matches, "C", getTopicById("julie"));
    addMatch(matches, "C", getTopicById("astri"));

    verifyQueryOrder(matches,
                     "parent($M, $F, $C) :- " +
                     "  parenthood($M : mother, $F : father, $C : child). " +
                     
                     "{ parent(may, petter, $C) | parent(bjorg, kfg, $C) } " +
                     "order by $C desc?");    
  }

  @Test
  public void testOptionalClauseInferencing()
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext())
      addMatch(matches, "A", it.next());

    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "topic($A), { association($A) }?");
  }

  @Test
  public void testOptionalClauseInferencing2()
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext())
      addMatch(matches, "A", it.next(), "B", null);

    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "{ topic-name($B, $A) }, topic($A)?");    
  }

  // previous test could be met by shorting out topic-name predicate
  // based on knowledge that $A can't be a topic name, but this test
  // raises the bar so that solution won't be enough
  @Test
  public void testOptionalClauseInferencing3()
    throws InvalidQueryException, IOException {
    load("jill.xtm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext()) {
      TopicIF topic = (TopicIF) it.next();
      TMObjectIF obj = topic.getReified();
      if (obj != null)
        addMatch(matches, "A", topic, "C", obj, "B", null);
    }

    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "{ topic-name($B, $A), topicmap($C) }, topic($A), reifies($A, $C)?");
  }
  
  @Test
  public void testNotClauseInferencing() 
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getTopics().iterator();
    while (it.hasNext())
      addMatch(matches, "A", it.next(), "B", null);

    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "not(topic-name($B, $A)), topic($A)?");
  }

  @Test
  public void testMergingOfObjectAsType()
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "F", getTopicById("kfg"));
    
    verifyQuery(matches, "{ parenthood($F : father, lmg : child) | " +
                "  child($F : father, kjellaug : mother) }?");

  }

  @Test
  public void testTypeTheoryMerging()
    throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "F", getTopicById("kfg"), "M", null);
    addMatch(matches, "F", null, "M", null);
    
    verifyQuery(matches, "{ parenthood($F : father, lmg : child) | " +
                "  not(child($F : father, $M : mother)) }?");

  }

  @Test
  public void testBug1005()
    throws InvalidQueryException, IOException {
    load("bug1005.ltm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("andy"));
    addMatch(matches, "A", getTopicById("nancy"));
    addMatch(matches, "A", getTopicById("peter"));
    addMatch(matches, "A", getTopicById("eileen"));
    
    verifyQuery(matches, "{instance-of($A, male) | instance-of($A, female)}?");

  }

  @Test
  public void testBug1233()
    throws InvalidQueryException, IOException {
    load("opera.ltm");

    List matches = new ArrayList();    
    TopicIF topic = getTopicById("il-tabarro");
    Iterator it = topic.getTopicNames().iterator();
    while (it.hasNext()) {
      TopicNameIF bn = (TopicNameIF) it.next();
      addMatch(matches, "N", bn, "SCOPE", null);
    }
    
    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "topic-name(il-tabarro, $N), " +
                "not(scope($SCOPE, $N))?");
  }

  @Test
  public void testBug1232() throws InvalidQueryException, IOException {
    load("opera.ltm", true);

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("nerone-c"));
    addMatch(matches, "TOPIC", getTopicById("nerone-src"));
    addMatch(matches, "TOPIC", getTopicById("nerone2"));
    addMatch(matches, "TOPIC", getTopicById("claudio-cesare-nerone"));
    addMatch(matches, "TOPIC", getTopicById("nerone"));
    
    verifyQuery(matches,
                "topic-matches($TOPIC, $SEARCH) :- " +
                "  value-like($X, $SEARCH), " +
                "  { topic-name($TOPIC, $X) | " +
                "    occurrence($TOPIC, $X) | " +
                "    topic-name($TOPIC, $TN), variant($TN, $X) }. " +
                
                "topic-matches($TOPIC, \"nerone\")?");
  }

  @Test
  public void testBug1083a() throws InvalidQueryException, IOException {
    load("family.ltm");

    TopicIF lmg = getTopicById("lmg");
    
    List matches = new ArrayList();
    Iterator it = lmg.getTopicNames().iterator();
    while (it.hasNext())
      addMatch(matches, "N", it.next());
    
    verifyQuery(matches,
                "has-name($T, $N) :- topic-name($T, $N) . " +
                "has-name(lmg, $N)?");
  }

  @Test
  public void testBug1083b() throws InvalidQueryException, IOException {
    load("family.ltm");

    findNothing("is-father($T) :- instance-of($T, father) . " +
                "is-father(lmg)?");
  }

  @Test
  public void testBug1378() throws InvalidQueryException, IOException {
    load("null-name.xtm");

    List matches = new ArrayList();
    addMatch(matches, "A", getTopicById("test-topic"));
    addMatch(matches, "A", getTopicById("test-topic2"));
    addMatch(matches, "A", getTopicById("test-type"));
    addMatch(matches, "A", getTopicBySI("http://psi.topicmaps.org/iso13250/model/topic-name"));
    
    verifyQuery(matches, "topic($A) order by $A?");
  }

  @Test
  public void testBug2019() throws InvalidQueryException, IOException {
    load("bug2019.ltm");

    List matches = new ArrayList();
    addMatch(matches);
    
    verifyQuery(matches, "leipziger($P) :- id6(id5 : id2, $P : id1). leipziger(id4)?");
  }
  
// FIXME: the problem here is that the behaviour of count() is
// undefined.  we will define it before progressing on this bug.
  
//   public void testBug1347() throws InvalidQueryException, IOException {
//     load("family.ltm");

//     List matches = new ArrayList();
//     addMatch(matches, "X", new Integer(0));

//     verifyQuery(matches, "select count($X) from direct-instance-of(silje, $X)?");
//   }

  @Test
  public void testShortCircuitingOrClause1() throws InvalidQueryException, IOException {
    load("opera.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("lombardo"));

    verifyQuery(matches, "select $TOPIC from " +
								"{ date-of-birth($TOPIC, $X), $X > \"9\" || " +
								"  date-of-birth($TOPIC, \"1869-11-28\") || " +
								"  date-of-birth($P, \"1866-07-23\") } ?");
	}

  @Test
  public void testShortCircuitingOrClause2() throws InvalidQueryException, IOException {
    makeEmpty();

    List matches = new ArrayList();
    addMatch(matches, "A", new Integer(5), "B", new Integer(2));

    verifyQuery(matches, "select $A, $B from $A = 5, { $A = 5, $B = 2 || $A < 4, $B = 1 || $B = 3 }?");
    verifyQuery(matches, "select $A, $B from $A = 5, { $A < 4, $B = 1 || $A = 5, $B = 2 || $B = 3 }?");

    matches = new ArrayList();
    addMatch(matches, "A", new Integer(5), "B", new Integer(3));

    verifyQuery(matches, "select $A, $B from $A = 5, { $A < 4, $B = 1 || $B = 3 || $A = 5, $B = 2 }?");
    verifyQuery(matches, "select $A, $B from $A = 5, { $B = 3 || $A < 4, $B = 1 || $A = 5, $B = 2 }?");
	}

  @Test
  public void testBug2149() throws InvalidQueryException, IOException {
    makeEmpty();

    List matches = new ArrayList();
    addMatch(matches, "X", new Integer(1));
    addMatch(matches, "X", "2");

    verifyQuery(matches, "select $X from { $X = 1 | $X = \"2\" } order by $X?");
	}

  @Test
  public void testShortCircuitingOR1() throws InvalidQueryException, IOException {
    makeEmpty();

    List matches = new ArrayList();
    addMatch(matches, "X", new Integer(1));

    verifyQuery(matches, "select $X from { $X = 1 || $X = 2 }?");
	}

  @Test
  public void testShortCircuitingOR2() throws InvalidQueryException, IOException {
    makeEmpty();

    List matches = new ArrayList();
    addMatch(matches, "X", new Integer(4), "Y", new Integer(2));

    verifyQuery(matches, "select $X, $Y from $X = 4, { $X < 1, $Y = 1 || $X > 2, $Y = 2 || $X > 3, $Y = 3 }?");
	}
  
  @Test
  public void testCircularRuleReferences() throws InvalidQueryException, IOException {
    load("circular-rule.ltm");

    List matches = new ArrayList();
    addMatch(matches, "Z", getTopicById("x3"));
    addMatch(matches, "Z", getTopicById("x5"));
    addMatch(matches, "Z", getTopicById("x7"));
    
    verifyQuery(matches, 
                "a($x, $z) :- a1($x : r1, $y : r2), b($y, $z). " +
                "b($x, $z) :- { b1($x : r1, $z : r2) | b1($x : r1, $y : r2), a($y, $z) }. " +
                "select $Z from a(x1, $Z)?");
  }

  @Test
  public void testIssue208() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("magnus"));

    verifyQuery(matches,
                "type-of($topic_, $type_) :- { " +
                "  instance-of($topic_, $type_) " +
                "| " +
                "  instance-of($topic_, $type), " +
                "  type-of($type, $type_) " +
                "}. " +

                "select $TOPIC from " +
                "type-of($TOPIC, father), $TOPIC = magnus?");
  }

  @Test
  public void testIssue277() throws InvalidQueryException, IOException {
    load("issue-277.ltm");

    findNothing("bn($B,$N) :- " +
		"  broader-narrower($B : broader-term, $N : narrower-term). " +
		
		"bn1($B,$N) :- bn($B,$I), bn($I,$N). " +

		"bn1($B,$B)?");
  }

  @Test
  public void testPrevNextOptimizerNPE()
    throws InvalidQueryException, IOException {
    load("int-occs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "TOPIC", getTopicById("topic1"),
                      "DESC", "topic1");
    
    verifyQuery(matches,
                "description($TOPIC, $DESC)" +
                "order by $DESC asc limit 1?");
  }  
  
}

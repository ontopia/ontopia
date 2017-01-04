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
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Test;

public class DynamicAssociationPredicateTest extends AbstractPredicateTest {
  
  /// tests
  
  //! public void testWithNullPlayer() throws InvalidQueryException, IOException {
  //!   makeEmpty();
  //!   TopicIF atype = builder.makeTopic();
  //!   TopicIF rtype1 = builder.makeTopic();
  //!   TopicIF rtype2 = builder.makeTopic();
  //!   TopicIF topic = builder.makeTopic();
  //!   TopicIF player = builder.makeTopic();
	//! 
  //!   AssociationBuilder abuilder = new AssociationBuilder(atype, rtype1, rtype2);
  //!   abuilder.makeAssociation(topic, player);
  //!   abuilder.makeAssociation(topic, null);
  //!   
  //!   List matches = new ArrayList();
  //!   addMatch(matches, "PLAYER", player);
 	//! 
  //!   verifyQuery(matches, "@" + atype.getObjectId() + "(@" + topic.getObjectId() +
  //!               " : @" + rtype1.getObjectId() + ", $PLAYER : @" +
  //!               rtype2.getObjectId() + ")?");
  //! }
  //! 
  //! public void testWithNullFirstPlayer() throws InvalidQueryException, IOException {
  //!   makeEmpty();
  //!   TopicIF atype = builder.makeTopic();
  //!   TopicIF rtype1 = builder.makeTopic();
  //!   TopicIF rtype2 = builder.makeTopic();
  //!   TopicIF topic = builder.makeTopic();
  //!   TopicIF player = builder.makeTopic();
	//! 
  //!   AssociationBuilder abuilder = new AssociationBuilder(atype, rtype1, rtype2);
  //!   abuilder.makeAssociation(player, topic);
  //!   abuilder.makeAssociation(null, topic);
  //!   
  //!   List matches = new ArrayList();
  //!   addMatch(matches, "PLAYER", player);
 	//! 
  //!   verifyQuery(matches, "@" + atype.getObjectId() +
  //!               "(@" + topic.getObjectId() + " : @" + rtype2.getObjectId() +
  //!               ", $PLAYER : @" + rtype1.getObjectId() + ")?");
  //! }
  //! 
  //! public void testWithNullRoletype() throws InvalidQueryException, IOException {
  //!   makeEmpty();
  //!   TopicIF atype = builder.makeTopic();
  //!   TopicIF rtype1 = builder.makeTopic();
  //!   TopicIF topic = builder.makeTopic();
  //!   TopicIF player1 = builder.makeTopic();
  //!   TopicIF player2 = builder.makeTopic();
	//! 
  //!   AssociationBuilder abuilder = new AssociationBuilder(atype, rtype1, null);
  //!   abuilder.makeAssociation(topic, player1);
  //!   abuilder.makeAssociation(topic, player2);
  //!   
  //!   List matches = new ArrayList();
 	//! 
  //!   verifyQuery(matches, "@" + atype.getObjectId() + "(@" + topic.getObjectId() +
  //!               " : @" + rtype1.getObjectId() + ", $PLAYER : @" +
  //!               player1.getObjectId() + ")?");
  //! }

  @Test
  public void testWithUnary() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
    TopicIF topic = builder.makeTopic();

    AssociationIF assoc = builder.makeAssociation(atype);
    builder.makeAssociationRole(assoc, rtype1, topic);
    
    List matches = new ArrayList();
 
    assertQueryMatches(matches, "@" + atype.getObjectId() + "(@" + topic.getObjectId() +
                " : @" + rtype1.getObjectId() + ", $PLAYER : @" +
                rtype2.getObjectId() + ")?");
  }

  
  /// family topic map

  @Test
  public void testAssocNomatches() throws InvalidQueryException, IOException{
    load("family.ltm");
    assertFindNothing("parenthood(petter : mother, may : father, $C : child)?");
  }

  @Test
  public void testAssocNomatchesSyntaxProblem1()
    throws InvalidQueryException, IOException{
    load("family.ltm");
    assertFindNothing("parenthood(petter: mother, may : father, $C : child)?");
  }

  @Test
  public void testAssocNomatchesSyntaxProblem2()
    throws InvalidQueryException, IOException{
    load("family.ltm");
    assertFindNothing("parenthood(petter : mother, may : father, $C: child)?");
  }
  
  @Test
  public void testAssocSimple() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"));
    addMatch(matches, "C", getTopicById("tine"));
    addMatch(matches, "C", getTopicById("julie"));
    
    assertQueryMatches(matches,
                "parenthood(may : mother, petter : father, $C : child)?");
  }

  @Test
  public void testAssocTwoVars() throws InvalidQueryException, IOException{
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("trygve"),
             "F", getTopicById("petter"));
    addMatch(matches, "C", getTopicById("tine"),
             "F", getTopicById("petter"));
    addMatch(matches, "C", getTopicById("julie"),
             "F", getTopicById("petter"));
    
    assertQueryMatches(matches,
                "parenthood(may : mother, $F : father, $C : child)?");
  }

  @Test
  public void testSymmetricAssoc() throws InvalidQueryException, IOException {
    load("factbook.ltm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches,
                "borders-with($A : country, $A : country)?");
  }

  @Test
  public void testTernarySymmetric() throws InvalidQueryException, IOException{
    load("partners.ltm");

    List matches = new ArrayList();
    addMatch(matches, "PARTNER", getTopicById("isogen"),
             "POS", getTopicById("pos01"));
    addMatch(matches, "PARTNER", getTopicById("synergy"),
             "POS", getTopicById("pos02"));
    addMatch(matches, "PARTNER", getTopicById("innodigital"),
             "POS", getTopicById("pos03"));
    addMatch(matches, "PARTNER", getTopicById("eurostep"),
             "POS", getTopicById("pos04"));

    assertQueryOrder(matches,
                     "partnership(ontopia : partner, " +
                     "            $PARTNER : partner, " +
                     "            $POS : position) " +
                     "order by $POS?");
  }

  @Test
  public void testAssocDouble() throws InvalidQueryException, IOException {
    load("family.ltm");

    List matches = new ArrayList();
    addMatch(matches, "GC", getTopicById("trygve"),
                      "C", getTopicById("petter"),
                      "M", getTopicById("may"));
    addMatch(matches, "GC", getTopicById("tine"),
                      "C", getTopicById("petter"),
                      "M", getTopicById("may"));
    addMatch(matches, "GC", getTopicById("julie"),
                      "C", getTopicById("petter"),
                      "M", getTopicById("may"));
    addMatch(matches, "GC", getTopicById("astri"),
                      "C", getTopicById("kfg"),
                      "M", getTopicById("bjorg"));
    addMatch(matches, "GC", getTopicById("lmg"),
                      "C", getTopicById("kfg"),
                      "M", getTopicById("bjorg"));
    addMatch(matches, "GC", getTopicById("silje"),
                      "C", getTopicById("kfg"),
                      "M", getTopicById("bjorg"));
    
    assertQueryMatches(matches,
                "parenthood(edvin : father, kjellaug : mother, $C : child)," +
                "parenthood($C : father, $M : mother, $GC : child)?");
  }

  /// role matching tests

  // this test was motivated by bug #476
  @Test
  public void testSameRole() throws InvalidQueryException, IOException {
    load("factbook.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C1", getTopicById("norway"),  "C2", getTopicById("sweden"));
    addMatch(matches, "C1", getTopicById("norway"),  "C2", getTopicById("finland"));
    addMatch(matches, "C1", getTopicById("norway"),  "C2", getTopicById("russia"));
    addMatch(matches, "C1", getTopicById("finland"), "C2", getTopicById("russia"));
    addMatch(matches, "C1", getTopicById("finland"), "C2", getTopicById("sweden"));
    addMatch(matches, "C1", getTopicById("sweden"),  "C2", getTopicById("norway"));
    addMatch(matches, "C1", getTopicById("finland"), "C2", getTopicById("norway"));
    addMatch(matches, "C1", getTopicById("russia"),  "C2", getTopicById("norway"));
    addMatch(matches, "C1", getTopicById("russia"),  "C2", getTopicById("finland"));
    addMatch(matches, "C1", getTopicById("sweden"),  "C2", getTopicById("finland"));
    
    assertQueryMatches(matches, "borders-with($C1 : country, $C2 : country), " +
                         "$C1 /= $C2?");
  }

  @Test
  public void testBordersThreeCountries() throws InvalidQueryException, IOException {
    load("factbook.ltm");

    List matches = new ArrayList();
    addMatch(matches, "C", getTopicById("norway"));
    addMatch(matches, "C", getTopicById("finland"));
    
    assertQueryMatches(matches, "select $C from " +
              "  borders-with($C : country, $N1 : country), " +
              "  borders-with($C : country, $N2 : country), $N1 /= $N2, " +
              "  borders-with($C : country, $N3 : country), $N1 /= $N3, $N2 /= $N3?");
  }
  
  @Test
  public void testSameRoleConstant() throws InvalidQueryException, IOException {
    load("factbook.ltm");

    List matches = new ArrayList();
    addMatch(matches, "N", getTopicById("sweden"));
    addMatch(matches, "N", getTopicById("finland"));
    addMatch(matches, "N", getTopicById("russia"));
    
    assertQueryMatches(matches, "borders-with(norway : country, $N : country)?");
  }

  @Test
  public void testSameRolePlayedManyTimesInBadlyFuckingDesignedTopicMapCourtesyOfRobert() throws InvalidQueryException, IOException {
    load("uc-literature.xtm");

    List matches = new ArrayList();
    addMatch(matches, "PAPER", getTopicById("pepper99a"),
             "AUTHOR", getTopicById("steve-pepper"));
    addMatch(matches, "PAPER", getTopicById("pepper99b"),
             "AUTHOR", getTopicById("steve-pepper"));
    addMatch(matches, "PAPER", getTopicById("pepp00"),
             "AUTHOR", getTopicById("steve-pepper"));
    addMatch(matches, "PAPER", getTopicById("d-topicmaps-color"),
             "AUTHOR", getTopicById("holger-rath"));
    addMatch(matches, "PAPER", getTopicById("bienew01"),
             "AUTHOR", getTopicById("steve-newcomb"));
    addMatch(matches, "PAPER", getTopicById("bienew01"),
             "AUTHOR", getTopicById("michel-biezunski"));
    
    assertQueryMatches(matches, "is-author-of($PAPER : opus, $AUTHOR : author)?");
  }

  // inconsistent use of role types
  @Test
  public void testBug1293() throws InvalidQueryException, IOException {
    load("bug1293.ltm");

    List matches = new ArrayList();
    addMatch(matches, "OPERA", getTopicById("attila"),
             "PLACE", getTopicById("italy"));
    addMatch(matches, "OPERA", getTopicById("tosca"),
             "PLACE", getTopicById("rome"));
    
    assertQueryMatches(matches, "takes-place-in($OPERA : opera, $PLACE : place)?");
  }  

  /// bug #655

  @Test
  public void testUnaryAssoc() throws InvalidQueryException, IOException {
    load("bug655.ltm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches, "subclass-of($A : superclass, $B : subclass)?");
  }

  /// type testing

  @Test
  public void testLiteralType() throws InvalidQueryException, IOException {
    load("bug662.xtm");

    TopicIF topic = getTopicById("history");
    AssociationRoleIF role = (AssociationRoleIF) topic.getRoles().iterator().next();
    String rid = role.getObjectId();
    
    assertFindNothing(OPT_TYPECHECK_OFF +
                "example-of(@" + rid + " : illustrated, $E : example)?");
  }

  @Test
  public void testTypeWithBounds() throws InvalidQueryException, IOException {
    load("bug662.xtm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic($R1), association-role($A, $R2), " +
                "example-of($R1 : illustrated, $R2 : example)?");
  }

  @Test
  public void testTypeWithBounds2() throws InvalidQueryException, IOException {
    load("bug662.xtm");

    assertFindNothing(OPT_TYPECHECK_OFF +
                "topic($R1), association-role($A, $R2), " +
                "example-of($R2 : illustrated, $R1 : example)?");
  }

  /// same variable twice

//   public void testBug972() throws InvalidQueryException, IOException {
//     load("opera.ltm");

//     List matches = new ArrayList();
//     addMatch(matches, "WORK", getTopicById("madama-butterfly"),
//              "SUICIDE", getTopicById("butterfly"));
//     addMatch(matches, "WORK", getTopicById("tosca"),
//              "SUICIDE", getTopicById("tosca-c"));
//     addMatch(matches, "WORK", getTopicById("tosca"),
//              "SUICIDE", getTopicById("angelotti"));
//     addMatch(matches, "WORK", getTopicById("turandot"),
//              "SUICIDE", getTopicById("liu"));
    
//     verifyQuery(matches, "select $WORK , $SUICIDE from " +
//                 "  appears-in($SUICIDE : character, $WORK : work), " +
//                 "  killed-by($SUICIDE : victim, $SUICIDE : perpetrator)?");
//   }

  // need to also test outside satisfyWhenBound
  @Test
  public void testBug972b() throws InvalidQueryException, IOException {
    load("opera.ltm");

    List matches = new ArrayList();
    addMatch(matches, "SUICIDE", getTopicById("butterfly"));
    addMatch(matches, "SUICIDE", getTopicById("tosca-c"));
    addMatch(matches, "SUICIDE", getTopicById("angelotti"));
    addMatch(matches, "SUICIDE", getTopicById("liu"));

    // NOTE: changed to subset matching when moving to opera.ltm from opera.hytm
    assertQuerySubset(matches, "killed-by($SUICIDE : victim, $SUICIDE : perpetrator)?"); 
  }

//   public void testBug1017() throws InvalidQueryException, IOException {
//     load("partners.ltm");

//     List matches = new ArrayList();
//     addMatch(matches, "PARTNER", getTopicById("ontopia"));
//     addMatch(matches, "PARTNER", getTopicById("isogen"));

//     verifyQuery(matches,
//                 "partnership(pos01 : position, $PARTNER : partner)?");
//   }

  /// unnumbered bug reported by KN
  //! public void testNullPlayer() throws InvalidQueryException, IOException {
  //!   load("null-player.xtm");
	//! 
  //!   findNothing("supertema($SUPER, $SUB) :- { " +
  //!               "  temahierarki($SUPER : overordnet, $SUB : underordnet) | " +
  //!               "  temahierarki($SUPER : overordnet, $X : underordnet), " +
  //!               "  supertema($X, $SUB) " +
  //!               "}. " +
  //!               "select $OTEMA from supertema($OTEMA, kultur)?");
  //! }

  //! public void testBug2001() throws InvalidQueryException, IOException {
  //!   load("null-role-type.xtm");
	//! 
  //!   List matches = new ArrayList();
  //!   addMatch(matches, "WHO", null,  "WHAT", getTopicById("ontopia"));
	//! 
  //!   // this match is also correct:
  //!   //addMatch(matches, "WHO", "foo", "WHAT", getTopicById("ontopia"));
	//! 
  //!   // the bug occurs because "employed-by" thinks $WHAT is bound
  //!   // (because the first row is non-null), but the second row
  //!   // contains a null
  //!   
  //!   verifyQuery(matches,
  //!               "/* #OPTION: optimizer.reorder = false */ " +
  //!               "{ $WHAT = ontopia | $WHO = \"foo\" }, " +
  //!               "employed-by(lmg : employee, $WHAT : employer)?");
  //! }
}

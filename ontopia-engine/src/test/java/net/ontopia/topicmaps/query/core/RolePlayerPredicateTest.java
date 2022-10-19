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
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import org.junit.Test;

public class RolePlayerPredicateTest extends AbstractPredicateTest {

  /// tests

  @Test
  public void testCompletelyOpen() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    while (it.hasNext()) {
      AssociationIF assoc = (AssociationIF) it.next();

      Iterator it2 = assoc.getRoles().iterator();
      while (it2.hasNext()) {
        AssociationRoleIF role = (AssociationRoleIF) it2.next();
        addMatch(matches, "PLAYER", role.getPlayer(), "ROLE", role);
      }
    }
    
    assertQueryMatches(matches, "role-player($ROLE, $PLAYER)?");
  }  

  @Test
  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    matches.add(new HashMap());
    
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", larer)?");
  }

  @Test
  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", gdm)?");
  } 

  @Test
  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    
    assertQueryMatches(matches, OPT_TYPECHECK_OFF +
                "topic($TOPIC), role-player($TOPIC, $ROLE)?");
  } 
  
  @Test
  public void testRolePlayerPredicate0() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF player = builder.makeTopic();

    List matches = new ArrayList();
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  @Test
  public void testRolePlayerPredicate1() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  @Test
  public void testRolePlayerPredicate2() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype1 = builder.makeTopic();
		TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  @Test
  public void testRolePlayerPredicate3() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);

    AssociationIF assoc2 = builder.makeAssociation(atype);
    AssociationRoleIF role3 = builder.makeAssociationRole(assoc2, rtype1, player);
    builder.makeAssociationRole(assoc2, rtype2, other);
    
    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
    addMatch(matches, "ROLE", role3);
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  @Test
  public void testRolePlayerPredicate1b() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
  }

  @Test
  public void testRolePlayerPredicate2b() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype1 = builder.makeTopic();
		TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);

    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
  }

  @Test
  public void testRolePlayerPredicate3b() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype1 = builder.makeTopic();
		TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);

    AssociationIF assoc2 = builder.makeAssociation(atype);
    builder.makeAssociationRole(assoc2, rtype1, player);
    builder.makeAssociationRole(assoc2, rtype2, other);
    
    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
  }

  @Test
  public void testTypeHoistUnapplicable() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("lmg"));
    addMatch(matches, "B", getTopicById("stine"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $B from role-player($A, $B), type($A, employee)?");
  }

  @Test
  public void testTypeHoistUnapplicable2() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    TopicIF lmg = getTopicById("lmg");
    TopicIF employee = getTopicById("employee");
    
    List matches = new ArrayList();
    addMatch(matches, "A", lmg.getRolesByType(employee).iterator().next());
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "type($A, employee), role-player($A, lmg)?");
  }

  @Test
  public void testTypeHoistUnapplicable3() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  type($A, works-for), " +
                "  association-role($A, $R), " +
                "  role-player($R, $T), " +
                "  type($R, employee)?");
  }

  @Test
  public void testTypeHoistUnapplicable4() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  type($A, works-for), " +
                "  association-role($A, $R), " +
                "  type($R, $RT), " +
                "  role-player($R, $T), " +
                "  reifies(lmg-position, $R2), " + // last two lines ensure that
                "  type($R2, $RT)?");              // we get only 'employee' roles
  }

  @Test
  public void testTypeHoistUnapplicable5() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("ontopia"));
    addMatch(matches, "T", getTopicById("norad"));
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("employee"));
    addMatch(matches, "T", getTopicById("person"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  role-player($R, $T), " +
                "  not(type($R, employee)), " +
                "  not(type($R, cohab))?");
  }

  @Test
  public void testTypeHoistUnapplicable6() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  role-player($R, $T), " +
                "  type($R, $RT)?");

    assertQueryMatches(matches,
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  role-player($R, $T), " +
                "  type($R, $RT)?");
  }
  
  @Test
  public void testTypeHoistLiteral() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, employee)?");

    assertQueryMatches(matches,
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, employee)?");
  }

  @Test
  public void testTypeHoistLiteral2() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, owner)?");

    assertQueryMatches(matches,
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, owner)?");
  }
  
  @Test
  public void testTypeHoistVariable() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");

    assertQueryMatches(matches,
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");
  }

  @Test
  public void testTypeHoistVariable2() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    TopicIF employee = getTopicById("employee");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"),   "RT", employee);
    addMatch(matches, "T", getTopicById("stine"), "RT", employee);
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T, $RT from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");

    assertQueryMatches(matches,
                "select $T, $RT from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");
  }
  
  @Test
  public void testTypeHoistParameter()
    throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));

    Map params = new HashMap();
    params.put("type", getTopicById("employee"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);

    assertQueryMatches(matches,
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);
  }

  @Test
  public void testTypeHoistParameter2()
    throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));

    Map params = new HashMap();
    params.put("type", getTopicById("owner"));
    
    assertQueryMatches(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);

    assertQueryMatches(matches,
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);
  }
  
}

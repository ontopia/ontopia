
// $Id: RolePlayerPredicateTest.java,v 1.12 2008/05/23 09:24:22 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;

public class RolePlayerPredicateTest extends AbstractPredicateTest {
  
  public RolePlayerPredicateTest(String name) {
    super(name);
  }

  public void tearDown() {
    closeStore();
  }
  
  /// tests

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
    
    verifyQuery(matches, "role-player($ROLE, $PLAYER)?");
  }  

  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    matches.add(new HashMap());
    
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", larer)?");
  }

  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", gdm)?");
  } 

  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    
    verifyQuery(matches, OPT_TYPECHECK_OFF +
                "topic($TOPIC), role-player($TOPIC, $ROLE)?");
  } 
  
  public void testRolePlayerPredicate0() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF player = builder.makeTopic();

    List matches = new ArrayList();
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  public void testRolePlayerPredicate1() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  public void testRolePlayerPredicate2() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype1 = builder.makeTopic();
		TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype2, other);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  public void testRolePlayerPredicate3() throws InvalidQueryException, IOException {
    makeEmpty();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype2, other);

    AssociationIF assoc2 = builder.makeAssociation(atype);
    AssociationRoleIF role3 = builder.makeAssociationRole(assoc2, rtype1, player);
    AssociationRoleIF role4 = builder.makeAssociationRole(assoc2, rtype2, other);
    
    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
    addMatch(matches, "ROLE", role3);
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
  }

  public void testRolePlayerPredicate1b() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
  }

  public void testRolePlayerPredicate2b() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype1 = builder.makeTopic();
		TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype2, other);

    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
  }

  public void testRolePlayerPredicate3b() throws InvalidQueryException, IOException {
    makeEmpty();
		TopicIF atype = builder.makeTopic();
		TopicIF rtype1 = builder.makeTopic();
		TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype2, other);

    AssociationIF assoc2 = builder.makeAssociation(atype);
    AssociationRoleIF role3 = builder.makeAssociationRole(assoc, rtype1, player);
    AssociationRoleIF role4 = builder.makeAssociationRole(assoc, rtype2, other);
    
    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
  }

  public void testTypeHoistUnapplicable() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "B", getTopicById("lmg"));
    addMatch(matches, "B", getTopicById("stine"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $B from role-player($A, $B), type($A, employee)?");
  }

  public void testTypeHoistUnapplicable2() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    TopicIF lmg = getTopicById("lmg");
    TopicIF employee = getTopicById("employee");
    
    List matches = new ArrayList();
    addMatch(matches, "A", lmg.getRolesByType(employee).iterator().next());
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "type($A, employee), role-player($A, lmg)?");
  }

  public void testTypeHoistUnapplicable3() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  type($A, works-for), " +
                "  association-role($A, $R), " +
                "  role-player($R, $T), " +
                "  type($R, employee)?");
  }

  public void testTypeHoistUnapplicable4() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  type($A, works-for), " +
                "  association-role($A, $R), " +
                "  type($R, $RT), " +
                "  role-player($R, $T), " +
                "  reifies(lmg-position, $R2), " + // last two lines ensure that
                "  type($R2, $RT)?");              // we get only 'employee' roles
  }

  public void testTypeHoistUnapplicable5() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("ontopia"));
    addMatch(matches, "T", getTopicById("norad"));
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("employee"));
    addMatch(matches, "T", getTopicById("person"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  role-player($R, $T), " +
                "  not(type($R, employee)), " +
                "  not(type($R, cohab))?");
  }

  public void testTypeHoistUnapplicable6() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  role-player($R, $T), " +
                "  type($R, $RT)?");

    verifyQuery(matches,
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  role-player($R, $T), " +
                "  type($R, $RT)?");
  }
  
  public void testTypeHoistLiteral() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, employee)?");

    verifyQuery(matches,
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, employee)?");
  }

  public void testTypeHoistLiteral2() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, owner)?");

    verifyQuery(matches,
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, owner)?");
  }
  
  public void testTypeHoistVariable() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");

    verifyQuery(matches,
                "select $T from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");
  }

  public void testTypeHoistVariable2() throws InvalidQueryException, IOException {
    load("assocs.ltm");

    TopicIF employee = getTopicById("employee");
    
    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"),   "RT", employee);
    addMatch(matches, "T", getTopicById("stine"), "RT", employee);
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T, $RT from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");

    verifyQuery(matches,
                "select $T, $RT from " +
                "  played-by(person : type, $RT : role), " +
                "  topic($T), " + // binds $T, but might be optimized away
                "  role-player($R, $T), " +
                "  type($R, $RT)?");
  }
  
  public void testTypeHoistParameter()
    throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));
    addMatch(matches, "T", getTopicById("stine"));

    Map params = new HashMap();
    params.put("type", getTopicById("employee"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);

    verifyQuery(matches,
                "select $T from " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);
  }

  public void testTypeHoistParameter2()
    throws InvalidQueryException, IOException {
    load("assocs.ltm");

    List matches = new ArrayList();
    addMatch(matches, "T", getTopicById("lmg"));

    Map params = new HashMap();
    params.put("type", getTopicById("owner"));
    
    verifyQuery(matches,
                "/* #OPTION: optimizer.reorder = false */ " +
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);

    verifyQuery(matches,
                "select $T from " +
                "instance-of($T, person), " +
                "role-player($R, $T), " +
                "type($R, %type%)?", params);
  }
  
}

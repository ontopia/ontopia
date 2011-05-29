
package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;

public class AssociationRolePredicateTest extends AbstractPredicateTest {
  
  public AssociationRolePredicateTest(String name) {
    super(name);
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
        addMatch(matches, "ASSOC", assoc, "ROLE", role);
      }
    }
    
    verifyQuery(matches, "association-role($ASSOC, $ROLE)?");
    
    closeStore();
  }

  public void testSpecificAssoc() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    Iterator it = topicmap.getAssociations().iterator();
    AssociationIF assoc = (AssociationIF) it.next();

    Iterator it2 = assoc.getRoles().iterator();
    while (it2.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it2.next();
      addMatch(matches, "ROLE", role);
    }
    
    verifyQuery(matches, "association-role(@" + assoc.getObjectId() + ", $ROLE)?");
    
    closeStore();
  }

  public void testSpecificRole() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    AssociationIF assoc = role.getAssociation();
    addMatch(matches, "ASSOC", assoc);
    
    verifyQuery(matches, "association-role($ASSOC, @" + role.getObjectId() + ")?");
    
    closeStore();
  }

  public void testBothBoundTrue() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("larer");
    AssociationRoleIF role = (AssociationRoleIF) teacher.getRoles().iterator().next();
    AssociationIF assoc = role.getAssociation();
    matches.add(new HashMap());
    
    verifyQuery(matches, "association-role(@" + assoc.getObjectId() + ", @" + role.getObjectId() + ")?");
    
    closeStore();
  }

  public void testBothBoundFalse() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF teacher = getTopicById("gdm");
    Iterator it = teacher.getRoles().iterator();
    AssociationRoleIF role = (AssociationRoleIF) it.next();
    AssociationRoleIF role2 = (AssociationRoleIF) it.next();
    AssociationIF assoc = role2.getAssociation();
    
    verifyQuery(matches, "association-role(@" + assoc.getObjectId() + ", @" + role.getObjectId() + ")?");
    
    closeStore();
  } 

  public void testCrossJoin() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    
    verifyQuery(matches,
                OPT_TYPECHECK_OFF +
                "role-player($TOPIC, $ROLE), " +
                "association-role($ASSOC, $ROLE)?");
    
    closeStore();
  } 
  
  public void testWithSpecificTopic() throws InvalidQueryException, IOException {
    load("bb-test.ltm");

    List matches = new ArrayList();
    TopicIF horse = getTopicById("white-horse");
    TopicIF comment = getTopicById("comment-on");
    Iterator it = horse.getRoles().iterator();
    while (it.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF) it.next();
      AssociationIF assoc = role.getAssociation();
      if (assoc.getType().equals(comment))
        addMatch(matches, "ASSOC", assoc);
    }
    
    verifyQuery(matches,
                "select $ASSOC from " +
                "  role-player($ROLE, white-horse), " +
                "  association-role($ASSOC, $ROLE), " +
                "  type($ASSOC, comment-on)?");
    
    closeStore();
  }
  
}

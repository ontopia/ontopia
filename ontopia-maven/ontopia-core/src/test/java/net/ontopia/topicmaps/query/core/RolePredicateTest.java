
// $Id: RolePredicateTest.java,v 1.7 2008/05/23 09:24:22 geir.gronmo Exp $

package net.ontopia.topicmaps.query.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;

public class RolePredicateTest extends AbstractQueryTest {
  
  public RolePredicateTest(String name) {
    super(name);
  }

  public void testRolePlayerPredicate0() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF player = builder.makeTopic();

    List matches = new ArrayList();
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
    closeStore();
  }

  public void testRolePlayerPredicate1() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
    closeStore();
  }

  public void testRolePlayerPredicate2() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
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
    closeStore();
  }

  public void testRolePlayerPredicate3() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
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
    addMatch(matches, "ROLE", role);
    addMatch(matches, "ROLE", role3);
 
    verifyQuery(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
    closeStore();
  }

  public void testRolePlayerPredicate1b() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
    closeStore();
  }

  public void testRolePlayerPredicate2b() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
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
    closeStore();
  }

  public void testRolePlayerPredicate3b() throws InvalidQueryException, IOException {
    makeEmpty();
    base = new URILocator("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
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
    AssociationRoleIF role4 = builder.makeAssociationRole(assoc, rtype2, player);
    
    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    verifyQuery(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
    closeStore();
  }

}

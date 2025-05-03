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
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import org.junit.Test;

public class RolePredicateTest extends AbstractQueryTest {
  
  @Test
  public void testRolePlayerPredicate0() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF player = builder.makeTopic();

    List matches = new ArrayList();
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
    closeStore();
  }

  @Test
  public void testRolePlayerPredicate1() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
    closeStore();
  }

  @Test
  public void testRolePlayerPredicate2() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
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
    closeStore();
  }

  @Test
  public void testRolePlayerPredicate3() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);

    builder.makeAssociation(atype);
    AssociationRoleIF role3 = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);
    
    List matches = new ArrayList();
    addMatch(matches, "ROLE", role);
    addMatch(matches, "ROLE", role3);
 
    assertQueryMatches(matches, "role-player($ROLE, @" + player.getObjectId() + ")?");
    closeStore();
  }

  @Test
  public void testRolePlayerPredicate1b() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype, player);

    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
    closeStore();
  }

  @Test
  public void testRolePlayerPredicate2b() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
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
    closeStore();
  }

  @Test
  public void testRolePlayerPredicate3b() throws InvalidQueryException, IOException {
    makeEmpty();
    base = URILocator.create("http://www.example.com");

    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF atype = builder.makeTopic();
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    AssociationIF assoc = builder.makeAssociation(atype);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, other);

    builder.makeAssociation(atype);
    builder.makeAssociationRole(assoc, rtype1, player);
    builder.makeAssociationRole(assoc, rtype2, player);
    
    List matches = new ArrayList();
    addMatch(matches, "PLAYER", player);
 
    assertQueryMatches(matches, "role-player(@" + role.getObjectId() + ", $PLAYER)?");
    closeStore();
  }

}

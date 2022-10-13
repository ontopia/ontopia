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

package net.ontopia.topicmaps.core;

import org.junit.Assert;
import org.junit.Test;

public abstract class AssociationTest extends AbstractTypedScopedTest {
  protected AssociationIF assoc;
  
  // --- Test cases

	@Test
	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = assoc;

    Assert.assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    Assert.assertTrue("Topic reifying the reifiable was found",
							 reifiable.getReifier() == null);

		reifiable.setReifier(reifier);
    Assert.assertTrue("No topic reifying the reifiable was found",
							 reifiable.getReifier() == reifier);
    Assert.assertTrue("No object reified by the reifying topic was found",
							 reifier.getReified() == reifiable);

		reifiable.setReifier(null);
    Assert.assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    Assert.assertTrue("Topic reifying the first reifiable was found",
							 reifiable.getReifier() == null);
	}

  @Test
  public void testRoles() {
    // STATE 1: no roles
    Assert.assertTrue("role set not empty initially", assoc.getRoles().size() == 0);

    // STATE 2: one child role
    AssociationRoleIF role = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    // builder should add it to the assoc

    Assert.assertTrue("role not added to child role set",
               assoc.getRoles().size() == 1);

    Assert.assertTrue("role identity not retained",
               assoc.getRoles().iterator().next().equals(role));

    // STATE 3: no child roles again
    role.remove();
        
    Assert.assertTrue("role not removed", assoc.getRoles().size() == 0);

    // checking that it's safe
    role.remove();
  }

  @Test
  public void testFourRoles() {
    // STATE 1: no roles
    Assert.assertTrue("role set not empty initially", assoc.getRoles().size() == 0);

    // STATE 2: four child roles
    TopicIF type = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationRoleIF role = builder.makeAssociationRole(assoc, type, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, type, player);
    AssociationRoleIF role3 = builder.makeAssociationRole(assoc, type, player);
    AssociationRoleIF role4 = builder.makeAssociationRole(assoc, type, player);

    Assert.assertTrue("roles not added to child role set",
               assoc.getRoles().size() == 4);
    Assert.assertTrue("roles not added to player's role set",
               player.getRoles().size() == 4);

    // STATE 3: no child roles again
    role.remove();
    role2.remove();
    role3.remove();
    role4.remove();
        
    Assert.assertTrue("roles not removed", assoc.getRoles().size() == 0);
    Assert.assertTrue("roles not removed from player's role set",
               player.getRoles().size() == 0);

    // checking that it's safe
    role.remove();
  }
  
  @Test
  public void testRolesByType() {
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
        
    Assert.assertTrue("roles by non-existent type initially not empty",
               assoc.getRolesByType(rtype1).size() == 0);

    builder.makeAssociationRole(assoc, rtype1, builder.makeTopic());

    Assert.assertTrue("roles of correct type not found",
               assoc.getRolesByType(rtype1).size() == 1);


    builder.makeAssociationRole(assoc, rtype2, builder.makeTopic());
    // builder adds role to assoc

    Assert.assertTrue("role with no type found",
               assoc.getRolesByType(rtype1).size() == 1);

    Assert.assertTrue("role with no type not found",
               assoc.getRolesByType(rtype2).size() == 1);
  }

  @Test
  public void testRoleTypes() {
    Assert.assertTrue("role type set not empty initially",
               assoc.getRoleTypes().size() == 0);

    builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    // builder adds role to assoc

    Assert.assertTrue("the null type is being counted as a role type",
               assoc.getRoleTypes().size() == 1);
        
    TopicIF type = builder.makeTopic();
    builder.makeAssociationRole(assoc, type, builder.makeTopic());

    Assert.assertTrue("role type lost",
               assoc.getRoleTypes().size() == 2);

    Assert.assertTrue("role type identity lost",
               assoc.getRoleTypes().contains(type));

    builder.makeAssociationRole(assoc, type, builder.makeTopic());

    Assert.assertTrue("duplicate role types returned",
               assoc.getRoleTypes().size() == 2);
  }

  @Test
  public void testParentTopicMap() {
    Assert.assertTrue("parent topic map is not correct",
               assoc.getTopicMap() == topicmap);
  }
    
  // --- Internal methods

  @Override
  public void setUp() throws Exception {
    super.setUp();
    assoc = builder.makeAssociation(builder.makeTopic());
    object = assoc;
    scoped = assoc;
    typed = assoc;
  }

  @Override
  protected TMObjectIF makeObject() {
    return builder.makeAssociation(builder.makeTopic());
  }
    
}


// $Id: AssociationTest.java,v 1.17 2008/05/23 09:24:21 geir.gronmo Exp $

package net.ontopia.topicmaps.core.test;

import junit.framework.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;

public class AssociationTest extends AbstractTypedScopedTest {
  protected AssociationIF assoc;
  
  public AssociationTest(String name) {
    super(name);
  }
    
  // --- Test cases

	public void testReification() {
		TopicIF reifier = builder.makeTopic();
		ReifiableIF reifiable = assoc;

    assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    assertTrue("Topic reifying the reifiable was found",
							 reifiable.getReifier() == null);

		reifiable.setReifier(reifier);
    assertTrue("No topic reifying the reifiable was found",
							 reifiable.getReifier() == reifier);
    assertTrue("No object reified by the reifying topic was found",
							 reifier.getReified() == reifiable);

		reifiable.setReifier(null);
    assertTrue("Object reified by the reifying topic was found",
							 reifier.getReified() == null);
    assertTrue("Topic reifying the first reifiable was found",
							 reifiable.getReifier() == null);
	}

  public void testRoles() {
    // STATE 1: no roles
    assertTrue("role set not empty initially", assoc.getRoles().size() == 0);

    // STATE 2: one child role
    AssociationRoleIF role = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    // builder should add it to the assoc

    assertTrue("role not added to child role set",
               assoc.getRoles().size() == 1);

    assertTrue("role identity not retained",
               assoc.getRoles().iterator().next().equals(role));

    // STATE 3: no child roles again
    role.remove();
        
    assertTrue("role not removed", assoc.getRoles().size() == 0);

    // checking that it's safe
    role.remove();
  }

  public void testFourRoles() {
    // STATE 1: no roles
    assertTrue("role set not empty initially", assoc.getRoles().size() == 0);

    // STATE 2: four child roles
    TopicIF type = builder.makeTopic();
    TopicIF player = builder.makeTopic();
    AssociationRoleIF role = builder.makeAssociationRole(assoc, type, player);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, type, player);
    AssociationRoleIF role3 = builder.makeAssociationRole(assoc, type, player);
    AssociationRoleIF role4 = builder.makeAssociationRole(assoc, type, player);

    assertTrue("roles not added to child role set",
               assoc.getRoles().size() == 4);
    assertTrue("roles not added to player's role set",
               player.getRoles().size() == 4);

    // STATE 3: no child roles again
    role.remove();
    role2.remove();
    role3.remove();
    role4.remove();
        
    assertTrue("roles not removed", assoc.getRoles().size() == 0);
    assertTrue("roles not removed from player's role set",
               player.getRoles().size() == 0);

    // checking that it's safe
    role.remove();
  }
  
  public void testRolesByType() {
    TopicIF rtype1 = builder.makeTopic();
    TopicIF rtype2 = builder.makeTopic();
        
    assertTrue("roles by non-existent type initially not empty",
               assoc.getRolesByType(rtype1).size() == 0);

    AssociationRoleIF role = builder.makeAssociationRole(assoc, rtype1, builder.makeTopic());

    assertTrue("roles of correct type not found",
               assoc.getRolesByType(rtype1).size() == 1);


    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, rtype2, builder.makeTopic());
    // builder adds role to assoc

    assertTrue("role with no type found",
               assoc.getRolesByType(rtype1).size() == 1);

    assertTrue("role with no type not found",
               assoc.getRolesByType(rtype2).size() == 1);
  }

  public void testRoleTypes() {
    assertTrue("role type set not empty initially",
               assoc.getRoleTypes().size() == 0);

    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());
    // builder adds role to assoc

    assertTrue("the null type is being counted as a role type",
               assoc.getRoleTypes().size() == 1);
        
    TopicIF type = builder.makeTopic();
    AssociationRoleIF role = builder.makeAssociationRole(assoc, type, builder.makeTopic());

    assertTrue("role type lost",
               assoc.getRoleTypes().size() == 2);

    assertTrue("role type identity lost",
               assoc.getRoleTypes().contains(type));

    AssociationRoleIF role3 = builder.makeAssociationRole(assoc, type, builder.makeTopic());

    assertTrue("duplicate role types returned",
               assoc.getRoleTypes().size() == 2);
  }

  public void testParentTopicMap() {
    assertTrue("parent topic map is not correct",
               assoc.getTopicMap() == topicmap);
  }
    
  // --- Internal methods

  public void setUp() {
    super.setUp();
    assoc = builder.makeAssociation(builder.makeTopic());
    object = assoc;
    scoped = assoc;
    typed = assoc;
  }

  protected TMObjectIF makeObject() {
    return builder.makeAssociation(builder.makeTopic());
  }
    
}


// $Id: TypeHierarchyTest.java,v 1.18 2008/05/23 09:24:23 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.*;

public class TypeHierarchyTest extends AbstractUtilsTestCase {
  public TypeHierarchyTest(String name) {
    super(name);
  }

  public void setUp() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapBuilderIF builder = store.getTopicMap().getBuilder();
    tm = store.getTopicMap();
  }

  public void testOne() {
    readFile("types.xtm");
        
    TopicIF kal = getTopic("kal");
    TopicIF musician = getTopic("musician");
    TopicIF living_thing = getTopic("living-thing");
    TopicIF thing = getTopic("thing");

    TypeHierarchyUtils u = new TypeHierarchyUtils();
    assertTrue(u.isInstanceOf(kal, musician));
    assertTrue(u.isInstanceOf(kal, living_thing));
    assertTrue(u.isInstanceOf(kal, thing));

    Collection c = u.getSuperclasses(musician);
    assertTrue(c.size() == 4);

    c = u.getSuperclasses(kal);
    assertTrue(c.size() == 0);
            
    c = u.getSubclasses(thing);
    assertTrue(c.size() == 5);
        
    c = u.getSupertypes(kal);
    assertTrue("Expected 5 supertypes for 'kal'. Found: " + String.valueOf(c.size()),
           c.size() == 5);

    c = u.getSupertypes(musician);
    assertTrue(c.size() == 0);
  }

  public void testIsAssociatedWith() {
    TopicMapBuilderIF builder = tm.getBuilder();

    TopicIF topic1 = builder.makeTopic();
    TopicIF topic2 = builder.makeTopic();

    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), topic1);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), topic2);

    TypeHierarchyUtils u = new TypeHierarchyUtils();
    assertTrue("failed to find topics associated with each other",
           u.isAssociatedWith(topic1, topic2));
  }

  public void testIsAssociatedWithNull() {
    TopicMapBuilderIF builder = tm.getBuilder();

    TopicIF topic1 = builder.makeTopic();
    TopicIF topic2 = builder.makeTopic();

    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), topic1);
    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), builder.makeTopic());

    TypeHierarchyUtils u = new TypeHierarchyUtils();
    assertTrue("found false positive",
           !u.isAssociatedWith(topic1, topic2));
  }
  
}

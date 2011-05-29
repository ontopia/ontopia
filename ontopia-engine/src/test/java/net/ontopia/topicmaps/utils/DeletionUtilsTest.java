
package net.ontopia.topicmaps.utils;

import java.io.*;
import java.util.*;
import junit.framework.TestCase;

import net.ontopia.utils.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

public class DeletionUtilsTest extends TestCase {

  public void setUp() {
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    return store.getTopicMap();
  }

  // --- Test cases

  public void testTopicDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    morituri.remove();

    assertTrue("Topic still connected to topic map",
               morituri.getTopicMap() == null);
    assertTrue("Topic map not empty", topicmap.getTopics().isEmpty());
  }

  public void testTopicTypeDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    TopicIF instance = builder.makeTopic(morituri);

    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map not empty", topicmap.getTopics().size() == 0);
  }

  public void testTopicAssociationRolePlayerDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), morituri);

    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), other);

    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map has too many topics", topicmap.getTopics().size() == 4);
    assertTrue("Role still part of topic map", role1.getTopicMap() == null);
    assertTrue("other still has role", other.getRoles().size() == 0);
    assertTrue("Topic map lost association", topicmap.getAssociations().size() == 0);
  }

  public void testTopicAssociationDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();

    TopicIF morituri = builder.makeTopic();
    TopicIF other = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(builder.makeTopic());
    AssociationRoleIF role1 = builder.makeAssociationRole(assoc, builder.makeTopic(), morituri);

    AssociationRoleIF role2 = builder.makeAssociationRole(assoc, builder.makeTopic(), other);

    morituri.remove();

    assertTrue("Topic still connected to topic map", morituri.getTopicMap() == null);
    assertTrue("Topic map has too many topics", topicmap.getTopics().size() == 4);
    assertTrue("Role 1 still connected to topic map", role1.getTopicMap() == null);
    assertTrue("Role 2 still connected to topic map", role2.getTopicMap() == null);
    assertTrue("Association still connected to topic map", assoc.getTopicMap() == null);
    assertTrue("Topic map still has association", topicmap.getAssociations().size() == 0);
  }
  
}

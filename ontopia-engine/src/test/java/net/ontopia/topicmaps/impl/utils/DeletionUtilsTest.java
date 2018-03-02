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

package net.ontopia.topicmaps.impl.utils;

import java.util.Collections;
import junit.framework.TestCase;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

public class DeletionUtilsTest extends TestCase {

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
  
  public void testScopingTopicDeletion() {
    TopicMapIF topicmap = makeTopicMap();
    TopicMapBuilderIF builder = topicmap.getBuilder();
    TopicIF morituri = builder.makeTopic();
    TopicIF other = builder.makeTopic();

    // association 1 and 2: arbitrary AT, player and RT
    AssociationIF assoc = builder.makeAssociation(other);
    builder.makeAssociationRole(assoc, other, other);
    
    // occurrence with arbitrary type and topic
    OccurrenceIF occurrence = builder.makeOccurrence(other, other, "foo");
    
    // name with arbitrary topic
    TopicNameIF name = builder.makeTopicName(other, "foo");
    
    // variant with arbitrary name
    VariantNameIF variant = builder.makeVariantName(
            builder.makeTopicName(other, "bar"), 
            "baz", Collections.singleton(morituri));
    
    // scope all with mortituri
    assoc.addTheme(morituri);
    occurrence.addTheme(morituri);
    name.addTheme(morituri);

    // remove topic 
    morituri.remove();
    
    // remainder: 2 topics: other, default-name-type
    assertEquals("Incorrect number of topics after removal", 2, topicmap.getTopics().size());
    assertTrue("Incorrect number of association after removal", topicmap.getAssociations().isEmpty());
    assertNull("Association bound to topicmap after removal", assoc.getTopicMap());
    assertNull("Occurrence bound to topicmap after removal", occurrence.getTopicMap());
    assertNull("Topic name bound to topicmap after removal", name.getTopicMap());
    assertNull("Variant name bound to topicmap after removal", variant.getTopicMap());
  }
}

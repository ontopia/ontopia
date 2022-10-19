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

package net.ontopia.topicmaps.core.index;

import java.util.Collections;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.PSI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public abstract class ClassInstanceIndexTest extends AbstractIndexTest {
  
  protected ClassInstanceIndexIF clsix;
  protected TopicIF type;

  @Override
  @Before
  public void setUp() throws Exception {
    clsix = (ClassInstanceIndexIF) super.setUp("ClassInstanceIndexIF");
    type = builder.makeTopic();
  }
  
  @Test
  public void testEmptyTypesIndexes() {
    Assert.assertTrue("AssociationRoleTypes not empty.", clsix.getAssociationRoleTypes().isEmpty());
    Assert.assertTrue("AssociationTypes not empty.", clsix.getAssociationTypes().isEmpty());
    Assert.assertTrue("OccurrenceTypes not empty", clsix.getOccurrenceTypes().isEmpty());
    Assert.assertTrue("TopicTypes not empty", clsix.getTopicTypes().isEmpty());
  }

  @Test
  public void testTopicTypes() {
    // STATE 1: empty topic map
    Assert.assertTrue("index finds spurious (or most likely no) topic types",
           clsix.getTopics(null).size() == 1);

    /* This test cannot be performed as the type topic is already
       created and it has a null type.
    Assert.assertTrue("null used as topic type in empty topic map",
           !clsix.usedAsTopicType(null));
    */
    Assert.assertTrue("index finds topic types in empty topic map",
           clsix.getTopicTypes().size() == 0);

    // STATE 2: untyped topic
    TopicIF inst = builder.makeTopic();

    Assert.assertTrue("Found topic type when none expected.", clsix.getTopicTypes().size() == 0);
    Assert.assertTrue("<type> incorrectly indexed as a topic type.", !clsix.usedAsTopicType(type));
    Assert.assertTrue("Expected no topics of type <type>", clsix.getTopics(type).size() == 0);
    Assert.assertTrue("Expected <type> to not be used as type.", !clsix.usedAsType(type));
    Assert.assertTrue("Expected <inst> to be indexed with null type.", clsix.getTopics(null).contains(inst));

    // STATE 3: typed topic
    inst.addType(type);

    Assert.assertTrue("Expected one topic type.", clsix.getTopicTypes().size() == 1);
    Assert.assertTrue("<type> not indexed as topic type.", clsix.getTopicTypes().contains(type));
    Assert.assertTrue("<type> not indexed as topic type.", clsix.usedAsTopicType(type));
    Assert.assertTrue("Expected one topic of type <type>", clsix.getTopics(type).size() == 1);
    Assert.assertTrue("Expected <inst> as instance of <type>", clsix.getTopics(type).contains(inst));
    Assert.assertTrue("Expected <type> to be used as type.", clsix.usedAsType(type));

    // STATE 4: untyped topic (via type removal)
    inst.removeType(type);

    Assert.assertTrue("Found topic type when none expected.", clsix.getTopicTypes().size() == 0);
    Assert.assertTrue("<type> incorrectly indexed as a topic type.", !clsix.usedAsTopicType(type));
    Assert.assertTrue("Expected no topics of type <type>", clsix.getTopics(type).size() == 0);
    Assert.assertTrue("Expected <type> to not be used as type.", !clsix.usedAsType(type));
    Assert.assertTrue("Expected <inst> to be indexed with null type.", clsix.getTopics(null).contains(inst));

    // STATE 5: duplicate typed topic
    TopicIF dup = builder.makeTopic();
    dup.addType(type);

    Assert.assertTrue("topic type not found",
           clsix.getTopics(type).size() == 1);
    Assert.assertTrue("topic not found via type",
           clsix.getTopics(type).contains(dup));
    Assert.assertTrue("duplicate topic types not suppressed",
           clsix.getTopicTypes().size() == 1);
  }

  @Test
  public void testAssociationTypes() {
    // STATE 1: empty topic map
    Assert.assertTrue("index finds role types in empty topic map",
           clsix.getAssociationTypes().size() == 0);
    Assert.assertTrue("index finds role types in empty topic map",
           clsix.getAssociationRoleTypes().size() == 0);

    // Create untyped association and association role
    AssociationIF inst = builder.makeAssociation(type);
    AssociationRoleIF role = builder.makeAssociationRole(inst, type, type);

    // STATE 2: Topic map contains untyped association and association role
    Assert.assertTrue("Found association type when none expected.", 
           clsix.getAssociationTypes().size() == 1);
    Assert.assertTrue("<type> incorrectly indexed as an association type.", 
           clsix.usedAsAssociationType(type));
    Assert.assertTrue("Expected one associations of type <type>", 
           clsix.getAssociations(type).size() == 1);

    Assert.assertTrue("Expected one role type.", 
           clsix.getAssociationRoleTypes().size() == 1);
    Assert.assertTrue("<type> not indexed as an association role type.", 
           clsix.usedAsAssociationRoleType(type));

    inst.setType(type);
    role.setType(type);

    // STATE 3: Topic map contains typed association and association role
    Assert.assertTrue("Expected one association type.", 
           clsix.getAssociationTypes().size() == 1);
    Assert.assertTrue("<type> not indexed as association type.",
           clsix.getAssociationTypes().contains(type));
    Assert.assertTrue("<type> not indexed as association type.", 
           clsix.usedAsAssociationType(type));
    Assert.assertTrue("Expected one association of type <type>", 
           clsix.getAssociations(type).size() == 1);
    Assert.assertTrue("Expected <inst> as instance of <type>", 
           clsix.getAssociations(type).contains(inst));

    Assert.assertTrue("Expected one association role type.", 
           clsix.getAssociationRoleTypes().size() == 1);
    Assert.assertTrue("<type> not indexed as association role type.",
           clsix.getAssociationRoleTypes().contains(type));
    Assert.assertTrue("<type> not indexed as association role type.", 
           clsix.usedAsAssociationRoleType(type));
    Assert.assertTrue("Expected one association role of type <type>", 
           clsix.getAssociationRoles(type).size() == 1);
    Assert.assertTrue("Expected <role> as instance of <type>", 
           clsix.getAssociationRoles(type).contains(role));
    Assert.assertTrue("Expected one association role of type <type> with at of <type>", 
           clsix.getAssociationRoles(type, type).size() == 1);
    Assert.assertTrue("Expected <role> as instance of <type> with at of <type>", 
           clsix.getAssociationRoles(type, type).contains(role));

    // STATE 4: Topic map has duplicates
    AssociationIF dup = builder.makeAssociation(type);
    AssociationRoleIF dupRole = builder.makeAssociationRole(dup, type , type);
    
    Assert.assertTrue("assoc type not found",
           clsix.getAssociations(type).size() == 2);
    Assert.assertTrue("assoc not found via type",
           clsix.getAssociations(type).contains(dup));
    Assert.assertTrue("duplicate assoc types not suppressed",
           clsix.getAssociationTypes().size() == 1);

    Assert.assertTrue("role type not found", 
           clsix.getAssociationRoles(type).size() == 2);
    Assert.assertTrue("roles not found via type",
           clsix.getAssociationRoles(type).contains(dupRole));
    Assert.assertTrue("duplicate role types not suppressed",
           clsix.getAssociationRoleTypes().size() == 1);

    Assert.assertTrue("role type not found", 
           clsix.getAssociationRoles(type, type).size() == 2);
    Assert.assertTrue("roles not found via type",
           clsix.getAssociationRoles(type, type).contains(dupRole));
  }

  @Test
  public void testOccurrenceTypes() {
    // STATE 1: empty topic map
    Assert.assertTrue("index finds occurrence types in empty topic map",
           clsix.getOccurrenceTypes().size() == 0);
    
    TopicIF topic = builder.makeTopic();
    OccurrenceIF inst = builder.makeOccurrence(topic, type, "");
        
    // STATE 3: Contains typed occurrences
    inst.setType(type);

    Assert.assertTrue("Expected one occurrence type.", 
           clsix.getOccurrenceTypes().size() == 1);
    Assert.assertTrue("<type> not indexed as occurrence type.",
           clsix.getOccurrenceTypes().contains(type));
    Assert.assertTrue("<type> not indexed as occurrence type.", 
           clsix.usedAsOccurrenceType(type));
    Assert.assertTrue("Expected one occurrence of type <type>", 
           clsix.getOccurrences(type).size() == 1);
    Assert.assertTrue("Expected <inst> as instance of <type>", 
           clsix.getOccurrences(type).contains(inst));

    // STATE 4: Contains duplicate type occurrence
    OccurrenceIF dup = builder.makeOccurrence(topic, type, "");

    Assert.assertTrue("occ type not found",
           clsix.getOccurrences(type).size() == 2);
    Assert.assertTrue("occ not found via type",
           clsix.getOccurrences(type).contains(dup));
    Assert.assertTrue("duplicate occ types not suppressed",
           clsix.getOccurrenceTypes().size() == 1);    
  }

  @Test
  public void testTopicNameTypes() {
    
    // STATE 1: empty topic map
    Assert.assertTrue("index finds spurious basename types",
           clsix.getTopicNames(null).size() == 0);
    
    Assert.assertTrue("null used as basename type in empty topic map",
           !clsix.usedAsTopicNameType(null));
    
    Assert.assertTrue("index finds basename types in empty topic map",
           clsix.getTopicNameTypes().size() == 0);
    
    // STATE 2: Contains basenames with default name type
    TopicIF topic = builder.makeTopic();
    TopicNameIF inst = builder.makeTopicName(topic, "");

    TopicIF defaultNameType = topicmap.getTopicBySubjectIdentifier(PSI
        .getSAMNameType());
    
    Assert.assertTrue("not exactly one default name type basename",
           clsix.getTopicNames(defaultNameType).size() == 1);

    Assert.assertTrue("Found no basename type when one expected.", 
           clsix.getTopicNameTypes().size() == 1);
    Assert.assertTrue("<type> incorrectly indexed as an basename type.", 
           !clsix.usedAsTopicNameType(type));
    Assert.assertTrue("Expected no basenames of type <type>", 
           clsix.getTopicNames(type).size() == 0);
    Assert.assertTrue("Expected <inst> to be indexed with default name type.", 
           clsix.getTopicNames(defaultNameType).contains(inst));
        
    // STATE 3: Contains typed basenames
    inst.setType(type);

    Assert.assertTrue("Expected one basename type.", 
           clsix.getTopicNameTypes().size() == 1);
    Assert.assertTrue("<type> not indexed as basename type.",
           clsix.getTopicNameTypes().contains(type));
    Assert.assertTrue("<type> not indexed as basename type.", 
           clsix.usedAsTopicNameType(type));
    Assert.assertTrue("Expected one basename of type <type>", 
           clsix.getTopicNames(type).size() == 1);
    Assert.assertTrue("Expected <inst> as instance of <type>", 
           clsix.getTopicNames(type).contains(inst));

    // STATE 4: Contains duplicate type basename
    TopicNameIF dup = builder.makeTopicName(topic, "");
    dup.setType(type);

    Assert.assertTrue("basename type not found",
           clsix.getTopicNames(type).size() == 2);
    Assert.assertTrue("basename not found via type",
           clsix.getTopicNames(type).contains(dup));
    Assert.assertTrue("duplicate basename types not suppressed",
           clsix.getTopicNameTypes().size() == 1);

    // STATE 5: Reset type
    dup.setType(null);

    Assert.assertTrue("not exactly one untyped basename",
           clsix.getTopicNames(defaultNameType).size() == 1);
    
  }

  @Test
  public void testAllTopicNames() {
    Assert.assertEquals(0, clsix.getAllTopicNames().size());
    
    TopicIF topic = builder.makeTopic();
    TopicNameIF tn = builder.makeTopicName(topic, "foo");
    TopicNameIF tn2 = builder.makeTopicName(topic, topic, "bar");
    Assert.assertEquals(2, clsix.getAllTopicNames().size());
    Assert.assertTrue(clsix.getAllTopicNames().contains(tn));
    Assert.assertTrue(clsix.getAllTopicNames().contains(tn2));
    
    tn.setType(topic);
    Assert.assertEquals(2, clsix.getAllTopicNames().size());
    
    tn.remove();
    Assert.assertEquals(1, clsix.getAllTopicNames().size());
    Assert.assertFalse(clsix.getAllTopicNames().contains(tn));
    
    topic.remove();
    Assert.assertEquals(0, clsix.getAllTopicNames().size());
    Assert.assertFalse(clsix.getAllTopicNames().contains(tn2));
  }

  @Test
  public void testAllVariantNames() {
    Assert.assertEquals(0, clsix.getAllVariantNames().size());
    
    TopicIF topic = builder.makeTopic();
    TopicNameIF tn = builder.makeTopicName(topic, "foo");
    VariantNameIF vn = builder.makeVariantName(tn, "bar", Collections.singleton(topic));
    VariantNameIF vn2 = builder.makeVariantName(tn, "bar2", Collections.singleton(topic));
    Assert.assertEquals(2, clsix.getAllVariantNames().size());
    Assert.assertTrue(clsix.getAllVariantNames().contains(vn));
    Assert.assertTrue(clsix.getAllVariantNames().contains(vn2));
    
    vn2.remove();
    Assert.assertEquals(1, clsix.getAllVariantNames().size());
    Assert.assertFalse(clsix.getAllVariantNames().contains(vn2));
    
    tn.remove();
    Assert.assertEquals(0, clsix.getAllVariantNames().size());
    Assert.assertFalse(clsix.getAllVariantNames().contains(vn));
  }

  @Test
  public void testAllOccurrences() {
    Assert.assertEquals(0, clsix.getAllOccurrences().size());
    
    TopicIF topic = builder.makeTopic();
    
    OccurrenceIF o = builder.makeOccurrence(topic, topic, "foo");
    OccurrenceIF o2 = builder.makeOccurrence(topic, topic, "bar");
    Assert.assertEquals(2, clsix.getAllOccurrences().size());
    Assert.assertTrue(clsix.getAllOccurrences().contains(o));
    Assert.assertTrue(clsix.getAllOccurrences().contains(o2));
    
    o.remove();
    Assert.assertEquals(1, clsix.getAllOccurrences().size());
    Assert.assertFalse(clsix.getAllOccurrences().contains(o));
    
    topic.remove();
    Assert.assertEquals(0, clsix.getAllOccurrences().size());
    Assert.assertFalse(clsix.getAllOccurrences().contains(o2));
  }

  @Test
  public void testBug1438_basenames() {

    Assert.assertTrue("index finds spurious occurrence types",
           clsix.getTopicNames(null).size() == 0);
    
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();

    builder.makeTopicName(topic, otype, "foo");

    Assert.assertTrue("index finds spurious basename types",
           clsix.getTopicNames(null).size() == 0);

    TopicNameIF bn2 = builder.makeTopicName(topic, otype, "foo");

    bn2.remove();
    Assert.assertTrue("index finds spurious basename types",
           clsix.getTopicNames(null).size() == 0);    
  }

  @Test
  public void testBug1438_topics() {
    Assert.assertTrue("index finds spurious topics (0)",
           clsix.getTopics(null).size() == 1);
    
    TopicIF topic = builder.makeTopic();
    
    Assert.assertTrue("index finds spurious topics (1)",
           clsix.getTopics(null).size() == 2);

    topic.addType(type);

    Assert.assertTrue("index finds spurious topics (2)",
           clsix.getTopics(null).size() == 1);

    Assert.assertTrue("index finds spurious topics (3)",
           clsix.getTopics(type).size() == 1);

    topic.remove();

    Assert.assertTrue("index finds spurious topics (4)",
           clsix.getTopics(null).size() == 1);

    Assert.assertTrue("index finds spurious topics (5)",
           clsix.getTopics(type).size() == 0);

  }

  /*
    Bug 510: Tests for differences in index implementations between basic and rmdbs 
    regarding null values for occurrence, name, role and association type.
  */
  
  @Test
  public void testBug510_N_default() {
    Assert.assertEquals("Index finds spurious names", 0, clsix.getTopicNames(null).size());
    builder.makeTopicName(type, "");
    Assert.assertEquals("Index does not find default name type names", 1, clsix.getTopicNames(null).size());
  }

  @Test
  public void testBug510_N_notDefault() {
    Assert.assertEquals("Index finds spurious names", 0, clsix.getTopicNames(null).size());
    builder.makeTopicName(type, type, "");
    Assert.assertEquals("Index finds names with wrong type", 0, clsix.getTopicNames(null).size());
  }

  @Test
  public void testBug510_O() {
    Assert.assertEquals("Index finds spurious occurrences", 0, clsix.getOccurrences(null).size());
    builder.makeOccurrence(type, type, "");
    Assert.assertEquals("Index finds spurious occurrences", 0, clsix.getOccurrences(null).size());
  }
  
  @Test
  public void testBug510_A() {
    Assert.assertEquals("Index finds spurious associations", 0, clsix.getAssociations(null).size());
    builder.makeAssociation(type);
    Assert.assertEquals("Index finds spurious associations", 0, clsix.getAssociations(null).size());
  }
  
  @Test
  public void testBug510_R() {
    Assert.assertEquals("Index finds spurious roles", 0, clsix.getAssociationRoles(null).size());
    builder.makeAssociationRole(builder.makeAssociation(type), type, type);
    Assert.assertEquals("Index finds spurious roles", 0, clsix.getAssociationRoles(null).size());
  }

  @Test
  public void testBug536() {
    Assert.assertFalse(clsix.usedAsAssociationRoleType(null));
    Assert.assertFalse(clsix.usedAsAssociationType(null));
    Assert.assertFalse(clsix.usedAsOccurrenceType(null));
    Assert.assertFalse(clsix.usedAsTopicNameType(null));
    Assert.assertFalse(clsix.usedAsTopicType(null));
    Assert.assertFalse(clsix.usedAsType(null));
  }
}

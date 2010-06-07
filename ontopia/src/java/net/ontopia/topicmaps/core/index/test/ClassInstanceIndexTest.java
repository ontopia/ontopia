
// $Id: ClassInstanceIndexTest.java,v 1.23 2008/06/12 14:37:13 geir.gronmo Exp $

package net.ontopia.topicmaps.core.index.test;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.utils.PSI;

public class ClassInstanceIndexTest extends AbstractIndexTest {
  
  protected ClassInstanceIndexIF clsix;
  protected TopicIF type;

  public ClassInstanceIndexTest(String name) {
    super(name);
  }

  protected void setUp() {
    clsix = (ClassInstanceIndexIF) super.setUp("ClassInstanceIndexIF");
    type = builder.makeTopic();
  }

  public void testTopicTypes() {
    // STATE 1: empty topic map
    assertTrue("index finds spurious (or most likely no) topic types",
           clsix.getTopics(null).size() == 1);

    /* This test cannot be performed as the type topic is already
       created and it has a null type.
    assertTrue("null used as topic type in empty topic map",
           !clsix.usedAsTopicType(null));
    */
    assertTrue("index finds topic types in empty topic map",
           clsix.getTopicTypes().size() == 0);

    // STATE 2: untyped topic
    TopicIF inst = builder.makeTopic();

    assertTrue("Found topic type when none expected.", clsix.getTopicTypes().size() == 0);
    assertTrue("<type> incorrectly indexed as a topic type.", !clsix.usedAsTopicType(type));
    assertTrue("Expected no topics of type <type>", clsix.getTopics(type).size() == 0);
    assertTrue("Expected <type> to not be used as type.", !clsix.usedAsType(type));
    assertTrue("Expected <inst> to be indexed with null type.", clsix.getTopics(null).contains(inst));

    // STATE 3: typed topic
    inst.addType(type);

    assertTrue("Expected one topic type.", clsix.getTopicTypes().size() == 1);
    assertTrue("<type> not indexed as topic type.", clsix.getTopicTypes().contains(type));
    assertTrue("<type> not indexed as topic type.", clsix.usedAsTopicType(type));
    assertTrue("Expected one topic of type <type>", clsix.getTopics(type).size() == 1);
    assertTrue("Expected <inst> as instance of <type>", clsix.getTopics(type).contains(inst));
    assertTrue("Expected <type> to be used as type.", clsix.usedAsType(type));

    // STATE 4: untyped topic (via type removal)
    inst.removeType(type);

    assertTrue("Found topic type when none expected.", clsix.getTopicTypes().size() == 0);
    assertTrue("<type> incorrectly indexed as a topic type.", !clsix.usedAsTopicType(type));
    assertTrue("Expected no topics of type <type>", clsix.getTopics(type).size() == 0);
    assertTrue("Expected <type> to not be used as type.", !clsix.usedAsType(type));
    assertTrue("Expected <inst> to be indexed with null type.", clsix.getTopics(null).contains(inst));

    // STATE 5: duplicate typed topic
    TopicIF dup = builder.makeTopic();
    dup.addType(type);

    assertTrue("topic type not found",
           clsix.getTopics(type).size() == 1);
    assertTrue("topic not found via type",
           clsix.getTopics(type).contains(dup));
    assertTrue("duplicate topic types not suppressed",
           clsix.getTopicTypes().size() == 1);
  }

  public void testAssociationTypes() {
    // STATE 1: empty topic map
    assertTrue("index finds role types in empty topic map",
           clsix.getAssociationTypes().size() == 0);
    assertTrue("index finds role types in empty topic map",
           clsix.getAssociationRoleTypes().size() == 0);

    // Create untyped association and association role
    AssociationIF inst = builder.makeAssociation(type);
    AssociationRoleIF role = builder.makeAssociationRole(inst, type, type);

    // STATE 2: Topic map contains untyped association and association role
    assertTrue("Found association type when none expected.", 
           clsix.getAssociationTypes().size() == 1);
    assertTrue("<type> incorrectly indexed as an association type.", 
           clsix.usedAsAssociationType(type));
    assertTrue("Expected one associations of type <type>", 
           clsix.getAssociations(type).size() == 1);

    assertTrue("Expected one role type.", 
           clsix.getAssociationRoleTypes().size() == 1);
    assertTrue("<type> not indexed as an association role type.", 
           clsix.usedAsAssociationRoleType(type));

    inst.setType(type);
    role.setType(type);

    // STATE 3: Topic map contains typed association and association role
    assertTrue("Expected one association type.", 
           clsix.getAssociationTypes().size() == 1);
    assertTrue("<type> not indexed as association type.",
           clsix.getAssociationTypes().contains(type));
    assertTrue("<type> not indexed as association type.", 
           clsix.usedAsAssociationType(type));
    assertTrue("Expected one association of type <type>", 
           clsix.getAssociations(type).size() == 1);
    assertTrue("Expected <inst> as instance of <type>", 
           clsix.getAssociations(type).contains(inst));

    assertTrue("Expected one association role type.", 
           clsix.getAssociationRoleTypes().size() == 1);
    assertTrue("<type> not indexed as association role type.",
           clsix.getAssociationRoleTypes().contains(type));
    assertTrue("<type> not indexed as association role type.", 
           clsix.usedAsAssociationRoleType(type));
    assertTrue("Expected one association role of type <type>", 
           clsix.getAssociationRoles(type).size() == 1);
    assertTrue("Expected <role> as instance of <type>", 
           clsix.getAssociationRoles(type).contains(role));

    // STATE 4: Topic map has duplicates
    AssociationIF dup = builder.makeAssociation(type);
    AssociationRoleIF dupRole = builder.makeAssociationRole(dup, type , type);
    
    assertTrue("assoc type not found",
           clsix.getAssociations(type).size() == 2);
    assertTrue("assoc not found via type",
           clsix.getAssociations(type).contains(dup));
    assertTrue("duplicate assoc types not suppressed",
           clsix.getAssociationTypes().size() == 1);

    assertTrue("role type not found", 
           clsix.getAssociationRoles(type).size() == 2);
    assertTrue("roles not found via type",
           clsix.getAssociationRoles(type).contains(dupRole));
    assertTrue("duplicate role types not suppressed",
           clsix.getAssociationRoleTypes().size() == 1);
  }

  public void testOccurrenceTypes() {
    // STATE 1: empty topic map
    assertTrue("index finds occurrence types in empty topic map",
           clsix.getOccurrenceTypes().size() == 0);
    
    TopicIF topic = builder.makeTopic();
    OccurrenceIF inst = builder.makeOccurrence(topic, type, "");
        
    // STATE 3: Contains typed occurrences
    inst.setType(type);

    assertTrue("Expected one occurrence type.", 
           clsix.getOccurrenceTypes().size() == 1);
    assertTrue("<type> not indexed as occurrence type.",
           clsix.getOccurrenceTypes().contains(type));
    assertTrue("<type> not indexed as occurrence type.", 
           clsix.usedAsOccurrenceType(type));
    assertTrue("Expected one occurrence of type <type>", 
           clsix.getOccurrences(type).size() == 1);
    assertTrue("Expected <inst> as instance of <type>", 
           clsix.getOccurrences(type).contains(inst));

    // STATE 4: Contains duplicate type occurrence
    OccurrenceIF dup = builder.makeOccurrence(topic, type, "");

    assertTrue("occ type not found",
           clsix.getOccurrences(type).size() == 2);
    assertTrue("occ not found via type",
           clsix.getOccurrences(type).contains(dup));
    assertTrue("duplicate occ types not suppressed",
           clsix.getOccurrenceTypes().size() == 1);    
  }

  public void testTopicNameTypes() {
    
    // STATE 1: empty topic map
    assertTrue("index finds spurious basename types",
           clsix.getTopicNames(null).size() == 0);
    
    assertTrue("null used as basename type in empty topic map",
           !clsix.usedAsTopicNameType(null));
    
    assertTrue("index finds basename types in empty topic map",
           clsix.getTopicNameTypes().size() == 0);
    
    // STATE 2: Contains basenames with default name type
    TopicIF topic = builder.makeTopic();
    TopicNameIF inst = builder.makeTopicName(topic, "");

    TopicIF defaultNameType = topicMap.getTopicBySubjectIdentifier(PSI
        .getSAMNameType());
    
    assertTrue("not exactly one default name type basename",
           clsix.getTopicNames(defaultNameType).size() == 1);

    assertTrue("Found no basename type when one expected.", 
           clsix.getTopicNameTypes().size() == 1);
    assertTrue("<type> incorrectly indexed as an basename type.", 
           !clsix.usedAsTopicNameType(type));
    assertTrue("Expected no basenames of type <type>", 
           clsix.getTopicNames(type).size() == 0);
    assertTrue("Expected <inst> to be indexed with default name type.", 
           clsix.getTopicNames(defaultNameType).contains(inst));
        
    // STATE 3: Contains typed basenames
    inst.setType(type);

    assertTrue("Expected one basename type.", 
           clsix.getTopicNameTypes().size() == 1);
    assertTrue("<type> not indexed as basename type.",
           clsix.getTopicNameTypes().contains(type));
    assertTrue("<type> not indexed as basename type.", 
           clsix.usedAsTopicNameType(type));
    assertTrue("Expected one basename of type <type>", 
           clsix.getTopicNames(type).size() == 1);
    assertTrue("Expected <inst> as instance of <type>", 
           clsix.getTopicNames(type).contains(inst));

    // STATE 4: Contains duplicate type basename
    TopicNameIF dup = builder.makeTopicName(topic, "");
    dup.setType(type);

    assertTrue("basename type not found",
           clsix.getTopicNames(type).size() == 2);
    assertTrue("basename not found via type",
           clsix.getTopicNames(type).contains(dup));
    assertTrue("duplicate basename types not suppressed",
           clsix.getTopicNameTypes().size() == 1);

    // STATE 5: Reset type
    dup.setType(null);

    assertTrue("not exactly one untyped basename",
           clsix.getTopicNames(defaultNameType).size() == 1);
    
  }

  public void testBug1438_basenames() {

    assertTrue("index finds spurious occurrence types",
           clsix.getTopicNames(null).size() == 0);
    
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();

    TopicNameIF bn1 = builder.makeTopicName(topic, otype, "foo");

    assertTrue("index finds spurious basename types",
           clsix.getTopicNames(null).size() == 0);

    TopicNameIF bn2 = builder.makeTopicName(topic, otype, "foo");

    bn2.remove();
    assertTrue("index finds spurious basename types",
           clsix.getTopicNames(null).size() == 0);    
  }

  public void testBug1438_topics() {
    assertTrue("index finds spurious topics (0)",
           clsix.getTopics(null).size() == 1);
    
    TopicIF topic = builder.makeTopic();
    
    assertTrue("index finds spurious topics (1)",
           clsix.getTopics(null).size() == 2);

    topic.addType(type);

    assertTrue("index finds spurious topics (2)",
           clsix.getTopics(null).size() == 1);

    assertTrue("index finds spurious topics (3)",
           clsix.getTopics(type).size() == 1);

    topic.remove();

    assertTrue("index finds spurious topics (4)",
           clsix.getTopics(null).size() == 1);

    assertTrue("index finds spurious topics (5)",
           clsix.getTopics(type).size() == 0);

  }

}


// $Id: DuplicateSuppressionUtilsTest.java,v 1.12 2008/06/12 14:37:24 geir.gronmo Exp $

package net.ontopia.topicmaps.utils.test;

import java.util.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.utils.DuplicateSuppressionUtils;

public class DuplicateSuppressionUtilsTest extends AbstractTopicMapTestCase {
  protected TopicMapIF        topicmap; 
  protected TopicMapBuilderIF builder;

  public DuplicateSuppressionUtilsTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap = makeTopicMap();
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
  // --- Test cases

  public void testVariantRemoval() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    VariantNameIF vn = builder.makeVariantName(bn, "duplicate");
    vn = builder.makeVariantName(bn, "duplicate");

    DuplicateSuppressionUtils.removeDuplicates(bn);

    assertTrue("duplicate variant names were not removed",
           bn.getVariants().size() == 1);
  }

  public void testVariantRemovalWithScope() {
    TopicIF theme1 = builder.makeTopic();
    TopicIF theme2 = builder.makeTopic();
    
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    VariantNameIF vn = builder.makeVariantName(bn, "duplicate");
    vn.addTheme(theme1);
    vn.addTheme(theme2);
    vn = builder.makeVariantName(bn, "duplicate");
    vn.addTheme(theme1);
    vn.addTheme(theme2);

    DuplicateSuppressionUtils.removeDuplicates(bn);

    assertTrue("duplicate variant names were not removed",
           bn.getVariants().size() == 1);
  }

// FIXME: does NOT succeed
//   public void testTopicNameRemovalWithScope() {
//     TopicIF theme1 = builder.makeTopic();
//     TopicIF theme2 = builder.makeTopic();
    
//     TopicIF topic = builder.makeTopic();
//     TopicNameIF bn = builder.makeTopicName(topic, "test");
//     bn.addTheme(theme1);
//     bn.addTheme(theme2);
//     VariantNameIF vn = builder.makeVariantName(bn, "not duplicate");
    
//     TopicNameIF bn2 = builder.makeTopicName(topic, "test");
//     bn2.addTheme(theme1);
//     bn2.addTheme(theme2);
//     vn = builder.makeVariantName(bn, "not duplicate, either");

//     DuplicateSuppressionUtils.removeDuplicates(topic);

//     assertTrue("duplicate base names were not removed",
//            topic.getTopicNames().size() == 1);
//     assertTrue("variant names were not merged",
//            bn.getVariants().size() == 2);
//   }
  
// FIXME: does NOT succeed
//   public void testTopicNameAndVariantNameRemovalWithScope() {
//     TopicIF theme1 = builder.makeTopic();
//     TopicIF theme2 = builder.makeTopic();
    
//     TopicIF topic = builder.makeTopic();
//     TopicNameIF bn = builder.makeTopicName(topic, "test");
//     bn.addTheme(theme1);
//     bn.addTheme(theme2);
//     VariantNameIF vn = builder.makeVariantName(bn, "duplicate");
    
//     TopicNameIF bn2 = builder.makeTopicName(topic, "test");
//     bn2.addTheme(theme1);
//     bn2.addTheme(theme2);
//     vn = builder.makeVariantName(bn, "duplicate");

//     DuplicateSuppressionUtils.removeDuplicates(topic);

//     assertTrue("duplicate base names were not removed",
//            topic.getTopicNames().size() == 1);
//     assertTrue("duplicate variant names were not removed",
//            bn.getVariants().size() == 1);
//   }
  
  public void testOccurrenceRemoval() {
    TopicIF type = builder.makeTopic();
    
    TopicIF topic = builder.makeTopic();
    OccurrenceIF occ = builder.makeOccurrence(topic, type, "duplicate");
    
    occ = builder.makeOccurrence(topic, type, "duplicate");

    DuplicateSuppressionUtils.removeDuplicates(topic);

    assertTrue("duplicate occurrence were not removed",
           topic.getOccurrences().size() == 1);
  }

  public void testAssociationRemoval() {
    TopicIF type = builder.makeTopic();
    TopicIF role1 = builder.makeTopic();
    TopicIF role2 = builder.makeTopic();
    TopicIF player1 = builder.makeTopic();
    TopicIF player2 = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(type);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, role1, player1);
    role = builder.makeAssociationRole(assoc, role2, player2);

    assoc = builder.makeAssociation(type);
    role = builder.makeAssociationRole(assoc, role1, player1);
    role = builder.makeAssociationRole(assoc, role2, player2);

    DuplicateSuppressionUtils.removeDuplicates(topicmap);

    assertTrue("duplicate association was not removed",
           topicmap.getAssociations().size() == 1);
  }


  public void testAssociationRoleRemoval() {
    TopicIF type = builder.makeTopic();
    TopicIF role1 = builder.makeTopic();
    TopicIF role2 = builder.makeTopic();
    TopicIF player1 = builder.makeTopic();
    TopicIF player2 = builder.makeTopic();
    
    AssociationIF assoc = builder.makeAssociation(type);
    AssociationRoleIF role = builder.makeAssociationRole(assoc, role1, player1);
    role = builder.makeAssociationRole(assoc, role2, player2);
    role = builder.makeAssociationRole(assoc, role2, player2);

    DuplicateSuppressionUtils.removeDuplicates(topicmap);

    assertTrue("duplicate association role was not removed",
               assoc.getRoles().size() == 2);
  }
}


// $Id: MergeTMTest.java,v 1.33 2009/02/27 12:04:24 lars.garshol Exp $

package net.ontopia.topicmaps.utils.test;

import java.util.Iterator;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.utils.MergeUtils;

public class MergeTMTest extends AbstractTopicMapTestCase {
  protected TopicMapIF    topicmap1; 
  protected TopicMapIF    topicmap2; 
  protected TopicMapBuilderIF builder1;
  protected TopicMapBuilderIF builder2;

  public MergeTMTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap1 = makeTopicMap();
    topicmap2 = makeTopicMap();
    builder1 = topicmap1.getBuilder();
    builder2 = topicmap2.getBuilder();
  }
    
  // intended to be overridden
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    return store.getTopicMap();
  }

  public URILocator makeLocator(String uri) {
    try {
      return new URILocator(uri);
    }
    catch (java.net.MalformedURLException e) {
      fail("malformed URL given", e);
      return null; // never executed...
    }
  }
    
  // --- Test cases for mergeInto(TM, TM)

  public void testEmptyTopicMaps() {
    try {
      MergeUtils.mergeInto(topicmap1, topicmap2);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }
 
  public void testEmptyTopics() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder2.makeTopic();

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("topics lost in merge",
             topicmap1.getTopics().size() == 2);
      assertTrue("original topic lost in merge",
             topicmap1.getTopics().contains(t1));
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }    

  public void testSubjectMerge() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("topics merged incorrectly",
             topicmap1.getTopics().size() == 1);
      assertTrue("original topic lost in merge",
             topicmap1.getTopics().contains(t1));
      assertTrue("original topic subject lost in merge",
             ((TopicIF) topicmap1.getTopics().iterator().next()).getSubjectLocators().contains(makeLocator("http://www.ontopia.net")));
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }    

  public void testSubjectIndicatorMerge() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      t2.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("topics merged incorrectly",
             topicmap1.getTopics().size() == 1);
      assertTrue("original topic lost in merge",
             topicmap1.getTopics().contains(t1));
      assertTrue("topic subject indicator lost in merge",
             ((TopicIF) topicmap1.getTopics().iterator().next()).getSubjectIdentifiers().size() == 2);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }    
    
  public void testTopicIsSubjectIndicatorMerge() {
    TopicIF t1 = builder1.makeTopic();
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t2.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics merged incorrectly",
           topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
           topicmap1.getTopics().contains(t1));

    // NOTE: According to bug #652 it is now allowed for topics to
    // have the same locator in their item identifiers and subject
    // indicators properties. Thus the following test has been updated
    // to check for 2 locators.
    TopicIF topic = (TopicIF) topicmap1.getTopics().iterator().next();
    assertTrue("topic has wrong number of subject identifiers",
               topic.getSubjectIdentifiers().size() == 2);

    // Of course, the item identifier should not be lost
    // http://code.google.com/p/ontopia/issues/detail?id=28
    assertTrue("topic lost item identifier in merge",
               topic.getItemIdentifiers().size() == 1);
  }    

  public void testTopicIsSubjectIndicatorMerge2() {
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t1.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addItemIdentifier(makeLocator("http://www.ontopia.net"));

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics merged incorrectly",
           topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
           topicmap1.getTopics().contains(t1));

    // NOTE: According to bug #652 it is now allowed for topics to
    // have the same locator in their item identifiers and subject
    // indicators properties. Thus the following test has been updated
    // to check for 2 locators.
    TopicIF topic = (TopicIF) topicmap1.getTopics().iterator().next();
    assertTrue("topic has wrong number of subject identifiers",
               topic.getSubjectIdentifiers().size() == 2);

    // Of course, the item identifier should not be lost
    // http://code.google.com/p/ontopia/issues/detail?id=28
    assertTrue("topic lost item identifier in merge",
               topic.getItemIdentifiers().size() == 1);
  }    

  public void testTopicIsSubjectIndicatorMerge3() {
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t1.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addItemIdentifier(makeLocator("http://www.ontopia.net"));

    MergeUtils.mergeInto(topicmap1, t2);

    assertTrue("topics merged incorrectly",
           topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
           topicmap1.getTopics().contains(t1));

    // NOTE: According to bug #652 it is now allowed for topics to
    // have the same locator in their item identifiers and subject
    // indicators properties. Thus the following test has been updated
    // to check for 2 locators.
    TopicIF topic = (TopicIF) topicmap1.getTopics().iterator().next();
    assertTrue("topic has wrong number of subject identifiers",
               topic.getSubjectIdentifiers().size() == 2);

    // Of course, the item identifier should not be lost
    // http://code.google.com/p/ontopia/issues/detail?id=28
    assertTrue("topic lost item identifier in merge",
               topic.getItemIdentifiers().size() == 1);
  }    

  public void testTopicIsSubjectIndicatorMerge4() {
    TopicIF t1 = builder1.makeTopic();
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t2.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));

    MergeUtils.mergeInto(topicmap1, t2);

    assertTrue("topics merged incorrectly",
           topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
           topicmap1.getTopics().contains(t1));

    // NOTE: According to bug #652 it is now allowed for topics to
    // have the same locator in their item identifiers and subject
    // indicators properties. Thus the following test has been updated
    // to check for 2 locators.
    TopicIF topic = (TopicIF) topicmap1.getTopics().iterator().next();
    assertTrue("topic has wrong number of subject identifiers",
               topic.getSubjectIdentifiers().size() == 2);

    // Of course, the item identifier should not be lost
    // http://code.google.com/p/ontopia/issues/detail?id=28
    assertTrue("topic lost item identifier in merge",
               topic.getItemIdentifiers().size() == 1);
  }    
  
  public void testNoSubjectConflict() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("ftp://www.ontopia.net"));
      t1.addSubjectIdentifier(makeLocator("http://www.ikke.no"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      t2.addSubjectIdentifier(makeLocator("http://www.ikke.no"));

      MergeUtils.mergeInto(topicmap1, topicmap2);
    }
    catch (ConstraintViolationException e) {
      fail("subject conflict should not have been detected", e);
    }
  }

  public void testThreeTopicMerge() {
    // one topic from target and two from source become one topic
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      t1.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      TopicIF t3 = builder2.makeTopic();
      t3.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("topics merged incorrectly",
             topicmap1.getTopics().size() == 1);
      assertTrue("original topic lost in merge",
             topicmap1.getTopics().contains(t1));
      assertTrue("topic subject indicator lost in merge",
             ((TopicIF) topicmap1.getTopics().iterator().next()).getSubjectIdentifiers().size() == 2);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  } 

  public void testCascadingMerge() {
    // merging in one topic from source makes two topics in target
    // merge
    
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder1.makeTopic();
    t2.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    TopicIF t3 = builder2.makeTopic();
    t3.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t3.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics merged incorrectly",
               topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
               topicmap1.getTopics().contains(t1) ||
               topicmap1.getTopics().contains(t2));
    assertTrue("topic subject indicator lost in merge",
               ((TopicIF) topicmap1.getTopics().iterator().next()).getSubjectIdentifiers().size() == 2);
  } 
  
  public void testCascadingMerge2() {
    // merging in two topics from source makes two topics in target
    // merge, end result is one topic
    
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder1.makeTopic();
    t2.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));
    
    TopicIF t3 = builder2.makeTopic();
    t3.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t3.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    TopicIF t4 = builder2.makeTopic();
    t4.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics merged incorrectly",
           topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
           topicmap1.getTopics().contains(t1) ||
           topicmap1.getTopics().contains(t2));
    assertTrue("topic subject indicator lost in merge",
           ((TopicIF) topicmap1.getTopics().iterator().next()).getSubjectIdentifiers().size() == 3);
  } 
  
  public void testCascadingMerge3() {
    // merging in three topics from source makes three topics in target
    // merge, end result is one topic
    
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder1.makeTopic();
    t2.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));
    TopicIF t3 = builder1.makeTopic();
    t3.addSubjectIdentifier(makeLocator("http://www.ontopia.no"));
    t3.addSubjectIdentifier(makeLocator("http://www.ontopia.org"));
    
    TopicIF t4 = builder2.makeTopic();
    t4.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t4.addSubjectIdentifier(makeLocator("ftp://www.ontopia.net"));
    TopicIF t5 = builder2.makeTopic();
    t5.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));
    t5.addSubjectIdentifier(makeLocator("http://www.ontopia.no"));
    TopicIF t6 = builder2.makeTopic();
    t6.addSubjectIdentifier(makeLocator("http://www.ontopia.org"));

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics merged incorrectly",
           topicmap1.getTopics().size() == 1);
    assertTrue("original topic lost in merge",
           topicmap1.getTopics().contains(t1) ||
           topicmap1.getTopics().contains(t2) ||
           topicmap1.getTopics().contains(t3));
    assertTrue("topic subject indicator lost in merge",
           ((TopicIF) topicmap1.getTopics().iterator().next()).getSubjectIdentifiers().size() == 5);
  } 
  
  public void testTopicsCopied() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("topic not copied to target topic map",
             topicmap1.getTopics().size() == 2);

      t2 = topicmap1.getTopicBySubjectLocator(makeLocator("http://www.ontopia.net"));
      assertTrue("topic copied but not registered in subject map",
             t2 != null);

      assertTrue("topic not copied correctly",
             t2.getTopicNames().size() == 0 &&
             t2.getOccurrences().size() == 0 &&
             t2.getSubjectIdentifiers().size() == 0 &&
             t2.getRoles().size() == 0 &&
             t2.getTypes().size() == 0);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }

  public void testMergeTopicNames() { // F.5.1, 2
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicNameIF bn1 = builder1.makeTopicName(t1, "bn1");
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicNameIF bn2 = builder2.makeTopicName(t2, "bn2");

      MergeUtils.mergeInto(topicmap1, topicmap2);
      assertTrue("wrong number of base names after merge",
             t1.getTopicNames().size() == 2);
            
      assertTrue("original base name lost",
             t1.getTopicNames().contains(bn1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  // FIXME: test base names with scope and variants and value

  public void testMergeOccurrences() { // F.5.1, 6
    try {
      TopicIF ot1 = builder1.makeTopic();
      ot1.addSubjectLocator(makeLocator("http://www.ikke.no"));
      TopicIF ot2 = builder2.makeTopic();
      ot2.addSubjectLocator(makeLocator("http://www.ikke.no"));
            
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      URILocator loc1 = makeLocator("http://www.ontopia.net");
      OccurrenceIF oc1 = builder1.makeOccurrence(t1, ot1, loc1);

      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      URILocator loc2 = makeLocator("ftp://www.ontopia.net");
      OccurrenceIF oc2 = builder2.makeOccurrence(t2, ot2, loc2);

      MergeUtils.mergeInto(topicmap1, topicmap2);
      assertTrue("wrong number of occurrences after merge",
             t1.getOccurrences().size() == 2);

      Iterator it = t1.getOccurrences().iterator();
      while (it.hasNext()) {
        OccurrenceIF occ = (OccurrenceIF) it.next();
                
        if (occ.getLocator() != null && occ.getLocator().equals(loc2))
          assertTrue("source occurrence type not copied correctly",
                 occ.getType().getSubjectLocators().contains(makeLocator("http://www.ikke.no")));
        else
          assertTrue("mysterious occurrence after merge: " + occ,
                 occ.equals(oc1));
                
        assertTrue("original occurrence lost",
               t1.getOccurrences().contains(oc1));
      }
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeSourceLocators() {
    try {
      LocatorIF loc = makeLocator("http://www.ontopia.net/tst.xtm#id");
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      t2.addItemIdentifier(loc);

      MergeUtils.mergeInto(topicmap1, topicmap2);
      assertTrue("source locator not copied",
             t1.getItemIdentifiers().size() == 1);
      assertTrue("source locator identity lost",
             t1.getItemIdentifiers().contains(loc));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }
    
  public void testMergeTypes() {
    try {
      TopicIF tt1 = builder1.makeTopic();
      TopicIF tt2 = builder2.makeTopic();
      tt2.addSubjectLocator(makeLocator("http://www.oppvask.com"));
      TopicIF t1 = builder1.makeTopic();
      t1.addType(tt1);
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addType(tt2);
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));

      MergeUtils.mergeInto(topicmap1, topicmap2);
      assertTrue("wrong number of types after merge",
             t1.getTypes().size() == 2);

      Iterator it = t1.getTypes().iterator();
      while (it.hasNext()) {
        TopicIF type = (TopicIF) it.next();
        assertTrue("null type sneaked in somehow!",
               type != null);
        assertTrue("strange type appeared after merging",
               type.equals(tt1) ||
               type.getSubjectLocators().contains(makeLocator("http://www.oppvask.com")));
      }
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTypes2() {
    // a test for a specific bug I once had where when a topic
    // had a type not already copied the source would be copied
    // instead, causing a ConstraintViolationException
    try {
      TopicIF dummy = builder2.makeTopic();
      dummy.addType(builder2.makeTopic());
      TopicIF tt1 = builder1.makeTopic();
      TopicIF tt2 = builder2.makeTopic();
      tt2.addSubjectLocator(makeLocator("http://www.oppvask.com"));
      tt2.addType(dummy);
      TopicIF t1 = builder1.makeTopic();
      t1.addType(tt1);
      TopicIF t2 = builder2.makeTopic();
      t2.addType(tt2);
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));

      MergeUtils.mergeInto(topicmap1, topicmap2);

      TopicIF topic = null;
      Iterator it = topicmap1.getTopics().iterator();
      while (it.hasNext()) {
        topic = (TopicIF) it.next();
        if (topic.getSubjectLocators().contains(makeLocator("http://www.ontopia.net")))
          break;
      }

      assertTrue("wrong number of types after merge",
             topic.getTypes().size() == 1);
      assertTrue("wrong topic type after merge",
             ((TopicIF) topic.getTypes().iterator().next()).getSubjectLocators().contains(makeLocator("http://www.oppvask.com")));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }
    
  public void testMergeAssociation() { // F.5.1, 5
    try {
      TopicIF t1 = builder1.makeTopic();
            
      AssociationIF assoc1 = builder2.makeAssociation(builder2.makeTopic());
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.m.tv"));
      TopicIF rtype = builder2.makeTopic();
      AssociationRoleIF ar1 = builder2.makeAssociationRole(assoc1, rtype, t2);

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("association not copied in merge",
             topicmap1.getAssociations().size() == 1);

      assoc1 = (AssociationIF) topicmap1.getAssociations().iterator().next();
      assertTrue("wrong number of roles in copied association",
             assoc1.getRoles().size() == 1);
            
      ar1 = (AssociationRoleIF) assoc1.getRoles().iterator().next();
      assertTrue("original player lost",
             ar1.getPlayer().getSubjectLocators().contains(makeLocator("http://www.m.tv")));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void _testTNCMergeTrivial() {
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "basename");
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "basename");

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in unconstrained scope not merged",
           topicmap1.getTopics().size() == 1);

    assertTrue("base name duplicates not suppressed",
           ((TopicIF) topicmap1.getTopics().iterator().next()).getTopicNames().size() == 1);
  }

  public void _testTNCMergeTrivialThreeWay() {
    TopicMapIF empty = makeTopicMap();
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "basename");
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "basename");

    MergeUtils.mergeInto(empty, topicmap1);
    MergeUtils.mergeInto(empty, topicmap2);

    assertTrue("topics with equal base names in unconstrained scope not merged",
           empty.getTopics().size() == 1);

    assertTrue("base name duplicates not suppressed",
           ((TopicIF) empty.getTopics().iterator().next()).getTopicNames().size() == 1);
  }

  public void _testTNCMergeWithScope() {
    TopicIF tt1 = builder1.makeTopic();
    tt1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF tt2 = builder2.makeTopic();
    tt2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "basename");
    bn1.addTheme(tt1);
    
    TopicIF t2 = builder2.makeTopic();
    TopicNameIF bn2 = builder2.makeTopicName(t2, "basename");
    bn2.addTheme(tt2);

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 2);

    assertTrue("base name duplicates not suppressed",
           t1.getTopicNames().size() == 1);
  }

  public void _testTNCMerge2_1() {
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "basename1");
    TopicIF t2 = builder1.makeTopic();
    builder1.makeTopicName(t2, "basename2");
    
    TopicIF t3 = builder2.makeTopic();
    builder2.makeTopicName(t3, "basename1");
    builder2.makeTopicName(t3, "basename2");

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 1);

    t1 = (TopicIF) topicmap1.getTopics().iterator().next();
    
    assertTrue("base name duplicates not suppressed",
           t1.getTopicNames().size() == 2);
  }
  
  public void _testTNCMerge1_2() {
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "basename1");
    builder1.makeTopicName(t1, "basename2");
    
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "basename1");
    TopicIF t3 = builder2.makeTopic();
    builder2.makeTopicName(t3, "basename2");

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 1);
    assertTrue("base name duplicates not suppressed",
           t1.getTopicNames().size() == 2);
  }

  public void _testTNCMergeCascade() {
    TopicIF tt1 = builder1.makeTopic();
    builder1.makeTopicName(tt1, "basename1");
    TopicIF tt2 = builder2.makeTopic();
    builder2.makeTopicName(tt2, "basename1");
    
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "basename2");
    bn1.addTheme(tt1);
    
    TopicIF t2 = builder2.makeTopic();
    TopicNameIF bn2 = builder2.makeTopicName(t2, "basename2");
    bn2.addTheme(tt2);

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 2);
  }
  
  public void _testTNCMergeCascadeDeep() {
    TopicIF ttt1 = builder1.makeTopic();
    builder1.makeTopicName(ttt1, "basename1");
    TopicIF ttt2 = builder2.makeTopic();
    builder2.makeTopicName(ttt2, "basename1");
    
    TopicIF tt1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(tt1, "basename2");
    bn1.addTheme(ttt1);
    
    TopicIF tt2 = builder2.makeTopic();
    TopicNameIF bn2 = builder2.makeTopicName(tt2, "basename2");
    bn2.addTheme(ttt2);

    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn3 = builder1.makeTopicName(t1, "basename3");
    bn3.addTheme(tt1);
    
    TopicIF t2 = builder2.makeTopic();
    TopicNameIF bn4 = builder2.makeTopicName(t2, "basename3");
    bn4.addTheme(tt2);

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 3);
  }
  
  public void _testTNCMergeWide() {
    TopicIF tt1 = builder1.makeTopic();
    builder1.makeTopicName(tt1, "basename1");
    TopicIF tt2 = builder2.makeTopic();
    builder2.makeTopicName(tt2, "basename1");
    
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "basename2");
    bn1.addTheme(tt1);
    TopicNameIF bn4 = builder1.makeTopicName(t1, "basename3");
    bn4.addTheme(tt1);
    
    TopicIF t2 = builder2.makeTopic();
    TopicNameIF bn2 = builder2.makeTopicName(t2, "basename2");
    bn2.addTheme(tt2);

    TopicIF t3 = builder2.makeTopic();
    TopicNameIF bn3 = builder2.makeTopicName(t3, "basename3");
    bn3.addTheme(tt2);

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 2);
  }

  public void testTopicNameScopeCopy() {
    // used to have a bug on this, hence the test
    TopicIF tt1 = builder1.makeTopic();
    builder1.makeTopicName(tt1, "basename1");
    
    TopicIF tt2 = builder2.makeTopic();
    TopicIF t2 = builder2.makeTopic();
    t2.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicNameIF bn1 = builder2.makeTopicName(t2, "basename2");
    bn1.addTheme(tt2);

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("incorrect number of topics in merged topic map",
           topicmap1.getTopics().size() == 3);

    t2 = (TopicIF) topicmap1.getObjectByItemIdentifier(makeLocator("http://www.ontopia.net"));
    bn1 = (TopicNameIF) t2.getTopicNames().iterator().next();
    assertTrue("merged topic lost base name",
           bn1 != null);
    assertTrue("merged base name lost scope",
           bn1.getScope().size() == 1);
  }

  public void _testVariantNameCopy() {
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "basename1");
    
    TopicIF t2 = builder2.makeTopic();
    TopicNameIF bn2 = builder2.makeTopicName(t2, "basename1");
    builder2.makeVariantName(bn2, "variant1");

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("incorrect number of topics in merged topic map",
           topicmap1.getTopics().size() == 1);

    TopicNameIF bnx = (TopicNameIF) t1.getTopicNames().iterator().next();
    assertTrue("merged topic lost base name",
           bnx != null);

    VariantNameIF vn1 = (VariantNameIF) bnx.getVariants().iterator().next();
    assertTrue("merged topic lost variant name",
           vn1 != null);
    assertTrue("variant name lost value",
           vn1.getValue() != null && vn1.getValue().equals("variant1"));
  }

  public void testSourceLocSubjIndConflict() {
    // used to have a bug on this, hence the test
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addItemIdentifier(makeLocator("http://www.ontopia.net"));

    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("incorrect number of topics in merged topic map",
           topicmap1.getTopics().size() == 1);
  }

  public void testTMSourceLocators() {
    URILocator orig = makeLocator("http://www.ontopia.net");
    URILocator extra = makeLocator("ftp://ftp.ontopia.net");
    topicmap1.addItemIdentifier(orig);
    topicmap2.addItemIdentifier(orig);
    topicmap2.addItemIdentifier(extra);
    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("wrong number of source locators after merge",
           topicmap1.getItemIdentifiers().size() == 1);
  }
 
  public void _testTNCMergeBug219() {
    // we do a TNC merge that causes an internal merge, followed by
    // another merge from the source into the target against one of
    // the topics now removed by the previous internal merge
    // (doesn't reproduce bug #219 after all, but is sufficiently
    // nasty that we keep it anyway)
    
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "basename1");
    builder1.makeTopicName(t1, "basename4");
    
    TopicIF t2 = builder1.makeTopic();
    builder1.makeTopicName(t2, "basename2");
    builder1.makeTopicName(t2, "basename3");
    
    TopicIF t3 = builder2.makeTopic();
    builder2.makeTopicName(t3, "basename1");
    builder2.makeTopicName(t3, "basename2");

    TopicIF t4 = builder2.makeTopic();
    builder2.makeTopicName(t4, "basename1");
    builder2.makeTopicName(t4, "basename3");

    TopicIF t5 = builder2.makeTopic();
    builder2.makeTopicName(t5, "basename1");
    builder2.makeTopicName(t5, "basename2");
    
    MergeUtils.mergeInto(topicmap1, topicmap2);

    assertTrue("topics with equal base names in same scope not merged",
           topicmap1.getTopics().size() == 1);

    t1 = (TopicIF) topicmap1.getTopics().iterator().next();
    
    assertTrue("base name duplicates not suppressed",
           t1.getTopicNames().size() == 4);
  }
 
  public void testMergeBug222() {
    try {
      TopicIF t1 = builder1.makeTopic();
			LocatorIF loc = makeLocator("http://www.ontopia.net");
      t1.addSubjectIdentifier(loc);

      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectIdentifier(loc);
      TopicIF rtype = builder2.makeTopic();
      TopicIF player = builder2.makeTopic();
      AssociationIF assoc = builder2.makeAssociation(builder2.makeTopic());
      AssociationRoleIF role = builder2.makeAssociationRole(assoc, rtype, player);

      MergeUtils.mergeInto(topicmap1, topicmap2);

      assertTrue("topics merged incorrectly",
             topicmap1.getTopics().size() == 4);
      assertTrue("association not copied",
             topicmap1.getAssociations().size() == 1);
      assertTrue("original topic lost in merge",
             topicmap1.getTopics().contains(t1));
			TopicIF xt = topicmap1.getTopicBySubjectIdentifier(loc);
      assertTrue("topic subject indicator lost in merge",
             xt.getSubjectIdentifiers().size() == 1);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }    

  public void testMergeBug657() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      builder1.makeTopicName(t1, "Ontopia");
      builder1.makeTopicName(t1, "Ontopia AS");
      builder1.makeTopicName(t1, "Ontopia Ltd.");
      builder1.makeTopicName(t1, "Ontopia Ltd");
      
      TopicIF t2 = builder1.makeTopic();
      t2.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));
      builder1.makeTopicName(t2, "Ontopia");
      builder1.makeTopicName(t2, "Ontopia AS");
      builder1.makeTopicName(t2, "Ontopia Ltd.");
      builder1.makeTopicName(t2, "Ontopopia");
      
      TopicIF t3 = builder2.makeTopic();
      t3.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      t3.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));

      MergeUtils.mergeInto(topicmap1, topicmap2);

      TopicIF merged = (TopicIF) topicmap1.getTopics().iterator().next();
      assertTrue("topics merged incorrectly",
                 topicmap1.getTopics().size() == 1);
      assertTrue("base names lost in merge",
                 merged.getTopicNames().size() == 5);
      assertTrue("topic subject indicator lost in merge",
                 merged.getSubjectIdentifiers().size() == 2);
    }
    catch (ConstraintViolationException e) {
      fail("spurious ConstraintViolationException", e);
    }
  }    
  
  public void testBug1790() {
    // test case for bug #1790
    
    TopicIF t1 = builder1.makeTopic();
    t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));

    TopicIF t2 = builder1.makeTopic();

    MergeUtils.mergeInto(t2, t1);

    assertTrue("topics merged incorrectly",
               topicmap1.getTopics().size() == 1);
  }

  public void testTMReifier() {
    // build test case
    TopicIF reifier = builder2.makeTopic();
    topicmap2.setReifier(reifier);

    // merge
    MergeUtils.mergeInto(topicmap1, topicmap2);

    // verify
    assertTrue("topicmap1 had reifier after merge",
               topicmap1.getReifier() == null);
    reifier = (TopicIF) topicmap1.getTopics().iterator().next();
    assertTrue("imported topic still reifying old topic map",
               reifier.getReified() == null);
  }

  // FIXME: test base name merge
  // FIXME: test that topic used as type&scope copied correctly
  // FIXME: test that topic used as type&scope replaced correctly

  // FIXME: test when two source topics merge to the same target topic
  // FIXME: test that handler is used properly
}

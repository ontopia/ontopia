
// $Id: MergeTest.java,v 1.38 2009/04/27 11:06:20 lars.garshol Exp $

package net.ontopia.topicmaps.utils.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.net.MalformedURLException;
import net.ontopia.utils.FileUtils;
import net.ontopia.topicmaps.test.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.ImportExportUtils;
import net.ontopia.test.AbstractOntopiaTestCase;
import net.ontopia.utils.ObjectUtils;

public class MergeTest extends AbstractTopicMapTestCase {
  protected TopicMapIF    topicmap1; 
  protected TopicMapIF    topicmap2; 
  protected TopicMapBuilderIF builder1;
  protected TopicMapBuilderIF builder2;

  public MergeTest(String name) {
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
    
  // --- Test cases for shouldMerge

  public void testShouldEmptyTopics() {
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder2.makeTopic();

    assertTrue("claims empty topics should be merged",
           !MergeUtils.shouldMerge(t1, t2));
  }

  public void testShouldNullSubject() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
            
      assertTrue("claims topics with different subjects should merge",
             !MergeUtils.shouldMerge(t1, t2));
    }
    catch (ConstraintViolationException e) {
      fail("(INTERNAL) ", e);
    }
  }

  public void testShouldSameSubject() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));
            
      assertTrue("claims topics with same subjects should not merge",
             MergeUtils.shouldMerge(t1, t2));
    }
    catch (ConstraintViolationException e) {
      fail("(INTERNAL) ", e);
    }
  }

  public void testShouldSameSubjectIndicator() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
            
      assertTrue("claims topics with same subjects indicator should not merge",
             MergeUtils.shouldMerge(t1, t2));
    }
    catch (ConstraintViolationException e) {
      fail("(INTERNAL) ", e);
    }
  }

  public void testShouldSameTopicName() {
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "Ontopia");
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "Ontopia");
        
    assertTrue("claims topics with same base name in unconstrained scope should merge",
           !MergeUtils.shouldMerge(t1, t2));
  }

  public void testShouldTopicIsSubjectIndicator() {
    TopicIF t1 = builder1.makeTopic();
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
        
    assertTrue("claims topics should not merge when one is subject indicator of other",
               MergeUtils.shouldMerge(t1, t2));
  }
  
  public void testShouldTopicIsSubjectIndicatorReverse() {
    TopicIF t1 = builder1.makeTopic();
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
        
    assertTrue("claims topics should not merge when one is subject indicator of other",
               MergeUtils.shouldMerge(t2, t1));
  }
  
  public void testSeveralTopicNames() {
    TopicIF dummy1 = builder1.makeTopic();
    TopicIF dummy2 = builder1.makeTopic();
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "Ontopia");
    bn1.addTheme(t1);
    TopicNameIF bn2 = builder1.makeTopicName(t1, "Ontopia");
    bn2.addTheme(dummy1);
    TopicNameIF bn3 = builder1.makeTopicName(t1, "Ontopia");
    bn3.addTheme(dummy2);
    TopicNameIF bn4 = builder1.makeTopicName(t1, "Ontopia");
    bn4.addTheme(dummy1);
    bn4.addTheme(dummy2);
    TopicNameIF bn5 = builder1.makeTopicName(t1, "Ontopia");
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "Ontopia");
        
    assertTrue("claims topics should merge when they have same BN in unconstrained scope",
           !MergeUtils.shouldMerge(t1, t2));
  }
  
  // --- Test cases for mergeInto(TopicIF, TopicIF)

  public void testMergeEmptyTopics() {
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();

    try {
      MergeUtils.mergeInto(t1, t2);
      assertTrue("source topic not removed from topic map!",
             t2.getTopicMap() == null);
    }
    catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

  public void testMergeDifferentSubjects1() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder1.makeTopic();
            
      MergeUtils.mergeInto(t1, t2);
      assertTrue("subject was lost during merge",
             t1.getSubjectLocators().contains(makeLocator("http://www.ontopia.net")));
    }
    catch (ConstraintViolationException e) {
      fail("didn't accept merging when only one topic had a subject", e);
    }
  }

  public void testMergeDifferentSubjects2() {  // F.5.1, 4
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));

      MergeUtils.mergeInto(t1, t2);
      assertTrue("subject was lost during merge",
             t1.getSubjectLocators().contains(makeLocator("http://www.ontopia.net")));
    }
    catch (ConstraintViolationException e) {
      fail("didn't accept merging when only one topic had a subject", e);
    }
  }

  public void testMergeDifferentSubjects3() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder1.makeTopic();
      t2.addSubjectLocator(makeLocator("ftp://www.ontopia.net"));

      MergeUtils.mergeInto(t1, t2);
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics with different subjects not accepted");
    }
  }

  public void testMergeSubjectIndicators() {  // F.5.1, 3
    try {
      TopicIF t1 = builder1.makeTopic();
      URILocator loc1 = makeLocator("http://www.ontopia.net");
      t1.addSubjectIdentifier(loc1);
      TopicIF t2 = builder1.makeTopic();
      URILocator loc2 = makeLocator("ftp://www.ontopia.net");
      t2.addSubjectIdentifier(loc2);
      URILocator loc3 = makeLocator("http://www.ontopia.no");
      t2.addSubjectIdentifier(loc3);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("wrong number of subject indicators after merge",
             t1.getSubjectIdentifiers().size() == 3);
            
      assertTrue("original subject indicator lost",
             t1.getSubjectIdentifiers().contains(loc1));
            
      assertTrue("source subject indicator not copied",
             t1.getSubjectIdentifiers().contains(loc2) &&
             t1.getSubjectIdentifiers().contains(loc3));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTopicNames() { // F.5.1, 2
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicNameIF bn1 = builder1.makeTopicName(t1, "bn1");
      TopicIF t2 = builder1.makeTopic();
      TopicNameIF bn2 = builder1.makeTopicName(t2, "bn2");

      MergeUtils.mergeInto(t1, t2);
      assertTrue("wrong number of base names after merge",
             t1.getTopicNames().size() == 2);
            
      assertTrue("original base name lost",
             t1.getTopicNames().contains(bn1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTopicNameDuplicates() { // bug #228
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicNameIF bn1 = builder1.makeTopicName(t1, "bn");
            
      TopicIF t2 = builder1.makeTopic();
      TopicNameIF bn2 = builder1.makeTopicName(t2, "bn");

      MergeUtils.mergeInto(t1, t2);
      assertTrue("base name duplicates not suppressed",
             t1.getTopicNames().size() == 1);
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }

    try {
      TopicIF tt1 = builder1.makeTopic();
      TopicIF tt2 = builder1.makeTopic();
            
      TopicIF t1 = builder1.makeTopic();
      TopicNameIF bn1 = builder1.makeTopicName(t1, "bn");
      bn1.addTheme(tt1);
      bn1.addTheme(tt2);
            
      TopicIF t2 = builder1.makeTopic();
      TopicNameIF bn2 = builder1.makeTopicName(t2, "bn");
      bn2.addTheme(tt1);
      bn2.addTheme(tt2);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("base name duplicates not suppressed",
             t1.getTopicNames().size() == 1);
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTopicNameDuplicatesWithVariants() {
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "bn");
    VariantNameIF vn1 = builder1.makeVariantName(bn1, "vn");
    
    TopicIF t2 = builder1.makeTopic();
    TopicNameIF bn2 = builder1.makeTopicName(t2, "bn");
    VariantNameIF vn2 = builder1.makeVariantName(bn2, "vn");

    MergeUtils.mergeInto(t1, t2);
    assertTrue("base name duplicates not suppressed",
           t1.getTopicNames().size() == 1);
  }
    
  public void testMergeOccurrences() { // F.5.1, 6
    try {
      TopicIF t1 = builder1.makeTopic();
      URILocator loc1 = makeLocator("http://www.ontopia.net");
			TopicIF ot1 = builder1.makeTopic();
      OccurrenceIF oc1 = builder1.makeOccurrence(t1, ot1, loc1);
      TopicIF t2 = builder1.makeTopic();
      URILocator loc2 = makeLocator("ftp://www.ontopia.net");
			TopicIF ot2 = builder1.makeTopic();
      OccurrenceIF oc2 = builder1.makeOccurrence(t2, ot2, loc2);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("wrong number of occurrences after merge",
             t1.getOccurrences().size() == 2);
            
      assertTrue("original occurrence lost",
             t1.getOccurrences().contains(oc1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeOccurrenceDuplicates() {
    try {
      TopicIF t1 = builder1.makeTopic();
      URILocator loc1 = makeLocator("http://www.ontopia.net");
      TopicIF ot1 = builder1.makeTopic();
      OccurrenceIF oc1 = builder1.makeOccurrence(t1, ot1, loc1);
            
      TopicIF t2 = builder1.makeTopic();
      OccurrenceIF oc2 = builder1.makeOccurrence(t2, ot1, loc1);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("occurrence duplicates not suppressed",
             t1.getOccurrences().size() == 1);
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }

    try {
      TopicIF tt1 = builder1.makeTopic();
      TopicIF tt2 = builder1.makeTopic();
            
      TopicIF t1 = builder1.makeTopic();
      URILocator loc1 = makeLocator("http://www.ontopia.net");
      TopicIF ot1 = builder1.makeTopic();
      OccurrenceIF oc1 = builder1.makeOccurrence(t1, ot1, loc1);
      oc1.addTheme(tt1);
      oc1.addTheme(tt2);
            
      TopicIF t2 = builder1.makeTopic();
      OccurrenceIF oc2 = builder1.makeOccurrence(t2, ot1, loc1);
      oc2.addTheme(tt1);
      oc2.addTheme(tt2);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("occurrence duplicates not suppressed",
             t1.getOccurrences().size() == 1);
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTypes() {
    try {
      TopicIF tt1 = builder1.makeTopic();
      TopicIF tt2 = builder1.makeTopic();
      TopicIF t1 = builder1.makeTopic();
      t1.addType(tt1);
      TopicIF t2 = builder1.makeTopic();
      t2.addType(tt2);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("wrong number of types after merge",
             t1.getTypes().size() == 2);
            
      assertTrue("original type lost",
             t1.getTypes().contains(tt1));
            
      assertTrue("source type not copied",
             t1.getTypes().contains(tt2));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }
    
  public void testMergeRoles() { // F.5.1, 5
    try {
      TopicIF atype = builder1.makeTopic();
      AssociationIF assoc1 = builder1.makeAssociation(atype);
      AssociationIF assoc2 = builder1.makeAssociation(atype);
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      TopicIF type1 = builder1.makeTopic();
      TopicIF type2 = builder1.makeTopic();
      AssociationRoleIF ar1 = builder1.makeAssociationRole(assoc1, type1, t1);
      AssociationRoleIF ar2 = builder1.makeAssociationRole(assoc2, type2, t2);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("wrong number of roles after merge",
             t1.getRoles().size() == 2);
            
      assertTrue("original role lost",
             t1.getRoles().contains(ar1));
            
      assertTrue("source role not copied",
             t1.getRoles().contains(ar2));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTopicTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicIF t3 = builder1.makeTopic();
      t3.addType(t2);

      MergeUtils.mergeInto(t1, t2);
      assertTrue("types of unrelated topic corrupted",
             t3.getTypes().size() == 1);
            
      assertTrue("topic type replacement not done correctly",
             t3.getTypes().contains(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeAssociationRoleTypeUse() {
    try {
      TopicIF atype = builder1.makeTopic();
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      TopicIF player = builder1.makeTopic();

      AssociationIF assoc1 = builder1.makeAssociation(atype);
      AssociationRoleIF ar1 = builder1.makeAssociationRole(assoc1, t2, player);

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("association role type replacement not done correctly",
             ar1.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeAssociationTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      AssociationIF assoc1 = builder1.makeAssociation(t2);

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("association type replacement not done correctly",
             assoc1.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTopicNameTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicNameIF bn = builder1.makeTopicName(t1, t2, "");
      
      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("basename type replacement not done correctly",
             bn.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeOccurrenceTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      OccurrenceIF occ = builder1.makeOccurrence(t1, t2, "");

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("occurrence type replacement not done correctly",
             occ.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeAssociationScopeUse() {
    try {
      TopicIF atype = builder1.makeTopic();
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      AssociationIF assoc = builder1.makeAssociation(atype);
      assoc.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("association scope corrupted",
             assoc.getScope().size() == 1);
      assertTrue("association theme replacement not done correctly",
             assoc.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeTopicNameScopeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicNameIF bn = builder1.makeTopicName(t1, "");
      bn.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("base name scope corrupted",
             bn.getScope().size() == 1);
      assertTrue("base name theme replacement not done correctly",
             bn.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeOccurrenceScopeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      TopicIF ot = builder1.makeTopic();

      OccurrenceIF occ = builder1.makeOccurrence(t1, ot, "");
      occ.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("occurrence scope corrupted",
             occ.getScope().size() == 1);
      assertTrue("occurrence theme replacement not done correctly",
             occ.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeVariantNameScopeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicNameIF bn = builder1.makeTopicName(t1, "");
      VariantNameIF vn = builder1.makeVariantName(bn, "");
      vn.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      assertTrue("variant name scope corrupted",
             vn.getScope().size() == 1);
      assertTrue("variant name theme replacement not done correctly",
             vn.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
  }

  public void testMergeDuplicateAssociations() throws IOException {
    TopicMapIF tm = ImportExportUtils.getReader(resolveFileName("various", "merge-duplicate-assoc.ltm")).read();
    TopicIF puccini1 = getTopicById(tm, "puccini1");
    TopicIF puccini2 = getTopicById(tm, "puccini2");
    
    MergeUtils.mergeInto(puccini1, puccini2);

    // the new puccini should have only a single place of birth; in
    // fact, only a single association

    assertTrue("Merge did not remove duplicate associations",
               puccini1.getRoles().size() == 1);
  }

  // FIXME: must test duplicates

  public void testMergeSelf() throws IOException {
    TopicMapIF tm1 = ImportExportUtils.getReader(resolveFileName("various", "jill.xtm")).read();
    TopicMapIF tm2 = ImportExportUtils.getReader(resolveFileName("various", "jill.xtm")).read();

    MergeUtils.mergeInto(tm1, tm2);
  }

  public void testMergeReified() {
    // build TM
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();
    OccurrenceIF occ = builder1.makeOccurrence(t1, t2, "");
    occ.setReifier(t2);

    // merge
    MergeUtils.mergeInto(t1, t2);

    // check
    assertTrue("Occurrence lost reifier on merge",
               occ.getReifier() == t1);
    assertTrue("Topic lost reified on merge",
               t1.getReified() == occ);
  }

  public void testMergeReified2() {
    // build TM
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();
    TopicIF t3 = builder1.makeTopic();
    OccurrenceIF occ = builder1.makeOccurrence(t3, t2, "");
    occ.setReifier(t2);

    // merge
    MergeUtils.mergeInto(t2, t1);

    // check
    assertTrue("Occurrence lost reifier on merge",
               occ.getReifier() == t2);
    assertTrue("Topic lost reified on merge",
               t2.getReified() == occ);
  }

  public void testMergeReified3() {
    // build TM
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();
    TopicIF t3 = builder1.makeTopic();
    OccurrenceIF occ1 = builder1.makeOccurrence(t3, t2, "x");
    occ1.setReifier(t2);
    OccurrenceIF occ2 = builder1.makeOccurrence(t3, t2, "y");
    occ2.setReifier(t1);

    // merge
    try {
      MergeUtils.mergeInto(t2, t1);
      fail("Successfully merged topics which reify different objects");
    } catch (ConstraintViolationException e) {
    }
  }
  
  public void testMergeReifiedTargetTopicMap() {
    // build TMs
    TopicIF reifier = builder1.makeTopic();
    topicmap1.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(topicmap1, topicmap2);
    // previous line checks if moving topicmap causes it to lose
    // its reifier

    // check
    assertTrue("Topic map lost reifier on merge",
               topicmap1.getReifier() == reifier);
    assertTrue("Topic lost reified on merge",
               reifier.getReified() == topicmap1);
  }
  
  public void testMergeReifiedSourceTopicMap() {
    // build TMs
    TopicIF reifier1 = builder1.makeTopic();
    reifier1.addSubjectIdentifier(URILocator.create("test:reifier"));
    TopicIF reifier2 = builder2.makeTopic();
    reifier2.addSubjectIdentifier(URILocator.create("test:reifier"));
    topicmap2.setReifier(reifier2);
    // merge
    MergeUtils.mergeInto(topicmap1, topicmap2);
    // previous line checks if moving topicmap causes it to lose
    // its reifier (which it should)

    // check
    assertTrue("Topic map did not lose reifier on merge",
               topicmap1.getReifier() == null);
    assertTrue("Topic did become reifier on merge",
               reifier1.getReified() == null);
  }
  
  public void testMergeReifiedTopicMaps() {
    // build TMs
    TopicIF reifier1 = builder1.makeTopic();
    reifier1.addSubjectIdentifier(URILocator.create("test:reifier"));
    topicmap1.setReifier(reifier1);
    TopicIF reifier2 = builder2.makeTopic();
    reifier2.addSubjectIdentifier(URILocator.create("test:reifier"));
    topicmap2.setReifier(reifier2);
    // merge
    MergeUtils.mergeInto(topicmap1, topicmap2);
    // previous line checks if moving topicmap causes it to lose
    // its reifier

    // check
    assertTrue("Topic map lost reifier on merge",
               topicmap1.getReifier() == reifier1);
    assertTrue("Topic did become reifier on merge",
               reifier1.getReified() == topicmap1);
  }
  
  public void testMergeReifiedBasename() {
    // build TM
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();
    TopicIF t3 = builder1.makeTopic();
    TopicNameIF bn = builder1.makeTopicName(t3, t2, "");
    bn.setReifier(t2);
    // merge
    MergeUtils.mergeInto(t1, t3);
    // previous line checks if moving bn from t3 to t1 causes it to lose
    // its reifier

    // check
		TopicNameIF newbn = (TopicNameIF)t1.getTopicNames().iterator().next();
    assertTrue("Basename lost reifier on merge",
               newbn.getReifier() == t2);
    assertTrue("Topic lost reified on merge",
               t2.getReified() == newbn);
  }
  
  public void testMergeReifiedOccurrence() {
    // build TM
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();
    TopicIF t3 = builder1.makeTopic();
    OccurrenceIF occ = builder1.makeOccurrence(t3, t2, "");
    occ.setReifier(t2);
    // merge
    MergeUtils.mergeInto(t1, t3);
    // previous line checks if moving occ from t3 to t1 causes it to lose
    // its reifier

    // check
		OccurrenceIF newocc = (OccurrenceIF)t1.getOccurrences().iterator().next();
    assertTrue("Occurrence lost reifier on merge",
               newocc.getReifier() == t2);
    assertTrue("Topic lost reified on merge",
               t2.getReified() == newocc);
  }
  
  public void testMergeReifiedAssociation() {
    // build TM
    TopicIF reifier = builder1.makeTopic();
    TopicIF at = builder1.makeTopic();
    TopicIF nrt = builder1.makeTopic();
    TopicIF nrp = builder1.makeTopic();
    TopicIF drt = builder1.makeTopic();
    TopicIF drp = builder1.makeTopic();
    TopicIF target = builder1.makeTopic();

		AssociationIF assoc = builder1.makeAssociation(at);
    AssociationRoleIF nrole = builder1.makeAssociationRole(assoc, nrt, nrp);
    AssociationRoleIF drole = builder1.makeAssociationRole(assoc, drt, drp);
		assoc.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(target, nrp);
    // previous line checks if moving association from nrp to target causes it to lose
    // its reifier

    // check
		AssociationRoleIF newrole = (AssociationRoleIF)target.getRoles().iterator().next();
		AssociationIF newassoc = newrole.getAssociation();
    assertTrue("Association lost reifier on merge",
               newassoc.getReifier() == reifier);
    assertTrue("Topic lost reified on merge",
               reifier.getReified() == newassoc);
  }
  
  public void testMergeReifiedNearRole() {
    // build TM
    TopicIF reifier = builder1.makeTopic();
    TopicIF at = builder1.makeTopic();
    TopicIF nrt = builder1.makeTopic();
    TopicIF nrp = builder1.makeTopic();
    TopicIF drt = builder1.makeTopic();
    TopicIF drp = builder1.makeTopic();
    TopicIF target = builder1.makeTopic();

		AssociationIF assoc = builder1.makeAssociation(at);
    AssociationRoleIF nrole = builder1.makeAssociationRole(assoc, nrt, nrp);
    AssociationRoleIF drole = builder1.makeAssociationRole(assoc, drt, drp);
		nrole.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(target, nrp);
    // previous line checks if moving association from nrp to target causes it to lose
    // its reifier

    // check
		AssociationRoleIF newnrole = (AssociationRoleIF)target.getRoles().iterator().next();
    assertTrue("Near role lost reifier on merge",
               newnrole.getReifier() == reifier);
    assertTrue("Topic lost reified on merge",
               reifier.getReified() == newnrole);
  }
  
  public void testMergeReifiedDistantRole() {
    // build TM
    TopicIF reifier = builder1.makeTopic();
    TopicIF at = builder1.makeTopic();
    TopicIF nrt = builder1.makeTopic();
    TopicIF nrp = builder1.makeTopic();
    TopicIF drt = builder1.makeTopic();
    TopicIF drp = builder1.makeTopic();
    TopicIF target = builder1.makeTopic();

		AssociationIF assoc = builder1.makeAssociation(at);
    AssociationRoleIF nrole = builder1.makeAssociationRole(assoc, nrt, nrp);
    AssociationRoleIF drole = builder1.makeAssociationRole(assoc, drt, drp);
		drole.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(target, nrp);
    // previous line checks if moving association from nrp to target causes it to lose
    // its reifier

    // check
    AssociationRoleIF newnrole = (AssociationRoleIF)target.getRoles().iterator().next();
    // locate distant role
    AssociationRoleIF newdrole = null;
    Iterator iter = newnrole.getAssociation().getRoles().iterator();
    while (iter.hasNext()) {
      AssociationRoleIF role = (AssociationRoleIF)iter.next();
      if (ObjectUtils.different(newnrole, role)) {
        newdrole = role;
        break;
      }
    }
    assertTrue("Distant role lost reifier on merge",
               newdrole.getReifier() == reifier);
    assertTrue("Topic lost reified on merge",
               reifier.getReified() == newdrole);
  }
  
  // --- Test cases for mergeInto(TopicMapIF, TopicIF)

  public void testMergeDuplicateSourceLocator() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addItemIdentifier(new URILocator("http://www.example.com"));
      TopicNameIF bn1 = builder1.makeTopicName(t1, "boodoo");
      bn1.addItemIdentifier(new URILocator("http://www.example.com/#1"));

      TopicIF t2 = builder2.makeTopic();
      t2.addItemIdentifier(new URILocator("http://www.example.com"));
      TopicNameIF bn2 = builder2.makeTopicName(t2, "boodoo");
      bn2.addItemIdentifier(new URILocator("http://www.example.com/#1"));

      MergeUtils.mergeInto(topicmap1, t2);
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
    catch (java.net.MalformedURLException e) {
      fail("URI literals malformed", e);
    }
  }

  public void testMergeDuplicateSourceLocator2() {

    // this test differs from the previous one in that the base names
    // are not equal, and so should not be merged
    
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addItemIdentifier(new URILocator("http://www.example.com"));
      TopicNameIF bn1 = builder1.makeTopicName(t1, "boodoo");
      bn1.addItemIdentifier(new URILocator("http://www.example.com/#1"));

      TopicIF t2 = builder2.makeTopic();
      t2.addItemIdentifier(new URILocator("http://www.example.com"));
      TopicNameIF bn2 = builder2.makeTopicName(t2, "boovoo");
      bn2.addItemIdentifier(new URILocator("http://www.example.com/#1"));

      MergeUtils.mergeInto(topicmap1, t2);
      fail("merge succeeded, even though duplicates did not match");
    }
    catch (ConstraintViolationException e) {
    }
    catch (java.net.MalformedURLException e) {
      fail("URI literals malformed", e);
    }
  }
  
  public void testMergeReifyingTopic() {
    try {
      TopicIF t1 = builder2.makeTopic();
      TopicNameIF bn1 = builder2.makeTopicName(t1, "famous misspelling");

      TopicIF t2 = builder2.makeTopic();
      TopicNameIF bn2 = builder2.makeTopicName(t2, "boodoo");
      bn2.addItemIdentifier(new URILocator("http://www.example.com/#1"));
			bn2.setReifier(t1);
      MergeUtils.mergeInto(topicmap1, t2);

      assertTrue("reifying topic was not included",
                 topicmap1.getTopics().size() == 2);

      TopicNameIF bn = (TopicNameIF) topicmap1.getObjectByItemIdentifier(new URILocator("http://www.example.com/#1"));
      TopicIF reifier = bn.getReifier();
      
      assertTrue("reification link was broken", reifier != null);

      assertTrue("reifier base name not copied", !reifier.getTopicNames().isEmpty());

      bn = (TopicNameIF) reifier.getTopicNames().iterator().next();
      assertTrue("reifier base name not copied correctly",
                 bn.getValue().equals("famous misspelling"));
    }
    catch (ConstraintViolationException e) {
      fail("merge of topics unaccountably failed", e);
    }
    catch (MalformedURLException e) {
      fail("URI literals malformed", e);
    }
  }

  public void testMergeAllTopics() throws MalformedURLException, IOException {
    String sep = File.separator;
    String root = AbstractOntopiaTestCase.getTestDirectory();
    verifyDirectory(root + sep + "canonical", "out");
      
    String file = root + sep + "various" + sep + "houdini.xtm";
    String outfile = root + sep + "canonical" + sep + "out" + sep + "houdini.xtm";
    String baseline = root + sep + "various" + sep + "baseline-houdini.xtm";

    TopicMapIF topicmap = new XTMTopicMapReader(new File(file)).read();    
    
    // save
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapIF newtm = store.getTopicMap();

    Iterator iterator = topicmap.getTopics().iterator();
    while (iterator.hasNext()) {
      TopicIF topic = (TopicIF) iterator.next();
      MergeUtils.mergeInto(newtm, topic);
    }

    new CanonicalTopicMapWriter(outfile).write(newtm);
    assertTrue("Topic map created by merging over topics not equal to original",
               FileUtils.compare(new File(outfile), new File(baseline)));
  }
}

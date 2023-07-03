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

package net.ontopia.topicmaps.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.topicmaps.xml.CanonicalTopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.utils.TestFileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MergeTest {
  protected TopicMapIF    topicmap1; 
  protected TopicMapIF    topicmap2; 
  protected TopicMapBuilderIF builder1;
  protected TopicMapBuilderIF builder2;

  @Before
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
      return URILocator.create(uri);
  }
    
  // --- Test cases for shouldMerge

  @Test
  public void testShouldEmptyTopics() {
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder2.makeTopic();

    Assert.assertTrue("claims empty topics should be merged",
           !MergeUtils.shouldMerge(t1, t2));
  }

  @Test
  public void testShouldNullSubject() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
            
      Assert.assertTrue("claims topics with different subjects should merge",
             !MergeUtils.shouldMerge(t1, t2));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("(INTERNAL) " + e.getMessage());
    }
  }

  @Test
  public void testShouldSameSubject() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));
            
      Assert.assertTrue("claims topics with same subjects should not merge",
             MergeUtils.shouldMerge(t1, t2));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("(INTERNAL) " + e.getMessage());
    }
  }

  @Test
  public void testShouldSameSubjectIndicator() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder2.makeTopic();
      t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
            
      Assert.assertTrue("claims topics with same subjects indicator should not merge",
             MergeUtils.shouldMerge(t1, t2));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("(INTERNAL) " + e.getMessage());
    }
  }

  @Test
  public void testShouldSameTopicName() {
    TopicIF t1 = builder1.makeTopic();
    builder1.makeTopicName(t1, "Ontopia");
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "Ontopia");
        
    Assert.assertTrue("claims topics with same base name in unconstrained scope should merge",
           !MergeUtils.shouldMerge(t1, t2));
  }

  @Test
  public void testShouldTopicIsSubjectIndicator() {
    TopicIF t1 = builder1.makeTopic();
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
        
    Assert.assertTrue("claims topics should not merge when one is subject indicator of other",
               MergeUtils.shouldMerge(t1, t2));
  }
  
  @Test
  public void testShouldTopicIsSubjectIndicatorReverse() {
    TopicIF t1 = builder1.makeTopic();
    t1.addItemIdentifier(makeLocator("http://www.ontopia.net"));
    TopicIF t2 = builder2.makeTopic();
    t2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
        
    Assert.assertTrue("claims topics should not merge when one is subject indicator of other",
               MergeUtils.shouldMerge(t2, t1));
  }
  
  @Test
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
    builder1.makeTopicName(t1, "Ontopia");
    TopicIF t2 = builder2.makeTopic();
    builder2.makeTopicName(t2, "Ontopia");
        
    Assert.assertTrue("claims topics should merge when they have same BN in unconstrained scope",
           !MergeUtils.shouldMerge(t1, t2));
  }
  
  // --- Test cases for mergeInto(TopicIF, TopicIF)

  @Test
  public void testMergeEmptyTopics() {
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();

    try {
      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("source topic not removed from topic map!",
             t2.getTopicMap() == null);
    }
    catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
  }

  @Test
  public void testMergeDifferentSubjects1() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder1.makeTopic();
            
      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("subject was lost during merge",
             t1.getSubjectLocators().contains(makeLocator("http://www.ontopia.net")));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("didn't accept merging when only one topic had a subject" + e.getMessage());
    }
  }

  @Test
  public void testMergeDifferentSubjects2() {  // F.5.1, 4
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      t2.addSubjectLocator(makeLocator("http://www.ontopia.net"));

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("subject was lost during merge",
             t1.getSubjectLocators().contains(makeLocator("http://www.ontopia.net")));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("didn't accept merging when only one topic had a subject" + e.getMessage());
    }
  }

  @Test
  public void testMergeDifferentSubjects3() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addSubjectLocator(makeLocator("http://www.ontopia.net"));
      TopicIF t2 = builder1.makeTopic();
      t2.addSubjectLocator(makeLocator("ftp://www.ontopia.net"));

      MergeUtils.mergeInto(t1, t2);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics with different subjects not accepted");
    }
  }

  @Test
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
      Assert.assertTrue("wrong number of subject indicators after merge",
             t1.getSubjectIdentifiers().size() == 3);
            
      Assert.assertTrue("original subject indicator lost",
             t1.getSubjectIdentifiers().contains(loc1));
            
      Assert.assertTrue("source subject indicator not copied",
             t1.getSubjectIdentifiers().contains(loc2) &&
             t1.getSubjectIdentifiers().contains(loc3));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTopicNames() { // F.5.1, 2
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicNameIF bn1 = builder1.makeTopicName(t1, "bn1");
      TopicIF t2 = builder1.makeTopic();
      builder1.makeTopicName(t2, "bn2");

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("wrong number of base names after merge",
             t1.getTopicNames().size() == 2);
            
      Assert.assertTrue("original base name lost",
             t1.getTopicNames().contains(bn1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTopicNameDuplicates() { // bug #228
    try {
      TopicIF t1 = builder1.makeTopic();
      builder1.makeTopicName(t1, "bn");
            
      TopicIF t2 = builder1.makeTopic();
      builder1.makeTopicName(t2, "bn");

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("base name duplicates not suppressed",
             t1.getTopicNames().size() == 1);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
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
      Assert.assertTrue("base name duplicates not suppressed",
             t1.getTopicNames().size() == 1);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTopicNameDuplicatesWithVariants() {
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "bn");
    builder1.makeVariantName(bn1, "vn", Collections.emptySet());
    
    TopicIF t2 = builder1.makeTopic();
    TopicNameIF bn2 = builder1.makeTopicName(t2, "bn");
    builder1.makeVariantName(bn2, "vn", Collections.emptySet());

    MergeUtils.mergeInto(t1, t2);
    Assert.assertTrue("base name duplicates not suppressed",
           t1.getTopicNames().size() == 1);
  }
    
  @Test
  public void testMergeOccurrences() { // F.5.1, 6
    try {
      TopicIF t1 = builder1.makeTopic();
      URILocator loc1 = makeLocator("http://www.ontopia.net");
      TopicIF ot1 = builder1.makeTopic();
      OccurrenceIF oc1 = builder1.makeOccurrence(t1, ot1, loc1);
      TopicIF t2 = builder1.makeTopic();
      URILocator loc2 = makeLocator("ftp://www.ontopia.net");
      TopicIF ot2 = builder1.makeTopic();
      builder1.makeOccurrence(t2, ot2, loc2);

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("wrong number of occurrences after merge",
             t1.getOccurrences().size() == 2);
            
      Assert.assertTrue("original occurrence lost",
             t1.getOccurrences().contains(oc1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeOccurrenceDuplicates() {
    try {
      TopicIF t1 = builder1.makeTopic();
      URILocator loc1 = makeLocator("http://www.ontopia.net");
      TopicIF ot1 = builder1.makeTopic();
      builder1.makeOccurrence(t1, ot1, loc1);
            
      TopicIF t2 = builder1.makeTopic();
      builder1.makeOccurrence(t2, ot1, loc1);

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("occurrence duplicates not suppressed",
             t1.getOccurrences().size() == 1);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
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
      Assert.assertTrue("occurrence duplicates not suppressed",
             t1.getOccurrences().size() == 1);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTypes() {
    try {
      TopicIF tt1 = builder1.makeTopic();
      TopicIF tt2 = builder1.makeTopic();
      TopicIF t1 = builder1.makeTopic();
      t1.addType(tt1);
      TopicIF t2 = builder1.makeTopic();
      t2.addType(tt2);

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("wrong number of types after merge",
             t1.getTypes().size() == 2);
            
      Assert.assertTrue("original type lost",
             t1.getTypes().contains(tt1));
            
      Assert.assertTrue("source type not copied",
             t1.getTypes().contains(tt2));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }
    
  @Test
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
      Assert.assertTrue("wrong number of roles after merge",
             t1.getRoles().size() == 2);
            
      Assert.assertTrue("original role lost",
             t1.getRoles().contains(ar1));
            
      Assert.assertTrue("source role not copied",
             t1.getRoles().contains(ar2));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTopicTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicIF t3 = builder1.makeTopic();
      t3.addType(t2);

      MergeUtils.mergeInto(t1, t2);
      Assert.assertTrue("types of unrelated topic corrupted",
             t3.getTypes().size() == 1);
            
      Assert.assertTrue("topic type replacement not done correctly",
             t3.getTypes().contains(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeAssociationRoleTypeUse() {
    try {
      TopicIF atype = builder1.makeTopic();
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      TopicIF player = builder1.makeTopic();

      AssociationIF assoc1 = builder1.makeAssociation(atype);
      AssociationRoleIF ar1 = builder1.makeAssociationRole(assoc1, t2, player);

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("association role type replacement not done correctly",
             ar1.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeAssociationTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      AssociationIF assoc1 = builder1.makeAssociation(t2);

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("association type replacement not done correctly",
             assoc1.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTopicNameTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicNameIF bn = builder1.makeTopicName(t1, t2, "");
      
      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("basename type replacement not done correctly",
             bn.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeOccurrenceTypeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      OccurrenceIF occ = builder1.makeOccurrence(t1, t2, "");

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("occurrence type replacement not done correctly",
             occ.getType().equals(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeAssociationScopeUse() {
    try {
      TopicIF atype = builder1.makeTopic();
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      AssociationIF assoc = builder1.makeAssociation(atype);
      assoc.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("association scope corrupted",
             assoc.getScope().size() == 1);
      Assert.assertTrue("association theme replacement not done correctly",
             assoc.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeTopicNameScopeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicNameIF bn = builder1.makeTopicName(t1, "");
      bn.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("base name scope corrupted",
             bn.getScope().size() == 1);
      Assert.assertTrue("base name theme replacement not done correctly",
             bn.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeOccurrenceScopeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();
      TopicIF ot = builder1.makeTopic();

      OccurrenceIF occ = builder1.makeOccurrence(t1, ot, "");
      occ.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("occurrence scope corrupted",
             occ.getScope().size() == 1);
      Assert.assertTrue("occurrence theme replacement not done correctly",
             occ.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeVariantNameScopeUse() {
    try {
      TopicIF t1 = builder1.makeTopic();
      TopicIF t2 = builder1.makeTopic();

      TopicNameIF bn = builder1.makeTopicName(t1, "");
      VariantNameIF vn = builder1.makeVariantName(bn, "", Collections.emptySet());
      vn.addTheme(t2);

      MergeUtils.mergeInto(t1, t2);
            
      Assert.assertTrue("variant name scope corrupted",
             vn.getScope().size() == 1);
      Assert.assertTrue("variant name theme replacement not done correctly",
             vn.getScope().contains(t1));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeDuplicateAssociations() throws IOException {
    TopicMapIF tm = ImportExportUtils.getReader(TestFileUtils.getTestInputFile("various", "merge-duplicate-assoc.ltm")).read();
    LocatorIF base = tm.getStore().getBaseAddress();
    TopicIF puccini1 = (TopicIF) tm.getObjectByItemIdentifier(base.resolveAbsolute("#puccini1"));
    TopicIF puccini2 = (TopicIF) tm.getObjectByItemIdentifier(base.resolveAbsolute("#puccini2"));
    
    MergeUtils.mergeInto(puccini1, puccini2);

    // the new puccini should have only a single place of birth; in
    // fact, only a single association

    Assert.assertTrue("Merge did not remove duplicate associations",
               puccini1.getRoles().size() == 1);
  }

  // FIXME: must test duplicates

  @Test
  public void testMergeSelf() throws IOException {
    TopicMapIF tm1 = ImportExportUtils.getReader(TestFileUtils.getTestInputFile("query", "jill.xtm")).read();
    TopicMapIF tm2 = ImportExportUtils.getReader(TestFileUtils.getTestInputFile("query", "jill.xtm")).read();

    MergeUtils.mergeInto(tm1, tm2);
    
    Assert.assertEquals(tm1.getTopics().size(), tm2.getTopics().size());
    Assert.assertEquals(tm1.getAssociations().size(), tm2.getAssociations().size());
  }

  @Test
  public void testMergeReified() {
    // build TM
    TopicIF t1 = builder1.makeTopic();
    TopicIF t2 = builder1.makeTopic();
    OccurrenceIF occ = builder1.makeOccurrence(t1, t2, "");
    occ.setReifier(t2);

    // merge
    MergeUtils.mergeInto(t1, t2);

    // check
    Assert.assertTrue("Occurrence lost reifier on merge",
               occ.getReifier() == t1);
    Assert.assertTrue("Topic lost reified on merge",
               t1.getReified() == occ);
  }

  @Test
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
    Assert.assertTrue("Occurrence lost reifier on merge",
               occ.getReifier() == t2);
    Assert.assertTrue("Topic lost reified on merge",
               t2.getReified() == occ);
  }

  @Test
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
      Assert.fail("Successfully merged topics which reify different objects");
    } catch (ConstraintViolationException e) {
    }
  }
  
  @Test
  public void testMergeReifiedTargetTopicMap() {
    // build TMs
    TopicIF reifier = builder1.makeTopic();
    topicmap1.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(topicmap1, topicmap2);
    // previous line checks if moving topicmap causes it to lose
    // its reifier

    // check
    Assert.assertTrue("Topic map lost reifier on merge",
               topicmap1.getReifier() == reifier);
    Assert.assertTrue("Topic lost reified on merge",
               reifier.getReified() == topicmap1);
  }
  
  @Test
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
    Assert.assertTrue("Topic map did not lose reifier on merge",
               topicmap1.getReifier() == null);
    Assert.assertTrue("Topic did become reifier on merge",
               reifier1.getReified() == null);
  }
  
  @Test
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
    Assert.assertTrue("Topic map lost reifier on merge",
               topicmap1.getReifier() == reifier1);
    Assert.assertTrue("Topic did become reifier on merge",
               reifier1.getReified() == topicmap1);
  }
  
  @Test
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
    Assert.assertTrue("Basename lost reifier on merge",
               newbn.getReifier() == t2);
    Assert.assertTrue("Topic lost reified on merge",
               t2.getReified() == newbn);
  }
  
  @Test
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
    Assert.assertTrue("Occurrence lost reifier on merge",
               newocc.getReifier() == t2);
    Assert.assertTrue("Topic lost reified on merge",
               t2.getReified() == newocc);
  }
  
  @Test
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
    builder1.makeAssociationRole(assoc, nrt, nrp);
    builder1.makeAssociationRole(assoc, drt, drp);
    assoc.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(target, nrp);
    // previous line checks if moving association from nrp to target causes it to lose
    // its reifier

    // check
    AssociationRoleIF newrole = (AssociationRoleIF)target.getRoles().iterator().next();
    AssociationIF newassoc = newrole.getAssociation();
    Assert.assertTrue("Association lost reifier on merge",
               newassoc.getReifier() == reifier);
    Assert.assertTrue("Topic lost reified on merge",
               reifier.getReified() == newassoc);
  }
  
  @Test
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
    builder1.makeAssociationRole(assoc, drt, drp);
    nrole.setReifier(reifier);
    // merge
    MergeUtils.mergeInto(target, nrp);
    // previous line checks if moving association from nrp to target causes it to lose
    // its reifier

    // check
    AssociationRoleIF newnrole = (AssociationRoleIF)target.getRoles().iterator().next();
    Assert.assertTrue("Near role lost reifier on merge",
               newnrole.getReifier() == reifier);
    Assert.assertTrue("Topic lost reified on merge",
               reifier.getReified() == newnrole);
  }
  
  @Test
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
    builder1.makeAssociationRole(assoc, nrt, nrp);
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
      if (!Objects.equals(newnrole, role)) {
        newdrole = role;
        break;
      }
    }
    Assert.assertTrue("Distant role lost reifier on merge",
               newdrole.getReifier() == reifier);
    Assert.assertTrue("Topic lost reified on merge",
               reifier.getReified() == newdrole);
  }
  
  // --- Test cases for mergeInto(TopicMapIF, TopicIF)

  @Test
  public void testMergeDuplicateSourceLocator() {
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addItemIdentifier(URILocator.create("http://www.example.com"));
      TopicNameIF bn1 = builder1.makeTopicName(t1, "boodoo");
      bn1.addItemIdentifier(URILocator.create("http://www.example.com/#1"));

      TopicIF t2 = builder2.makeTopic();
      t2.addItemIdentifier(URILocator.create("http://www.example.com"));
      TopicNameIF bn2 = builder2.makeTopicName(t2, "boodoo");
      bn2.addItemIdentifier(URILocator.create("http://www.example.com/#1"));

      MergeUtils.mergeInto(topicmap1, t2);
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeDuplicateSourceLocator2() {

    // this test differs from the previous one in that the base names
    // are not equal, and so should not be merged
    
    try {
      TopicIF t1 = builder1.makeTopic();
      t1.addItemIdentifier(URILocator.create("http://www.example.com"));
      TopicNameIF bn1 = builder1.makeTopicName(t1, "boodoo");
      bn1.addItemIdentifier(URILocator.create("http://www.example.com/#1"));

      TopicIF t2 = builder2.makeTopic();
      t2.addItemIdentifier(URILocator.create("http://www.example.com"));
      TopicNameIF bn2 = builder2.makeTopicName(t2, "boovoo");
      bn2.addItemIdentifier(URILocator.create("http://www.example.com/#1"));

      MergeUtils.mergeInto(topicmap1, t2);
      Assert.fail("merge succeeded, even though duplicates did not match");
    }
    catch (ConstraintViolationException e) {
    }
  }
  
  @Test
  public void testMergeReifyingTopic() {
    try {
      TopicIF t1 = builder2.makeTopic();
      builder2.makeTopicName(t1, "famous misspelling");

      TopicIF t2 = builder2.makeTopic();
      TopicNameIF bn2 = builder2.makeTopicName(t2, "boodoo");
      bn2.addItemIdentifier(URILocator.create("http://www.example.com/#1"));
      bn2.setReifier(t1);
      MergeUtils.mergeInto(topicmap1, t2);

      Assert.assertTrue("reifying topic was not included",
                 topicmap1.getTopics().size() >= 2);

      TopicNameIF bn = (TopicNameIF) topicmap1.getObjectByItemIdentifier(URILocator.create("http://www.example.com/#1"));
      TopicIF reifier = bn.getReifier();
      
      Assert.assertTrue("reification link was broken", reifier != null);

      Assert.assertTrue("reifier base name not copied", !reifier.getTopicNames().isEmpty());

      bn = (TopicNameIF) reifier.getTopicNames().iterator().next();
      Assert.assertTrue("reifier base name not copied correctly",
                 bn.getValue().equals("famous misspelling"));
    }
    catch (ConstraintViolationException e) {
      Assert.fail("merge of topics unaccountably Assert.failed" + e.getMessage());
    }
  }

  @Test
  public void testMergeAllTopics() throws IOException {
    String sep = File.separator;
    String root = TestFileUtils.getTestdataOutputDirectory();
    TestFileUtils.verifyDirectory(root, "canonical", "out");
      
    String file = TestFileUtils.getTestInputFile("various", "houdini.xtm");
    File outfile = new File(root + sep + "canonical" + sep + "out" + sep + "houdini.xtm");
    String baseline = TestFileUtils.getTestInputFile("various", "baseline-houdini.xtm");

    TopicMapIF topicmap = new XTMTopicMapReader(TestFileUtils.getTestInputURL(file)).read();
    
    // save
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    TopicMapIF newtm = store.getTopicMap();

    Iterator iterator = topicmap.getTopics().iterator();
    while (iterator.hasNext()) {
      TopicIF topic = (TopicIF) iterator.next();
      MergeUtils.mergeInto(newtm, topic);
    }

    new CanonicalTopicMapWriter(outfile).write(newtm);
    Assert.assertTrue("Topic map created by merging over topics not equal to original",
               TestFileUtils.compareFileToResource(outfile, baseline));
  }

  @Test
  public void testMergeReifiedNames() {
    TopicIF t1 = builder1.makeTopic();
    TopicNameIF bn1 = builder1.makeTopicName(t1, "bn1");
    TopicIF r1 = builder1.makeTopic();
    bn1.setReifier(r1);
    builder1.makeTopicName(r1, "reifier1");

    TopicIF t2 = builder1.makeTopic();
    TopicNameIF bn2 = builder1.makeTopicName(t2, "bn1");
    TopicIF r2 = builder1.makeTopic();
    bn2.setReifier(r2);
    builder1.makeTopicName(r2, "reifier2");

    MergeUtils.mergeInto(t1, t2);
    
    Assert.assertTrue("wrong number of base names after merge",
               t1.getTopicNames().size() == 1);
            
    Assert.assertTrue("original base name lost",
               t1.getTopicNames().contains(bn1));

    bn1 = (TopicNameIF) t1.getTopicNames().iterator().next();
    r1 = bn1.getReifier();

    Assert.assertTrue("reifier lost", r1 != null);

    Assert.assertTrue("wrong number of names on reifier: " + r1.getTopicNames().size(),
               r1.getTopicNames().size() == 2);
  }

  @Test
  public void testMergeReifiedOccurrences() {
    TopicIF occtype = builder1.makeTopic();
    
    TopicIF t1 = builder1.makeTopic();
    OccurrenceIF occ1 = builder1.makeOccurrence(t1, occtype, "occ1");
    TopicIF r1 = builder1.makeTopic();
    occ1.setReifier(r1);
    builder1.makeTopicName(r1, "reifier1");

    TopicIF t2 = builder1.makeTopic();
    OccurrenceIF occ2 = builder1.makeOccurrence(t2, occtype, "occ1");
    TopicIF r2 = builder1.makeTopic();
    occ2.setReifier(r2);
    builder1.makeTopicName(r2, "reifier2");

    MergeUtils.mergeInto(t1, t2);
    
    Assert.assertTrue("wrong number of occurrences after merge",
               t1.getOccurrences().size() == 1);
            
    Assert.assertTrue("original occurrence lost",
               t1.getOccurrences().contains(occ1));

    occ1 = (OccurrenceIF) t1.getOccurrences().iterator().next();
    r1 = occ1.getReifier();

    Assert.assertTrue("reifier lost", r1 != null);

    Assert.assertTrue("wrong number of names on reifier: " + r1.getTopicNames().size(),
               r1.getTopicNames().size() == 2);
  }

  @Test
  public void testMergeAssociationReifiers() {
    // the idea here is: what happens if you attempt to merge two topics
    // which reify duplicate associations?
    
    TopicIF atype = builder1.makeTopic();
    AssociationIF assoc1 = builder1.makeAssociation(atype);
    AssociationIF assoc2 = builder1.makeAssociation(atype);
    TopicIF player = builder1.makeTopic();
    TopicIF type = builder1.makeTopic();
    builder1.makeAssociationRole(assoc1, type, player);
    builder1.makeAssociationRole(assoc2, type, player);

    // the two associations should be equal

    TopicIF r1 = builder1.makeTopic();
    assoc1.setReifier(r1);
    TopicIF r2 = builder1.makeTopic();
    assoc2.setReifier(r2);
    
    MergeUtils.mergeInto(r1, r2);
    
    Assert.assertTrue("wrong number of roles after merge",
               player.getRoles().size() == 1);
  }

  @Test
  public void mergeReificationTest() throws IOException {
    String targetFile = TestFileUtils.getTestInputFile("various", "merge-reified-target.ltm");

    TopicMapIF targetTopicmap = new LTMTopicMapReader(TestFileUtils.getTestInputURL(targetFile)).read();
    TopicMapIF sourceTopicmap = new LTMTopicMapReader(TestFileUtils.getTestInputURL(targetFile)).read();
    
    // should succeed
    MergeUtils.mergeInto(targetTopicmap, sourceTopicmap);
    
    Assert.assertEquals(2, targetTopicmap.getAssociations().size());    
  }
}

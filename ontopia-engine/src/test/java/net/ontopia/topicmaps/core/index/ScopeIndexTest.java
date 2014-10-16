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
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;

public abstract class ScopeIndexTest extends AbstractIndexTest {

  protected ScopeIndexIF ix;
  protected TopicIF theme1, theme2;

  public ScopeIndexTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    ix = (ScopeIndexIF)super.setUp("ScopeIndexIF");
    theme1 = builder.makeTopic();
    theme2 = builder.makeTopic();
  }

  public void testAssociationIndex() {
    AssociationIF scoped1 = builder.makeAssociation(builder.makeTopic());
    AssociationIF scoped2 = builder.makeAssociation(builder.makeTopic());
    AssociationIF scoped3 = builder.makeAssociation(builder.makeTopic());

    assertTrue("Number of unscoped associations is not 3",
           ix.getAssociations(null).size() == 3);
    assertTrue("Index of associations by scope is not empty",
           ix.getAssociations(theme1).isEmpty() &&
           ix.getAssociations(theme2).isEmpty());
    assertTrue("AssociationThemes is not empty",
           ix.getAssociationThemes().isEmpty());
    assertTrue("theme1 indexed as AssociationTheme",
           !ix.usedAsAssociationTheme(theme1));
    assertTrue("theme2 indexed as AssociationTheme",
           !ix.usedAsAssociationTheme(theme2));

    scoped1.addTheme(theme1);
    scoped2.addTheme(theme2);
    scoped3.addTheme(theme1);
    scoped3.addTheme(theme2);

    assertTrue("Number of unscoped associations is not 0",
           ix.getAssociations(null).size() == 0);
    assertTrue("Index of associations by theme1 is empty",
           ix.getAssociations(theme1).size() == 2);
    assertTrue("Index of associations by theme2 is empty",
           ix.getAssociations(theme2).size() == 2);

    assertTrue("AssociationThemes is empty",
           ix.getAssociationThemes().size() == 2);
    assertTrue("theme1 not indexed as AssociationTheme",
           ix.usedAsAssociationTheme(theme1));
    assertTrue("theme2 not indexed as AssociationTheme",
           ix.usedAsAssociationTheme(theme2));

    assertTrue("scoped1 not indexed against theme1",
           ix.getAssociations(theme1).contains(scoped1));
    assertTrue("scoped3 not indexed against theme1",
           ix.getAssociations(theme1).contains(scoped3));
    assertTrue("scoped2 not indexed against theme2",
           ix.getAssociations(theme2).contains(scoped2));
    assertTrue("scoped3 not indexed against theme2",
           ix.getAssociations(theme2).contains(scoped3));
  }

  public void testTopicNameIndex() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF scoped1 = builder.makeTopicName(topic, "");
    TopicNameIF scoped2 = builder.makeTopicName(topic, "");
    TopicNameIF scoped3 = builder.makeTopicName(topic, "");
    
    assertTrue("Number of unscoped basenames is not 3",
           ix.getTopicNames(null).size() == 3);
    assertTrue("Index of TopicNames by scope is not empty",
           ix.getTopicNames(theme1).isEmpty() &&
           ix.getTopicNames(theme2).isEmpty());
    assertTrue("TopicNameThemes is not empty",
           ix.getTopicNameThemes().isEmpty());
    assertTrue("theme1 indexed as TopicNameTheme",
           !ix.usedAsTopicNameTheme(theme1));
    assertTrue("theme2 indexed as TopicNameTheme",
           !ix.usedAsTopicNameTheme(theme2));

    scoped1.addTheme(theme1);
    scoped2.addTheme(theme2);
    scoped3.addTheme(theme1);
    scoped3.addTheme(theme2);

    assertTrue("Number of unscoped basenames is not 0",
           ix.getTopicNames(null).size() == 0);
    assertTrue("Index of TopicNames by theme1 is empty",
           ix.getTopicNames(theme1).size() == 2);
    assertTrue("Index of TopicNames by theme2 is empty",
           ix.getTopicNames(theme2).size() == 2);
    
    assertTrue("TopicNameThemes is empty",
           ix.getTopicNameThemes().size() == 2);
    assertTrue("theme1 not in TopicNameThemes",
           ix.getTopicNameThemes().contains(theme1));
    assertTrue("theme2 not in TopicNameThemes",
           ix.getTopicNameThemes().contains(theme2));
    assertTrue("theme1 not indexed as TopicNameTheme",
           ix.usedAsTopicNameTheme(theme1));
    assertTrue("theme2 not indexed as TopicNameTheme",
           ix.usedAsTopicNameTheme(theme2));

    assertTrue("scoped1 not indexed against theme1",
           ix.getTopicNames(theme1).contains(scoped1));
    assertTrue("scoped3 not indexed against theme1",
           ix.getTopicNames(theme1).contains(scoped3));
    assertTrue("scoped2 not indexed against theme2",
           ix.getTopicNames(theme2).contains(scoped2));
    assertTrue("scoped3 not indexed against theme2",
           ix.getTopicNames(theme2).contains(scoped3));
  }

  public void testOccurrenceIndex() {
    TopicIF topic = builder.makeTopic();
    TopicIF otype = builder.makeTopic();
    OccurrenceIF scoped1 = builder.makeOccurrence(topic, otype, "");
    OccurrenceIF scoped2 = builder.makeOccurrence(topic, otype, "");
    OccurrenceIF scoped3 = builder.makeOccurrence(topic, otype, "");

    assertTrue("Number of unscoped occurrences is not 3",
           ix.getOccurrences(null).size() == 3);
    assertTrue("Index of Occurrences by scope is not empty",
           ix.getOccurrences(theme1).isEmpty() &&
           ix.getOccurrences(theme2).isEmpty());
    assertTrue("OccurrenceThemes is not empty",
           ix.getOccurrenceThemes().isEmpty());
    assertTrue("theme1 indexed as OccurrenceTheme",
           !ix.usedAsOccurrenceTheme(theme1));
    assertTrue("theme2 indexed as OccurrenceTheme",
           !ix.usedAsOccurrenceTheme(theme2));

    scoped1.addTheme(theme1);
    scoped2.addTheme(theme2);
    scoped3.addTheme(theme1);
    scoped3.addTheme(theme2);

    assertTrue("Number of unscoped occurrences is not 0",
           ix.getOccurrences(null).size() == 0);
    assertTrue("Index of Occurrences by theme1 is empty",
           ix.getOccurrences(theme1).size() == 2);
    assertTrue("Index of Occurrences by theme2 is empty",
           ix.getOccurrences(theme2).size() == 2);

    assertTrue("OccurrenceThemes is empty",
           ix.getOccurrenceThemes().size() == 2);
    assertTrue("theme1 not in OccurrenceThemes",
           ix.getOccurrenceThemes().contains(theme1));
    assertTrue("theme2 not in OccurrenceThemes",
           ix.getOccurrenceThemes().contains(theme2));
    assertTrue("theme1 not indexed as OccurrenceTheme",
           ix.usedAsOccurrenceTheme(theme1));
    assertTrue("theme2 not indexed as OccurrenceTheme",
           ix.usedAsOccurrenceTheme(theme2));


    assertTrue("scoped1 not indexed against theme1",
           ix.getOccurrences(theme1).contains(scoped1));
    assertTrue("scoped3 not indexed against theme1",
           ix.getOccurrences(theme1).contains(scoped3));
    assertTrue("scoped2 not indexed against theme2",
           ix.getOccurrences(theme2).contains(scoped2));
    assertTrue("scoped3 not indexed against theme2",
           ix.getOccurrences(theme2).contains(scoped3));
  }

  public void testVariantNameIndex() {
    TopicIF topic = builder.makeTopic();
    TopicNameIF bn = builder.makeTopicName(topic, "");
    VariantNameIF scoped1 = builder.makeVariantName(bn, "", Collections.<TopicIF>emptySet());
    VariantNameIF scoped2 = builder.makeVariantName(bn, "", Collections.<TopicIF>emptySet());
    VariantNameIF scoped3 = builder.makeVariantName(bn, "", Collections.<TopicIF>emptySet());

    assertTrue("Number of unscoped variants is not 3",
           ix.getVariants(null).size() == 3);
    assertTrue("Index of Variants by scope is not empty",
           ix.getVariants(theme1).isEmpty() &&
           ix.getVariants(theme2).isEmpty());
    assertTrue("VariantNameThemes is not empty",
           ix.getVariantThemes().isEmpty());
    assertTrue("theme1 indexed as VariantTheme",
           !ix.usedAsVariantTheme(theme1));
    assertTrue("theme2 indexed as VariantTheme",
           !ix.usedAsVariantTheme(theme2));

    scoped1.addTheme(theme1);
    scoped2.addTheme(theme2);
    scoped3.addTheme(theme1);
    scoped3.addTheme(theme2);

    assertTrue("Number of unscoped variants is not 0",
           ix.getVariants(null).size() == 0);
    assertTrue("Index of Variants by theme1 is empty",
           ix.getVariants(theme1).size() == 2);
    assertTrue("Index of Variants by theme2 is empty",
           ix.getVariants(theme2).size() == 2);

    assertTrue("VariantThemes is empty",
           ix.getVariantThemes().size() == 2);
    assertTrue("theme1 not in VariantThemes",
           ix.getVariantThemes().contains(theme1));
    assertTrue("theme2 not in VariantThemes",
           ix.getVariantThemes().contains(theme2));
    assertTrue("theme1 not indexed as VariantTheme",
           ix.usedAsVariantTheme(theme1));
    assertTrue("theme2 not indexed as VariantTheme",
           ix.usedAsVariantTheme(theme2));

    assertTrue("scoped1 not indexed against theme1",
           ix.getVariants(theme1).contains(scoped1));
    assertTrue("scoped3 not indexed against theme1",
           ix.getVariants(theme1).contains(scoped3));
    assertTrue("scoped2 not indexed against theme2",
           ix.getVariants(theme2).contains(scoped2));
    assertTrue("scoped3 not indexed against theme2",
           ix.getVariants(theme2).contains(scoped3));
  }

  public void _testNullParameters() {
    testNull("getAssociations", "net.ontopia.topicmaps.core.TopicIF");
    testNull("getTopicNames", "net.ontopia.topicmaps.core.TopicIF");
    testNull("getOccurrences", "net.ontopia.topicmaps.core.TopicIF");
    testNull("getVariants", "net.ontopia.topicmaps.core.TopicIF");
    testNull("usedAsAssociationTheme", "net.ontopia.topicmaps.core.TopicIF");
    testNull("usedAsTopicNameTheme", "net.ontopia.topicmaps.core.TopicIF");
    testNull("usedAsOccurrenceTheme", "net.ontopia.topicmaps.core.TopicIF");
    testNull("usedAsVariantTheme", "net.ontopia.topicmaps.core.TopicIF");
  }

}






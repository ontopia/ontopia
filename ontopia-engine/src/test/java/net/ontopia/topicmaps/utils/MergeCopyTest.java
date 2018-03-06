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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MergeCopyTest {
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

  public void onlyContains(String what, Collection coll, Object element) {
    Assert.assertTrue(what + " collection has wrong number of elements",
               coll.size() == 1);

    Assert.assertTrue(what + " collection contains wrong element",
               coll.iterator().next().equals(element));
  }
    
  // --- Test cases for mergeInto(TopicMapIF, TopicIF)

  @Test
  public void testMergeEmptyTopics() {
    TopicIF topic = builder2.makeTopic();

    MergeUtils.mergeInto(topicmap1, topic);
    
    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 1);
    topic = (TopicIF) topicmap1.getTopics().iterator().next();
    Assert.assertTrue("empty topic suddenly has source locators",
               topic.getItemIdentifiers().isEmpty());
    Assert.assertTrue("empty topic suddenly has subject indicators",
               topic.getSubjectIdentifiers().isEmpty());
    Assert.assertTrue("empty topic suddenly has subject address",
               topic.getSubjectLocators().isEmpty());
    Assert.assertTrue("empty topic suddenly has base names",
               topic.getTopicNames().isEmpty());
    Assert.assertTrue("empty topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("empty topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("empty topic suddenly has types",
               topic.getTypes().isEmpty());
  }

  @Test
  public void testMergeTopicWithURIs() {
    TopicIF topic = builder2.makeTopic();
    topic.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    topic.addSubjectLocator(makeLocator("http://www.example.com"));
    topic.addItemIdentifier(makeLocator("http://www.ontopia.com"));

    MergeUtils.mergeInto(topicmap1, topic);
    
    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 1);
    topic = (TopicIF) topicmap1.getTopics().iterator().next();
    onlyContains("source locator", topic.getItemIdentifiers(),
                 makeLocator("http://www.ontopia.com"));
    onlyContains("subject indicator", topic.getSubjectIdentifiers(),
                 makeLocator("http://www.ontopia.net"));
    Assert.assertTrue("topic has wrong subject address",
               topic.getSubjectLocators().contains(makeLocator("http://www.example.com")));
    
    Assert.assertTrue("empty topic suddenly has base names",
               topic.getTopicNames().isEmpty());
    Assert.assertTrue("empty topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("empty topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("empty topic suddenly has types",
               topic.getTypes().isEmpty());
  }  

  @Test
  public void testMergeTopicsWithURIs() {
    TopicIF topic1 = builder1.makeTopic();
    topic1.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    topic1.addSubjectLocator(makeLocator("http://www.example.com"));
    topic1.addItemIdentifier(makeLocator("http://www.ontopia.com"));
    
    TopicIF topic2 = builder2.makeTopic();
    topic2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    topic2.addSubjectLocator(makeLocator("http://www.example.com"));
    topic2.addItemIdentifier(makeLocator("http://www.ontopia.com"));

    MergeUtils.mergeInto(topicmap1, topic2);

    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 1);
    
    TopicIF topic = (TopicIF) topicmap1.getTopics().iterator().next();
    onlyContains("source locator", topic.getItemIdentifiers(),
                 makeLocator("http://www.ontopia.com"));
    onlyContains("subject indicator", topic.getSubjectIdentifiers(),
                 makeLocator("http://www.ontopia.net"));
    Assert.assertTrue("topic has wrong subject address",
               topic.getSubjectLocators().contains(makeLocator("http://www.example.com")));
    
    Assert.assertTrue("empty topic suddenly has base names",
               topic.getTopicNames().isEmpty());
    Assert.assertTrue("empty topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("empty topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("empty topic suddenly has types",
               topic.getTypes().isEmpty());
  }  


  @Test
  public void testTopicOtherObjectCollision() {
    TopicIF topic1 = builder1.makeTopic();
    TopicNameIF bn = builder1.makeTopicName(topic1, "");
    bn.addItemIdentifier(makeLocator("http://www.ontopia.com"));
    
    TopicIF topic2 = builder2.makeTopic();
    topic2.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    topic2.addSubjectLocator(makeLocator("http://www.example.com"));
    topic2.addItemIdentifier(makeLocator("http://www.ontopia.com"));

    try {
      MergeUtils.mergeInto(topicmap1, topic2);
      Assert.fail("collision not detected");
    } catch (ConstraintViolationException e) {
    }
  }  

  @Test
  public void testTopicNameCopying() {
    TopicIF topic = builder2.makeTopic();
    builder2.makeTopicName(topic, "The topic");

    MergeUtils.mergeInto(topicmap1, topic);
    
    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 2);

    // a bit tricky, but of the two topics in the TM (the copied one
    // and default name type) we have to pick the copied one, which
    // has no identifier
    TopicIF defnametype = topicmap1.getTopicBySubjectIdentifier(PSI.getSAMNameType());
    Iterator it = topicmap1.getTopics().iterator();
    topic = (TopicIF) it.next();
    if (topic.equals(defnametype)) {
      topic = (TopicIF) it.next();
    }
      
    Assert.assertTrue("empty topic suddenly has source locators",
               topic.getItemIdentifiers().isEmpty());
    Assert.assertTrue("empty topic suddenly has subject indicators",
               topic.getSubjectIdentifiers().isEmpty());
    Assert.assertTrue("empty topic suddenly has subject address",
               topic.getSubjectLocators().isEmpty());
    Assert.assertTrue("empty topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("empty topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("empty topic suddenly has types",
               topic.getTypes().isEmpty());
    
    Assert.assertTrue("topic lost base name",
               topic.getTopicNames().size() == 1);
    TopicNameIF bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    Assert.assertTrue("base name has variants",
               bn.getVariants().isEmpty());
    Assert.assertTrue("base name has wrong value",
               bn.getValue().equals("The topic"));
    Assert.assertTrue("base name has non-empty scope",
               bn.getScope().isEmpty());
  }

  @Test
  public void testTopicNameScopeCopying() {
    TopicIF topic = builder2.makeTopic();
    topic.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));
    TopicNameIF bn = builder2.makeTopicName(topic, "The topic");
    
    TopicIF theme = builder2.makeTopic();
    theme.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    builder2.makeTopicName(theme, "The theme");
    bn.addTheme(theme);

    MergeUtils.mergeInto(topicmap1, topic);
    
    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 3); // topic + theme + default name type
    topic = topicmap1.getTopicBySubjectIdentifier(makeLocator("http://www.ontopia.com"));
    Assert.assertTrue("empty topic suddenly has source locators",
               topic.getItemIdentifiers().isEmpty());
    onlyContains("subject indicators",
                 topic.getSubjectIdentifiers(),
                 makeLocator("http://www.ontopia.com"));
    Assert.assertTrue("empty topic suddenly has subject address",
               topic.getSubjectLocators().isEmpty());
    Assert.assertTrue("empty topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("empty topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("empty topic suddenly has types",
               topic.getTypes().isEmpty());
    
    Assert.assertTrue("topic lost base name",
               topic.getTopicNames().size() == 1);
    bn = (TopicNameIF) topic.getTopicNames().iterator().next();
    Assert.assertTrue("base name has variants",
               bn.getVariants().isEmpty());
    Assert.assertTrue("base name has wrong value",
               bn.getValue().equals("The topic"));

    Assert.assertTrue("base name scope has wrong size",
               bn.getScope().size() == 1);
    topic = (TopicIF) bn.getScope().iterator().next();
    Assert.assertTrue("theme suddenly has base names",
               topic.getTopicNames().isEmpty());    
    Assert.assertTrue("theme suddenly has source locators",
               topic.getItemIdentifiers().isEmpty());
    Assert.assertTrue("theme suddenly has subject address",
               topic.getSubjectLocators().isEmpty());

    onlyContains("theme subject indicator",
                 topic.getSubjectIdentifiers(),
                 makeLocator("http://www.ontopia.net"));
  }

  @Test
  public void testTopicNameScopeCopyingFalse() {
    TopicIF topic = builder2.makeTopic();
    topic.addSubjectIdentifier(makeLocator("http://www.ontopia.com"));
    TopicNameIF bn = builder2.makeTopicName(topic, "The topic");
    
    TopicIF theme = builder2.makeTopic();
    theme.addSubjectIdentifier(makeLocator("http://www.ontopia.net"));
    builder2.makeTopicName(theme, "The theme");
    bn.addTheme(theme);

    MergeUtils.mergeInto(topicmap1, topic, (o) -> false);
    
    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 1); // topic
    topic = topicmap1.getTopicBySubjectIdentifier(makeLocator("http://www.ontopia.com"));
    Assert.assertTrue("empty topic suddenly has source locators",
               topic.getItemIdentifiers().isEmpty());
    onlyContains("subject indicators",
                 topic.getSubjectIdentifiers(),
                 makeLocator("http://www.ontopia.com"));
    Assert.assertTrue("empty topic suddenly has subject address",
               topic.getSubjectLocators().isEmpty());
    Assert.assertTrue("empty topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("empty topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("empty topic suddenly has types",
               topic.getTypes().isEmpty());
    
    Assert.assertTrue("topic has base names, despite using false decider",
               topic.getTopicNames().isEmpty());
  }

  @Test
  public void testVariantNames() {
    TopicIF topic = builder2.makeTopic();
    topic.addSubjectLocator(makeLocator("http://www.ontopia.com"));
    TopicNameIF bn = builder2.makeTopicName(topic, "The Ontopia Website");
    VariantNameIF vn = builder2.makeVariantName(bn, "ontopia website, the", Collections.emptySet());
    
    TopicIF theme = builder2.makeTopic();
    theme.addSubjectIdentifier(PSI.getXTMSort());
    builder2.makeTopicName(theme, "Sort name");
    vn.addTheme(theme);

    MergeUtils.mergeInto(topicmap1, topic, (o) -> true);
    
    Assert.assertTrue("topic map has wrong number of topics after merge",
               topicmap1.getTopics().size() == 3); // topic + theme + default name type
    topic = topicmap1.getTopicBySubjectLocator(makeLocator("http://www.ontopia.com"));
    theme = topicmap1.getTopicBySubjectIdentifier(PSI.getXTMSort());

    Assert.assertTrue("can't find test topic after merge", topic != null);
    Assert.assertTrue("can't find theme after merge", theme != null);
    
    Assert.assertTrue("test topic suddenly has source locators",
               topic.getItemIdentifiers().isEmpty());
    Assert.assertTrue("test topic suddenly has subject indicators",
               topic.getSubjectIdentifiers().isEmpty());
    Assert.assertTrue("test topic has lost subject locator",
               topic.getSubjectLocators().contains(makeLocator("http://www.ontopia.com")));

    Assert.assertTrue("theme topic suddenly has source locators",
               theme.getItemIdentifiers().isEmpty());
    onlyContains("subject indicator", theme.getSubjectIdentifiers(),
                 PSI.getXTMSort());
    Assert.assertTrue("theme topic suddenly has subject locator",
               theme.getSubjectLocators().isEmpty());
    
    Assert.assertTrue("test topic suddenly has occurrences",
               topic.getOccurrences().isEmpty());
    Assert.assertTrue("test topic suddenly has roles",
               topic.getRoles().isEmpty());
    Assert.assertTrue("test topic suddenly has types",
               topic.getTypes().isEmpty());
    
    Assert.assertTrue("test topic has lost base names",
               !topic.getTopicNames().isEmpty());

    bn = (TopicNameIF) topic.getTopicNames().iterator().next();

    Assert.assertTrue("test topic has lost variant name",
               bn.getVariants().size() == 1);

    vn = (VariantNameIF) bn.getVariants().iterator().next();

    Assert.assertTrue("variant name has lost scope",
               vn.getScope().size() == 1 && vn.getScope().contains(theme));
    Assert.assertTrue("variant name has value",
               vn.getValue().equals("ontopia website, the"));
  }  
}

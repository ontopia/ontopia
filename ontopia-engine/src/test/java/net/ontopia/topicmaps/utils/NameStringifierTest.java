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

import java.util.Collections;
import java.util.function.Function;
import net.ontopia.topicmaps.core.NameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NameStringifierTest {
  protected TopicMapIF        topicmap; 
  protected TopicIF           topic;
  protected TopicNameIF        basename;
  protected VariantNameIF     variant;
  protected TopicMapBuilderIF builder;
  protected Function<NameIF, String> stringifier;

  @Before
  public void setUp() {
    topicmap = makeTopicMap();
    topic = builder.makeTopic();
    basename = builder.makeTopicName(topic, "");
    variant = builder.makeVariantName(basename, "", Collections.emptySet());
    stringifier = new NameStringifier();
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
  // --- Test cases

  @Test
  public void testTopicNameEmpty() {
    Assert.assertTrue("base name with no name did not stringify to \"\"",
							 stringifier.apply(basename).equals(""));
  }

  @Test
  public void testTopicName() {
    basename.setValue("basename");
    Assert.assertTrue("base name stringified wrongly",
           stringifier.apply(basename).equals("basename"));
  }

  @Test
  public void testVariantEmpty() {
    Assert.assertTrue("variant with no name did not stringify to \"\"",
							 stringifier.apply(variant).equals(""));
  }

  @Test
  public void testVariant() {
    variant.setValue("variant");
    Assert.assertTrue("variant stringified wrongly",
           stringifier.apply(variant).equals("variant"));
  }
  
}

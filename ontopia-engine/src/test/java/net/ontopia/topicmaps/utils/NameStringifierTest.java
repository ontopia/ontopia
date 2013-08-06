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

import junit.framework.TestCase;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.StringifierIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

public class NameStringifierTest extends TestCase {
  protected TopicMapIF        topicmap; 
  protected TopicIF           topic;
  protected TopicNameIF        basename;
  protected VariantNameIF     variant;
  protected TopicMapBuilderIF builder;
  protected StringifierIF     stringifier;

  public NameStringifierTest(String name) {
    super(name);
  }
    
  public void setUp() {
    topicmap = makeTopicMap();
    topic = builder.makeTopic();
    basename = builder.makeTopicName(topic, "");
    variant = builder.makeVariantName(basename, "");
    stringifier = new NameStringifier();
  }
    
  protected TopicMapIF makeTopicMap() {
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();
    builder = store.getTopicMap().getBuilder();
    return store.getTopicMap();
  }
 
  // --- Test cases

  public void testTopicNameEmpty() {
    assertTrue("base name with no name did not stringify to \"\"",
							 stringifier.toString(basename).equals(""));
  }

  public void testTopicName() {
    basename.setValue("basename");
    assertTrue("base name stringified wrongly",
           stringifier.toString(basename).equals("basename"));
  }

  public void testVariantEmpty() {
    assertTrue("variant with no name did not stringify to \"\"",
							 stringifier.toString(variant).equals(""));
  }

  public void testVariant() {
    variant.setValue("variant");
    assertTrue("variant stringified wrongly",
           stringifier.toString(variant).equals("variant"));
  }
  
}





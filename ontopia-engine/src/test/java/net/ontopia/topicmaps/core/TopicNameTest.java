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

package net.ontopia.topicmaps.core;

import java.util.Collections;

public abstract class TopicNameTest extends AbstractTypedScopedTest {
  protected TopicNameIF basename;
  
  public TopicNameTest(String name) {
    super(name);
  }
    
  // --- Test cases

  public void testReification() {
    TopicIF reifier = builder.makeTopic();
    ReifiableIF reifiable = basename;
    
    assertTrue("Object reified by the reifying topic was found",
               reifier.getReified() == null);
    assertTrue("Topic reifying the reifiable was found",
               reifiable.getReifier() == null);

    reifiable.setReifier(reifier);
    assertTrue("No topic reifying the reifiable was found",
               reifiable.getReifier() == reifier);
    assertTrue("No object reified by the reifying topic was found",
               reifier.getReified() == reifiable);
    
    reifiable.setReifier(null);
    assertTrue("Object reified by the reifying topic was found",
               reifier.getReified() == null);
    assertTrue("Topic reifying the first reifiable was found",
               reifiable.getReifier() == null);
  }

  public void testValue() {
    assertTrue("initial name value not \"\"", "".equals(basename.getValue()));

    basename.setValue("testfaen");
    assertTrue("name not set correctly",
               basename.getValue().equals("testfaen"));

    try {
      basename.setValue(null);
      fail("value could be set to null");
    } catch (NullPointerException e) {
    }
		
    basename.setValue("foo");
    assertTrue("name value not set to foo",
               "foo".equals(basename.getValue()));
  }

  public void testTopic() {
    if (parent instanceof TopicIF) {
      // we are now testing a basename
      assertTrue("parent is not right object",
                 parent.equals(basename.getTopic()));
    } else {
      VariantNameIF variant = (VariantNameIF) basename;
      assertTrue("parent and grandparent do not agree",
                 variant.getTopicName().getTopic().equals(basename.getTopic()));
    }
  }
    
  public void testVariants() {
    // STATE 1: no child variants
    assertTrue("initial variant child set not empty",
               basename.getVariants().size() == 0);

    // STATE 2: one child variant
    VariantNameIF variant = builder.makeVariantName(basename, "foo", Collections.singleton(builder.makeTopic()));
    // builder adds the child for us

    assertTrue("variant child of " + basename + " not added",
               basename.getVariants().size() == 1);

    assertTrue("variant child identity lost",
               basename.getVariants().iterator().next().equals(variant));

    // STATE 3: no children again
    variant.remove();
    assertTrue("variant child not removed",
               basename.getVariants().size() == 0);

    // removing non-existent variant to check that it does not complain
    variant.remove();
  }
    
  // --- Internal methods

  public void setUp() throws Exception {
    super.setUp();
    TopicIF topic = builder.makeTopic();
    parent = topic;
    basename = builder.makeTopicName(topic, "");
    scoped = basename;
    typed = basename;
    object = basename;
  }

  protected TMObjectIF makeObject() {
    TopicIF topic = builder.makeTopic();
    return builder.makeTopicName(topic, "");
  }
}

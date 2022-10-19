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

package net.ontopia.topicmaps.entry;

import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.topicmaps.utils.SameStoreFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultTopicMapSourceTest {
  private DefaultTopicMapSource source;

  @Before
  public void setUp() {
    source = new DefaultTopicMapSource();
  }
  
  // --- Test cases

  @Test
  public void testReferences() {
    Assert.assertTrue("source not empty by default",
           source.getReferences().size() == 0);

    TopicMapReferenceIF ref =
      new StoreFactoryReference("id", "title", new SameStoreFactory(new InMemoryTopicMapStore()));
    source.addReference(ref);

    Assert.assertTrue("source not registered with reference",
               ref.getSource() == source);

    Assert.assertTrue("source did not discover add",
           source.getReferences().size() == 1);
    Assert.assertTrue("reference identity lost",
           source.getReferences().iterator().next() == ref);

    source.removeReference(ref);
    
    Assert.assertTrue("source not deregistered with reference",
               ref.getSource() == null);
    
  }
}

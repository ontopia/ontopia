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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TopicMapSourceManagerTest {
  private TopicMapSourceManager manager;

  @Before
  public void setUp() {
    manager = new TopicMapSourceManager();
  }
  
  // --- Test cases

  @Test
  public void testEmpty() {
    verifyEmptyManager("");
    manager.refresh();
    verifyEmptyManager(" after refresh");
  }

  @Test
  public void testSingleSourceSingleRef() {
    DefaultTopicMapSource source = new DefaultTopicMapSource();
    DefaultTopicMapReference ref =
      new DefaultTopicMapReference("id", "title", new InMemoryTopicMapStore());
    source.addReference(ref);
    manager.addSource(source);
    manager.refresh();

    Assert.assertTrue("id collection of wrong size",
           manager.getIds().size() == 1);
    Assert.assertTrue("id of reference lost",
           manager.getIds().contains("id"));
                       
    Assert.assertTrue("reference not found",
           manager.getReferenceById("id") == ref);
    
    Assert.assertTrue("reference collection of wrong size",
           manager.getReferences().size() == 1);
    Assert.assertTrue("reference identity lost",
           manager.getReferences().contains(ref));
    
    Assert.assertTrue("source collection of wrong size",
           manager.getSources().size() == 1);
    Assert.assertTrue("source lost",
           manager.getSources().contains(source));

    manager.removeSource(source);
    manager.refresh();
    verifyEmptyManager(" after removal");

    manager.removeSource(source); // checking that it's OK
  }

  @Test
  public void testSingleSourceDoubleRef() {
    DefaultTopicMapSource source = new DefaultTopicMapSource();
    DefaultTopicMapReference ref =
      new DefaultTopicMapReference("id", "title", new InMemoryTopicMapStore());
    DefaultTopicMapReference ref2 =
      new DefaultTopicMapReference("id2", "title", new InMemoryTopicMapStore());
    source.addReference(ref);
    source.addReference(ref2);
    manager.addSource(source);
    manager.refresh();

    Assert.assertTrue("id collection of wrong size",
           manager.getIds().size() == 2);
    Assert.assertTrue("id of reference lost",
           manager.getIds().contains("id") &&
           manager.getIds().contains("id2"));
    
    Assert.assertTrue("reference not found",
           manager.getReferenceById("id") == ref &&
           manager.getReferenceById("id2") == ref2 );
    
    Assert.assertTrue("reference collection of wrong size",
           manager.getReferences().size() == 2);
    Assert.assertTrue("reference identity lost",
           manager.getReferences().contains(ref) &&
           manager.getReferences().contains(ref2));
    
    Assert.assertTrue("source collection of wrong size",
           manager.getSources().size() == 1);
    Assert.assertTrue("source lost",
           manager.getSources().contains(source));

    manager.removeSource(source);
    manager.refresh();
    verifyEmptyManager(" after removal");

    manager.removeSource(source); // checking that it's OK
  }

  @Test
  public void testDoubleSourceSingleRef() {
    DefaultTopicMapSource source = new DefaultTopicMapSource();
    DefaultTopicMapReference ref =
      new DefaultTopicMapReference("id", "title", new InMemoryTopicMapStore());
    source.addReference(ref);
    manager.addSource(source);
    
    DefaultTopicMapSource source2 = new DefaultTopicMapSource();
    DefaultTopicMapReference ref2 =
      new DefaultTopicMapReference("id2", "title", new InMemoryTopicMapStore());
    source2.addReference(ref2);
    manager.addSource(source2);
    manager.refresh();
    
    Assert.assertTrue("id collection of wrong size",
           manager.getIds().size() == 2);
    Assert.assertTrue("id of reference lost",
           manager.getIds().contains("id") &&
           manager.getIds().contains("id2"));
    
    Assert.assertTrue("reference not found",
           manager.getReferenceById("id") == ref &&
           manager.getReferenceById("id2") == ref2 );
    
    Assert.assertTrue("reference collection of wrong size",
           manager.getReferences().size() == 2);
    Assert.assertTrue("reference identity lost",
           manager.getReferences().contains(ref) &&
           manager.getReferences().contains(ref2));
    
    Assert.assertTrue("source collection of wrong size",
           manager.getSources().size() == 2);
    Assert.assertTrue("source lost",
           manager.getSources().contains(source) &&
           manager.getSources().contains(source2));

    manager.removeSource(source);
    manager.removeSource(source2);
    manager.refresh();
    verifyEmptyManager(" after removal");

    manager.removeSource(source); // checking that it's OK
  }

  
  // --- INTERNAL METHODS

  private void verifyEmptyManager(String suffix) {
    Assert.assertTrue("id collection not empty" + suffix,
           manager.getIds().size() == 0);

    Assert.assertTrue("non-existent reference found" + suffix,
           manager.getReferenceById("rongobongo") == null);
    
    Assert.assertTrue("reference collection not empty" + suffix,
           manager.getReferences().size() == 0);
    
    Assert.assertTrue("source collection not empty" + suffix,
           manager.getSources().size() == 0);
  }
}

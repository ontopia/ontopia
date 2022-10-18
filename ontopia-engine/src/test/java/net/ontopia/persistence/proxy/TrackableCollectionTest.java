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

package net.ontopia.persistence.proxy;

import net.ontopia.topicmaps.impl.rdbms.Topic;
import org.junit.Assert;
import org.junit.Test;

public abstract class TrackableCollectionTest {
  
  protected abstract TrackableCollectionIF<Object> createTrackableCollection();
    
  // --- Test cases
  
  @Test
  public void testAddRemove() {
    // Bug#627: adding and removing the same object before the
    // added/removed member(s) was initialized led to the object being
    // registered as either added or removed when it should be
    // neither.
    TrackableCollectionIF<Object> set = createTrackableCollection();
    Object obj1 = new Object();
    Object obj2 = new Object();

    Assert.assertTrue("size of trackable set is not 0", set.size() == 0);
    set.addWithTracking(obj1);
    Assert.assertTrue("size of trackable set is not 1", set.size() == 1);
    set.addWithTracking(obj1);
    Assert.assertTrue("size of trackable set is not 1", set.size() == 1);
    set.addWithTracking(obj2);
    Assert.assertTrue("size of trackable set is not 2", set.size() == 2);
    set.addWithTracking(obj2);
    Assert.assertTrue("size of trackable set is not 2", set.size() == 2);

    Assert.assertTrue("size of trackable set added is not 2", set.getAdded().size() == 2);

    set.removeWithTracking(obj1);
    Assert.assertTrue("size of trackable set is not 1", set.size() == 1);
    set.removeWithTracking(obj1);
    Assert.assertTrue("size of trackable set is not 1", set.size() == 1);
    set.removeWithTracking(obj2);
    Assert.assertTrue("size of trackable set is not 0", set.size() == 0);
    set.removeWithTracking(obj2);
    Assert.assertTrue("size of trackable set is not 0", set.size() == 0);

    Assert.assertTrue("size of trackable set added is not 0 (" + set.getAdded() + ")",
               set.getAdded() == null || set.getAdded().isEmpty());
    Assert.assertTrue("size of trackable set removed is not 0 (" + set.getRemoved() + ")",
               set.getRemoved() == null || set.getRemoved().isEmpty());
    
    set.addWithTracking(obj2);
    set.clearWithTracking();
    Assert.assertTrue("size of trackable set is not 0", set.size() == 0);
  }
  
  @Test
  public void testClear() {
    // Test clear() method
    TrackableCollectionIF<Object> set = createTrackableCollection();

    Object obj1 = "1";
    Object obj2 = "2";
    Object obj3 = "3";
    Object obj4 = "4";
    Object obj5 = "5";
    
    set.addWithTracking(obj1);
    set.addWithTracking(obj2);
    set.addWithTracking(obj3);
    set.addWithTracking(obj4);
    set.addWithTracking(obj5);
    set.removeWithTracking(obj1);
    set.removeWithTracking(obj3); // [[2,4,5] [2,4,5] []]

    set.clearWithTracking(); // [[], [] []]
    
    Assert.assertTrue("size of trackable set added is not 0", set.getAdded() == null || set.getAdded().isEmpty());
    Assert.assertTrue("size of trackable set removed is not 0", set.getRemoved() == null || set.getRemoved().isEmpty());
  }
  
  @Test
  public void testBug627() {
    // BUG: adding and removing the same object before the
    // added/removed member(s) was initialized led to the object being
    // registered as either added or removed when it should be
    // neither.
    TrackableCollectionIF<Object> set = createTrackableCollection();
    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    set.addWithTracking(obj1);
    set.addWithTracking(obj2);
    set.addWithTracking(obj3);
    
    Assert.assertTrue("size of trackable set is not 3", set.size() == 3);
    set.removeWithTracking(obj1);
    set.removeWithTracking(obj2);
    Assert.assertTrue("size of trackable set is not 1", set.size() == 1);
    set.addWithTracking(obj2);
    Assert.assertTrue("size of trackable set removed is not 0", set.getRemoved() == null);

    set.resetTracking();
    set.removeWithTracking(obj3);
    
    Assert.assertTrue("size of trackable set added is not 0", set.getAdded() == null);
    Assert.assertTrue("size of trackable set removed is not 1", set.getRemoved().size() == 1);

    set.clearWithTracking();
    Assert.assertTrue("size of trackable set is not 0", set.size() == 0);    
    Assert.assertTrue("size of trackable set removed is not 2", set.getRemoved().size() == 2);
  }
  
  @Test
  public void testStreamingIssue555() {
    TrackableCollectionIF<Object> set = createTrackableCollection();
    Topic topic = new Topic();
    topic._p_setIdentity(new LongIdentity(Topic.class, 41));
    set.addWithTracking(topic);

    set.stream().forEach((t) -> {
      Assert.assertTrue("Expected Topic, found " + t.getClass(), t instanceof Topic);
    });
  }
}

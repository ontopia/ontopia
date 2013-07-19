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

import junit.framework.TestCase;

public abstract class TrackableCollectionTest extends TestCase {
  
  public TrackableCollectionTest(String name) {
    super(name);
  }

  protected abstract TrackableCollectionIF createTrackableCollection();
    
  // --- Test cases
  
  public void testAddRemove() {
    // Bug#627: adding and removing the same object before the
    // added/removed member(s) was initialized led to the object being
    // registered as either added or removed when it should be
    // neither.
    TrackableCollectionIF set = createTrackableCollection();
    Object obj1 = new Object();
    Object obj2 = new Object();

    assertTrue("size of trackable set is not 0", set.size() == 0);
    set.addWithTracking(obj1);
    assertTrue("size of trackable set is not 1", set.size() == 1);
    set.addWithTracking(obj1);
    assertTrue("size of trackable set is not 1", set.size() == 1);
    set.addWithTracking(obj2);
    assertTrue("size of trackable set is not 2", set.size() == 2);
    set.addWithTracking(obj2);
    assertTrue("size of trackable set is not 2", set.size() == 2);

    assertTrue("size of trackable set added is not 2", set.getAdded().size() == 2);

    set.removeWithTracking(obj1);
    assertTrue("size of trackable set is not 1", set.size() == 1);
    set.removeWithTracking(obj1);
    assertTrue("size of trackable set is not 1", set.size() == 1);
    set.removeWithTracking(obj2);
    assertTrue("size of trackable set is not 0", set.size() == 0);
    set.removeWithTracking(obj2);
    assertTrue("size of trackable set is not 0", set.size() == 0);

    assertTrue("size of trackable set added is not 0 (" + set.getAdded() + ")",
               set.getAdded() == null || set.getAdded().isEmpty());
    assertTrue("size of trackable set removed is not 0 (" + set.getRemoved() + ")",
               set.getRemoved() == null || set.getRemoved().isEmpty());
    
    set.addWithTracking(obj2);
    set.clearWithTracking();
    assertTrue("size of trackable set is not 0", set.size() == 0);
  }
  
  public void testClear() {
    // Test clear() method
    TrackableCollectionIF set = createTrackableCollection();

    Object obj1 = new String("1");
    Object obj2 = new String("2");
    Object obj3 = new String("3");
    Object obj4 = new String("4");
    Object obj5 = new String("5");
    
    set.addWithTracking(obj1);
    set.addWithTracking(obj2);
    set.addWithTracking(obj3);
    set.addWithTracking(obj4);
    set.addWithTracking(obj5);
    set.removeWithTracking(obj1);
    set.removeWithTracking(obj3); // [[2,4,5] [2,4,5] []]

    set.clearWithTracking(); // [[], [] []]
    
    assertTrue("size of trackable set added is not 0", set.getAdded() == null || set.getAdded().isEmpty());
    assertTrue("size of trackable set removed is not 0", set.getRemoved() == null || set.getRemoved().isEmpty());
  }
  
  public void testBug627() {
    // BUG: adding and removing the same object before the
    // added/removed member(s) was initialized led to the object being
    // registered as either added or removed when it should be
    // neither.
    TrackableCollectionIF set = createTrackableCollection();
    Object obj1 = new Object();
    Object obj2 = new Object();
    Object obj3 = new Object();

    set.addWithTracking(obj1);
    set.addWithTracking(obj2);
    set.addWithTracking(obj3);
    
    assertTrue("size of trackable set is not 3", set.size() == 3);
    set.removeWithTracking(obj1);
    set.removeWithTracking(obj2);
    assertTrue("size of trackable set is not 1", set.size() == 1);
    set.addWithTracking(obj2);
    assertTrue("size of trackable set removed is not 0", set.getRemoved() == null);

    set.resetTracking();
    set.removeWithTracking(obj3);
    
    assertTrue("size of trackable set added is not 0", set.getAdded() == null);
    assertTrue("size of trackable set removed is not 1", set.getRemoved().size() == 1);

    set.clearWithTracking();
    assertTrue("size of trackable set is not 0", set.size() == 0);    
    assertTrue("size of trackable set removed is not 2", set.getRemoved().size() == 2);
  }
  
}

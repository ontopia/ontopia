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

package net.ontopia.utils;

import java.util.List;
import junit.framework.TestCase;

public class RingBufferTest extends TestCase {

  public RingBufferTest(String name) {
    super(name);
  }
  
  public void testExceedMaxSize() {
    RingBuffer rb = new RingBuffer(3);
    rb.addElement("item1");
    rb.addElement("item2");
    rb.addElement("item3");
    rb.addElement("item4");

    assertTrue("RingBuffer has exceeded max size", 
               rb.getElements().size() == 3);

    List list = rb.getElements();
    assertTrue("Wrong item at start of list: " + list.get(0),
               list.get(0).equals("item4"));
    assertTrue("Wrong item at end of list: " + list.get(2),
               list.get(2).equals("item2"));
  }

  public void testMoveToFront() {
    RingBuffer rb = new RingBuffer(3);

    rb.addElement("item1");
    rb.addElement("item2");
    rb.addElement("item1");

    List list = rb.getElements();
    assertTrue("RingBuffer has exceeded max size",
               list.size() == 2);
    assertTrue("Wrong item at start of list: " + list.get(0),
               list.get(0).equals("item1"));
    assertTrue("Wrong item at end of list: " + list.get(1),
               list.get(1).equals("item2"));
  }

  public void testClear() {
    RingBuffer rb = new RingBuffer(3);

    rb.addElement("item1");
    rb.addElement("item2");
    rb.addElement("item1");
    rb.clear();
    
    assertTrue("RingBuffer was not properly cleared",
               rb.getElements().isEmpty());
  }

  public void testDontMessWithTheList() {
    RingBuffer rb = new RingBuffer(3);

    rb.addElement("item1");
    rb.addElement("item2");
    rb.addElement("item3");
    rb.addElement("item4");

    rb.getElements().clear();
    
    assertTrue("RingBuffer should not be empty",
               !rb.getElements().isEmpty());
  }
}

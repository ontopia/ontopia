
// $Id: RingBufferTest.java,v 1.4 2003/08/29 12:37:12 larsga Exp $

package net.ontopia.utils;

import java.util.*;
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

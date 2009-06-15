
// $Id: RingBuffer.java,v 1.5 2003/12/19 09:12:05 larsga Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: Utility for storing objects in a ring buffer. Adding an
 * object may cause another one to 'fall' off if the buffer is full.
 *
 * @since 2.0
 */
public class RingBuffer {

  /** max size. */
  protected int maxSize;

  /** the buffer collection */
  protected ArrayList buffer;

  /**
   * Creates a new RingBuffer with a default size of 50.
   */
  public RingBuffer() {
    maxSize = 50;
    buffer = new ArrayList(50);
  }
  
  /**
   * Creates a new RingBuffer with the specified size.
   */
  public RingBuffer(int maxSize){
    this.maxSize = maxSize;
    buffer = new ArrayList(maxSize);
  }
  
  /**
   * Returns the elements in the buffer, in order.
   * @return the collection of elements in the buffer.
   */
  public List getElements(){
    return (List) buffer.clone();
  }
  
  /**
   * Adds a new element to the buffer. If the buffer is full an
   * element is bumped off the back. If the item already exists it is
   * moved to the front and the rest of the buffer closes up the gap.
   */
  public void addElement(Object obj){        
    int objectIndex = buffer.indexOf(obj);
    if (objectIndex > -1)
      buffer.remove(objectIndex);      
    
    if (buffer.size() == maxSize)
      buffer.remove(buffer.size() - 1);
    
    buffer.add(0, obj);
  }

  /**
   * Empties the buffer.
   */
  public void clear() {
    buffer.clear();
  }
}

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

import java.util.ArrayList;
import java.util.List;

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

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

package net.ontopia.topicmaps.query.impl.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * INTERNAL: Expected input is a map with arbitrary keys and values of Object[] type.
 */

public class CrossProduct {
  
  protected Object[] keys;
  protected Object[] values;
  
  protected int[] indexes;  
  protected Object[] tuple;

  protected int size = 1;
  protected boolean finished;
  
  public CrossProduct(Map data) {
    // Initialize keys array
    int length = data.size();
    this.keys = data.keySet().toArray();
    // Sort keys array
    Arrays.sort(this.keys);

    // Initialize value tuple
    this.tuple = new Object[length];
    // Initialize indexes array;
    this.indexes = new int[length];

    // Intialize values array
    this.values = new Object[length];
    for (int i=0; i < length; i++) {
      Object[] v = (Object[])data.get(keys[i]);
      if (v == null || v.length == 0) {
        finished = true;
        size = 0;
      } else {
        size = size * v.length;
      }
      values[i] = v;
      //! System.out.println("-> " + keys[i] + "=" + Arrays.asList((Object[])this.values[i]));    
    }
    //! if (length == 0) finished = true;
  }
  
  public void reset() {
    // Reset indexes and finished flag
    if (size != 0) {
      Arrays.fill(this.indexes, 0);
      this.finished = false;
    }
  }
  
  public int getSize() {
    return size;
  }
  
  public Object[] getKeys() {
    return keys;
  }
  
  public Object[] getTuple() {
    return tuple;
  }

  public Map getMap() {
    Map result = new HashMap(keys.length);
    for (int i=0; i < keys.length; i++) {
      result.put(keys[i], tuple[i]);
    }
    return result;
  }
  
  public boolean nextTuple() {
    if (finished) {
      return false;
    }
    
    // Loop over indexes and use current index pointer
    for (int i=0; i < tuple.length; i++) {
      int index = indexes[i];
      tuple[i] = ((Object[])values[i])[index];
    }
        
    // Increment index pointer (search backwards)
    if (tuple.length == 0 ) {
      finished = true;
    } else {    
      for (int i=tuple.length-1; i >= 0; i--) {
        int index = indexes[i];
        //! System.out.println(": " + index + " " + ((Object[])values[index]).length);
        if (index+1 < ((Object[])values[i]).length) {
          indexes[i]++;
          //! System.out.println("INC: " + i + "=" + indexes[i] + " [" + ((Object[])values[i]).length + "]");
          return true;
        } else {
          indexes[i] = 0;
          if (i == 0) {
            finished = true;
          }
          //! System.out.println("RES: " + i + "=" + indexes[i] + " [" + ((Object[])values[i]).length + "]");
        }
      }
    }
    return true;
  }

  public static void main(String args[]) {
    Map data = new HashMap();
    data.put("A", new Object[] {"1", "2", "3"});
    data.put("B", new Object[] {"4"});
    data.put("C", new Object[] {"5"});
    data.put("D", new Object[] {"6", "7"});
    data.put("E", new Object[] {"8", "9"});
    //! data.put("F", new Object[] {});
    
    CrossProduct cp = new CrossProduct(data);
    for (int i=1; i <= 3; i++) {
      System.out.println(i + ": size " + cp.getSize());
      while (cp.nextTuple()) {
        System.out.print(Arrays.asList(cp.getKeys()).toString());
        System.out.print(Arrays.asList(cp.getTuple()).toString());
        System.out.print(cp.getMap().toString());
        System.out.println();
      }
      cp.reset();
    }
  }
  
}

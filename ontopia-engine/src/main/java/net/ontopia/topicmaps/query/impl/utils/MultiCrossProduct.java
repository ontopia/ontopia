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
 * INTERNAL: Expected input is an array of maps with arbitrary keys and values of Object[] type.
 */

public class MultiCrossProduct {

  protected int totsize;
  protected int[] sizes;
  protected int[] offsets;
  
  protected Object[] keys;
  protected Object[] values;
  
  protected int[] indexes;  
  protected Object[] tuple;

  protected int size = 1;
  protected boolean finished;
  
  public MultiCrossProduct(Map[] data) {
    // Calculate total tuple widths
    this.sizes = new int[data.length];
    this.offsets = new int[data.length];
    for (int i=0; i < data.length; i++) {
      this.sizes[i] = data[i].size();
      this.offsets[i] = this.totsize;
      this.totsize += this.sizes[i];
    }
    // Initialize value tuple
    this.tuple = new Object[this.totsize];
    // Initialize keys array;
    this.keys = new Object[this.totsize];
    // Initialize indexes array;
    this.indexes = new int[this.totsize];
    // Intialize values array
    this.values = new Object[this.totsize];

    // Loop over input data
    for (int i=0; i < data.length; i++) {
      Object[] _keys = data[i].keySet().toArray(); 
      // Sort keys array
      Arrays.sort(_keys);
      for (int i2=0; i2 < _keys.length; i2++) {
        //  Prepare key data
        this.keys[offsets[i] + i2] = _keys[i2];        
        //  Prepare value data
        Object[] v = (Object[])data[i].get(_keys[i2]);
        if (v == null || v.length == 0) {
          finished = true;
          size = 0;
        } else {
          size = size * v.length;
        }
        this.values[offsets[i] + i2] = v;        
      }      
    }
    //! System.out.println("-> " + keys[i] + "=" + Arrays.asList((Object[])this.values[i]));    
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
  public Object[] getKeys(int index) {
    Object[] result = new Object[sizes[index]];
    for (int i=0; i < result.length; i++) {
      result[i] = keys[offsets[index]+i];
    }
    return result;
  }
  
  public Object[] getTuple() {
    return tuple;
  }
  public Object[] getTuple(int index) {
    Object[] result = new Object[sizes[index]];
    for (int i=0; i < result.length; i++) {
      result[i] = tuple[offsets[index]+i];
    }
    return result;
  }


  public Map getMap() {
    Map result = new HashMap(keys.length);
    for (int i=0; i < keys.length; i++) {
      result.put(keys[i], tuple[i]);
    }
    return result;
  }
  public Map getMap(int index) {
    Map result = new HashMap(sizes[index]);
    for (int i=0; i < sizes[index]; i++) {
      result.put(keys[offsets[index]+i], tuple[offsets[index]+i]);
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
    Map data1 = new HashMap();
    data1.put("A", new Object[] {"1", "2", "3"});
    data1.put("B", new Object[] {"4"});

    Map data2 = new HashMap();
    data2.put("C", new Object[] {"5"});
    data2.put("D", new Object[] {"6", "7"});
    data2.put("E", new Object[] {"8", "9"});
    //! data2.put("F", new Object[] {});
    
    MultiCrossProduct cp = new MultiCrossProduct(new Map[] {data1, data2});
    for (int i=1; i <= 3; i++) {
      System.out.println(i + ": size " + cp.getSize());
      while (cp.nextTuple()) {
        System.out.print(Arrays.asList(cp.getKeys()).toString());
        System.out.print(Arrays.asList(cp.getTuple()).toString());
        System.out.print(cp.getMap(0).toString());
        System.out.print(cp.getMap(1).toString());
        System.out.println();
      }
      cp.reset();
    }
  }
  
}

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

import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: Useful debugging methods.
 */
public class DebugUtils {

  public static String decodeString(String str) {
    StringBuilder buf = new StringBuilder(str.length() * 5);
    buf.append(str + " [");

    for (int ix = 0; ix < str.length(); ix++) {
      if (ix > 0)
        buf.append(", ");
      buf.append((int) str.charAt(ix));
    }
    
    buf.append("]");

    return buf.toString();
  }

  public static String toString(Object[] array) {
    if (array == null)
      return "null";
    
    return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static String toString(int[] array) {
    if (array == null)
      return "null";
    
    StringBuilder list = new StringBuilder("[");

    if (array.length > 0) {
      list.append(Integer.toString(array[0]));
    
      for (int ix = 1; ix < array.length; ix++) {
        list.append(", ");
        list.append(Integer.toString(array[ix]));
      }
    }
    
    list.append("]");
    return list.toString();
  }

  public static String toString(boolean[] array) {
    if (array == null)
      return "null";
    
    StringBuilder list = new StringBuilder("[");

    if (array.length > 0) {
      list.append(Boolean.toString(array[0]));
    
      for (int ix = 1; ix < array.length; ix++) {
        list.append(", ");
        list.append(Boolean.toString(array[ix]));
      }
    }
    
    list.append("]");
    return list.toString();
  }  
}

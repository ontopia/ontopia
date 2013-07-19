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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * INTERNAL: Class that contains useful array methods.
 */

public class ArrayUtils {

  protected static final Random random = new Random();

  public static String[] extend(String[] array, int extend_by) {
    String[] newarray = new String[array.length + extend_by];
    System.arraycopy(array, 0, newarray, 0, array.length);
    return newarray;
  }

  public static int[] extend(int[] array, int extend_by) {
    int[] newarray = new int[array.length + extend_by];
    System.arraycopy(array, 0, newarray, 0, array.length);
    return newarray;
  }

  public static boolean[] extend(boolean[] array, int extend_by) {
    boolean[] newarray = new boolean[array.length + extend_by];
    System.arraycopy(array, 0, newarray, 0, array.length);
    return newarray;
  }

  public static int indexOf(String[] array, String key) {    
    for (int i=0; i < array.length; i++) {
      if (array[i].equals(key)) return i;
    }
    return -1;
  }
  
  public static int indexOf(String[] array, int size, String key) {   
    for (int i=0; i < size; i++) {
      if (array[i].equals(key)) return i;
    }
    return -1;
  }
  
  public static String[] slice(String[] array, int index, int length) {
    String[] slice = new String[length];
    for (int i=0; i < length; i++) {
      slice[i] = array[index+i];
    }
    return slice;
  }

  public static String toString(Object[] array) {
    if (array == null)
      return "null";
    else
      return Arrays.asList(array).toString();
  }

  public static String toString(boolean[] array) {
    if (array == null)
      return "null";
    else
      return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static String toString(byte[] array) {
    if (array == null)
      return "null";
    else
      return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static String toString(char[] array) {
    if (array == null)
      return "null";
    else
      return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static String toString(int[] array) {
    if (array == null)
      return "null";
    else
      return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static String toString(long[] array) {
    if (array == null)
      return "null";
    else
      return "[" + StringUtils.join(array, ", ") + "]";
  }

  public static Object getRandom(Object[] array) {
    int chosen = random.nextInt(array.length);
    return array[chosen];
  }

  public static Object getRandom(Object[] array, int offset) {
    int chosen = random.nextInt(array.length-offset);
    return array[offset+chosen];
  }

  /**
   * INTERNAL: Performs a binary search and then returns the index of
   * the first occurrence. Note that this method extends
   * Arrays.binarySearch(Object[], Object, Comparator) by guaranteeing
   * that it returns the first occurrence of the element.
   *
   * @since 4.0.5
   */
  public static int binarySearchFirst(Object[] array, Object o, Comparator c) {
    int r = Arrays.binarySearch(array, o, c);
    if (r < 0) 
      return -1;
    else
      return findFirst(array, o, r);
  }

  private static int findFirst(Object[] array, Object o, int offset) {
    int result = offset;
    for (int i=offset-1; i >= 0; i--) {
      if (ObjectUtils.different(array[i], o)) 
        return result;
      else
        result = i;
    }
    return result;
  }
  
}

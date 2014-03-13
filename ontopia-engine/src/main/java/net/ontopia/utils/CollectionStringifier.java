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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * INTERNAL: Stringifier that stringifies collections. A comparator can
 * be used to order the objects in the collection.</p>
 */

public class CollectionStringifier<T> implements StringifierIF<Collection<T>> {

  protected Comparator<? super T> comparator;
  protected StringifierIF<? super T> stringifier;

  protected String item_prefix = "";
  protected String item_suffix = "";
  protected String list_prefix = "";
  protected String list_suffix = "";
  
  public CollectionStringifier(StringifierIF<? super T> stringifier) {
    this.stringifier = stringifier;
  }
  
  public CollectionStringifier(StringifierIF<? super T> stringifier, Comparator<? super T> comparator) {
    this.stringifier = stringifier;
    this.comparator = comparator;
  }

  public String getItemPrefix() {
    return item_prefix;
  }
  
  public void setItemPrefix(String prefix) {
    item_prefix = prefix;
  }

  public String getItemSuffix() {
    return item_suffix;
  }

  public void setItemSuffix(String suffix) {
    item_suffix = suffix;
  }

  public String getListPrefix() {
    return list_prefix;
  }
  
  public void setListPrefix(String prefix) {
    list_prefix = prefix;
  }

  public String getListSuffix() {
    return list_suffix;
  }

  public void setListSuffix(String suffix) {
    list_suffix = suffix;
  }
  
  public String toString(Collection<T> objects) {
    if (objects == null) return "null";
    StringBuilder sb = new StringBuilder();
    
    // Sort and output list
    List<T> list = new ArrayList<T>(objects);
    if (comparator != null)
      Collections.sort(list, comparator);

    // Loop over objects
    Iterator<T> iter = list.iterator();
    while (iter.hasNext())
      sb.append(item_prefix + stringifier.toString(iter.next()) + item_suffix);

    return list_prefix + sb.toString() + list_suffix;
  }
}





// $Id: CollectionStringifier.java,v 1.6 2004/11/29 19:10:44 grove Exp $

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

public class CollectionStringifier implements StringifierIF {

  protected Comparator comparator;
  protected StringifierIF stringifier;

  protected String item_prefix = "";
  protected String item_suffix = "";
  protected String list_prefix = "";
  protected String list_suffix = "";
  
  public CollectionStringifier(StringifierIF stringifier) {
    this.stringifier = stringifier;
  }
  
  public CollectionStringifier(StringifierIF stringifier, Comparator comparator) {
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
  
  public String toString(Object objects) {
    if (objects == null) return "null";
    StringBuffer sb = new StringBuffer();
    
    // Sort and output list
    List list = new ArrayList((Collection)objects);
    if (comparator != null)
      Collections.sort(list, comparator);

    // Loop over objects
    Iterator iter = list.iterator();
    while (iter.hasNext())
      sb.append(item_prefix + stringifier.toString(iter.next()) + item_suffix);

    return list_prefix + sb.toString() + list_suffix;
  }
}





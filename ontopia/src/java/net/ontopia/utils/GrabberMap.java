// $Id: GrabberMap.java,v 1.8 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: An implementation of Map that uses a key grabber and a
 * value grabber to extract its content.
 */

public class GrabberMap implements Map, CachedIF {

  protected Collection coll;
  protected GrabberIF key_grabber;
  protected GrabberIF value_grabber;

  protected Map grabbed_map;
  protected boolean grabbed;
  
  public GrabberMap(Collection coll, GrabberIF key_grabber, GrabberIF value_grabber) {
    this.coll = coll;
    this.key_grabber = key_grabber;
    this.value_grabber = value_grabber;
  }

  public Map getNestedMap() {
    return getMap();
  }
  
  public void setNestedMap(Map map) {
    this.grabbed_map = map;    
  }
  
  /**
   * Refreshes the map by looping over the nested collection
   * and regrabbing keys and values. Any changes done to the previous
   * collection will be lost.
   */
  public void refresh() {
    if (grabbed_map == null)
      grabbed_map = new HashMap();
    else
      grabbed_map.clear();
    
    // Loop over all objects in the collection
    Iterator iter = coll.iterator();
    while (iter.hasNext()) {
      Object object = iter.next();
      Object grabbed_key = key_grabber.grab(object);
      Object grabbed_value = value_grabber.grab(object);
      if (!grabbed_map.containsKey(grabbed_key)) grabbed_map.put(grabbed_key, new HashSet());
      ((Collection)grabbed_map.get(grabbed_key)).add(grabbed_value);
    }
    grabbed = true;
  }

  protected Map getMap() {
    if (grabbed) return grabbed_map;
    refresh();
    return grabbed_map;
  }

  public void clear() {
    getMap().clear();
  }

  public boolean containsKey(Object key) {
    return getMap().containsKey(key);
  }

  public boolean containsValue(Object value) {
    return getMap().containsValue(value);
  }

  public Set entrySet() {
    return getMap().entrySet();
  }
  

  public boolean equals(Object o) {
    return getMap().equals(o);
  }

  public Object get(Object key) {
    return getMap().get(key);
  }

  public int hashCode() {
    return getMap().hashCode();
  }

  public boolean isEmpty() {
    return getMap().isEmpty();
  }

  public Set keySet() {
    return getMap().keySet();
  }

  public Object put(Object key, Object value) {
    return getMap().put(key, value);
  }

  public void putAll(Map t) {
    getMap().putAll(t);
  }

  public Object remove(Object key) {
    return getMap().remove(key);
  }

  public int size() {
    return getMap().size();
  }

  public Collection values() {
    return getMap().values();
  }
   
}





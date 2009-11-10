
// $Id: TMRevitalizer.java,v 1.8 2007/03/28 12:20:47 grove Exp $

package net.ontopia.topicmaps.impl.utils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.infoset.core.LocatorIF;

import org.apache.commons.collections.map.IdentityMap;

/**
 * INTERNAL: Default implementation of the TMRevitalizerIF interface.
 */
public class TMRevitalizer implements TMRevitalizerIF {

  protected TopicMapIF topicmap;
  protected Map processed;
  
  public TMRevitalizer(TopicMapIF topicmap) {
    this.topicmap = topicmap;
    this.processed = new IdentityMap();
  }
  
  public Object revitalize(Object o) {
    // no need to revitalize null
    if (o == null) return null;
    // check to see if object has already been processed
    if (processed.containsKey(o))
      return processed.get(o);
      
    if (o instanceof TMRevitalizableIF) {
      processed.put(o, o);
      ((TMRevitalizableIF)o).revitalize(this);
      return o;
    } else if (o instanceof TMObjectIF) {
      Object x = revitalize((TMObjectIF)o);
      processed.put(o, x);
      return x;
    } else if (o instanceof List) {
      processed.put(o, null);
      Object x = revitalize((List)o);
      processed.put(o, x);
      return x;      
    } else if (o instanceof Set) {
      processed.put(o, null);
      Object x = revitalize((Set)o);
      processed.put(o, x);
      return x;      
    } else if (o instanceof Collection) {
      processed.put(o, null);
      Object x = revitalize((Collection)o);
      processed.put(o, x);
      return x;      
    } else if (o instanceof Map) {
      processed.put(o, null);
      Object x = revitalize((Map)o);
      processed.put(o, x);
      return x;      
    } else if (o.getClass().isPrimitive() ||
               o instanceof String ||
               o instanceof LocatorIF ||
               o instanceof Boolean ||
               o instanceof Integer) {
      return o;
    } else {
      throw new OntopiaRuntimeException("Cannot revitalize object " + o);
    }
  }

  // -- private methods
  
  private TMObjectIF revitalize(TMObjectIF o) {
    TMObjectIF revitalized = topicmap.getObjectById(o.getObjectId());
    // ISSUE: what if new object have different type than old
    // object. this can actually happen if the topic map is reloaded
    // from file in the meantime.
    if (revitalized == null)
      throw new OntopiaRuntimeException("Object " + o + " could not be revitalized because it can no longer be found in the topic map.");
    else
      return revitalized;
  }

  private Object[] revitalize(Object[] a) {
    Object[] result = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), a.length);
    for (int i=0; i < a.length; i++) {
      result[i] = revitalize(a[i]);
    }
    return result;
  }

  private Collection revitalize(Collection c) {
    Collection result = new ArrayList(c.size());
    Iterator iter = c.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      result.add(revitalize(o));      
    }
    return result;
  }

  private List revitalize(List c) {
    int size = c.size();
    List result = new ArrayList(size);
    for (int i=0; i < size; i++) {
      result.add(revitalize(c.get(i)));
    }
    return result;
  }

  private Set revitalize(Set c) {
    Set result = new HashSet(c.size());
    Iterator iter = c.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      result.add(revitalize(o));      
    }
    return result;
  }

  private Map revitalize(Map m) {
    Map result = new HashMap(m.size());
    Iterator i = m.keySet().iterator();
    while (i.hasNext()) {
      Object k = i.next();      
      result.put(revitalize(k), revitalize(m.get(k)));
    }
    return result;
  }
  
}

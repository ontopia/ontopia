
// $Id: ContextManagerMapWrapper.java,v 1.14 2006/08/09 09:42:46 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;

/**
 * INTERNAL: Used to make the OKS variables available as request
 * attributes.
 */
public class ContextManagerMapWrapper implements Map {

  private ContextManagerIF contextManager;

  public ContextManagerMapWrapper(ContextManagerIF contextManager) {
    this.contextManager = contextManager;
  }

  public Object get(Object key) {
    return contextManager.getValue((String) key, null);
  }
    
  public boolean containsKey(Object key) {
    return contextManager.getValue((String)key, null) != null;
  }

  public Object put(Object key, Object value) {
    Object retVal;
    try {
      // Get the old mapped value of the key, if there is one
      retVal = get(key);
    } catch (VariableNotSetException e) {
      retVal= null;
    }
    
    Collection valueCollection;
    if (value instanceof Collection)
      valueCollection = (Collection)value;
    else {
      valueCollection = new HashSet();
      valueCollection.add(value);
    }
    
    contextManager.setValue((String)key, valueCollection);
    
    // Return the old mapped value of the key or null if it wasn't mapped.
    return retVal;
  }
  
  public int size() { 
    // we shouldn't really implement this method, as we have no idea
    // how many (if any) variables there are inside the context, but
    // Liferay insists on calling this method, and so we can't crash.
    // we "solve" it by returning zero, which makes Liferay shut up
    // and go away.
    return 0;
  } 
  
  // Interface methods that are not supported.
  
  public void clear() { 
    throw new UnsupportedOperationException();
  }
  
  public boolean containsValue(Object value) { 
    throw new UnsupportedOperationException();
  }
  
  public Set entrySet() { 
    throw new UnsupportedOperationException();
  }
  
  public boolean isEmpty() { 
    throw new UnsupportedOperationException();
  }
  
  public Set keySet() { 
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map t) {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Overwrites the old value of the given key with an empty collection.
   * If the key was not already bound to a value then nothing is done
   *   and null is returned.
   * This violates the Map contract with regards to size, 
   * but size is not implemented, and will not be.
   * @param key - The key of the mapping.
   */
  public Object remove(Object key) {
    // Get the old mapped value of the key, if there is one
    Object retVal = get(key);

    if (retVal == null)
      return null;

    // Make the mapping.
    contextManager.setValue((String)key, Collections.EMPTY_SET);
    
    // Return the old mapped value of the key or null if it wasn't mapped.
    return retVal;
  }
  
  public Collection values() { 
    throw new UnsupportedOperationException();
  }
}

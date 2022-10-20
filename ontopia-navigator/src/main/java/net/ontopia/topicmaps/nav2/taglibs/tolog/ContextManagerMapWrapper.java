/*
 * #!
 * Ontopia Navigator
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

  @Override
  public Object get(Object key) {
    return contextManager.getValue((String) key, null);
  }
    
  @Override
  public boolean containsKey(Object key) {
    return contextManager.getValue((String)key, null) != null;
  }

  @Override
  public Object put(Object key, Object value) {
    Object retVal;
    try {
      // Get the old mapped value of the key, if there is one
      retVal = get(key);
    } catch (VariableNotSetException e) {
      retVal= null;
    }
    
    Collection valueCollection;
    if (value instanceof Collection) {
      valueCollection = (Collection)value;
    } else {
      valueCollection = new HashSet();
      valueCollection.add(value);
    }
    
    contextManager.setValue((String)key, valueCollection);
    
    // Return the old mapped value of the key or null if it wasn't mapped.
    return retVal;
  }
  
  @Override
  public int size() { 
    // we shouldn't really implement this method, as we have no idea
    // how many (if any) variables there are inside the context, but
    // Liferay insists on calling this method, and so we can't crash.
    // we "solve" it by returning zero, which makes Liferay shut up
    // and go away.
    return 0;
  } 
  
  // Interface methods that are not supported.
  
  @Override
  public void clear() { 
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean containsValue(Object value) { 
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set entrySet() { 
    throw new UnsupportedOperationException();
  }
  
  @Override
  public boolean isEmpty() { 
    throw new UnsupportedOperationException();
  }
  
  @Override
  public Set keySet() { 
    throw new UnsupportedOperationException();
  }
  
  @Override
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
  @Override
  public Object remove(Object key) {
    // Get the old mapped value of the key, if there is one
    Object retVal = get(key);

    if (retVal == null) {
      return null;
    }

    // Make the mapping.
    contextManager.setValue((String)key, Collections.EMPTY_SET);
    
    // Return the old mapped value of the key or null if it wasn't mapped.
    return retVal;
  }
  
  @Override
  public Collection values() { 
    throw new UnsupportedOperationException();
  }
}

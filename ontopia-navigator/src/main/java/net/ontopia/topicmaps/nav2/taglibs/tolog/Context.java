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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.utils.BeanUtils;

/**
 * INTERNAL: Used to make the topic map repository and its references
 * available as a request attribute.
 */
public class Context {

  private ContextTag contextTag;

  public Context(ContextTag contextTag) {
    this.contextTag = contextTag;
  }

  public Collection getReferences() {
    TopicMapRepositoryIF rep = contextTag.getTopicMapRepository();
    Collection refs = rep.getReferences();
    List result = new ArrayList(refs.size());
    Iterator iter = refs.iterator();
    while (iter.hasNext()) {
      TopicMapReferenceIF ref = (TopicMapReferenceIF)iter.next();
      result.add(new Reference(ref));
    }
    Collections.sort(result);
    return result;
  }
  
  public static class Reference implements Map, Comparable {
    private TopicMapReferenceIF ref;
    private Map params;
    Reference(TopicMapReferenceIF ref) {
      this.ref = ref;
    }
    private Map getParams() {
      if (params == null) {
        // populate reference parameters
        params = BeanUtils.beanMap(ref.getSource(), false);
        params.remove("references");
        params.put("sourceId", params.get("id"));
        params.put("sourceTitle", params.get("title"));
        params.put("id", ref.getId());
        params.put("title", ref.getTitle());
      }
      return params;
    }

    @Override
    public int compareTo(Object o) {
      Reference oref = (Reference)o;
      String key1 = (ref.getTitle() == null ? ref.getId() : ref.getTitle());
      String key2 = (oref.ref.getTitle() == null ? oref.ref.getId() : oref.ref.getTitle());
      return key1.compareToIgnoreCase(key2);
    }

    // -- Map implementation

    @Override
    public Object get(Object key) {
      return getParams().get(key);
    }
    
    @Override
    public boolean containsKey(Object key) {
      return getParams().containsKey(key);
    }

    @Override
    public Object put(Object key, Object value) {
      return getParams().put(key, value);
    }
  
    @Override
    public void clear() { 
      getParams().clear();
    }
  
    @Override
    public boolean containsValue(Object value) { 
      return getParams().containsValue(value);
    }
  
    @Override
    public Set entrySet() { 
      return getParams().entrySet();    
    }
  
    @Override
    public int hashCode() { 
      return ref.hashCode();
    }
  
    @Override
    public boolean isEmpty() { 
      return getParams().isEmpty();
    }
  
    @Override
    public Set keySet() { 
      return getParams().keySet();
    }
  
    @Override
    public void putAll(Map t) {
      getParams().putAll(t);
    }

    @Override
    public Object remove(Object key) {
      return getParams().remove(key);
    }
  
    @Override
    public int size() { 
      return getParams().size();
    }
  
    @Override
    public Collection values() { 
      return getParams().values();
    }

  }    

}

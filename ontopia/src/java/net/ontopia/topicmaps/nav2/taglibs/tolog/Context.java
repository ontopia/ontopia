
// $Id: Context.java,v 1.2 2005/12/13 14:16:20 opland Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

import org.apache.commons.collections.BeanMap;

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
        params = new HashMap(new BeanMap(ref.getSource()));
        params.remove("references");
        params.put("sourceId", params.get("id"));
        params.put("sourceTitle", params.get("title"));
        params.put("id", ref.getId());
        params.put("title", ref.getTitle());
      }
      return params;
    }

    public int compareTo(Object o) {
      Reference oref = (Reference)o;
      String key1 = (ref.getTitle() == null ? ref.getId() : ref.getTitle());
      String key2 = (oref.ref.getTitle() == null ? oref.ref.getId() : oref.ref.getTitle());
      return key1.compareToIgnoreCase(key2);
    }

    // -- Map implementation

    public Object get(Object key) {
      return getParams().get(key);
    }
    
    public boolean containsKey(Object key) {
      return getParams().containsKey(key);
    }

    public Object put(Object key, Object value) {
      return getParams().put(key, value);
    }
  
    public void clear() { 
      getParams().clear();
    }
  
    public boolean containsValue(Object value) { 
      return getParams().containsValue(value);
    }
  
    public Set entrySet() { 
      return getParams().entrySet();    
    }
  
    public int hashCode() { 
      return ref.hashCode();
    }
  
    public boolean isEmpty() { 
      return getParams().isEmpty();
    }
  
    public Set keySet() { 
      return getParams().keySet();
    }
  
    public void putAll(Map t) {
      getParams().putAll(t);
    }

    public Object remove(Object key) {
      return getParams().remove(key);
    }
  
    public int size() { 
      return getParams().size();
    }
  
    public Collection values() { 
      return getParams().values();
    }

  }    

}

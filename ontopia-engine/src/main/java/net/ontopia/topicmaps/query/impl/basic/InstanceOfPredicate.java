
package net.ontopia.topicmaps.query.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;
import net.ontopia.utils.CompactHashSet;

/**
 * INTERNAL: Implements the 'instance-of' predicate using the indexes.
 */
public class InstanceOfPredicate extends AbstractInstanceOfPredicate {
  protected TypeHierarchyUtils typeutils;
  protected Map superTypeCache;
 
  public InstanceOfPredicate(TopicMapIF topicmap) {
    super(topicmap);
    typeutils = new TypeHierarchyUtils();
    superTypeCache = new HashMap();
  }
  
  public String getName() {
    return "instance-of";
  }

  // --- Data interface implementation

  protected void start() {
    superTypeCache.clear();
  }
  
  protected Collection getClasses(TopicIF instance) {
    Set types = new CompactHashSet();
    Iterator it = instance.getTypes().iterator();
    while (it.hasNext()) 
      types.addAll(getSupertypes((TopicIF) it.next()));
    return types;
  }

  protected Collection getInstances(TopicIF klass) {
    Set instances = new CompactHashSet();
    Iterator it = getSubtypes(klass).iterator();
    while (it.hasNext()) 
      instances.addAll(index.getTopics((TopicIF) it.next()));
    return instances;
  }

  protected Collection getTypes() {
    Set types = new CompactHashSet();
    Iterator it = index.getTopicTypes().iterator();
    while (it.hasNext()) {
      TopicIF type = (TopicIF) it.next();
      types.addAll(getSupertypes(type));
      types.addAll(typeutils.getSubclasses(type));
    }
    return types;
  }

  protected Collection getSupertypes(TopicIF type) {
    Collection supers = (Collection) superTypeCache.get(type);
    if (supers == null) {
      supers = new ArrayList(typeutils.getSuperclasses(type));
      supers.add(type);
      superTypeCache.put(type, supers);
    }
    return supers;
  }

  // --- Internal helpers

  protected Collection getSubtypes(TopicIF type) {
    Collection subs = new ArrayList(typeutils.getSubclasses(type));
    subs.add(type);
    return subs;
  }
}

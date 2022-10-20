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
  
  @Override
  public String getName() {
    return "instance-of";
  }

  // --- Data interface implementation

  @Override
  protected void start() {
    superTypeCache.clear();
  }
  
  @Override
  protected Collection getClasses(TopicIF instance) {
    Set types = new CompactHashSet();
    Iterator it = instance.getTypes().iterator();
    while (it.hasNext()) {
      types.addAll(getSupertypes((TopicIF) it.next()));
    }
    return types;
  }

  @Override
  protected Collection getInstances(TopicIF klass) {
    Set instances = new CompactHashSet();
    Iterator it = getSubtypes(klass).iterator();
    while (it.hasNext()) {
      instances.addAll(index.getTopics((TopicIF) it.next()));
    }
    return instances;
  }

  @Override
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

  @Override
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

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

package net.ontopia.topicmaps.impl.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: A class that configures event listeners for an object
 * tree manager object and figures out which objects have been
 * detached from the object model.
 */

@Deprecated
public class AttachManager extends AbstractAttachManager {

  protected Map<Class<?>, AttachHandlerIF> ahandlers;
  protected Map<Class<?>, DetachHandlerIF> dhandlers;
  
  public AttachManager(ObjectTreeManager otree) {
    super(otree);
    
    // Declare handlers
    ahandlers = new HashMap<Class<?>, AttachHandlerIF>();
    dhandlers = new HashMap<Class<?>, DetachHandlerIF>();

    // Create shared collections
    Collection<Object> assocs = new HashSet<Object>();
    Collection<Object> roles = new HashSet<Object>();
    Collection<Object> basenames = new HashSet<Object>();
    Collection<Object> occurs  = new HashSet<Object>();
    Collection<Object> topics  = new HashSet<Object>();
    Collection<Object> variants  = new HashSet<Object>();
    
    ahandlers.put(AssociationIF.class, new AttachHandler(assocs));
    dhandlers.put(AssociationIF.class, new DetachHandler(assocs));
    
    ahandlers.put(AssociationRoleIF.class, new AttachHandler(roles));
    dhandlers.put(AssociationRoleIF.class, new DetachHandler(roles));
                                           
    ahandlers.put(TopicNameIF.class, new AttachHandler(basenames));
    dhandlers.put(TopicNameIF.class, new DetachHandler(basenames));

    ahandlers.put(OccurrenceIF.class, new AttachHandler(occurs));
    dhandlers.put(OccurrenceIF.class, new DetachHandler(occurs));
                                           
    ahandlers.put(TopicIF.class, new AttachHandler(topics));
    dhandlers.put(TopicIF.class, new DetachHandler(topics));
                                           
    ahandlers.put(VariantNameIF.class, new AttachHandler(variants));
    dhandlers.put(VariantNameIF.class, new DetachHandler(variants));
    
    // Register handlers
    register();
  }
  
  // -----------------------------------------------------------------------------
  // Attach/detach handler initializer methods
  // -----------------------------------------------------------------------------

  @Override
  public AttachHandlerIF getAttachHandler(Class<?> klass) {
    if (!ahandlers.containsKey(klass)) throw new OntopiaRuntimeException("AttachHandler missing: " + klass);
    return ahandlers.get(klass);
  }
  
  @Override
  public DetachHandlerIF getDetachHandler(Class<?> klass) {
    if (!dhandlers.containsKey(klass)) throw new OntopiaRuntimeException("DetachHandler missing: " + klass);
    return dhandlers.get(klass);
  }
  
  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * EventHandler:
   */
  class AttachHandler implements AttachHandlerIF, java.io.Serializable {
    protected Collection<Object> objects;
    AttachHandler(Collection<Object> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      // Object has been added so we should register it with the unit of work.
      // log.debug("Adding object: " + new_value + " to parent " + object);
      // Object is no longer detached.
      objects.remove(new_value);
    }
    @Override
    public boolean isAttached(Object object) {
      throw new UnsupportedOperationException("");
    }
    @Override
    public Collection<Object> getAttached() {
      throw new UnsupportedOperationException("");
    }
    @Override
    public void refresh() {
      objects.clear();
    }
  }
  /**
   * EventHandler:
   */
  class DetachHandler implements DetachHandlerIF, java.io.Serializable {
    protected Collection<Object> objects;
    DetachHandler(Collection<Object> objects) {
      this.objects = objects;
    }
    @Override
    public void processEvent(Object object, String event, Object new_value, Object old_value) {
      // Object has been added so we should register it with the unit of work.
      // log.debug("Removing object " + old_value + " from parent " + object);
      // Put object on list of detached objects.
      objects.add(old_value);
    }
    @Override
    public boolean isDetached(Object object) {
      return objects.contains(object);
    }
    @Override
    public Collection<Object> getDetached() {
      return objects;
    }
    @Override
    public void refresh() {
      objects.clear();
    }
  }
}






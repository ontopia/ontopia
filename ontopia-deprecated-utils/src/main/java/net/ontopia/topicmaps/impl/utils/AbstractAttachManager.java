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
import java.util.HashSet;

import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.utils.CachedIF;

/**
 * INTERNAL: A class that configures event listeners for an object
 * tree manager object and figures out which objects have been
 * detached from the object model.
 */

@Deprecated
public abstract class AbstractAttachManager {

  protected ObjectTreeManager otree;
  
  public AbstractAttachManager(ObjectTreeManager otree) {
    this.otree = otree;
  }

  /**
   * INTERNAL: Registers the handlers with the object tree manager.
   */
  public void register() {
    // Initialize object tree event handlers [objects added or removed]                           
    otree.addListener(getAttachHandler(AssociationIF.class), AssociationIF.EVENT_ADDED);
    otree.addListener(getDetachHandler(AssociationIF.class), AssociationIF.EVENT_REMOVED);
    
    otree.addListener(getAttachHandler(AssociationRoleIF.class), AssociationRoleIF.EVENT_ADDED);
    otree.addListener(getDetachHandler(AssociationRoleIF.class), AssociationRoleIF.EVENT_REMOVED);
    
    otree.addListener(getAttachHandler(TopicNameIF.class), TopicNameIF.EVENT_ADDED);
    otree.addListener(getDetachHandler(TopicNameIF.class), TopicNameIF.EVENT_REMOVED);
                                       
    otree.addListener(getAttachHandler(OccurrenceIF.class), OccurrenceIF.EVENT_ADDED);
    otree.addListener(getDetachHandler(OccurrenceIF.class), OccurrenceIF.EVENT_REMOVED);
    
    otree.addListener(getAttachHandler(TopicIF.class), TopicIF.EVENT_ADDED);
    otree.addListener(getDetachHandler(TopicIF.class), TopicIF.EVENT_REMOVED);
                                       
    otree.addListener(getAttachHandler(VariantNameIF.class), VariantNameIF.EVENT_ADDED);
    otree.addListener(getDetachHandler(VariantNameIF.class), VariantNameIF.EVENT_REMOVED);    
  }
  
  // -----------------------------------------------------------------------------
  // Attach/detach handler initializer methods
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Creates a new attach handler. This method can be
   * overridden in order to change the behaviour.
   */    
  public abstract AttachHandlerIF getAttachHandler(Class<?> klass);
  
  /**
   * INTERNAL: Creates a new detach handler. This method can be
   * overridden in order to change the behaviour. 
   */    
  public abstract DetachHandlerIF getDetachHandler(Class<?> klass);

  // -----------------------------------------------------------------------------
  // Attached and detached objects
  // -----------------------------------------------------------------------------

  public void clear() {
    getAssociationAttachHandler().refresh();
    getAssociationRoleAttachHandler().refresh();
    getTopicNameAttachHandler().refresh();
    getOccurrenceAttachHandler().refresh();
    getTopicAttachHandler().refresh();
    getVariantNameAttachHandler().refresh();
  }
  
  /**
   * INTERNAL: Returns all the objects that have been attached.
   */    
  public Collection<Object> getAttachedObjects() {
    Collection<Object> attached = new HashSet<Object>();
    attached.addAll(getAssociationAttachHandler().getAttached());
    attached.addAll(getAssociationRoleAttachHandler().getAttached());
    attached.addAll(getTopicNameAttachHandler().getAttached());
    attached.addAll(getOccurrenceAttachHandler().getAttached());
    attached.addAll(getTopicAttachHandler().getAttached());
    attached.addAll(getVariantNameAttachHandler().getAttached());
    return attached;
  }

  /**
   * INTERNAL: Returns all the objects that have been detached.
   */    
  public Collection<Object> getDetachedObjects() {
    Collection<Object> detached = new HashSet<Object>();
    detached.addAll(getAssociationDetachHandler().getDetached());
    detached.addAll(getAssociationRoleDetachHandler().getDetached());
    detached.addAll(getTopicNameDetachHandler().getDetached());
    detached.addAll(getOccurrenceDetachHandler().getDetached());
    detached.addAll(getTopicDetachHandler().getDetached());
    detached.addAll(getVariantNameDetachHandler().getDetached());
    return detached;
  }
  
  /**
   * INTERNAL: Returns all the association attach handler.
   */    
  public AttachHandlerIF getAssociationAttachHandler() {
    return getAttachHandler(AssociationIF.class);
  }
  public DetachHandlerIF getAssociationDetachHandler() {
    return getDetachHandler(AssociationIF.class);
  }
  
  public AttachHandlerIF getAssociationRoleAttachHandler() {
    return getAttachHandler(AssociationRoleIF.class);
  }
  public DetachHandlerIF getAssociationRoleDetachHandler() {
    return getDetachHandler(AssociationRoleIF.class);
  }
  
  public AttachHandlerIF getTopicNameAttachHandler() {
    return getAttachHandler(TopicNameIF.class);
  }
  public DetachHandlerIF getTopicNameDetachHandler() {
    return getDetachHandler(TopicNameIF.class);
  }
  
  public AttachHandlerIF getOccurrenceAttachHandler() {
    return getAttachHandler(OccurrenceIF.class);
  }
  public DetachHandlerIF getOccurrenceDetachHandler() {
    return getDetachHandler(OccurrenceIF.class);
  }
  
  public AttachHandlerIF getTopicAttachHandler() {
    return getAttachHandler(TopicIF.class);
  }
  public DetachHandlerIF getTopicDetachHandler() {
    return getDetachHandler(TopicIF.class);
  }
  
  public AttachHandlerIF getVariantNameAttachHandler() {
    return getAttachHandler(VariantNameIF.class);
  }
  public DetachHandlerIF getVariantNameDetachHandler() {
    return getDetachHandler(VariantNameIF.class);
  }
  
  // -----------------------------------------------------------------------------
  // Event handlers
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Interface that manages attaching new objects to the object model.
   */
  public interface AttachHandlerIF extends EventListenerIF, CachedIF {
    boolean isAttached(Object object);
    Collection<Object> getAttached();
    @Override
    void refresh();
  }
  
  /**
   * INTERNAL: Interface that manages detaching new objects to the object model.
   */
  public interface DetachHandlerIF extends EventListenerIF, CachedIF {
    boolean isDetached(Object object);
    Collection<Object> getDetached();
    @Override
    void refresh();
  }
}






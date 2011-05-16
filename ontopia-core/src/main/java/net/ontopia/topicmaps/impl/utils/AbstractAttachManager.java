// $Id: AbstractAttachManager.java,v 1.9 2008/06/12 14:37:16 geir.gronmo Exp $

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
    otree.addListener(getAttachHandler(AssociationIF.class), "AssociationIF.added");
    otree.addListener(getDetachHandler(AssociationIF.class), "AssociationIF.removed");
    
    otree.addListener(getAttachHandler(AssociationRoleIF.class), "AssociationRoleIF.added");
    otree.addListener(getDetachHandler(AssociationRoleIF.class), "AssociationRoleIF.removed");
    
    otree.addListener(getAttachHandler(TopicNameIF.class), "TopicNameIF.added");
    otree.addListener(getDetachHandler(TopicNameIF.class), "TopicNameIF.removed");
                                       
    otree.addListener(getAttachHandler(OccurrenceIF.class), "OccurrenceIF.added");
    otree.addListener(getDetachHandler(OccurrenceIF.class), "OccurrenceIF.removed");
    
    otree.addListener(getAttachHandler(TopicIF.class), "TopicIF.added");
    otree.addListener(getDetachHandler(TopicIF.class), "TopicIF.removed");
                                       
    otree.addListener(getAttachHandler(VariantNameIF.class), "VariantNameIF.added");
    otree.addListener(getDetachHandler(VariantNameIF.class), "VariantNameIF.removed");    
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
    public boolean isAttached(Object object);
    public Collection<Object> getAttached();
    public void refresh();
  }
  
  /**
   * INTERNAL: Interface that manages detaching new objects to the object model.
   */
  public interface DetachHandlerIF extends EventListenerIF, CachedIF {
    public boolean isDetached(Object object);
    public Collection<Object> getDetached();
    public void refresh();
  }
}







// $Id: TMObject.java,v 1.58 2008/06/13 08:17:51 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.impl.utils.*;
import net.ontopia.persistence.proxy.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public abstract class TMObject extends AbstractRWPersistent
  implements TMObjectIF {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(TMObject.class.getName());
  
  // ---------------------------------------------------------------------------
  // Field declarations
  // ---------------------------------------------------------------------------

  // Implementation specific field indexes
  static final int LF_sources = 0;
  static final int LF_topicmap = 1; // Note TopicMapIF.
  
  // static String[] fields; // Defined in concrete class
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  static String CLASS_INDICATOR;

  public TMObject() {
  }
  
  public TMObject(TransactionIF txn) {
    super(txn);

    // create object identity
    txn.assignIdentity(this);
  }
  
  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  /**
   * INTERNAL: Returns the token that can be used to indicate the
   * class of this instance. This indicator is currently only used by
   * source locators.
   */
  public abstract String getClassIndicator();

  /**
   * INTERNAL: Called when the transaction to which the object belongs
   * has changed.
   */
  protected void transactionChanged(TopicMap topicmap) {
    // Note: this means that the object was attached or detached from
    // the topic map transaction.
    if (topicmap != null) {
      // Register with same transaction as topic map
      TransactionIF ptxn = topicmap._p_getTransaction();
      // FIXME: can the transaction ever be null in this case?
      if (ptxn != null) ptxn.create(this);
    } else {
      // Delete from repository
      if (isPersistent()) txn.delete(this);
    }
  }
  
  long getLongId() {
    return ((Long)id.getKey(0)).longValue();
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public abstract String getObjectId();

  public boolean isReadOnly() {
    return txn.isReadOnly();
  }

  public TopicMapIF getTopicMap() {
    try {
      return (TopicMapIF)loadField(LF_topicmap);
    } catch (IdentityNotFoundException e) {
      // object has been deleted by somebody else, so return null
      return null;
    }
  }

  public Collection<LocatorIF> getItemIdentifiers() {
    return (Collection<LocatorIF>) loadCollectionField(LF_sources);
  }

  public void addItemIdentifier(LocatorIF source_locator)
    throws ConstraintViolationException {
    if (source_locator == null)
      throw new NullPointerException("null is not a valid argument.");
    // Notify topic map
    if (getTopicMap() == null)
      throw new ConstraintViolationException("Cannot modify source locators when object isn't attached to a topic map.");

    // Check to see if the source locator is already a source locator
    // of this topic.    
    Collection sources = loadCollectionField(LF_sources);
    if (sources.contains(source_locator)) return;    

    // Note: Need to morph it into source locator to ensure that it is
    // correctly handled by the mapping.

    // FIXME: Since this is an aggregate field, the O/R mapper should
    // be able to figure this out itself by looking at the field index
    // and the interface implemented by the value.
    SourceLocator _source_locator = new SourceLocator(source_locator);
    _source_locator._setTMObject(this.getLongId());
    _source_locator._setClassIndicator(this.getClassIndicator());
    _source_locator._setTopicMap(((TopicMap)getTopicMap()).getLongId());
    
    // Notify listeners
    fireEvent("TMObjectIF.addItemIdentifier", _source_locator, null);
    // Notify transaction
    valueAdded(LF_sources, _source_locator, true);
  }

  public void removeItemIdentifier(LocatorIF source_locator) {
    if (source_locator == null)
      throw new NullPointerException("null is not a valid argument.");
    // Notify topic map
    if (getTopicMap() == null)
      throw new ConstraintViolationException("Cannot modify source locators " +
                                 "when object isn't attached to a topic map.");
    
    // Check to see if source locator is a source locator of this topic.
    Collection sources = loadCollectionField(LF_sources);
    if (!sources.contains(source_locator)) return;

    // Note: Need to morph it into source locator to ensure that it is
    // correctly handled by the mapping.
    SourceLocator _source_locator = new SourceLocator(source_locator);
    _source_locator._setTMObject(this.getLongId());
    _source_locator._setClassIndicator(this.getClassIndicator());
    _source_locator._setTopicMap(((TopicMap)getTopicMap()).getLongId());
    
    // Notify listeners
    fireEvent("TMObjectIF.removeItemIdentifier", null, _source_locator);
    // Notify transaction
    valueRemoved(LF_sources, _source_locator, true);
  }

  // ---------------------------------------------------------------------------
  // Event handling
  // ---------------------------------------------------------------------------

  /**
   * INTERNAL: Fires an event, so that listeners can be informed about
   * the event. This method is typically called when the object id
   * modified.
   */
  protected void fireEvent(String event, Object new_value, Object old_value) {
    // Note: The object should already have been materialized at this
    // point, because it is about to be modified.
    TopicMapIF topicmap = getTopicMap();
    if (topicmap == null) return;
		RDBMSTopicMapStore store = (RDBMSTopicMapStore)topicmap.getStore();
    EventManagerIF emanager = (EventManagerIF)store.getTransaction();
    // System.out.println("->Object: " + this + " event: " + event + " new: " + new_value + " old:" + old_value);
    emanager.processEvent(this, event, new_value, old_value);
  }
  
}

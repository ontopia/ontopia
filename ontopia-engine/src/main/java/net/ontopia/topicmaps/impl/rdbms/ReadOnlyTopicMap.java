
// $Id: ReadOnlyTopicMap.java,v 1.13 2008/06/13 08:36:26 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.ConnectionFactoryIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.persistence.proxy.RDBMSAccess;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.ConstraintViolationException;
import net.ontopia.topicmaps.core.NotRemovableException;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.core.ReadOnlyException;
import net.ontopia.topicmaps.impl.utils.ObjectStrings;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: The read-only rdbms topic map implementation.
 */

public class ReadOnlyTopicMap extends ReadOnlyTMObject implements TopicMapIF {
  
  // ---------------------------------------------------------------------------
  // Data members
  // ---------------------------------------------------------------------------

  protected transient RDBMSTopicMapTransaction transaction;  

  public ReadOnlyTopicMap() {  
  }

  public LocatorIF getBaseAddress() {
    String base_address = (String)loadField(TopicMap.LF_base_address);
    if (base_address == null) return null;
    try {
      return new URILocator(base_address);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  public void setBaseAddress(LocatorIF baseAddress) {
    throw new ReadOnlyException();
  }

  public String getTitle() {
    return (String)loadField(TopicMap.LF_title);
  }

  public void setTitle(String title) {
    throw new ReadOnlyException();
  }

  // ---------------------------------------------------------------------------
  // PersistentIF implementation
  // ---------------------------------------------------------------------------

  public int _p_getFieldCount() {
    return TopicMap.fields.length;
  }
  
  // ---------------------------------------------------------------------------
  // TMObjectIF implementation
  // ---------------------------------------------------------------------------

  public String getClassIndicator() {
    return TopicMap.CLASS_INDICATOR;
  }

  public String getObjectId() {
    return (id == null ? null : TopicMap.CLASS_INDICATOR + id.getKey(0));
  }

  public TopicMapIF getTopicMap() {
    return this;
  }

  // ---------------------------------------------------------------------------
  // TopicMapIF implementation
  // ---------------------------------------------------------------------------

  public TopicMapStoreIF getStore() {
    return transaction.getStore();
  }

  public TopicMapTransactionIF getTransaction() {
    return transaction;
  }

  public TopicMapBuilderIF getBuilder() {
    return getTransaction().getBuilder();
  }

  public Object getIndex(String name) {
    return getTransaction().getIndexManager().getIndex(name);
  }

  public void setTransaction(RDBMSTopicMapTransaction transaction) {
    // The transaction property can only be set once
    if (this.transaction != null)
      throw new OntopiaRuntimeException("Transaction can only be set once.");
    
    this.transaction = transaction;
  }
  
  public Collection<TopicIF> getTopics() {
    Object[] params = new Object[] { getTopicMap() };
    return new QueryCollection(txn, "TopicMap.getTopics_size", params,
                               "TopicMap.getTopics_iterator", params);
  }

  /**
   * Adds a topic to the set of topics.
   */
  void addTopic(TopicIF topic) {
    throw new ReadOnlyException();
  }

  /**
   * Removes a topic from the set of topics.
   */
  void removeTopic(TopicIF topic) {
    throw new ReadOnlyException();
  }
  
  public Collection getAssociations() {
    Object[] params = new Object[] { getTopicMap() };
    return new QueryCollection(txn, "TopicMap.getAssociations_size", params,
                               "TopicMap.getAssociations_iterator", params);
  }

  /**
   * Adds an association to the set of associations.
   */
  void addAssociation(AssociationIF association) {
    throw new ReadOnlyException();
  }
  
  /**
   * Removes an associations from the set of associations.
   */
  void removeAssociation(AssociationIF association) {
    throw new ReadOnlyException();
  }

  public TMObjectIF getObjectById(String object_id) {
    if (object_id == null) throw new NullPointerException("null is not a valid argument.");

    // Cut off the indicator character and lookup identity in the database.
    try {
      long numid;
      try {
        numid = Long.parseLong(object_id.substring(1), 10);
      } catch (NumberFormatException e) {
        return null; // if not a valid ID no object will have it... :)
      }
      
      TMObjectIF result = null;
      switch ( object_id.charAt(0) ) {
      case 'T': {
        // Lookup topic
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(Topic.class, numid));
        break;
      }
      case 'A': {
        // Lookup association
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(Association.class, numid));
        break;
      }
      case 'O': {
        // Lookup occurrence
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(Occurrence.class, numid));
        break;
      }
      case 'B': {
        // Lookup base
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(TopicName.class, numid));
        break;
      }
      case 'N': {
        // Lookup variant name
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(VariantName.class, numid));
        break;
      }
      case 'R': {
        // Lookup association role
        result = (TMObjectIF)txn.getObject(txn.getAccessRegistrar().createIdentity(AssociationRole.class, numid));
        break;
      }
      case 'M': {
        // Lookup topic map (only this one can match)
        if (object_id.equals(getObjectId()))
          return this;
      }
      default:
        // No object was found
        return null;
      }
      
      // return object if it is part of this topic map
      return ((result == null || result.getTopicMap() != this) ? null : result);

    } catch (IndexOutOfBoundsException e) {
      return null;
    } catch (OntopiaRuntimeException e) {
      return null;
    }
  }

  public void clear() {
    throw new ReadOnlyException();    
  }
  
  // ---------------------------------------------------------------------------
  // Misc. methods
  // ---------------------------------------------------------------------------

  public String toString() {
    return ObjectStrings.toString("rdbms.ReadOnlyTopicMap", (TopicMapIF)this);
  }

  // ---------------------------------------------------------------------------
  // Subject identity cache
  // ---------------------------------------------------------------------------

  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    return transaction.getObjectByItemIdentifier(locator);
  }

  public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
    return transaction.getTopicBySubjectLocator(locator);
  }

  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    return transaction.getTopicBySubjectIdentifier(locator);
  }

  // ---------------------------------------------------------------------------
  // Role type cache
  // ---------------------------------------------------------------------------

  public Collection getRolesByType(TopicIF player, TopicIF rtype) {
    return transaction.getRolesByType(player, rtype);
  }

  // ---------------------------------------------------------------------------
  // Role type and association type cache
  // ---------------------------------------------------------------------------


  public Collection getRolesByType(TopicIF player, TopicIF rtype, TopicIF atype) {
    return transaction.getRolesByType(player, rtype, atype);
  }
  
  // ---------------------------------------------------------------------------
  // ReifiableIF implementation
  // ---------------------------------------------------------------------------

  public TopicIF getReifier() {
    return (TopicIF)loadField(TopicMap.LF_reifier);
  }
  
  public void setReifier(TopicIF reifier) {
    throw new ReadOnlyException();
  }
  
}

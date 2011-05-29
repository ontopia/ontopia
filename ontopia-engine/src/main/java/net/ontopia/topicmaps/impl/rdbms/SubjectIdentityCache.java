
package net.ontopia.topicmaps.impl.rdbms;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.persistence.proxy.CachesIF;
import net.ontopia.persistence.proxy.IdentityIF;
import net.ontopia.persistence.proxy.PersistentIF;
import net.ontopia.persistence.proxy.QueryCache;
import net.ontopia.persistence.proxy.RDBMSStorage;
import net.ontopia.persistence.proxy.TransactionIF;
import net.ontopia.persistence.proxy.TransactionalLookupIndexIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.utils.TopicMapTransactionIF;
import net.ontopia.topicmaps.impl.utils.AbstractSubjectIdentityCache;
import net.ontopia.utils.CollectionFactoryIF;
import net.ontopia.utils.PropertyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL:
 */

public class SubjectIdentityCache extends AbstractSubjectIdentityCache {

  private static final long serialVersionUID = 4342065742305830481L;

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(SubjectIdentityCache.class.getName());

  protected TopicMapTransactionIF txn;  
  protected TransactionIF ptxn;
  
  protected TransactionalLookupIndexIF source_locators;
  protected TransactionalLookupIndexIF subject_indicators;
  protected TransactionalLookupIndexIF subjects;
        
  public SubjectIdentityCache(TopicMapTransactionIF txn, CollectionFactoryIF cfactory) {
    super(null);
    this.handlers = cfactory.makeLargeMap();
    this.txn = txn;
    this.ptxn = ((RDBMSTopicMapTransaction)txn).getTransaction();
    
    // initialize state
    initialize();
  }

  protected String getProperty(String name) {
    return ptxn.getStorageAccess().getProperty(name);
  }
  
  public void initialize() {
    //! boolean debug = PropertyUtils.isTrue(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.debug"), false);

    // lookup caches
    TopicMapIF tm = txn.getTopicMap();
    IdentityIF tmid = ((PersistentIF)tm)._p_getIdentity();
    RDBMSStorage storage = (RDBMSStorage)ptxn.getStorageAccess().getStorage();
    
    if (storage.isSharedCache()) {
      source_locators = new SharedLocatorLookup(ptxn.getStorageAccess(),
          (QueryCache)storage.getHelperObject(CachesIF.QUERY_CACHE_SRCLOC, tmid), tmid);
      subject_indicators = new SharedLocatorLookup(ptxn.getStorageAccess(),
          (QueryCache)storage.getHelperObject(CachesIF.QUERY_CACHE_SUBIND, tmid), tmid);
      subjects = new SharedLocatorLookup(ptxn.getStorageAccess(),
          (QueryCache)storage.getHelperObject(CachesIF.QUERY_CACHE_SUBLOC, tmid), tmid);

    } else {

      int lrusize_srcloc = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.srcloc.lru"), 2000);
      int lrusize_subind = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subind.lru"), 1000);
      int lrusize_subloc = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subloc.lru"), 100);    
      
      source_locators = new LocatorLookup("TopicMapIF.getObjectByItemIdentifier", ptxn, tm, lrusize_srcloc);
      subject_indicators  = new LocatorLookup("TopicMapIF.getTopicBySubjectIdentifier", ptxn, tm, lrusize_subind);
      subjects = new LocatorLookup("TopicMapIF.getTopicBySubject", ptxn, tm, lrusize_subloc);
    }
  }

  // -----------------------------------------------------------------------------
  // transaction callbacks
  // -----------------------------------------------------------------------------

  public void commit() {    
    subjects.commit();
    subject_indicators.commit();
    source_locators.commit();
  }

  public void abort() {
    subjects.abort();
    subject_indicators.abort();
    source_locators.abort();
  }

  // -----------------------------------------------------------------------------
  // TopicMapIF locator lookup methods
  // -----------------------------------------------------------------------------

  // These methods are called by the topic map object.
  
  public TMObjectIF getObjectById(String object_id) {
    throw new UnsupportedOperationException("This method should not be called.");
  }
  
  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    Object o = source_locators.get(locator);
    return (TMObjectIF)(o == null ? null : ptxn.getObject((IdentityIF)o));
  }
  
  public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
    Object o = subjects.get(locator);
    return (TopicIF)(o == null ? null : ptxn.getObject((IdentityIF)o));
  }
  
  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    Object o = subject_indicators.get(locator);
    return (TopicIF)(o == null ? null : ptxn.getObject((IdentityIF)o));
  }

  // -----------------------------------------------------------------------------
  // Event handler methods
  // -----------------------------------------------------------------------------

  // The following methods populate the various caches when object
  // model events are triggered.
  
  protected TMObjectIF _getObjectByItemIdentifier(LocatorIF source_locator) {
    Object o = source_locators.get(source_locator);
    return (TMObjectIF)(o == null ? null : ptxn.getObject((IdentityIF)o));
  }

  protected void registerSourceLocator(LocatorIF source_locator, TMObjectIF object) {
    source_locators.put(source_locator, ((PersistentIF)object)._p_getIdentity());
  }
  
  protected void unregisterSourceLocator(LocatorIF source_locator) {
    source_locators.remove(source_locator);
  }

  protected TopicIF _getTopicBySubjectIdentifier(LocatorIF subject_indicator) {
    Object o = subject_indicators.get(subject_indicator);
    return (TopicIF)(o == null ? null : ptxn.getObject((IdentityIF)o));
  }

  protected void registerSubjectIndicator(LocatorIF subject_indicator, TopicIF object) {
    subject_indicators.put(subject_indicator, ((PersistentIF)object)._p_getIdentity());
  }
  
  protected void unregisterSubjectIndicator(LocatorIF subject_indicator) {
    subject_indicators.remove(subject_indicator);
  }

  protected TopicIF _getTopicBySubjectLocator(LocatorIF subject) {
    Object o = subjects.get(subject);
    return (TopicIF)(o == null ? null : ptxn.getObject((IdentityIF)o));
  }

  protected void registerSubject(LocatorIF subject, TopicIF object) {
    subjects.put(subject, ((PersistentIF)object)._p_getIdentity());
  }
  
  protected void unregisterSubject(LocatorIF subject) {
    subjects.remove(subject);
  }
        
}

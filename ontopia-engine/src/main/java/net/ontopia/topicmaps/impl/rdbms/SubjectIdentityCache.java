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

/**
 * INTERNAL:
 */

public class SubjectIdentityCache extends AbstractSubjectIdentityCache {

  private static final long serialVersionUID = 4342065742305830481L;

  protected TopicMapTransactionIF txn;  
  protected TransactionIF ptxn;
  
  protected TransactionalLookupIndexIF<LocatorIF, IdentityIF> source_locators;
  protected TransactionalLookupIndexIF<LocatorIF, IdentityIF> subject_indicators;
  protected TransactionalLookupIndexIF<LocatorIF, IdentityIF> subjects;
        
  private final IdentityIF NULL_OBJECT_IDENTITY = new IdentityIF() {
    @Override
    public Class<?> getType() { throw new UnsupportedOperationException(); }
    @Override
    public int getWidth() { throw new UnsupportedOperationException(); }
    @Override
    public Object getKey(int index) { throw new UnsupportedOperationException(); }
    @Override
    public Object createInstance() throws Exception { throw new UnsupportedOperationException(); }
    @Override
    public Object clone() { throw new UnsupportedOperationException(); }
  };

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
      source_locators = new SharedLocatorLookup<IdentityIF>(ptxn.getStorageAccess(),
          (QueryCache<LocatorIF, IdentityIF>)storage.getHelperObject(CachesIF.QUERY_CACHE_SRCLOC, tmid), tmid);
      subject_indicators = new SharedLocatorLookup<IdentityIF>(ptxn.getStorageAccess(),
          (QueryCache<LocatorIF, IdentityIF>)storage.getHelperObject(CachesIF.QUERY_CACHE_SUBIND, tmid), tmid);
      subjects = new SharedLocatorLookup<IdentityIF>(ptxn.getStorageAccess(),
          (QueryCache<LocatorIF, IdentityIF>)storage.getHelperObject(CachesIF.QUERY_CACHE_SUBLOC, tmid), tmid);

    } else {

      int lrusize_srcloc = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.srcloc.lru"), 2000);
      int lrusize_subind = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subind.lru"), 1000);
      int lrusize_subloc = PropertyUtils.getInt(getProperty("net.ontopia.topicmaps.impl.rdbms.Cache.subjectidentity.subloc.lru"), 100);    
      
      source_locators = new LocatorLookup<IdentityIF>("TopicMapIF.getObjectByItemIdentifier", ptxn, tm, lrusize_srcloc, NULL_OBJECT_IDENTITY);
      subject_indicators  = new LocatorLookup<IdentityIF>("TopicMapIF.getTopicBySubjectIdentifier", ptxn, tm, lrusize_subind, NULL_OBJECT_IDENTITY);
      subjects = new LocatorLookup<IdentityIF>("TopicMapIF.getTopicBySubject", ptxn, tm, lrusize_subloc, NULL_OBJECT_IDENTITY);
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
  
  @Override
  public TMObjectIF getObjectById(String object_id) {
    throw new UnsupportedOperationException("This method should not be called.");
  }
  
  @Override
  public TMObjectIF getObjectByItemIdentifier(LocatorIF locator) {
    IdentityIF o = source_locators.get(locator);
    return (TMObjectIF)(o == null ? null : ptxn.getObject(o));
  }
  
  @Override
  public TopicIF getTopicBySubjectLocator(LocatorIF locator) {
    IdentityIF o = subjects.get(locator);
    return (TopicIF)(o == null ? null : ptxn.getObject(o));
  }
  
  @Override
  public TopicIF getTopicBySubjectIdentifier(LocatorIF locator) {
    IdentityIF o = subject_indicators.get(locator);
    return (TopicIF)(o == null ? null : ptxn.getObject(o));
  }

  // -----------------------------------------------------------------------------
  // Event handler methods
  // -----------------------------------------------------------------------------

  // The following methods populate the various caches when object
  // model events are triggered.
  
  @Override
  protected TMObjectIF _getObjectByItemIdentifier(LocatorIF source_locator) {
    IdentityIF o = source_locators.get(source_locator);
    return (TMObjectIF)(o == null ? null : ptxn.getObject(o));
  }

  @Override
  protected void registerSourceLocator(LocatorIF source_locator, TMObjectIF object) {
    source_locators.put(source_locator, ((PersistentIF)object)._p_getIdentity());
  }
  
  @Override
  protected void unregisterSourceLocator(LocatorIF source_locator) {
    source_locators.remove(source_locator);
  }

  @Override
  protected TopicIF _getTopicBySubjectIdentifier(LocatorIF subject_indicator) {
    IdentityIF o = subject_indicators.get(subject_indicator);
    return (TopicIF)(o == null ? null : ptxn.getObject(o));
  }

  @Override
  protected void registerSubjectIndicator(LocatorIF subject_indicator, TopicIF object) {
    subject_indicators.put(subject_indicator, ((PersistentIF)object)._p_getIdentity());
  }
  
  @Override
  protected void unregisterSubjectIndicator(LocatorIF subject_indicator) {
    subject_indicators.remove(subject_indicator);
  }

  @Override
  protected TopicIF _getTopicBySubjectLocator(LocatorIF subject) {
    IdentityIF o = subjects.get(subject);
    return (TopicIF)(o == null ? null : ptxn.getObject(o));
  }

  @Override
  protected void registerSubject(LocatorIF subject, TopicIF object) {
    subjects.put(subject, ((PersistentIF)object)._p_getIdentity());
  }
  
  @Override
  protected void unregisterSubject(LocatorIF subject) {
    subjects.remove(subject);
  }
        
}

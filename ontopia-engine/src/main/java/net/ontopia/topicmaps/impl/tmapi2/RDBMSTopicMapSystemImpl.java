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

package net.ontopia.topicmaps.impl.tmapi2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.impl.rdbms.RDBMSStoreFactory;
import net.ontopia.topicmaps.impl.rdbms.RDBMSTopicMapSource;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TopicMapExistsException;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 * 
 * Required properties:
 * <ul>
 * <li>net.ontopia.topicmaps.impl.rdbms.Database (e.g. mysql)
 * <li>net.ontopia.topicmaps.impl.rdbms.ConnectionString (e.g. jdbc:mysql://localhost/ontopia)
 * <li>net.ontopia.topicmaps.impl.rdbms.DriverClass (e.g. com.mysql.jdbc.Driver)
 * <li>net.ontopia.topicmaps.impl.rdbms.UserName
 * <li>net.ontopia.topicmaps.impl.rdbms.Password
 * <li>net.ontopia.topicmaps.impl.rdbms.ConnectionPool (e.g. true)
 * </ul>
 */
public class RDBMSTopicMapSystemImpl implements TopicMapSystemIF {

  private TopicMapSystemFactory systemFactory;
  private RDBMSTopicMapSource source;
  private TopicMapStoreFactoryIF storeFactory;
  
  public RDBMSTopicMapSystemImpl(TopicMapSystemFactory topicMapSystemFactory) {
    systemFactory = topicMapSystemFactory;
    Map<String, String> props = new HashMap<String, String>();
    for (String key : systemFactory.properties.stringPropertyNames()) {
      props.put(key, systemFactory.properties.getProperty(key));
    }
    source = new RDBMSTopicMapSource(props);
    source.setSupportsDelete(true);
    source.setSupportsCreate(true);
    storeFactory = new RDBMSStoreFactory(systemFactory.properties);
  }

  @Override
  public void close() {
    for (Object obj: source.getReferences()) {
      TopicMapReferenceIF ref = (TopicMapReferenceIF) obj;
      if (ref.isOpen() && !ref.isDeleted()) {
        TopicMapStoreIF store = null;
        try {
           store = ref.createStore(false);
        }
        catch (IOException ex) {
          //TODO: Log the error? Raise a TMAPIRuntimeEx?
          continue;
        }
        store.commit();
      }
      // The underlying store gets closed as well.
      ref.close();
    }
    source = null;
    storeFactory = null;
  }

  private LocatorIF createLocatorIF(String reference) {
    try {
      return new URILocator(reference);
    } catch (URISyntaxException ex) {
      throw new MalformedIRIException(ex.getMessage());
    }
  }
  
  @Override
  public Locator createLocator(String reference) {
    return wrapLocator(createLocatorIF(reference));
  }

  @Override
  public Locator wrapLocator(LocatorIF loc) {
    return new LocatorImpl(loc);
  }

  @Override
  public TopicMapImpl createTopicMap(Locator loc) throws TopicMapExistsException {
    TopicMapImpl tm = getTopicMap(loc);
    if (tm != null) {
      tm.close();
      throw new TopicMapExistsException("Topic map with base locator " + loc + " already exists."); 
    }
    
    TopicMapStoreIF store = storeFactory.createStore();
    store.setBaseAddress(((LocatorImpl) loc).getWrapped());
    store.commit();
    
    // refresh the source
    source.refresh();
    
    tm = new TopicMapImpl(this, store);
    return tm;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#createTopicMap(java.lang.String)
   */
  
  @Override
  public TopicMapImpl createTopicMap(String reference) throws TopicMapExistsException {
    return createTopicMap(createLocator(reference));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getFeature(java.lang.String)
   */
  
  @Override
  public boolean getFeature(String feature) throws FeatureNotRecognizedException {
    return systemFactory.getFeature(feature);
  }

  @Override
  public Set<Locator> getLocators() {
    Collection refs = source.getReferences();
    Set<Locator> locators = new HashSet<Locator>();
    
    for (Object obj : refs) {
      TopicMapReferenceIF ref = (TopicMapReferenceIF) obj;

      try {
        TopicMapStoreIF store = ref.createStore(true);
        locators.add(wrapLocator(store.getBaseAddress()));
      } catch (IOException e) {
        // TODO: do logging
      }
    }
    
    return locators;
  }

  @Override
  public Object getProperty(String property) {
    return systemFactory.getProperty(property);
  }

  @Override
  public TopicMapImpl getTopicMap(String reference) {
    return getTopicMap(createLocator(reference));
  }

  @Override
  public TopicMapImpl getTopicMap(Locator loc) {
    Collection refs = source.getReferences();
    for (Object obj : refs) {
      TopicMapReferenceIF ref = (TopicMapReferenceIF) obj;
      
      try {
        TopicMapStoreIF store = ref.createStore(false);
        if (loc.equals(wrapLocator(store.getBaseAddress()))) {
          return new TopicMapImpl(this, store);
        }
        store.close();
      } catch (IOException e) {
        // TODO: do logging
      }
    }
    return null;
  }

  /* --- callbacks */

  @Override
  public void remove(LocatorIF loc) {
    source.refresh();
  }

}

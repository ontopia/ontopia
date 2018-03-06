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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TopicMapExistsException;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */
public class MemoryTopicMapSystemImpl implements TopicMapSystemIF {
  private TopicMapSystemFactory sf;
  private Map<Locator, TopicMapImpl> loc2tm =
    new HashMap<Locator, TopicMapImpl>();

  public MemoryTopicMapSystemImpl(TopicMapSystemFactory topicMapSystemFactory) {
    sf = topicMapSystemFactory;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#close()
   */  
  @Override
  public void close() {
    loc2tm.clear();
    loc2tm = null;
  }

  private LocatorIF createLocatorIF(String reference) {
    try {
      return new URILocator(reference);
    } catch (URISyntaxException ex) {
      throw new MalformedIRIException(ex.getMessage());
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#createLocator(java.lang.String)
   */
  
  @Override
  public Locator createLocator(String reference) {
    return wrapLocator(createLocatorIF(reference));
  }

  @Override
  public Locator wrapLocator(LocatorIF loc) {
    return new LocatorImpl(loc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#createTopicMap(org.tmapi.core.Locator)
   */
  
  @Override
  public TopicMapImpl createTopicMap(Locator loc) throws TopicMapExistsException {
    if (loc2tm.containsKey(loc)) {
      throw new TopicMapExistsException("Topic map with base locator " + loc +
                                        " already exists");
    } else {
      // create new topic map
      InMemoryTopicMapStore store = new InMemoryTopicMapStore();
      store.setBaseAddress(((LocatorImpl) loc).getWrapped());
      TopicMapImpl tm = new TopicMapImpl(this, store);
      // register base locator
      loc2tm.put(loc, tm);
      return tm;
    }
  }

  public TopicMapImpl createTopicMap(TopicMapIF topicmap)
    throws TopicMapExistsException {
    Locator loc = wrapLocator(topicmap.getStore().getBaseAddress());
    if (loc2tm.containsKey(loc)) {
      throw new TopicMapExistsException("Topic map with base locator " + loc +
                                        " already exists");
    }
    // wrap the topic map
    TopicMapImpl tm = new TopicMapImpl(this, topicmap.getStore());
    // register base locator
    loc2tm.put(loc, tm);
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
    return sf.getFeature(feature);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getLocators()
   */
  
  @Override
  public Set<Locator> getLocators() {
    return new HashSet<Locator>(loc2tm.keySet());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getProperty(java.lang.String)
   */
  
  @Override
  public Object getProperty(String property) {
    return sf.getProperty(property);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getTopicMap(java.lang.String)
   */
  
  @Override
  public TopicMapImpl getTopicMap(String reference) {
    return getTopicMap(createLocator(reference));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getTopicMap(org.tmapi.core.Locator)
   */
  
  @Override
  public TopicMapImpl getTopicMap(Locator loc) {
    return loc2tm.get(loc);
  }

  /* --- callbacks */

  @Override
  public void remove(LocatorIF loc) {
    loc2tm.remove(wrapLocator(loc));
  }

}

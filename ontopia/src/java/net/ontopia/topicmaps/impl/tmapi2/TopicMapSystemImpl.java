// $Id:$
package net.ontopia.topicmaps.impl.tmapi2;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

import org.tmapi.core.FeatureNotRecognizedException;
import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;
import org.tmapi.core.TopicMapExistsException;
import org.tmapi.core.TopicMapSystem;

/**
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */
public class TopicMapSystemImpl implements TopicMapSystem {

  private TopicMapSystemFactory sf;
  private Map<Locator, TopicMapImpl> loc2tm = new HashMap<Locator, TopicMapImpl>();

  public TopicMapSystemImpl(TopicMapSystemFactory topicMapSystemFactory) {
    sf = topicMapSystemFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#close()
   */
  
  public void close() {
    loc2tm.clear();
    loc2tm = null;
  }

  private LocatorIF createLocatorIF(String reference) {
    try {
      return new URILocator(reference);
    } catch (MalformedURLException ex) {
      throw new MalformedIRIException(ex.getMessage());
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#createLocator(java.lang.String)
   */
  
  public Locator createLocator(String reference) {
    return wrapLocator(createLocatorIF(reference));
  }

  Locator wrapLocator(LocatorIF loc) {
    return new LocatorImpl(loc);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#createTopicMap(org.tmapi.core.Locator)
   */
  
  public TopicMapImpl createTopicMap(Locator loc) throws TopicMapExistsException {
    if (loc2tm.containsKey(loc)) {
      throw new TopicMapExistsException("Topic map with base locator " + loc + " already exists.");
    }
    else {
      // create new topic map
      InMemoryTopicMapStore store = new InMemoryTopicMapStore();
      store.setBaseAddress(((LocatorImpl) loc).getWrapped());
      TopicMapImpl tm = new TopicMapImpl(this, store);
      // register base locator
      loc2tm.put(loc, tm);
      return tm;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#createTopicMap(java.lang.String)
   */
  
  public TopicMapImpl createTopicMap(String reference) throws TopicMapExistsException {
    return createTopicMap(createLocator(reference));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getFeature(java.lang.String)
   */
  
  public boolean getFeature(String feature) throws FeatureNotRecognizedException {
    return sf.getFeature(feature);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getLocators()
   */
  
  public Set<Locator> getLocators() {
    return new HashSet<Locator>(loc2tm.keySet());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getProperty(java.lang.String)
   */
  
  public Object getProperty(String property) {
    return sf.getProperty(property);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getTopicMap(java.lang.String)
   */
  
  public TopicMapImpl getTopicMap(String reference) {
    return getTopicMap(createLocator(reference));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.core.TopicMapSystem#getTopicMap(org.tmapi.core.Locator)
   */
  
  public TopicMapImpl getTopicMap(Locator loc) {
    return loc2tm.get(loc);
  }

  /* --- callbacks */

  void close(TopicMapImpl topicmap) {
    // Something to do here?
  }

  void remove(TopicMapImpl topicmap) {
    loc2tm.remove(wrapLocator(topicmap.getWrapped().getStore().getBaseAddress()));
  }

}

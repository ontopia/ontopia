
// $Id: TopicMapSystem.java,v 1.6 2006/05/08 12:51:09 grove Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.utils.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.infoset.impl.basic.GenericLocator;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class TopicMapSystem implements org.tmapi.core.TopicMapSystem {

  TopicMapSystemFactory sf;
  Map baselocs = new HashMap();

  TopicMapSystem(TopicMapSystemFactory sf) {
    this.sf = sf;
  }

  public org.tmapi.core.TopicMap createTopicMap(String baseLocatorReference, String baseLocatorNotation)
    throws org.tmapi.core.TopicMapExistsException {
    return createTopicMap(createLocator(baseLocatorReference, baseLocatorNotation));
  }

  public org.tmapi.core.TopicMap createTopicMap(String baseLocatorReference)
    throws org.tmapi.core.TopicMapExistsException {
    return createTopicMap(createLocator(baseLocatorReference));
  }

  private org.tmapi.core.TopicMap createTopicMap(org.tmapi.core.Locator baseLocator)
    throws org.tmapi.core.TopicMapExistsException {
    if (baselocs.containsKey(baseLocator))
      throw new org.tmapi.core.TopicMapExistsException("Topic map with base locator " + baseLocator + " already exists.");
    else {
      // create new topic map
      InMemoryTopicMapStore store = new InMemoryTopicMapStore();
      store.setBaseAddress(createLocatorIF(baseLocator.getReference(), baseLocator.getNotation()));
      TopicMap tm = new TopicMap(this, store);
      // register base locator
      baselocs.put(baseLocator, tm);
      return tm;
    }
  }
  
  public org.tmapi.core.TopicMap getTopicMap(String baseLocatorReference) {
    return getTopicMap(createLocator(baseLocatorReference));
  }
   
  public org.tmapi.core.TopicMap getTopicMap(String baseLocatorReference, String baseLocatorNotation) {
    return getTopicMap(createLocator(baseLocatorReference, baseLocatorNotation));
  }
  
  public org.tmapi.core.TopicMap getTopicMap(org.tmapi.core.Locator baseLocator) {
    return (org.tmapi.core.TopicMap)baselocs.get(baseLocator);
  }
  
  public Set getBaseLocators() {
    return new HashSet(baselocs.keySet());
  }

  public boolean getFeature(String feature)
    throws org.tmapi.core.FeatureNotRecognizedException {
    return sf.getFeature(feature);
  }


  public String getProperty(String propname) {
    return sf.getProperty(propname);
  }

  public void close() {
    // TODO: implement close
  }

  /* --- callbacks */

  void close(org.tmapi.core.TopicMap topicmap) {
    //! System.out.println("Closing " + topicmap);
  }

  void remove(org.tmapi.core.TopicMap topicmap) {
    //! System.out.println("Removing " + topicmap);
    // get base locator
    org.tmapi.core.Locator baseLocator = topicmap.getBaseLocator();
    // remove topic map
    baselocs.remove(baseLocator);    
  }

  /* --- factory methods */

  static org.tmapi.core.Locator createLocator(String address) {
    return createLocator(address, null);
  }
  
  static org.tmapi.core.Locator createLocator(String address, String notation) {
    if (notation == null)
      return new Locator(createLocatorIF(address));
    else
      return new Locator(createLocatorIF(address, notation));
  }

  static LocatorIF createLocatorIF(String address) {
    return createLocatorIF(address, null);
  }

  static LocatorIF createLocatorIF(String address, String notation) {
    if (notation == null || notation.equals("URI")) {
      try {
        return new URILocator(address);
      } catch (java.net.MalformedURLException e) {
        return new GenericLocator((notation == null ? "URI" : notation), address);
      }
    } else
      return new GenericLocator(notation, address);
  }

}

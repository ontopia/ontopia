package net.ontopia.tropics.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import net.ontopia.topicmaps.utils.jtm.JTMTopicMapWriter;
import net.ontopia.topicmaps.xml.XTM2TopicMapWriter;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;

public class TopicMapUtils {
  public static TopicIF getTopicFromFragment(TopicMapIF tm, String fragment) 
  {
    LocatorIF refLoc = URILocator.create(tm.getStore().getBaseAddress().getAddress() + "#" + fragment);
    TopicIF topic = (TopicIF) tm.getObjectByItemIdentifier(refLoc);

    if (topic == null) 
    {
      LocatorIF topicRef = URILocator.create(fragment);
      topic = (topicRef != null) ? (TopicIF) tm.getObjectByItemIdentifier(topicRef)
          : null;
    }
    return topic;
  }

  public static String getMainName(TopicIF instance) 
  {
    TopicNameIF name = getMainNameIF(instance);
    if (name != null)
      return name.getValue();
    return "";
  }

  public static TopicNameIF getMainNameIF(TopicIF instance) 
  {
    if (instance != null)
    {
      Collection<TopicNameIF> c = instance.getTopicNames();

      for (TopicNameIF name : c) 
        if (name.getScope().size() == 0) 
          return name;

      if (c.iterator().hasNext()) 
        return c.iterator().next();
    }
    return null;
  }
  
  public String extractIdFromLocator(LocatorIF locator) {
    return (locator.getAddress().indexOf('#') >= 0) 
                  ? locator.getAddress().substring(locator.getAddress().lastIndexOf('#') + 1)
                  : null;
  }

  public TopicMapIF readFromXTM(String response, String base_address) {
    TopicMapIF tm = null;
    try {
      XTMTopicMapReader reader = new XTMTopicMapReader(new StringReader(response), new URILocator(base_address));
      tm = reader.read();      
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }    
    
    return tm;
  }
  
  public TopicMapIF readFromXTM(File xtmFile) {
    TopicMapIF tm = null;
    try {
      XTMTopicMapReader reader = new XTMTopicMapReader(xtmFile);
      tm = reader.read();      
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }    
    
    return tm;
  }
  
  public String writeToXTM(TopicMapIF tm) {
    StringWriter sw =  new StringWriter();
    try {
      XTM2TopicMapWriter tmWriter = new XTM2TopicMapWriter(sw, "UTF-8");
      tmWriter.write(tm);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }   
    return sw.toString();
  }
  
  public String writeToJTM(TopicMapIF tm) {
    StringWriter writer = new StringWriter();    
    
    try {      
      JTMTopicMapWriter jtmWriter = new JTMTopicMapWriter(writer);
      jtmWriter.write(tm);
    } catch (IOException e) {
      return null;      
    }    
   
    return writer.toString();
  }

  @SuppressWarnings("unchecked")
  public void iterateTopicMapIds(TopicMapRepositoryIF tmRepository, Predicate pred) {
    Collection<TopicMapReferenceIF> tmReferences = tmRepository.getReferences();
    for (Iterator<TopicMapReferenceIF> iterator = tmReferences.iterator(); iterator.hasNext();) {
      TopicMapReferenceIF tmReference = iterator.next();
      
      try {
        TopicMapStoreIF tmStore = tmReference.createStore(true);
        String baseAddress = tmStore.getBaseAddress().toString();
        
        String topicMapId = baseAddress.substring(baseAddress.lastIndexOf('/') + 1);
        topicMapId = topicMapId.substring(0, topicMapId.lastIndexOf('.'));
        
        pred.apply(topicMapId);
      } catch (IOException e) {
        e.printStackTrace();
      }      
    }
  }
  
  public TopicMapIF getTopicMap(String hostRef, String topicMapId) throws IOException {      
    TopicMapReferenceIF tmReference = findTopicMapReference(topicMapId + ".xtm", false);
    if (tmReference == null) return null;
    
    TopicMapStoreIF tmStore = tmReference.createStore(false);
    TopicMapIF tm = tmStore.getTopicMap();
    
    String iiPrefix = hostRef + "/api/v1/topics/"; 
    
    for (TopicIF topic : tm.getTopics()) {
      updateItemIdentiersToREST(topic, iiPrefix);
      updateNullOccurrences(topic);
    }
    
    tmStore.commit();
    tmReference.storeClosed(tmStore);

    return tm;
  }

  private void updateItemIdentiersToREST(TopicIF topic, String iiPrefix) {
    Collection<LocatorIF> itemIdentifiers = topic.getItemIdentifiers();
    boolean containsRESTfulII = false;
    
    for (LocatorIF itemIdentifier : itemIdentifiers) {
      if (itemIdentifier.getAddress().toString().startsWith(iiPrefix)) {
        containsRESTfulII = true;
        break;
      }
    }
    
    if (!containsRESTfulII) {
      for (LocatorIF itemIdentifier : itemIdentifiers) {
        if (itemIdentifier.getAddress().contains("#")) {
          topic.addItemIdentifier(URILocator.create(iiPrefix + extractIdFromLocator(itemIdentifier)));            
          break;
        }
      }
    }
  }
  
  /**
   * Empty occurrence values are turned into null pointers by Ontopia when you  
   * get their value which interferes with merging later on. This method 
   * replaces those null values with single space (' ') strings.
   * 
   * @param topic The topic to check for null values in occurrences
   */
  private void updateNullOccurrences(TopicIF topic) {
    for (OccurrenceIF occurrence : topic.getOccurrences()) {
      if (occurrence.getValue() == null) {
        System.out.println("Found (null) in occurrence of Topic<" + topic.toString() + ">");
        occurrence.setValue(" ");
      }
    }
  }

  @SuppressWarnings("unchecked")
  public TopicMapReferenceIF findTopicMapReference(String topicMapId, boolean readonly) throws IOException {
    TopicMapRepositoryIF tmRepository = TopicMaps.getRepository();
    
    Collection<TopicMapReferenceIF> tmReferences = tmRepository.getReferences();
    for (TopicMapReferenceIF tmReference : tmReferences) {
      TopicMapStoreIF store = tmReference.createStore(readonly);
      
      if (store.getBaseAddress().getAddress().endsWith(topicMapId)) {
        if (!readonly) tmReference.storeClosed(store);
             
        return tmReference;
      }
      
      if (!readonly) tmReference.storeClosed(store);
    }
    
    return null;
  }
  
}

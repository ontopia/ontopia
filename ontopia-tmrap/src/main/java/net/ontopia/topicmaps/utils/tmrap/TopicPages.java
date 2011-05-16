package net.ontopia.topicmaps.utils.tmrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.topicmaps.utils.TopicStringifiers;

public class TopicPages {

  Map pagesMap;
  Map tmNameMap;
  private String name;
  private Collection sourceLocators;
  private Collection subjectIndicators;
  private Collection subjectLocators;
  
  public TopicPages() {
    pagesMap = new HashMap();
    tmNameMap = new HashMap();
    name = null;
    sourceLocators = new ArrayList();
    subjectIndicators = new ArrayList();
    subjectLocators = new ArrayList();
  }
  
  public Collection getTopicMapHandles() {
    return pagesMap.keySet();
  }
  
  public Collection getPages(String mapHandle) {
    return (Collection)pagesMap.get(mapHandle);
  }
  
  public String getTMName(String mapHandle) {
    return (String)tmNameMap.get(mapHandle);
  }
  
  public void addPage(String mapHandle, TopicPage page, String tmName) {
    Collection currentPages = (Collection)pagesMap.get(mapHandle);
    if (currentPages == null) {
      currentPages = new HashSet();
      pagesMap.put(mapHandle, currentPages);
      
      if (tmName != null)
        tmNameMap.put(mapHandle, tmName);
    }
    currentPages.add(page);
    
    if (name == null)
      name = TopicStringifiers.getDefaultStringifier()
          .toString(page.getTopic());
    sourceLocators.addAll(page.getTopic().getItemIdentifiers());
    subjectIndicators.addAll(page.getTopic().getSubjectIdentifiers());
    subjectLocators.addAll(page.getTopic().getSubjectLocators());
  }
  
  /**
   * Add all TopicPage objects from otherPages to this TopicPages object.
   * @param otherPages The TopicPages to add to TopicPages.
   */
  public void addAll(TopicPages otherPages) {
    Iterator otherMapHandles = otherPages.getTopicMapHandles().iterator();
    while (otherMapHandles.hasNext()) {
      String otherMapHandle = (String)otherMapHandles.next();
      
      Collection currentOtherPages = otherPages.getPages(otherMapHandle);
      Collection currentPages = getPages(otherMapHandle);
      if (currentPages == null)
        pagesMap.put(otherMapHandle, new HashSet(currentOtherPages));
      else
        currentPages.addAll(currentOtherPages);

      String otherName = otherPages.getTMName(otherMapHandle);
      String currentName = getTMName(otherMapHandle);
      if (currentName == null && otherName != null)
        tmNameMap.put(otherMapHandle, otherName);
    }
    
    if (name == null)
      name = otherPages.getName();
    sourceLocators.addAll(otherPages.getItemIdentifiers());
    subjectIndicators.addAll(otherPages.getSubjectIdentifiers());
    subjectLocators.addAll(otherPages.getSubjectLocators());
  }
  
  public String getName() {
    return name;
  }
  
  public Collection getItemIdentifiers() {
    return sourceLocators;
  }
  
  public Collection getSubjectIdentifiers() {
    return subjectIndicators;
  }
  
  public Collection getSubjectLocators() {
    return subjectLocators;
  }
}

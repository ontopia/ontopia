/*
 * #!
 * Ontopia TMRAP
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
package net.ontopia.topicmaps.utils.tmrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

public class TopicPages {

  private Map<String, Collection<TopicPage>> pagesMap;
  private Map<String, String> tmNameMap;
  private String name;
  private Collection<LocatorIF> sourceLocators;
  private Collection<LocatorIF> subjectIndicators;
  private Collection<LocatorIF> subjectLocators;
  
  public TopicPages() {
    pagesMap = new HashMap<String, Collection<TopicPage>>();
    tmNameMap = new HashMap<String, String>();
    name = null;
    sourceLocators = new ArrayList<LocatorIF>();
    subjectIndicators = new ArrayList<LocatorIF>();
    subjectLocators = new ArrayList<LocatorIF>();
  }
  
  public Collection<String> getTopicMapHandles() {
    return pagesMap.keySet();
  }
  
  public Collection<TopicPage> getPages(String mapHandle) {
    return pagesMap.get(mapHandle);
  }
  
  public String getTMName(String mapHandle) {
    return tmNameMap.get(mapHandle);
  }
  
  public void addPage(String mapHandle, TopicPage page, String tmName) {
    Collection<TopicPage> currentPages = pagesMap.get(mapHandle);
    if (currentPages == null) {
      currentPages = new HashSet<TopicPage>();
      pagesMap.put(mapHandle, currentPages);
      
      if (tmName != null)
        tmNameMap.put(mapHandle, tmName);
    }
    currentPages.add(page);
    
    if (name == null)
      name = TopicStringifiers.toString(page.getTopic());
    sourceLocators.addAll(page.getTopic().getItemIdentifiers());
    subjectIndicators.addAll(page.getTopic().getSubjectIdentifiers());
    subjectLocators.addAll(page.getTopic().getSubjectLocators());
  }
  
  /**
   * Add all TopicPage objects from otherPages to this TopicPages object.
   * @param otherPages The TopicPages to add to TopicPages.
   */
  public void addAll(TopicPages otherPages) {
    Iterator<String> otherMapHandles = otherPages.getTopicMapHandles().iterator();
    while (otherMapHandles.hasNext()) {
      String otherMapHandle = otherMapHandles.next();
      
      Collection<TopicPage> currentOtherPages = otherPages.getPages(otherMapHandle);
      Collection<TopicPage> currentPages = getPages(otherMapHandle);
      if (currentPages == null)
        pagesMap.put(otherMapHandle, new HashSet<TopicPage>(currentOtherPages));
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
  
  public Collection<LocatorIF> getItemIdentifiers() {
    return sourceLocators;
  }
  
  public Collection<LocatorIF> getSubjectIdentifiers() {
    return subjectIndicators;
  }
  
  public Collection<LocatorIF> getSubjectLocators() {
    return subjectLocators;
  }
}

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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import net.ontopia.utils.StringTemplateUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMFragmentExporter;

/**
 * EXPERIMENTAL: An implementation that looks up topics in all currently open
 * topic maps in the given registry.
 */
public class RegistryTopicIndex implements TopicIndexIF {
  protected TopicMapRepositoryIF repository;
  protected boolean readonly;
  protected String editBaseuri;
  protected String viewBaseuri;

  /**
   * @param editBaseuri a URL of the form
   * http://whatever/omnigator/stuff.jsp?tmid=%tmid%&id=%topicid% Note
   * that the %key% tokens are used to build the correct URI.
   */
  public RegistryTopicIndex(TopicMapRepositoryIF repository, boolean readonly,
                            String editBaseuri,
                            String viewBaseuri) {
    this.repository = repository;
    this.readonly = readonly;
    this.editBaseuri = editBaseuri;
    this.viewBaseuri = viewBaseuri;
  }

  @Override
  public Collection<TopicIF> getTopics(Collection<LocatorIF> indicators,
                              Collection<LocatorIF> sources,
                              Collection<LocatorIF> subjects) {
    Collection<TopicIF> topics = new ArrayList<TopicIF>();
    Iterator<String> iter = repository.getReferenceKeys().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      TopicMapReferenceIF ref = repository.getReferenceByKey(key);
      if (!ref.isOpen()) {
        continue; // if store not open, we skip it
      }

      TopicMapStoreIF store = null;
      try {
        store = ref.createStore(readonly);
        TopicMapIF topicmap = store.getTopicMap();
        TopicIF topic;
        
        Iterator<LocatorIF> it = indicators.iterator();
        while (it.hasNext()) {
          LocatorIF indicator = it.next();
          topic = topicmap.getTopicBySubjectIdentifier(indicator);
          if (topic != null) {
            topics.add(topic);
          }
        }
        
        it = sources.iterator();
        while (it.hasNext()) {
          LocatorIF srcloc = it.next();
          TMObjectIF object = null;
          String address = srcloc.getAddress();
          if (XTMFragmentExporter.isVirtualReference(address)) {
            if (key.equals(XTMFragmentExporter
                           .sourceTopicMapFromVirtualReference(address))) {
              object = topicmap.getObjectById(XTMFragmentExporter
                                              .resolveVirtualReference(address, key));
            } else {
              continue;
            }
          } else {
            object = topicmap.getObjectByItemIdentifier(srcloc);
          }
          
          if (object instanceof TopicIF) {
            topics.add((TopicIF) object);
          }
        }

        it = subjects.iterator();
        while (it.hasNext()) {
          LocatorIF subject = it.next();
          topic = topicmap.getTopicBySubjectLocator(subject);
          if (topic != null) {
            topics.add(topic);
          }
        }
      } catch (java.io.IOException e) {
        continue;
      } finally {
        if (store != null) {
          store.close();
        }
      }
    }
    return topics;
  }

  @Override
  public Collection<TopicIF> loadRelatedTopics(Collection<LocatorIF> indicators,
                                      Collection<LocatorIF> sources,
                                      Collection<LocatorIF> subjects,
                                      boolean two_step) {
    return getTopics(indicators, sources, subjects);
  }

  @Override
  public Collection<TopicPage> getTopicPages(Collection<LocatorIF> indicators,
                                  Collection<LocatorIF> sources,
                                  Collection<LocatorIF> subjects) {
    Collection<TopicPage> pages = new ArrayList<TopicPage>();
    Iterator<String> iter = repository.getReferenceKeys().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      TopicMapReferenceIF ref = repository.getReferenceByKey(key);
      if (!ref.isOpen()) {
        continue; // if store not open, we skip it
      }

      TopicMapStoreIF store = null;
      try {
        store = ref.createStore(readonly);
        TopicMapIF topicmap = store.getTopicMap();
        TopicIF topic;
        
        Iterator<LocatorIF> it = indicators.iterator();
        while (it.hasNext()) {
          LocatorIF indicator = it.next();
          topic = topicmap.getTopicBySubjectIdentifier(indicator);
          if (topic != null) {
            pages.add(getTopicPage(topic, key));
          }
        }
        
        it = sources.iterator();
        while (it.hasNext()) {
          LocatorIF srcloc = it.next();
          TMObjectIF object = null;
          String address = srcloc.getAddress();
          if (XTMFragmentExporter.isVirtualReference(address)) {
            if (key.equals(XTMFragmentExporter
                           .sourceTopicMapFromVirtualReference(address))) {
              object = topicmap.getObjectById(XTMFragmentExporter
                                              .resolveVirtualReference(address, key));
            } else {
              continue;
            }
          } else {
            object = topicmap.getObjectByItemIdentifier(srcloc);
          }
          
          if (object instanceof TopicIF) {
            pages.add(getTopicPage((TopicIF) object, key));
          }
        }
        
        it = subjects.iterator();
        while (it.hasNext()) {
          LocatorIF subject = it.next();
          topic = topicmap.getTopicBySubjectLocator(subject);
          if (topic != null) {
            pages.add(getTopicPage(topic, key));
          }
        }
      } catch (java.io.IOException e) {
        continue;
      } finally {
        if (store != null) {
          store.close();
        }
      }
    }
    return pages;
  }

  @Override
  public TopicPages getTopicPages2(Collection<LocatorIF> indicators,
                                   Collection<LocatorIF> sources,
                                   Collection<LocatorIF> subjects) {
    TopicPages retVal = new TopicPages();

    Iterator<String> iter = repository.getReferenceKeys().iterator();
    while (iter.hasNext()) {
      String key = iter.next();
      TopicMapReferenceIF ref = repository.getReferenceByKey(key);
      if (!ref.isOpen()) {
        continue; // if store not open, we skip it
      }

      String topicMapHandle = ref.getId();
      
      TopicMapStoreIF store = null;
      try {
        store = ref.createStore(readonly);
        TopicMapIF topicmap = store.getTopicMap();        
        TopicIF topic;
        
        String tmReifierName = TopicPage.getReifierName(topicmap);
        
        Iterator<LocatorIF> it = indicators.iterator();
        while (it.hasNext()) {
          LocatorIF indicator = it.next();
          topic = topicmap.getTopicBySubjectIdentifier(indicator);
          if (topic != null) {
            retVal.addPage(topicMapHandle, getTopicPage(topic, key), 
                           tmReifierName);
          }
        }
        
        it = sources.iterator();
        while (it.hasNext()) {
          LocatorIF srcloc = it.next();
          TMObjectIF object = null;
          String address = srcloc.getAddress();
          if (XTMFragmentExporter.isVirtualReference(address)) {
            if (key.equals(XTMFragmentExporter
                           .sourceTopicMapFromVirtualReference(address))) {
              object = topicmap.getObjectById(XTMFragmentExporter
                                              .resolveVirtualReference(address, key));
            } else {
              continue;
            }
          } else {
            object = topicmap.getObjectByItemIdentifier(srcloc);
          }
          
          if (object instanceof TopicIF) {
            retVal.addPage(topicMapHandle, getTopicPage((TopicIF) object, key),
                           tmReifierName);
          }
        }
        
        it = subjects.iterator();
        while (it.hasNext()) {
          LocatorIF subject = it.next();
          topic = topicmap.getTopicBySubjectLocator(subject);
          if (topic != null) { 
            retVal.addPage(topicMapHandle, getTopicPage(topic, key),
                           tmReifierName);
          }
        }
      } catch (java.io.IOException e) {
        continue;
      } finally {
        if (store != null) {
          store.close();
        }
      }
    }    
    return retVal;
  }

  @Override
  public void close() {
    repository = null;
  }

  // Internal methods

  private TopicPage getTopicPage(TopicIF topic, String key) {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("tmid", key);
    map.put("topicid", topic.getObjectId());

    String name = TopicStringifiers.toString(topic);
    String editUrl = null;
    String viewUrl = null;
    if (editBaseuri != null) {
      editUrl = StringTemplateUtils.replace(editBaseuri, map);
    }
    if (viewBaseuri != null) {
      viewUrl = StringTemplateUtils.replace(viewBaseuri, map);
    }
    return new TopicPage(editUrl, viewUrl, name, name, topic);
  }
}

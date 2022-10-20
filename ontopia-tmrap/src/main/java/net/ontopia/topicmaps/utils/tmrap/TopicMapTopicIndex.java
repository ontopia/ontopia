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
import java.util.Iterator;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;
import net.ontopia.topicmaps.xml.XTMFragmentExporter;
import net.ontopia.utils.StringTemplateUtils;

/**
 * EXPERIMENTAL: An implementation that looks up topics in all the given topic
 * map.
 */
public class TopicMapTopicIndex implements TopicIndexIF {
  protected TopicMapIF topicmap;
  protected String editBaseuri;
  protected String viewBaseuri;
  protected String tmid;

  /**
   * @param editBaseuri a URL of the form
   * http://whatever/omnigator/stuff.jsp?tmid=%tmid%&id=%topicid% Note
   * that the %key% tokens are used to build the correct URI.
   */
  public TopicMapTopicIndex(TopicMapIF topicmap, String editBaseuri,
      String viewBaseuri, String tmid) {
    this.topicmap = topicmap;
    this.editBaseuri = editBaseuri;
    this.viewBaseuri = viewBaseuri;
    this.tmid = tmid;
  }

  @Override
  public Collection<TopicIF> getTopics(Collection<LocatorIF> indicators,
                              Collection<LocatorIF> sources,
                              Collection<LocatorIF> subjects) {
    Collection<TopicIF> topics = new ArrayList<TopicIF>();
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
      TMObjectIF object;
      String address = srcloc.getAddress();
      if (XTMFragmentExporter.isVirtualReference(address)) {
        object = topicmap
          .getObjectById(XTMFragmentExporter.resolveVirtualReference(address, tmid));
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
    TopicIF topic;

    Iterator<LocatorIF> it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF indicator = it.next();
      topic = topicmap.getTopicBySubjectIdentifier(indicator);
      if (topic != null) {
        pages.add(getTopicPage(topic, tmid));
      }
    }

    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = it.next();
      TMObjectIF object;
      String address = srcloc.getAddress();
      if (XTMFragmentExporter.isVirtualReference(address)) {
        object = topicmap
          .getObjectById(XTMFragmentExporter.resolveVirtualReference(address, tmid));
      } else {
        object = topicmap.getObjectByItemIdentifier(srcloc);
      }
      
      if (object instanceof TopicIF) {
        pages.add(getTopicPage((TopicIF) object, tmid));
      }
    }

    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = it.next();
      topic = topicmap.getTopicBySubjectLocator(subject);
      if (topic != null) {
        pages.add(getTopicPage(topic, tmid));
      }
    }

    return pages;
  }

  @Override
  public TopicPages getTopicPages2(Collection<LocatorIF> indicators,
                                   Collection<LocatorIF> sources,
                                   Collection<LocatorIF> subjects) {
    TopicPages retVal = new TopicPages();
    String topicHandle = topicmap.getStore().getReference().getId();

    String tmReifierName = TopicPage.getReifierName(topicmap);

    TopicIF topic = null;

    Iterator<LocatorIF> it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF indicator = it.next();
      topic = topicmap.getTopicBySubjectIdentifier(indicator);
      if (topic != null) {
        retVal.addPage(topicHandle, getTopicPage(topic, tmid), tmReifierName);
      }
    }

    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = it.next();
      TMObjectIF object;
      String address = srcloc.getAddress();
      if (XTMFragmentExporter.isVirtualReference(address)) {
        object = topicmap
          .getObjectById(XTMFragmentExporter.resolveVirtualReference(address, tmid));
      } else {
        object = topicmap.getObjectByItemIdentifier(srcloc);
      }
      
      if (object instanceof TopicIF) {
        retVal.addPage(topicHandle, getTopicPage((TopicIF) object, tmid),
                tmReifierName);
      }
    }

    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = it.next();
      topic = topicmap.getTopicBySubjectLocator(subject);
      if (topic != null) {
        retVal.addPage(topicHandle, getTopicPage(topic, tmid), tmReifierName);
      }
    }
    return retVal;
  }

  @Override
  public void close() {
    topicmap.getStore().close();
    topicmap = null;
  }

  // Internal methods

  private TopicPage getTopicPage(TopicIF topic, String key) {

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("tmid", tmid);
    map.put("topicid", topic.getObjectId());

    String name = TopicStringifiers.toString(topic);
    String editUrl = (editBaseuri == null) ? null
        : StringTemplateUtils.replace(editBaseuri, map);
    String viewUrl = (viewBaseuri == null) ? null
        : StringTemplateUtils.replace(viewBaseuri, map);
    return new TopicPage(editUrl, viewUrl, name, name, topic);
  }
}

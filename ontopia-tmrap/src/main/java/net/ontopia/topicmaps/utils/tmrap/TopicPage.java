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

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.utils.TopicStringifiers;

/**
 * EXPERIMENTAL: Represents a topic page (that is, the primary page
 * for a topic in a web interface to a topic map).
 */
public class TopicPage {
  private String editUrl;
  private String viewUrl;
  private String title;
  private String linktext;
  private TopicIF topic;

  public TopicPage(String editUrl, String viewUrl, String title,
                   String linktext, TopicIF topic) {
    this.editUrl = editUrl;
    this.viewUrl = viewUrl;
    this.title = title;
    this.linktext = linktext;
    this.topic = topic;
  }
  
  public String getEditURL() {
    return editUrl;
  }

  public String getViewURL() {
    return viewUrl;
  }
  
  public String getURL() {
    return viewUrl == null ? editUrl : viewUrl;
  }

  public String getTitle() {
    return title;
  }

  public String getLinkText() {
    return linktext;
  }
  
  public TopicIF getTopic() {
    return topic;
  }

  public static String getReifierName(TopicMapIF topicmap) {
    TopicIF topicmapReifier = topicmap.getReifier();
    if (topicmapReifier != null) {
      return TopicStringifiers.toString(topicmapReifier);
    }
    return null;
  }
}

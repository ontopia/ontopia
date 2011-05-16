
// $Id: TopicPage.java,v 1.5 2008/05/21 13:40:14 geir.gronmo Exp $

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
      return TopicStringifiers.getDefaultStringifier()
          .toString(topicmapReifier);
    }
    return null;
  }
}

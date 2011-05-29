
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
import net.ontopia.utils.StringifierIF;

/**
 * EXPERIMENTAL: An implementation that looks up topics in all the given topic
 * map.
 */
public class TopicMapTopicIndex implements TopicIndexIF {
  protected TopicMapIF topicmap;
  protected String editBaseuri;
  protected String viewBaseuri;
  protected StringifierIF strify;
  protected String tmid;

  /**
   * @param baseuri a URL of the form
   * http://whatever/omnigator/stuff.jsp?tmid=%tmid%&id=%topicid% Note
   * that the %key% tokens are used to build the correct URI.
   */
  public TopicMapTopicIndex(TopicMapIF topicmap, String editBaseuri,
      String viewBaseuri, String tmid) {
    this.topicmap = topicmap;
    this.editBaseuri = editBaseuri;
    this.viewBaseuri = viewBaseuri;
    this.strify = TopicStringifiers.getDefaultStringifier();
    this.tmid = tmid;
  }

  public Collection getTopics(Collection indicators,
                              Collection sources,
                              Collection subjects) {
    Collection topics = new ArrayList();
    TopicIF topic;

    Iterator it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF indicator = (LocatorIF) it.next();
      topic = topicmap.getTopicBySubjectIdentifier(indicator);
      if (topic != null)
        topics.add(topic);
    }

    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = (LocatorIF) it.next();
      TMObjectIF object;
      String address = srcloc.getAddress();
      if (XTMFragmentExporter.isVirtualReference(address))
        object = topicmap
          .getObjectById(XTMFragmentExporter.resolveVirtualReference(address, tmid));
      else
        object = topicmap.getObjectByItemIdentifier(srcloc);

      if (object instanceof TopicIF)
        topics.add(object);
    }

    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = (LocatorIF) it.next();
      topic = topicmap.getTopicBySubjectLocator(subject);
      if (topic != null) topics.add(topic);
    }

    return topics;
  }

  public Collection loadRelatedTopics(Collection indicators,
                                      Collection sources,
                                      Collection subjects,
                                      boolean two_step) {
    return getTopics(indicators, sources, subjects);
  }

  public Collection getTopicPages(Collection indicators,
                                  Collection sources,
                                  Collection subjects) {
    Collection pages = new ArrayList();
    TopicIF topic;

    Iterator it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF indicator = (LocatorIF) it.next();
      topic = topicmap.getTopicBySubjectIdentifier(indicator);
      if (topic != null)
        pages.add(getTopicPage(topic, tmid));
    }

    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = (LocatorIF) it.next();
      TMObjectIF object;
      String address = srcloc.getAddress();
      if (XTMFragmentExporter.isVirtualReference(address))
        object = topicmap
          .getObjectById(XTMFragmentExporter.resolveVirtualReference(address, tmid));
      else
        object = topicmap.getObjectByItemIdentifier(srcloc);
      
      if (object instanceof TopicIF)
        pages.add(getTopicPage((TopicIF) object, tmid));
    }

    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = (LocatorIF) it.next();
      topic = topicmap.getTopicBySubjectLocator(subject);
      if (topic != null)
        pages.add(getTopicPage(topic, tmid));
    }

    return pages;
  }

  public TopicPages getTopicPages2(Collection indicators,
                                   Collection sources,
                                   Collection subjects) {
    TopicPages retVal = new TopicPages();
    String topicHandle = topicmap.getStore().getReference().getId();

    String tmReifierName = TopicPage.getReifierName(topicmap);

    TopicIF topic = null;

    Iterator it = indicators.iterator();
    while (it.hasNext()) {
      LocatorIF indicator = (LocatorIF) it.next();
      topic = topicmap.getTopicBySubjectIdentifier(indicator);
      if (topic != null)
        retVal.addPage(topicHandle, getTopicPage(topic, tmid), tmReifierName);
    }

    it = sources.iterator();
    while (it.hasNext()) {
      LocatorIF srcloc = (LocatorIF) it.next();
      TMObjectIF object;
      String address = srcloc.getAddress();
      if (XTMFragmentExporter.isVirtualReference(address))
        object = topicmap
          .getObjectById(XTMFragmentExporter.resolveVirtualReference(address, tmid));
      else
        object = topicmap.getObjectByItemIdentifier(srcloc);
      
      if (object instanceof TopicIF)
        retVal.addPage(topicHandle, getTopicPage((TopicIF) object, tmid),
                       tmReifierName);
    }

    it = subjects.iterator();
    while (it.hasNext()) {
      LocatorIF subject = (LocatorIF) it.next();
      topic = topicmap.getTopicBySubjectLocator(subject);
      if (topic != null)
        retVal.addPage(topicHandle, getTopicPage(topic, tmid), tmReifierName);
    }
    return retVal;
  }

  public void close() {
    topicmap.getStore().close();
    topicmap = null;
  }

  // Internal methods

  private TopicPage getTopicPage(TopicIF topic, String key) {

    Map map = new HashMap();
    map.put("tmid", tmid);
    map.put("topicid", topic.getObjectId());

    String name = strify.toString(topic);
    String editUrl = (editBaseuri == null) ? null
        : StringTemplateUtils.replace(editBaseuri, map);
    String viewUrl = (viewBaseuri == null) ? null
        : StringTemplateUtils.replace(viewBaseuri, map);
    return new TopicPage(editUrl, viewUrl, name, name, topic);
  }
}

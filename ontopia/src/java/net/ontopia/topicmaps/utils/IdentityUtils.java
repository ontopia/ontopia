
// $Id: IdentityUtils.java,v 1.8 2008/06/13 08:36:28 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.infoset.core.*;

/**
 * INTERNAL: Utilities for working with identities of topic map
 * objects.
 *
 * @since 3.4.4
 */
public class IdentityUtils {

  /**
   * INTERNAL: Looks up a topic map object by its symbolic id.
   */
  public static TMObjectIF getObjectBySymbolicId(TopicMapIF topicmap, String symbolicId) {
    LocatorIF loc = getSymbolicIdLocator(topicmap, symbolicId);
    if (loc != null)
      return topicmap.getObjectByItemIdentifier(loc);
    else
      return null;
  }

  /**
   * INTERNAL: Expands a symbolic id into a LocatorIF based on the
   * base locator of the given topic map..
   */
  public static LocatorIF getSymbolicIdLocator(TopicMapIF topicmap, String symbolicId) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    if (base != null)
      return base.resolveAbsolute('#' + symbolicId);
    else
      return null;
  }

  /**
   * INTERNAL: Returns the topic or topics with overlapping identities
   * in the given topic map. The topic argument should of course come
   * from a different topic map.
   *
   * @since 4.0
   */
  public static Collection findSameTopic(TopicMapIF topicmap, TopicIF topic) {
    Set result = new HashSet();
    // item identifiers
    Iterator srclocs = topic.getItemIdentifiers().iterator();
    while (srclocs.hasNext()) {
      LocatorIF srcloc = (LocatorIF)srclocs.next();
      TMObjectIF o = topicmap.getObjectByItemIdentifier(srcloc);
      if (o instanceof TopicIF)
        result.add(o);
    }
    // subject identifiers
    Iterator subinds = topic.getSubjectIdentifiers().iterator();
    while (subinds.hasNext()) {
      LocatorIF subind = (LocatorIF)subinds.next();
      TopicIF t = topicmap.getTopicBySubjectIdentifier(subind);
      if (t != null)
        result.add(t);
    }
    // subject locators
    Iterator sublocs = topic.getSubjectLocators().iterator();
    while (sublocs.hasNext()) {
      LocatorIF subloc = (LocatorIF)sublocs.next();
      TopicIF t = topicmap.getTopicBySubjectLocator(subloc);
      if (t != null)
        result.add(t);
    }
    return result;
  }

}

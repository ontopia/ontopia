
// $Id: LastModifiedAt.java,v 1.13 2008/06/13 08:36:30 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.impl.utils.ActionSignature;


/**
 * PUBLIC: Action that sets a timestamp on the topic passed to it as
 * an internal occurrence using the last-modified-at PSI as the
 * occurrence type.
 *
 * @since 2.0
 */
public class LastModifiedAt implements ActionIF {
  private static final String PSI_URI = "http://psi.ontopia.net/xtm/occurrence-type/last-modified-at";
  private LocatorIF psi;
  protected DateFormat formatter;
  
  public LastModifiedAt() {
    try {
      psi = new URILocator(PSI_URI);
    } catch (java.net.MalformedURLException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }

    formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  }

  public void perform(ActionParametersIF params,
                      ActionResponseIF response) {

    // test params
    ActionSignature paramsType = ActionSignature.getSignature("t");
    paramsType.validateArguments(params, this);

    TopicIF topic = (TopicIF) params.get(0);
    if (topic == null)
      // the topic has been deleted
      return;

    TopicMapIF topicmap = topic.getTopicMap();
    if (topicmap == null)
      // this means that the topic has been deleted, almost certainly by the
      // current request. that means that it does not make any sense for us
      // to do anything, so we just silently stop
      return;
    
    TopicIF lastmod = getLastModifiedTopic(topicmap);

    OccurrenceIF lastocc = null;
    
    Iterator it = topic.getOccurrences().iterator();
    while (it.hasNext()) {
      OccurrenceIF occ = (OccurrenceIF) it.next();
      if (lastmod.equals(occ.getType())) {
        lastocc = occ;
        break;
      }
    }

    if (lastocc == null)
      lastocc = topicmap.getBuilder().makeOccurrence(topic, lastmod, getTimeStamp());
		else
			lastocc.setValue(getTimeStamp());
  }

  // Internals

  protected String getTimeStamp() {
    return formatter.format(new Date());
  }

  private TopicIF getLastModifiedTopic(TopicMapIF topicmap) {
    TopicIF lastmod = topicmap.getTopicBySubjectIdentifier(psi);
    if (lastmod == null) {
      lastmod = topicmap.getBuilder().makeTopic();
      lastmod.addSubjectIdentifier(psi);
    }
    return lastmod;
  }
  
}

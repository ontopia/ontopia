// $Id: IndicatorsTag.java,v 1.11 2008/06/13 08:17:52 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the subject indicators
 * (as LocatorIF objects) of all the topics in a collection.
 */
public class IndicatorsTag extends BaseValueProducingAndAcceptingTag {

  public Collection process(Collection topics) throws JspTagException {
    // find all subject indicators of all topics in collection
    List subjectIndicators = new ArrayList();
    if (topics != null) {
      Iterator iter = topics.iterator();
      
      while (iter.hasNext()) {
        TopicIF topic = null;
        
        try {
          topic = (TopicIF) iter.next();
        } catch (ClassCastException e) {
          continue; // non-topic objects have no indicators
        }
        
        // get all subject indicators for specified topic as LocatorIF objects
        if (topic != null)
          subjectIndicators.addAll( topic.getSubjectIdentifiers() );
      }
    }
    return subjectIndicators;
  }

}






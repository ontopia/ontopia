// $Id: SubjectAddressTag.java,v 1.3 2008/06/13 08:17:53 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.infoset.core.LocatorIF;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;
import net.ontopia.utils.CollectionUtils;

/**
 * INTERNAL: Value Producing Tag for finding the subject address (as
 * LocatorIF objects) of all the topics in a collection.
 */
public class SubjectAddressTag extends BaseValueProducingAndAcceptingTag {
  
  public Collection process(Collection topics) throws JspTagException {
    // find all subject addresses of all topics in collection
    List subjectAddresses = new ArrayList();
    if (topics != null) {
      Iterator iter = topics.iterator();
      
      while (iter.hasNext()) {
        TopicIF topic = null;
        
        try {
          topic = (TopicIF) iter.next();
        } catch (ClassCastException e) {
          continue; // non-topic objects have no subject address
        }
        
        // get subject address for specified topic
        if (topic != null) {
          LocatorIF subject_address = (LocatorIF)CollectionUtils.getFirst(topic.getSubjectLocators());
          if (subject_address != null)
            subjectAddresses.add(subject_address);
        }
      }
    }
    return subjectAddresses;
  }

}

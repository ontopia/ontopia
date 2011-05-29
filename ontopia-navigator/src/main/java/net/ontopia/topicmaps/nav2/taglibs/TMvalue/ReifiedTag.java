
package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.*;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Finds the topic map objects which are reified by topics in
 * the input collection.
 */
public class ReifiedTag extends BaseValueProducingAndAcceptingTag {

  public Collection process(Collection topics) throws JspTagException {
    // Find all topics which are reified by topic map objects in collection
    if (topics == null)
      return Collections.EMPTY_SET;
    else {
      ArrayList reifiedObjects = new ArrayList();
      Iterator iter = topics.iterator();
      ReifiableIF reifiedObject;

      // Loop over the topic map objects
      while (iter.hasNext()) {
        // Get the topic map object that is reified by the given topic
        try {
          TopicIF topic = (TopicIF) iter.next();
          reifiedObject = topic.getReified();
          // If an object was found add it to the result list.
          if (reifiedObject != null)
            reifiedObjects.add(reifiedObject);
        } catch (ClassCastException e) {
          throw new NavigatorRuntimeException("'reified' tag got a collection containing non-topic objects.");
        }
      } // while
      // Return all reified objects found.
      return reifiedObjects;
    }
  }
  
}






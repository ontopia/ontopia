// $Id: NamesTag.java,v 1.15 2008/06/12 14:37:18 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.utils.FilterIF;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseScopedTag;

/**
 * INTERNAL: Value Producing Tag for finding all the base names
 * of all the topics in a collection.
 */
public class NamesTag extends BaseScopedTag {

  // tag attributes
  public boolean uniqueValues = false;
  
  public Collection process(Collection topics) throws JspTagException {
    if (topics == null)
      return Collections.EMPTY_SET;
    else {
      Iterator iter = topics.iterator();
      TopicIF topic = null;
      Collection curTopicNames;
      Object obj = null;
      FilterIF scopeFilter = null;

      // find all base names of all topics in collection
      Set basenames = new HashSet();

      // for performance we keep a separate set to store the base name values
      Set basenameValues = (uniqueValues ? new HashSet() : null);
      
      // setup scope filter for user context filtering
      if (useUserContextFilter)
        scopeFilter = getScopeFilter(SCOPE_BASENAMES);
      
      try {
        // loop over input collection of topics
        while (iter.hasNext()) {
          obj = iter.next();
          // --- only care about TopicIF instances
          topic = (TopicIF) obj;
          curTopicNames = topic.getTopicNames();
          if (!curTopicNames.isEmpty()) {
            if (scopeFilter != null)
              curTopicNames = scopeFilter.filter(curTopicNames.iterator());
            if (uniqueValues) {
              Iterator itBN = curTopicNames.iterator();
              while (itBN.hasNext()) {
                TopicNameIF cur = (TopicNameIF) itBN.next();
                if (!basenameValues.contains(cur.getValue())) {
                  basenames.add( cur );
                  basenameValues.add( cur.getValue() );
                }
              } // while itBN
            } else {
              basenames.addAll(curTopicNames);
            }
          }
        } // while iter
      } catch (ClassCastException e) {
        String msg = "NamesTag expected to get a input collection of topic " +
          "instances, but got instance of class " + obj.getClass().getName();
        throw new NavigatorRuntimeException(msg);
      }
    
      return basenames;
    }
  }


  /**
   * If the result collection should not contain the same basename
   * value more than one time.
   *
   * @param unique - String which is mapped to boolean
   *                 (allowed values: true|yes|false|no)
   */
  public void setUniqueValues(String unique) {
    this.uniqueValues = (unique.equalsIgnoreCase("true")
                         || unique.equalsIgnoreCase("yes"));
  }
  
}







// $Id: InstancesTag.java,v 1.14 2008/06/11 16:56:00 geir.gronmo Exp $

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the instances of all
 * the objects in a collection.
 */
public class InstancesTag extends BaseValueProducingAndAcceptingTag {

  // constants
  public final static String KIND_ALL   = "all";
  public final static String KIND_TOPIC = "topic";
  public final static String KIND_OCC   = "occurrence";
  public final static String KIND_ASSOC = "association";
  public final static String KIND_ROLE  = "role";
  
  // tag attributes
  protected String instancesAs = KIND_TOPIC; // default behaviour

  
  public Collection process(Collection tmObjects) throws JspTagException {
    ClassInstanceIndexIF index = null;

    if (tmObjects == null || tmObjects.isEmpty())
      return Collections.EMPTY_SET;
    else{
      Collection instances = new HashSet();
      Iterator iter = tmObjects.iterator();
      TopicIF topic;
      while (iter.hasNext()) {
        topic = (TopicIF) iter.next();
        if (index == null)
          index = getIndex(topic);

        if (instancesAs.equals(KIND_TOPIC))
          instances.addAll( index.getTopics(topic) );
        else if (instancesAs.equals(KIND_ASSOC))
          instances.addAll( index.getAssociations(topic) );
        else if (instancesAs.equals(KIND_ROLE))
          instances.addAll( index.getAssociationRoles(topic) );
        else if (instancesAs.equals(KIND_OCC))
          instances.addAll( index.getOccurrences(topic) );
        else if (instancesAs.equals(KIND_ALL)) {
          instances.addAll( index.getTopics(topic) );
          instances.addAll( index.getAssociations(topic) );
          instances.addAll( index.getAssociationRoles(topic) );
          instances.addAll( index.getOccurrences(topic) );
        }

      } // while    
      return instances;
    }
  }

  // ---------------------------------------------------
  // additional set methods for the tag attributes
  // ---------------------------------------------------
  
  public void setAs(String as) throws NavigatorRuntimeException {
    if (!as.equals(KIND_ALL)
        && !as.equals(KIND_TOPIC)
        && !as.equals(KIND_OCC)
        && !as.equals(KIND_ASSOC)
        && !as.equals(KIND_ROLE))
      throw new NavigatorRuntimeException("Not supported value ('" + as + "')" +
                                          " given for attribute 'as' in" +
                                          " element 'instances'.");

    this.instancesAs = as;
  }

  
  // ---------------------------------------------------
  // internal methods
  // ---------------------------------------------------

  protected ClassInstanceIndexIF getIndex(TopicIF topic) {
    return (ClassInstanceIndexIF) topic.getTopicMap()
      .getIndex("net.ontopia.topicmaps.core.index.ClassInstanceIndexIF");
  }
  
}

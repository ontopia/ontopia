/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.taglibs.TMvalue;

import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.core.TopicIF;
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

  
  @Override
  public Collection process(Collection tmObjects) throws JspTagException {
    ClassInstanceIndexIF index = null;

    if (tmObjects == null || tmObjects.isEmpty()) {
      return Collections.EMPTY_SET;
    } else{
      Collection instances = new HashSet();
      Iterator iter = tmObjects.iterator();
      TopicIF topic;
      while (iter.hasNext()) {
        topic = (TopicIF) iter.next();
        if (index == null) {
          index = getIndex(topic);
        }

        if (instancesAs.equals(KIND_TOPIC)) {
          instances.addAll( index.getTopics(topic) );
        } else if (instancesAs.equals(KIND_ASSOC)) {
          instances.addAll( index.getAssociations(topic) );
        } else if (instancesAs.equals(KIND_ROLE)) {
          instances.addAll( index.getAssociationRoles(topic) );
        } else if (instancesAs.equals(KIND_OCC)) {
          instances.addAll( index.getOccurrences(topic) );
        } else if (instancesAs.equals(KIND_ALL)) {
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
        && !as.equals(KIND_ROLE)) {
      throw new NavigatorRuntimeException("Not supported value ('" + as + "')" +
                                          " given for attribute 'as' in" +
                                          " element 'instances'.");
    }

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

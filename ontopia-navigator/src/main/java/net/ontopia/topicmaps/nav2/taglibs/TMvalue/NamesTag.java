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
  
  @Override
  public Collection process(Collection topics) throws JspTagException {
    if (topics == null) {
      return Collections.EMPTY_SET;
    } else {
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
      if (useUserContextFilter) {
        scopeFilter = getScopeFilter(SCOPE_BASENAMES);
      }
      
      try {
        // loop over input collection of topics
        while (iter.hasNext()) {
          obj = iter.next();
          // --- only care about TopicIF instances
          topic = (TopicIF) obj;
          curTopicNames = topic.getTopicNames();
          if (!curTopicNames.isEmpty()) {
            if (scopeFilter != null) {
              curTopicNames = scopeFilter.filter(curTopicNames.iterator());
            }
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






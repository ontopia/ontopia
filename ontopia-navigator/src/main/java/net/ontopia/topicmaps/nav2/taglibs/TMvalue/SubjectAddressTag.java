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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.infoset.core.LocatorIF;

import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;
import net.ontopia.utils.CollectionUtils;

/**
 * INTERNAL: Value Producing Tag for finding the subject address (as
 * LocatorIF objects) of all the topics in a collection.
 */
public class SubjectAddressTag extends BaseValueProducingAndAcceptingTag {
  
  @Override
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
          if (subject_address != null) {
            subjectAddresses.add(subject_address);
          }
        }
      }
    }
    return subjectAddresses;
  }

}

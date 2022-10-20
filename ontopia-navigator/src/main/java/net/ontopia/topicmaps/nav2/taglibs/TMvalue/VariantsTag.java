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
import java.util.Collections;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseScopedTag;

/**
 * INTERNAL: Value Producing Tag for finding all variant names related
 * to the objects in a collection (can be either objects of classes
 * TopicNameIF, TopicIF or TopicMapIF).
 */
public class VariantsTag extends BaseScopedTag {

  @Override
  public Collection process(Collection characteristics) throws JspTagException {
    // find all variant names of all characteristics in collection
    if (characteristics == null) {
      return Collections.EMPTY_SET;
    } else {
      ArrayList variantnames = new ArrayList();
      Iterator iter = characteristics.iterator();
      Object obj = null;
      while (iter.hasNext()) {
        obj = iter.next();
        // -- first try if instance of TopicNameIF
        try {
          TopicNameIF basename = (TopicNameIF) obj;
          accumulateVariantsOfBasename( basename, variantnames );
        } catch (ClassCastException e) {
          // --- if TopicIF
          if (obj instanceof TopicIF) {
            TopicIF topic = (TopicIF) obj;
            accumulateVariantsOfTopic( topic, variantnames );
          }
          // --- if TopicMapIF
          else if (obj instanceof TopicMapIF) {
            TopicMapIF topicmap = (TopicMapIF) obj;
            accumulateVariantsOfTopicmap( topicmap, variantnames );
          }
          // --- otherwise
          else {
            String msg = "VariantsTag expected to get a input collection of " +
              "basename, topic or topicmap instances, " +
              "but got instance of class " + obj.getClass().getName();
            throw new NavigatorRuntimeException(msg);
          }
        }
      } // while iter
      return variantnames;
    }
  }

  // -----------------------------------------------------------------
  // internal helper methods
  // -----------------------------------------------------------------

  private void accumulateVariantsOfBasename(TopicNameIF basename, Collection variants) {
    variants.addAll(basename.getVariants());
  }

  private void accumulateVariantsOfTopic(TopicIF topic, Collection variants) {
    if (topic != null) {
      Iterator iterBase = topic.getTopicNames().iterator();
      while (iterBase.hasNext()) {
        TopicNameIF basename = (TopicNameIF) iterBase.next();
        accumulateVariantsOfBasename(basename, variants);
      }
    }
  }

  private void accumulateVariantsOfTopicmap(TopicMapIF topicmap, Collection variants) {
    if (topicmap != null) {
      Iterator iterTopic = topicmap.getTopics().iterator();
      while (iterTopic.hasNext()) {
        TopicIF topic = (TopicIF) iterTopic.next();
        accumulateVariantsOfTopic(topic, variants);
      }
    }
  }
  
}

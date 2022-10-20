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
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import javax.servlet.jsp.JspTagException;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.nav2.taglibs.value.BaseValueProducingAndAcceptingTag;

/**
 * INTERNAL: Value Producing Tag for finding all the topics of all the
 * topic map objects (allowed instances are TopicMapIF, TopicIF,
 * AssociationRoleIF, AssociationIF, TopicNameIF, VariantNameIF and
 * OccurrenceIF) in a collection.
 */
public class TopicsTag extends BaseValueProducingAndAcceptingTag {

  @Override
  public Collection process(Collection tmObjs) throws JspTagException {
    // find all topics of all topic map objects in collection
    Set topics = new HashSet();
    if (tmObjs != null) {
      Iterator iter = tmObjs.iterator();
      while (iter.hasNext()) {
        Object obj = iter.next();
        if (obj instanceof AssociationRoleIF) {
          AssociationRoleIF role = (AssociationRoleIF) obj;
          if (role.getPlayer() != null) {
            topics.add( role.getPlayer() );
          }
        } else if (obj instanceof AssociationIF) {
          Collection roles = ((AssociationIF) obj).getRoles();
          Iterator it = roles.iterator();
          while (it.hasNext()) {
            AssociationRoleIF role = (AssociationRoleIF) it.next();
            topics.add( role.getPlayer() );
          }
        } else if (obj instanceof TopicNameIF) {
          topics.add( ((TopicNameIF) obj).getTopic() );
        } else if (obj instanceof VariantNameIF) {
          topics.add( ((VariantNameIF) obj).getTopic() );
        } else if (obj instanceof OccurrenceIF) {
          topics.add( ((OccurrenceIF) obj).getTopic() );
        } else if (obj instanceof TopicMapIF) {
          topics.addAll( ((TopicMapIF) obj).getTopics() );
        } else if (obj instanceof TopicIF) {
          topics.add( (TopicIF) obj );
        }
          
      } // while
    }

    return new ArrayList(topics);
  }

}

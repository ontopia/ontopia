/*
 * #!
 * Ontopoly Editor
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

package ontopoly.utils;

import java.util.Iterator;
import java.util.function.Predicate;

import ontopoly.model.PSI;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.utils.TypeHierarchyUtils;

/**
 * INTERNAL: Filters out Ontopoly constructs from topic maps.
 */
public class SchemaFilter implements Predicate {
  
  protected TypeHierarchyUtils thutils = new TypeHierarchyUtils();
  
  @Override
  public boolean test(Object object) {
    if (object instanceof TopicIF) {
      return includeTopic((TopicIF)object);
    } else if (object instanceof AssociationIF) {
      return includeTopic(((AssociationIF)object).getType());
    } else if (object instanceof OccurrenceIF) {
      return includeTopic(((OccurrenceIF)object).getType());
    } else if (object instanceof TopicNameIF) {
      TopicIF type = ((TopicNameIF)object).getType();
      return type == null ? true : includeTopic(type);
    }
    return true;
  }

  protected boolean includeTopic(TopicIF topic) {
    boolean include = true;
    TopicIF systemTopic = topic.getTopicMap().getTopicBySubjectIdentifier(PSI.ON_SYSTEM_TOPIC);
    
    if (thutils.isInstanceOf(topic, systemTopic))
      include = false;
    
    // check subject identifiers first
    Iterator<LocatorIF> it = topic.getSubjectIdentifiers().iterator();
    while (it.hasNext()) {
      LocatorIF psi = it.next();

      // There are some exceptions
      if (PSI.TMDM_TOPIC_NAME.equals(psi) ||
          PSI.ON_DESCRIPTION.equals(psi) ||
          PSI.ON_CREATOR.equals(psi) ||
          PSI.ON_VERSION.equals(psi) ||
          PSI.ON_SUPERCLASS_SUBCLASS.equals(psi) ||
          PSI.ON_SUPERCLASS.equals(psi) ||
          PSI.ON_SUBCLASS.equals(psi))
        return true;
      
      // If one PSI is an Ontopoly system PSI, then the topic should
      // be filtered out unless there is an exception
      if (psi.getAddress().startsWith("http://psi.ontopia.net/ontology/"))
        include = false;
    }
    return include;
  }
  
}

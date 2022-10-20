/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;
import java.util.Collections;
import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.utils.PSI;

/**
 * INTERNAL: The rdbms class instance index implementation.
 */
public class ClassInstanceIndex extends RDBMSIndex
  implements ClassInstanceIndexIF {

  ClassInstanceIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  // ---------------------------------------------------------------------------
  // ClassInstanceIndexIF
  // ---------------------------------------------------------------------------
  
  @Override
  public Collection<TopicIF> getTopics(TopicIF topic_type) {
    if (topic_type == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection<TopicIF>(transaction.getTransaction(),
                             "ClassInstanceIndexIF.getTopics_null_size", params,
                             "ClassInstanceIndexIF.getTopics_null", params);
    } else {
      Object[] params = new Object[] { topic_type };
      return new QueryCollection<TopicIF>(transaction.getTransaction(),
                                 "ClassInstanceIndexIF.getTopics_size", params,
                                 "ClassInstanceIndexIF.getTopics", params);
    }
  }
  
  @Override
  public Collection<TopicNameIF> getTopicNames(TopicIF name_type) {
    if (name_type == null) {
      name_type = getTopicMap().getTopicBySubjectIdentifier(PSI.getSAMNameType());
      if (name_type == null) {
        return Collections.emptySet();
      }
      return getTopicNames(name_type);
    } else {
      Object[] params = new Object[] { getTopicMap(), name_type };
      return new QueryCollection<TopicNameIF>(transaction.getTransaction(), "ClassInstanceIndexIF.getTopicNames_size", params,
                                 "ClassInstanceIndexIF.getTopicNames", params);
    }
  }

  @Override
  public Collection<TopicNameIF> getAllTopicNames() {
    Object[] params = new Object[] { transaction.getTopicMap() };
    return new QueryCollection<TopicNameIF>(transaction.getTransaction(),
                                 "ClassInstanceIndexIF.getAllTopicNames_size", params,
                                 "ClassInstanceIndexIF.getAllTopicNames", params);
  }

  @Override
  public Collection<VariantNameIF> getAllVariantNames() {
    Object[] params = new Object[] { transaction.getTopicMap() };
    return new QueryCollection<VariantNameIF>(transaction.getTransaction(),
                                 "ClassInstanceIndexIF.getAllVariantNames_size", params,
                                 "ClassInstanceIndexIF.getAllVariantNames", params);
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrences(TopicIF occurrence_type) {
    if (occurrence_type == null) {
      return Collections.emptySet();
    } else {
      Object[] params = new Object[] { getTopicMap(), occurrence_type };
      return new QueryCollection<OccurrenceIF>(transaction.getTransaction(), "ClassInstanceIndexIF.getOccurrences_size", params,
                                 "ClassInstanceIndexIF.getOccurrences", params);
    }
  }

  @Override
  public Collection<OccurrenceIF> getAllOccurrences() {
    Object[] params = new Object[] { transaction.getTopicMap() };
    return new QueryCollection<OccurrenceIF>(transaction.getTransaction(),
                                 "ClassInstanceIndexIF.getAllOccurrences_size", params,
                                 "ClassInstanceIndexIF.getAllOccurrences", params);
  }
  
  @Override
  public Collection<AssociationIF> getAssociations(TopicIF association_type) {
    if (association_type == null) {
      return Collections.emptySet();
    } else {
      Object[] params = new Object[] { getTopicMap(), association_type };
      return new QueryCollection<AssociationIF>(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociations_size", params,
                                 "ClassInstanceIndexIF.getAssociations", params);
    }
  }

  @Override
  public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type) {
    if (association_role_type == null) {
      return Collections.emptySet();
    } else {
      Object[] params = new Object[] { getTopicMap(), association_role_type };
      return new QueryCollection<AssociationRoleIF>(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociationRoles_size", params,
                                 "ClassInstanceIndexIF.getAssociationRoles", params);
    }
  }

  @Override
  public Collection<AssociationRoleIF> getAssociationRoles(TopicIF association_role_type, TopicIF association_type) {
    if ((association_role_type == null) || (association_type == null)) {
      return Collections.emptySet();
    } else {
      Object[] params = new Object[] { getTopicMap(), association_role_type, getTopicMap(), association_type };
      return new QueryCollection<AssociationRoleIF>(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociationRolesRTAT_size", params,
                                 "ClassInstanceIndexIF.getAssociationRolesRTAT", params);
    }
  }

  @Override
  public Collection<TopicIF> getTopicTypes() {
    return (Collection<TopicIF>)executeQuery("ClassInstanceIndexIF.getTopicTypes",
                                    new Object[] { getTopicMap() });
  }
  
  @Override
  public Collection<TopicIF> getTopicNameTypes() {
    return (Collection<TopicIF>)executeQuery("ClassInstanceIndexIF.getTopicNameTypes",
                                    new Object[] { getTopicMap() });
  }
  
  @Override
  public Collection<TopicIF> getOccurrenceTypes() {
    return (Collection<TopicIF>)executeQuery("ClassInstanceIndexIF.getOccurrenceTypes",
                                    new Object[] { getTopicMap() });
  }
  
  @Override
  public Collection<TopicIF> getAssociationTypes() {
    return (Collection<TopicIF>)executeQuery("ClassInstanceIndexIF.getAssociationTypes",
                                    new Object[] { getTopicMap() });
  }
  
  @Override
  public Collection<TopicIF> getAssociationRoleTypes() {
    return (Collection<TopicIF>)executeQuery("ClassInstanceIndexIF.getAssociationRoleTypes",
                                    new Object[] { getTopicMap() });
  }
  
  @Override
  public boolean usedAsTopicType(TopicIF topic) {
    if (topic == null) { return false; }
    return !(getTopics(topic).isEmpty());    
  }

  @Override
  public boolean usedAsTopicNameType(TopicIF topic) {
    if (topic == null) { return false; }
    return !(getTopicNames(topic).isEmpty());
  }

  @Override
  public boolean usedAsOccurrenceType(TopicIF topic) {
    if (topic == null) { return false; }
    return !(getOccurrences(topic).isEmpty());
  }

  @Override
  public boolean usedAsAssociationType(TopicIF topic) {
    if (topic == null) { return false; }
    return !(getAssociations(topic).isEmpty());
  }
  
  @Override
  public boolean usedAsAssociationRoleType(TopicIF topic) {
    if (topic == null) { return false; }
    return !(getAssociationRoles(topic).isEmpty());
  }
  
  @Override
  public boolean usedAsType(TopicIF topic) {
    if (topic == null) {
      return false;
    }
    return (usedAsTopicType(topic) ||
            usedAsAssociationType(topic) ||
            usedAsAssociationRoleType(topic) ||
            usedAsOccurrenceType(topic) ||
            usedAsTopicNameType(topic));
  }
}

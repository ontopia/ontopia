
// $Id: ClassInstanceIndex.java,v 1.25 2008/06/12 14:37:16 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;

import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.ClassInstanceIndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;

/**
 * INTERNAL: The rdbms class instance index implementation.
 */

public class ClassInstanceIndex extends RDBMSIndex implements ClassInstanceIndexIF {

  ClassInstanceIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  // -----------------------------------------------------------------------------
  // ClassInstanceIndexIF
  // -----------------------------------------------------------------------------
  
  public Collection getTopics(TopicIF topic_type) {
    if (topic_type == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getTopics_null_size", params,
                                 "ClassInstanceIndexIF.getTopics_null", params);
    } else {
      // Object[] params = new Object[] { getTopicMap(), topic_type };
      Object[] params = new Object[] { topic_type };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getTopics_size", params,
                                 "ClassInstanceIndexIF.getTopics", params);
    }
  }
  
  public Collection getTopicNames(TopicIF name_type) {
    if (name_type == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getTopicNames_null_size", params,
                                 "ClassInstanceIndexIF.getTopicNames_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), name_type };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getTopicNames_size", params,
                                 "ClassInstanceIndexIF.getTopicNames", params);
    }
  }
  
  public Collection getOccurrences(TopicIF occurrence_type) {
    if (occurrence_type == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getOccurrences_null_size", params,
                                 "ClassInstanceIndexIF.getOccurrences_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), occurrence_type };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getOccurrences_size", params,
                                 "ClassInstanceIndexIF.getOccurrences", params);
    }
  }
  
  public Collection getAssociations(TopicIF association_type) {
    if (association_type == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociations_null_size", params,
                                 "ClassInstanceIndexIF.getAssociations_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), association_type };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociations_size", params,
                                 "ClassInstanceIndexIF.getAssociations", params);
    }
  }

  public Collection getAssociationRoles(TopicIF association_role_type) {
    if (association_role_type == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociationRoles_null_size", params,
                                 "ClassInstanceIndexIF.getAssociationRoles_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), association_role_type };
      return new QueryCollection(transaction.getTransaction(), "ClassInstanceIndexIF.getAssociationRoles_size", params,
                                 "ClassInstanceIndexIF.getAssociationRoles", params);
    }
  }

  public Collection getTopicTypes() {
    return (Collection)executeQuery("ClassInstanceIndexIF.getTopicTypes",
                                    new Object[] { getTopicMap() });
  }
  
  public Collection getTopicNameTypes() {
    return (Collection)executeQuery("ClassInstanceIndexIF.getTopicNameTypes",
                                    new Object[] { getTopicMap() });
  }
  
  public Collection getOccurrenceTypes() {
    return (Collection)executeQuery("ClassInstanceIndexIF.getOccurrenceTypes",
                                    new Object[] { getTopicMap() });
  }
  
  public Collection getAssociationTypes() {
    return (Collection)executeQuery("ClassInstanceIndexIF.getAssociationTypes",
                                    new Object[] { getTopicMap() });
  }
  
  public Collection getAssociationRoleTypes() {
    return (Collection)executeQuery("ClassInstanceIndexIF.getAssociationRoleTypes",
                                    new Object[] { getTopicMap() });
  }
  
  public boolean usedAsTopicType(TopicIF topic) {
    return !(getTopics(topic).isEmpty());    
  }

  public boolean usedAsTopicNameType(TopicIF topic) {
    return !(getTopicNames(topic).isEmpty());
  }

  public boolean usedAsOccurrenceType(TopicIF topic) {
    return !(getOccurrences(topic).isEmpty());
  }

  public boolean usedAsAssociationType(TopicIF topic) {
    return !(getAssociations(topic).isEmpty());
  }
  
  public boolean usedAsAssociationRoleType(TopicIF topic) {
    return !(getAssociationRoles(topic).isEmpty());
  }
  
  public boolean usedAsType(TopicIF topic) {
    if (topic == null) return false;
    return (usedAsTopicType(topic) ||
            usedAsAssociationType(topic) ||
            usedAsAssociationRoleType(topic) ||
            usedAsOccurrenceType(topic) ||
            usedAsTopicNameType(topic));
  }
}

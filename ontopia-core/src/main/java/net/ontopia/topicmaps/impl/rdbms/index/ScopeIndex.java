
package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;

import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;

/**
 * INTERNAL: The rdbms scope index implementation.
 */
public class ScopeIndex extends RDBMSIndex implements ScopeIndexIF {

  ScopeIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  // ---------------------------------------------------------------------------
  // ScopeIndexIF
  // ---------------------------------------------------------------------------
    
  public Collection<TopicNameIF> getTopicNames(TopicIF theme) {
    if (theme == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getTopicNames_null_size", params,
                                 "ScopeIndexIF.getTopicNames_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getTopicNames_size", params,
                                 "ScopeIndexIF.getTopicNames", params);
    }
  }
  
  public Collection<VariantNameIF> getVariants(TopicIF theme) {
    if (theme == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getVariants_null_size", params,
                                 "ScopeIndexIF.getVariants_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getVariants_size", params,
                                 "ScopeIndexIF.getVariants", params);
    }
  }
  
  public Collection<OccurrenceIF> getOccurrences(TopicIF theme) {
    if (theme == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getOccurrences_null_size", params,
                                 "ScopeIndexIF.getOccurrences_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getOccurrences_size", params,
                                 "ScopeIndexIF.getOccurrences", params);
    }
  }
  
  public Collection<AssociationIF> getAssociations(TopicIF theme) {
    if (theme == null) {
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getAssociations_null_size", params,
                                 "ScopeIndexIF.getAssociations_null", params);
    } else {
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getAssociations_size", params,
                                 "ScopeIndexIF.getAssociations", params);
    }
  }
    
  public Collection<TopicIF> getTopicNameThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getTopicNameThemes",
                                    new Object[] { getTopicMap() });
  }

  public Collection<TopicIF> getVariantThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getVariantThemes",
                                    new Object[] { getTopicMap() });
  }

  public Collection<TopicIF> getOccurrenceThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getOccurrenceThemes",
                                    new Object[] { getTopicMap() });
  }
  
  public Collection<TopicIF> getAssociationThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getAssociationThemes",
                                    new Object[] { getTopicMap() });
  }

  public boolean usedAsTopicNameTheme(TopicIF topic) {
    return !(getTopicNames(topic).isEmpty());    
  }

  public boolean usedAsVariantTheme(TopicIF topic) {
    return !(getVariants(topic).isEmpty());    
  }

  public boolean usedAsOccurrenceTheme(TopicIF topic) {
    return !(getOccurrences(topic).isEmpty());    
  }

  public boolean usedAsAssociationTheme(TopicIF topic) {
    return !(getAssociations(topic).isEmpty());    
  }
  
  public boolean usedAsTheme(TopicIF topic) {
    return (usedAsTopicNameTheme(topic) ||
            usedAsVariantTheme(topic) ||
            usedAsOccurrenceTheme(topic) ||
            usedAsAssociationTheme(topic));
  }
  
}






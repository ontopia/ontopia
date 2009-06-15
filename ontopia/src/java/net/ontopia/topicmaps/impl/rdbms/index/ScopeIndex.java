// $Id: ScopeIndex.java,v 1.17 2008/06/12 14:37:16 geir.gronmo Exp $

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;

import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.ScopeIndexIF;

/**
 * INTERNAL: The rdbms scope index implementation.
 */

public class ScopeIndex extends RDBMSIndex implements ScopeIndexIF {

  ScopeIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  // -----------------------------------------------------------------------------
  // ScopeIndexIF
  // -----------------------------------------------------------------------------
    
  public Collection getTopicNames(TopicIF theme) {
    if (theme == null) {
      //! return (Collection)executeQuery("ScopeIndexIF.getTopicNames_null",
      //!                                 new Object[] { getTopicMap() });
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getTopicNames_null_size", params,
                                 "ScopeIndexIF.getTopicNames_null", params);
    } else {
      //! return (Collection)executeQuery("ScopeIndexIF.getTopicNames",
      //!                                 new Object[] { getTopicMap(), theme });
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getTopicNames_size", params,
                                 "ScopeIndexIF.getTopicNames", params);
    }
  }
  
  public Collection getVariants(TopicIF theme) {
    if (theme == null) {
      //! return (Collection)executeQuery("ScopeIndexIF.getVariants_null",
      //!                                 new Object[] { getTopicMap() });
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getVariants_null_size", params,
                                 "ScopeIndexIF.getVariants_null", params);
    } else {
      //! return (Collection)executeQuery("ScopeIndexIF.getVariants",
      //!                                 new Object[] { getTopicMap(), theme });
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getVariants_size", params,
                                 "ScopeIndexIF.getVariants", params);
    }
  }
  
  public Collection getOccurrences(TopicIF theme) {
    if (theme == null) {
      //! return (Collection)executeQuery("ScopeIndexIF.getOccurrences_null",
      //!                                 new Object[] { getTopicMap() });
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getOccurrences_null_size", params,
                                 "ScopeIndexIF.getOccurrences_null", params);
    } else {
      //! return (Collection)executeQuery("ScopeIndexIF.getOccurrences",
      //!                                 new Object[] { getTopicMap(), theme });
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getOccurrences_size", params,
                                 "ScopeIndexIF.getOccurrences", params);
    }
  }
  
  public Collection getAssociations(TopicIF theme) {
    if (theme == null) {
      //! return (Collection)executeQuery("ScopeIndexIF.getAssociations_null",
      //!                                 new Object[] { getTopicMap() });
      Object[] params = new Object[] { getTopicMap() };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getAssociations_null_size", params,
                                 "ScopeIndexIF.getAssociations_null", params);
    } else {
      //! return (Collection)executeQuery("ScopeIndexIF.getAssociations",
      //!                                 new Object[] { getTopicMap(), theme });
      Object[] params = new Object[] { getTopicMap(), theme };
      return new QueryCollection(transaction.getTransaction(), "ScopeIndexIF.getAssociations_size", params,
                                 "ScopeIndexIF.getAssociations", params);
    }
  }
    
  public Collection getTopicNameThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getTopicNameThemes",
                                    new Object[] { getTopicMap() });
  }

  public Collection getVariantThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getVariantThemes",
                                    new Object[] { getTopicMap() });
  }

  public Collection getOccurrenceThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getOccurrenceThemes",
                                    new Object[] { getTopicMap() });
  }
  
  public Collection getAssociationThemes() {
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






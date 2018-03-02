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
    
  @Override
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
  
  @Override
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
  
  @Override
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
  
  @Override
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
    
  @Override
  public Collection<TopicIF> getTopicNameThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getTopicNameThemes",
                                    new Object[] { getTopicMap() });
  }

  @Override
  public Collection<TopicIF> getVariantThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getVariantThemes",
                                    new Object[] { getTopicMap() });
  }

  @Override
  public Collection<TopicIF> getOccurrenceThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getOccurrenceThemes",
                                    new Object[] { getTopicMap() });
  }
  
  @Override
  public Collection<TopicIF> getAssociationThemes() {
    return (Collection)executeQuery("ScopeIndexIF.getAssociationThemes",
                                    new Object[] { getTopicMap() });
  }

  @Override
  public boolean usedAsTopicNameTheme(TopicIF topic) {
    return !(getTopicNames(topic).isEmpty());    
  }

  @Override
  public boolean usedAsVariantTheme(TopicIF topic) {
    return !(getVariants(topic).isEmpty());    
  }

  @Override
  public boolean usedAsOccurrenceTheme(TopicIF topic) {
    return !(getOccurrences(topic).isEmpty());    
  }

  @Override
  public boolean usedAsAssociationTheme(TopicIF topic) {
    return !(getAssociations(topic).isEmpty());    
  }
  
  @Override
  public boolean usedAsTheme(TopicIF topic) {
    return (usedAsTopicNameTheme(topic) ||
            usedAsVariantTheme(topic) ||
            usedAsOccurrenceTheme(topic) ||
            usedAsAssociationTheme(topic));
  }
  
}






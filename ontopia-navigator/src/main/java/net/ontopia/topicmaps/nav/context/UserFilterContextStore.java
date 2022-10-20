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

package net.ontopia.topicmaps.nav.context;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.ontopia.topicmaps.core.TopicMapIF;

/**
 * INTERNAL: Storage of four different sets of scoping
 * themes in dependance of a topicmap.
 * <ul>
 *  <li>base name,
 *  <li>variant name,
 *  <li>occurrence and
 *  <li>association scopes
 * </ul>
 * <p>
 * An object of this class can be set to the user session or
 * referenced by another class which stores user information
 * in the JSP session context.
 */
public class UserFilterContextStore {

  /** Stores as key the TopicMapIF object and
      as value the Collection of basename themes (TopicIF) */
  private Map scope_baseNames;

  /** Stores as key the TopicMapIF object and
      as value the Collection of variant name themes (TopicIF) */
  private Map scope_variantNames;

  /** Stores as key the TopicMapIF object and
      as value the Collection of occurrences themes (TopicIF) */
  private Map scope_occurrences;

  /** Stores as key the TopicMapIF object and
      as value the Collection of association themes (TopicIF) */
  private Map scope_associations;

  /**
   * default constructor
   */
  public UserFilterContextStore() {
    scope_baseNames = new WeakHashMap();
    scope_variantNames = new WeakHashMap();
    scope_occurrences = new WeakHashMap();
    scope_associations = new WeakHashMap();
  }

  /**
   * default destructor
   */
  @Override
  protected void finalize() {
    scope_baseNames = null;
    scope_variantNames = null;
    scope_occurrences = null;
    scope_associations = null;
  }

  /**
   * serialize this for debugging purposes.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UserFilterContextStore [")
      .append("scope_baseNames: ").append(scope_baseNames)
      .append(", scope_variantNames: ").append(scope_variantNames)
      .append(", scope_occurrences: ").append(scope_occurrences)
      .append(", scope_associations: ").append(scope_associations)
      .append("]");
    return sb.toString();
  }
  
  // -------------------------------------------------------------
  // set methods
  // -------------------------------------------------------------
  
  public void setScopeTopicNames(TopicMapIF topicmap,
                                Collection themes_baseNames) {
    scope_baseNames.put(topicmap, themes_baseNames);
  }

  public void setScopeVariantNames(TopicMapIF topicmap,
                                Collection themes_variantNames) {
    scope_variantNames.put(topicmap, themes_variantNames);
  }

  public void setScopeOccurrences(TopicMapIF topicmap,
                                  Collection themes_occurrences) {
    scope_occurrences.put(topicmap, themes_occurrences);
  }

  public void setScopeAssociations(TopicMapIF topicmap,
                                   Collection themes_associations) {
    scope_associations.put(topicmap, themes_associations);
  }

  // -------------------------------------------------------------
  // reset methods
  // -------------------------------------------------------------
  
  public void resetScopeTopicNames(TopicMapIF topicmap) {
    scope_baseNames.remove(topicmap);
  }

  public void resetScopeVariantNames(TopicMapIF topicmap) {
    scope_variantNames.remove(topicmap);
  }

  public void resetScopeOccurrences(TopicMapIF topicmap) {
    scope_occurrences.remove(topicmap);
  }

  public void resetScopeAssociations(TopicMapIF topicmap) {
    scope_associations.remove(topicmap);
  }

  // -------------------------------------------------------------
  // get methods
  // -------------------------------------------------------------
  
  public Collection getScopeTopicNames(TopicMapIF topicmap) {
    if (scope_baseNames.get(topicmap) != null) {
      return (Collection) scope_baseNames.get(topicmap);
    } else {
      return Collections.EMPTY_LIST;
    }
  }
  
  public Collection getScopeVariantNames(TopicMapIF topicmap) {
    if (scope_variantNames.get(topicmap) != null) {
      return (Collection) scope_variantNames.get(topicmap);
    } else {
      return Collections.EMPTY_LIST;
    }
  }
  
  public Collection getScopeOccurrences(TopicMapIF topicmap) {
    if (scope_occurrences.get(topicmap) != null) {
      return (Collection) scope_occurrences.get(topicmap);
    } else {
      return Collections.EMPTY_LIST;
    }
  }
  
  public Collection getScopeAssociations(TopicMapIF topicmap) {
    if (scope_associations.get(topicmap) != null) {
      return (Collection) scope_associations.get(topicmap);
    } else {
      return Collections.EMPTY_LIST;
    }
  }
  
}

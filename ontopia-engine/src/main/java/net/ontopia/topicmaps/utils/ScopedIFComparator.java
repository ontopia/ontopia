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

package net.ontopia.topicmaps.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.utils.LexicalComparator;

/**
 * INTERNAL: Comparator that compares ScopedIF objects based on their
 * applicability in the specified scope.</p>
 *
 * This comparator may only be used with objects that are
 * implementations of ScopedIF. The default subcomparator is
 * LexicalComparator.CASE_SENSITIVE.</p>
 *
 * <b>Technique:</b></p>
 *
 * <ol>
 *  <li>Compare the number of matching themes for each of the objects. The
 *      more matching themes, the higher ranked.</li>
 *  <li>Compare by number of themes specified on objects. The fewer
 *      themes, the higher ranked. </li>
 *  <li>If subcomparator doesn't exist they're ranked equally.</li>
 *  <li>Use subcomparator to compare them.</li>
 * </ol>
 */

public class ScopedIFComparator<T extends ScopedIF> implements Comparator<T> {

  protected TopicIF[] scope;
  protected Comparator<? super T> subcomparator;

  public ScopedIFComparator() {
    this(Collections.<TopicIF>emptySet());
  }

  public ScopedIFComparator(Collection<TopicIF> scope) {
    this(scope, LexicalComparator.CASE_SENSITIVE);
  }

  public ScopedIFComparator(Collection<TopicIF> scope, Comparator<? super T> subcomparator) {    
    this.scope = new TopicIF[scope.size()];
    scope.toArray(this.scope);
    this.subcomparator = subcomparator;
  }
  
  /**
   * INTERNAL: Compares the two ScopedIF objects for their applicability
   * in the scope specified in the constructor.
   *
   * @param obj1 An object implementing ScopedIF.
   * @param obj2 An object implementing ScopedIF.
   * @return See {@link java.util.Comparator#compare(Object,Object)}
   */
  @Override
  public int compare(T obj1, T obj2) {
    // Collect the scope of both objects
    Collection<TopicIF> scope1 = obj1.getScope();
    Collection<TopicIF> scope2 = obj2.getScope();

    // Count number of matching themes
    int matches = 0;
    for (int i=0; i < scope.length; i++) {
      if (scope1.contains(scope[i])) {
        matches = matches + 1;
      }
      if (scope2.contains(scope[i])) {
        matches = matches - 1;
      }
    }

    // Rank by matched themes
    if (matches > 0) {
      return -1;
    } else if (matches < 0) {
      return 1;
    }
    
    // Rank by lesser scope
    if ((scope1.isEmpty() && !scope2.isEmpty()) || (scope1.size() < scope2.size())) {
      return -1;
    } else if ((scope2.isEmpty() && !scope1.isEmpty()) || (scope2.size() < scope1.size())) {
      return 1;
    }      

    // System.out.println("1:" + obj1 + scope1.size() + " 2:" + obj2 + scope2.size());
    if (subcomparator == null) {
      return 0;
    }

    // Use subcomparator when equally ranked
    return subcomparator.compare(obj1, obj2);
  }
  
}






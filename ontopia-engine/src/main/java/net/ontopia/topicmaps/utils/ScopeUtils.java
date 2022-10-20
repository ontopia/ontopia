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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TopicIF;

/**
 * INTERNAL: Scope processing utilities.
 */

public class ScopeUtils {

  /**
   * Checks to see if the ScopedIF's scope is applicable in the user
   * context. This is implies that the ScopedIF's scope must be either
   * the unconstrained scope (empty) or a superset of the user
   * context.<p>
   *
   * @param obj The ScopedIF object to compare with.
   * @param context The user context; a collection of TopicIFs.
   *
   * @return boolean; true if the scoped object's scope is applicable in the user context.
   */
  public static boolean isApplicableInContext(ScopedIF obj, Collection<TopicIF> context) {
    // Get object scope
    Collection<TopicIF> objscope = obj.getScope();
    return (objscope.isEmpty() || objscope.containsAll(context));
  }
  
  /**
   * Checks to see if the ScopedIF's scope is a superset of the user
   * context. The scope is a superset if it contains all the context
   * themes.<p>
   *
   * Note that the unconstrained scope is in this case considered an
   * empty set.<p>
   *
   * @param obj The ScopedIF object to compare with.
   * @param context The user context; a collection of TopicIFs.
   *
   * @return boolean; true if the scoped object's scope is a superset of the context.
   */
  public static boolean isSupersetOfContext(ScopedIF obj, Collection<TopicIF> context) {
    // Get object scope
    Collection<TopicIF> objscope = obj.getScope();
    return objscope.containsAll(context);
  }
  /**
   * EXPERIMENTAL:
   */
  public static boolean isSupersetOfContext(ScopedIF obj, TopicIF[] context) {
    // Get object scope
    Collection<TopicIF> objscope = obj.getScope();

    for (int i=0; i < context.length; i++) {
      if (!objscope.contains(context[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks to see if the ScopedIF's scope is a subset of the user
   * context. The scope is a subset if the context contains all the
   * scope themes.<p>
   *
   * Note that the unconstrained scope is in this case considered an
   * empty set.<p>
   *
   * @param obj The ScopedIF object to compare with.
   * @param context The user context; a collection of TopicIFs.
   
   * @return boolean; true if the scoped object's scope is a subset of the context.
   */
  public static boolean isSubsetOfContext(ScopedIF obj, Collection<TopicIF> context) {
    // Get object scope
    Collection<TopicIF> objscope = obj.getScope();
    return context.containsAll(objscope);
  }

  /**
   * Checks to see if the ScopedIF's scope intersects with the user
   * context. Note that there is no intersection when either
   * collection is empty.<p>
   *
   * Note that the unconstrained scope is in this case considered an
   * empty set.<p>
   *
   * @param obj The ScopedIF object to compare with.
   * @param context The user context; a collection of TopicIFs.
   *
   * @return boolean; true if the scoped object's scope intersects
   * with the user context.
   */
  public static boolean isIntersectionOfContext(ScopedIF obj,
                                                Collection<TopicIF> context) {
    // Get object scope
    Collection<TopicIF> objscope = obj.getScope();

    // Loop over context to see if there is an intersection with the object scope.
    Iterator<TopicIF> iter = context.iterator();
    while (iter.hasNext()) {
      // If object scope contains context theme then there is an intersection.
      if (objscope.contains(iter.next())) {
        return true;
      }
    }
    // There is no intersection with the object scope.
    return false;
  }
  /**
   * EXPERIMENTAL:
   */
  public static boolean isIntersectionOfContext(ScopedIF obj,
                                                TopicIF[] context) {
    // Get object scope
    Collection<TopicIF> objscope = obj.getScope();

    // Loop over context to see if there is an intersection with the object scope.
    for (int i=0; i < context.length; i++) {
      // If object scope contains context theme then there is an intersection.
      if (objscope.contains(context[i])) {
        return true;
      }
    }
    
    // There is no intersection with the object scope.
    return false;
  }

  /**
   * Ranks the ScopedIFs by the applicability to the specified scope.
   */
  public static <S extends ScopedIF> List<S> rankByScope(Collection<S> scoped, TopicIF theme) {
    return rankByScope(scoped, Collections.singleton(theme));
  }
  
  /**
   * Ranks the ScopedIFs by the applicability to the specified scope.
   */
  public static <S extends ScopedIF> List<S> rankByScope(Collection<S> scoped, Collection<TopicIF> scope) {
    // Initialize result
    List<S> ranklist = new ArrayList<S>(scoped);
    Collections.sort(ranklist, new ScopedIFComparator<S>(scope));
    return ranklist;
  }

}

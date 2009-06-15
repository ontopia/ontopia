// $Id: ScopeUtils.java,v 1.12 2008/01/10 11:08:49 geir.gronmo Exp $

package net.ontopia.topicmaps.utils;

import java.util.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.utils.*;

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
  public static boolean isApplicableInContext(ScopedIF obj, Collection context) {
    // Get object scope
    Collection objscope = obj.getScope();
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
  public static boolean isSupersetOfContext(ScopedIF obj, Collection context) {
    // Get object scope
    Collection objscope = obj.getScope();
    return objscope.containsAll(context);
  }
  /**
   * EXPERIMENTAL:
   */
  public static boolean isSupersetOfContext(ScopedIF obj, Object[] context) {
    // Get object scope
    Collection objscope = obj.getScope();

    for (int i=0; i < context.length; i++) {
      if (!objscope.contains(context[i]))
        return false;
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
  public static boolean isSubsetOfContext(ScopedIF obj, Collection context) {
    // Get object scope
    Collection objscope = obj.getScope();
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
                                                Collection context) {
    // Get object scope
    Collection objscope = obj.getScope();

    // Loop over context to see if there is an intersection with the object scope.
    Iterator iter = context.iterator();
    while (iter.hasNext()) {
      // If object scope contains context theme then there is an intersection.
      if (objscope.contains(iter.next())) return true;
    }
    // There is no intersection with the object scope.
    return false;
  }
  /**
   * EXPERIMENTAL:
   */
  public static boolean isIntersectionOfContext(ScopedIF obj,
                                                Object[] context) {
    // Get object scope
    Collection objscope = obj.getScope();

    // Loop over context to see if there is an intersection with the object scope.
    for (int i=0; i < context.length; i++) {
      // If object scope contains context theme then there is an intersection.
      if (objscope.contains(context[i])) return true;
    }
    
    // There is no intersection with the object scope.
    return false;
  }

  /**
   * Ranks the ScopedIFs by the applicability to the specified scope.
   */
  public static List rankByScope(Collection scoped, TopicIF theme) {
    return rankByScope(scoped, Collections.singleton(theme));
  }
  
  /**
   * Ranks the ScopedIFs by the applicability to the specified scope.
   */
  public static List rankByScope(Collection scoped, Collection scope) {
    // Initialize result
    Object[] ranklist = scoped.toArray();
    Arrays.sort(ranklist, new ScopedIFComparator(scope));
    return Arrays.asList(ranklist);
  }

}

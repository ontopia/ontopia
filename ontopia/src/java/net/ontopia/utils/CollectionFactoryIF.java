// $Id: CollectionFactoryIF.java,v 1.8 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.Set;
import java.util.List;
import java.util.Map;

/**
 * INTERNAL: Factory that creates collection objects.</p>
 *
 * Implementations should be made if it is necessary to work with
 * customized collection classes. Classes that use a collection
 * factory instead of creating collection objects itself, will be able
 * to work with multiple collection implementations.</p>
 */

public interface CollectionFactoryIF {

  /**
   * INTERNAL: Creates a set that is expected to contain a small number of objects.
   */
  public <T> Set<T> makeSmallSet();

  /**
   * INTERNAL: Creates a set that is expected to contain a large number of objects.
   */
  public <T> Set<T> makeLargeSet();

  /**
   * INTERNAL: Creates a map that is expected to contain a small number of objects.
   */
  public <V, K> Map<V, K> makeSmallMap();

  /**
   * INTERNAL: Creates a map that is expected to contain a large number of objects.
   */
  public <V, K> Map<V, K> makeLargeMap();

  /**
   * INTERNAL: Creates a list that is expected to contain a small number of objects.
   */
  public <T> List<T> makeSmallList();

  /**
   * INTERNAL: Creates a list that is expected to contain a large number of objects.
   */
  public <T> List<T> makeLargeList();
  
}





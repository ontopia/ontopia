
package net.ontopia.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: A collection factory that returns non-synchronized standard
 * java.util collection objects.</p>
 */

public class CollectionFactory implements CollectionFactoryIF, java.io.Serializable {

  static final long serialVersionUID = -4670702015296061304L;
  protected int initsize;

  public CollectionFactory() {
    initsize = 4;
  }

  public CollectionFactory(int initsize) {
    this.initsize = initsize;
  }

  public <T> Set<T> makeSmallSet() {
    return (Set<T>) new HashSet<T>(initsize);
  }

  public <T> Set<T> makeLargeSet() {
    return (Set<T>) new HashSet<T>();
  }

  public <V, K> Map<V, K> makeSmallMap() {
    return (Map<V, K>) new HashMap<V, K>(initsize);
  }

  public <V, K> Map<V, K> makeLargeMap() {
    return (Map<V, K>) new HashMap<V, K>();
  }
  
  public <T> List<T> makeSmallList() {
    return (List<T>) new ArrayList<T>(initsize);
  }

  public <T> List<T> makeLargeList() {
    return (List<T>) new ArrayList<T>();
  }

}


// $Id: SynchronizedCollectionFactory.java,v 1.8 2004/11/29 19:10:44 grove Exp $

package net.ontopia.utils;

import java.util.*;

/**
 * INTERNAL: A collection factory that returns synchronized standard
 * java.util collection objects.
 */
public class SynchronizedCollectionFactory implements CollectionFactoryIF, java.io.Serializable {

  static final long serialVersionUID = -4670702015296061304L;
  protected int initsize;

  public SynchronizedCollectionFactory() {
    initsize = 4;
  }

  public SynchronizedCollectionFactory(int initsize) {
    this.initsize = initsize;
  }

  public <T> Set<T> makeSmallSet() {
    return new SynchronizedCompactHashSet<T>();
  }

  public <T> Set<T> makeLargeSet() {
    return new SynchronizedCompactHashSet<T>();
  }

  public <K, V> Map<K, V> makeSmallMap() {
    return Collections.synchronizedMap(new HashMap<K, V>(initsize));
  }

  public <K, V> Map<K, V> makeLargeMap() {
    return Collections.synchronizedMap(new HashMap<K, V>());
  }
  
  public <T> List<T> makeSmallList() {
    return Collections.synchronizedList(new ArrayList<T>(initsize));
  }

  public <T> List<T> makeLargeList() {
    return Collections.synchronizedList(new ArrayList<T>());
  }

}

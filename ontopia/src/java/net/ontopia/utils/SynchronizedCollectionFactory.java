
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

  public Set makeSmallSet() {
    return new SynchronizedCompactHashSet();
  }

  public Set makeLargeSet() {
    return new SynchronizedCompactHashSet();
  }

  public Map makeSmallMap() {
    return Collections.synchronizedMap(new HashMap(initsize));
  }

  public Map makeLargeMap() {
    return Collections.synchronizedMap(new HashMap());
  }
  
  public List makeSmallList() {
    return Collections.synchronizedList(new ArrayList(initsize));
  }

  public List makeLargeList() {
    return Collections.synchronizedList(new ArrayList());
  }

}

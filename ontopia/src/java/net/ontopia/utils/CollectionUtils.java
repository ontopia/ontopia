
// $Id: CollectionUtils.java,v 1.27 2008/09/09 08:06:55 geir.gronmo Exp $

package net.ontopia.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

/**
 * INTERNAL: Class that contains useful collection methods.
 */

public class CollectionUtils {

  private static final Random random = new Random();

  private CollectionUtils() {
  }
  
  /**
   * INTERNAL: Gets the first object in the collection. If the
   * collection is empty, null is returned.
   */
  public static <T> T getFirst(Collection<T> coll) {
    if (coll == null || coll.isEmpty()) return null;

    // If it's a list return it directly
    if (coll instanceof List) return ((List<T>)coll).get(0);
    Iterator<T> iter = coll.iterator();
    while (iter.hasNext()) {
      return iter.next();
    }
    return null;
  }

  /**
   * INTERNAL: Gets the first object in the collection. If the
   * collection does not contain any elements NoSuchElementException
   * is thrown.<p>
   *
   * @since 1.3.4
   */
  public static <T> T getFirstElement(Collection<T> coll)
    throws NoSuchElementException {
    
    if (coll instanceof List)
      try {
        return ((List<T>)coll).get(0);
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    else
      return coll.iterator().next();
  }

  public static <T> Collection<T> getSingletonCollectionOrEmptyIfNull(T o) {
    if (o == null)
      return Collections.emptySet();
    else
      return Collections.singleton(o);
  }
  
  /**
   * INTERNAL: Gets a random object from the collection. If the
   * collection is empty, null is returned.
   */
  public static <T> T getRandom(Collection<T> coll) {

    if (coll == null || coll.isEmpty()) return null;
    int chosen = random.nextInt(coll.size());

    // NOTE: Test should ideally be against java.util.RandomAccess,
    // but it's only supported with JDK 1.4

    // If it's a list return it directly
    if (coll instanceof List) return ((List<T>)coll).get(chosen);

    // Otherwise loop through the collection
    long count = 0;
    Iterator<T> iter = coll.iterator();
    while (iter.hasNext()) {
      T obj = iter.next();
      if (count == chosen) return obj;
      count++;
    }
    return null;
  }

  /**
   * INTERNAL: Compares two collections to see if they contain the same
   * elements.
   *
   * @since 1.4.1
   */
  public static <T> boolean equalsUnorderedSet(Collection<T> coll1, Collection<T> coll2) {
    
    // Take care of nulls
    if (coll1 == null)
      if (coll2 == null)
        // 1: null 2: null
        return true;
      else
        // 1: null 2: not null
        return false;
    else
      if (coll2 == null)
        // 1: not null 2: null
        return false;
    
    // Compare set size
    int size1 = coll1.size();
    int size2 = coll2.size();    
    if (size1 != size2)
      return false;
    
    // If both have 1 element compare first element
    if (size1 == 1) {      
      T obj1 = coll1.iterator().next();
      T obj2 = coll2.iterator().next();
      return (obj1 == null ? obj2 == null : obj1.equals(obj2));
    }
    
    // Compare collections as sets
    if (coll1 instanceof Set)
      if (coll2 instanceof Set)
        return coll1.equals(coll2);
      else
        return coll1.equals(new HashSet<T>(coll2));
    else if (coll2 instanceof Set)
      return coll2.equals(new HashSet<T>(coll1));
    else
      return new HashSet<T>(coll2).equals(new HashSet<T>(coll1));
  }

  /**
   * EXPERIMENTAL: Iterates over up to <i>length</i> number of
   * elements in the iterator and returns those elements as a
   * Collection. If the iterator is exhausted only the iterated
   * elements are returned.
   */
  public static <T> List<T> nextBatch(Iterator<T> iter, int length) {
    List<T> batch = new ArrayList<T>(length);
    int i = 0;
    do {
      batch.add(iter.next());
      i++;
    } while (i < length && iter.hasNext());
    return batch;
  }

  /**
   * EXPERIMENTAL: Iterates over up to <i>length</i> number of
   * elements in the iterator and adds those elements to the given
   * collection. If the iterator is exhausted only the iterated
   * elements are added.
   *
   * @return the number of elements inserted into the array
   */
  public static <T> int nextBatch(Iterator<T> iter, int length, Collection<T> batch) {
    int i = 0;
    do {
      batch.add(iter.next());
      i++;
    } while (i < length && iter.hasNext());
    return i;
  }

  /**
   * EXPERIMENTAL: Iterates over up to <i>values.length</i> number of
   * elements in the iterator and inserts those elements in the
   * <i>values> array. If the iterator is exhausted only the iterated
   * elements are included.
   *
   * @return the number of elements inserted into the array
   */
  public static <T> int nextBatch(Iterator<T> iter, T[] values) {
    int i = 0;
    do {
      values[i] = iter.next();
      i++;
    } while (i < values.length && iter.hasNext());
    return i;
  }
  

  /**
   * EXPERIMENTAL: Iterates over up to <i>length</i> number of
   * elements in the iterator and inserts those elements in the
   * <i>values> array. The first element is inserted at the specified
   * <i>offset</i>. If the iterator is exhausted only the iterated
   * elements are included.
   *
   * @return the number of elements inserted into the array
   */
  public static <T> int nextBatch(Iterator<T> iter, T[] values, int offset, int length) {
    int i = 0;
    do {
      values[offset+i] = iter.next();
      i++;
    } while (i < length && iter.hasNext());
    return offset + i;
  }

  /**
   * INTERNAL: Cast collection as list or make a new list.
   */
  public static <T> List<T> castList(Collection<T> c) {
    if (c instanceof List)
      return (List<T>)c;
    else
      return new ArrayList<T>(c);
  }

  /**
   * INTERNAL: Adds all elements in the array to the collection.
   */
  public static <T> void addAll(Collection<T> c, T[] a) {
    for (int i=0; i < a.length; i++) {
      c.add(a[i]);
    }
  }

  /**
   * INTERNAL: Returns true if the two collections overlap with one or
   * more elements.
   */
  public static <T> boolean overlaps(Collection<T> c1, Collection<T> c2) {
    if (c1.size() > c2.size())
      return _overlaps(c2, c1);
    else
      return _overlaps(c1, c2);      
  }
  
  private static <T> boolean _overlaps(Collection<T> c1, Collection<T> c2) {
    // NOTE: loop over smallest collection, which should always be the first argument
    Iterator<T> iter = c1.iterator();
    while (iter.hasNext()) {
      if (c2.contains(iter.next())) return true;
    }
    return false;
  }

  /**
   * INTERNAL: Creates new concurrent java.util.Map instance.
   */
  public static <K, V> Map<K, V> createConcurrentMap() {
    try {
      Class<?> klass = Class.forName("java.util.concurrent.ConcurrentHashMap");
      return (Map<K, V>)klass.newInstance();
    } catch (Exception e1) {
      try {
        Class<?> klass = Class.forName("EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap");
        return (Map<K, V>)klass.newInstance();
      } catch (Exception e2) {
        return Collections.synchronizedMap(new HashMap<K, V>());
      }
    }
  }

  /**
   * INTERNAL: Creates new concurrent java.util.Set instance.
   */
  public static <T> Set<T> createConcurrentSet() {
    try {
      Class<?> klass = Class.forName("EDU.oswego.cs.dl.util.concurrent.CopyOnWriteArraySet");
      return (Set<T>)klass.newInstance();
    } catch (Exception e1) {
      return Collections.synchronizedSet(new HashSet<T>());
    }
  }

  /**
   * INTERNAL: Creates new Set that contains the elements from the
   * input collection that the decider deems ok.
   */
  public static <T> Set<T> filterSet(Collection<T> coll, DeciderIF decider) {
    if (coll.isEmpty()) return Collections.emptySet();
    Set<T> result = new HashSet<T>(coll.size());
    Iterator<T> iter = coll.iterator();
    while (iter.hasNext()) {
      T o = iter.next();
      if (decider.ok(o))
        result.add(o);
    }
    return result;
  }
  
  /**
   * INTERNAL: Removes all except the first occurrence of each element
   * in the list. Use only with fairly small RandomAccess
   * collections. Method trades speed for memory.
   */
  public static <T> List<T> removeDuplicates(List<T> list) {
    int size = list.size();
    for (int index=0; index < size; index++) {
      // remove all but first occurrence
      T elem = list.get(index);
      for (int i=index+1; i < size; i++) {
        if (ObjectUtils.equals(elem, list.get(i))) {
          list.remove(i);
          size--;
        }
      }
    }
    return list;
  }

}

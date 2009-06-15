
// $Id: CollectionUtils.java,v 1.27 2008/09/09 08:06:55 geir.gronmo Exp $

package net.ontopia.utils;

import java.util.*;

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
  public static Object getFirst(Collection coll) {
    if (coll == null || coll.isEmpty()) return null;

    // If it's a list return it directly
    if (coll instanceof List) return ((List)coll).get(0);
    Iterator iter = coll.iterator();
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
  public static Object getFirstElement(Collection coll)
    throws NoSuchElementException {
    
    if (coll instanceof List)
      try {
        return ((List)coll).get(0);
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    else
      return coll.iterator().next();
  }

  public static Collection getSingletonCollectionOrEmptyIfNull(Object o) {
    if (o == null)
      return Collections.EMPTY_SET;
    else
      return Collections.singleton(o);
  }
  
  /**
   * INTERNAL: Gets a random object from the collection. If the
   * collection is empty, null is returned.
   */
  public static Object getRandom(Collection coll) {

    if (coll == null || coll.isEmpty()) return null;
    int chosen = random.nextInt(coll.size());

    // NOTE: Test should ideally be against java.util.RandomAccess,
    // but it's only supported with JDK 1.4

    // If it's a list return it directly
    if (coll instanceof List) return ((List)coll).get(chosen);

    // Otherwise loop through the collection
    long count = 0;
    Iterator iter = coll.iterator();
    while (iter.hasNext()) {
      Object obj = iter.next();
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
  public static boolean equalsUnorderedSet(Collection coll1, Collection coll2) {
    
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
      Object obj1 = coll1.iterator().next();
      Object obj2 = coll2.iterator().next();
      return (obj1 == null ? obj2 == null : obj1.equals(obj2));
    }
    
    // Compare collections as sets
    if (coll1 instanceof Set)
      if (coll2 instanceof Set)
        return coll1.equals(coll2);
      else
        return coll1.equals(new HashSet(coll2));
    else if (coll2 instanceof Set)
      return coll2.equals(new HashSet(coll1));
    else
      return new HashSet(coll2).equals(new HashSet(coll1));
  }

  /**
   * EXPERIMENTAL: Iterates over up to <i>length</i> number of
   * elements in the iterator and returns those elements as a
   * Collection. If the iterator is exhausted only the iterated
   * elements are returned.
   */
  public static List nextBatch(Iterator iter, int length) {
    List batch = new ArrayList(length);
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
  public static int nextBatch(Iterator iter, int length, Collection batch) {
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
  public static int nextBatch(Iterator iter, Object[] values) {
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
  public static int nextBatch(Iterator iter, Object[] values, int offset, int length) {
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
  public static List castList(Collection c) {
    if (c instanceof List)
      return (List)c;
    else
      return new ArrayList(c);
  }

  /**
   * INTERNAL: Adds all elements in the array to the collection.
   */
  public static void addAll(Collection c, Object[] a) {
    for (int i=0; i < a.length; i++) {
      c.add(a[i]);
    }
  }

  /**
   * INTERNAL: Returns true if the two collections overlap with one or
   * more elements.
   */
  public static boolean overlaps(Collection c1, Collection c2) {
    if (c1.size() > c2.size())
      return _overlaps(c2, c1);
    else
      return _overlaps(c1, c2);      
  }
  
  private static boolean _overlaps(Collection c1, Collection c2) {
    // NOTE: loop over smallest collection, which should always be the first argument
    Iterator iter = c1.iterator();
    while (iter.hasNext()) {
      if (c2.contains(iter.next())) return true;
    }
    return false;
  }

  /**
   * INTERNAL: Creates new concurrent java.util.Map instance.
   */
  public static Map createConcurrentMap() {
    try {
      Class klass = Class.forName("java.util.concurrent.ConcurrentHashMap");
      return (Map)klass.newInstance();
    } catch (Exception e1) {
      try {
        Class klass = Class.forName("EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap");
        return (Map)klass.newInstance();
      } catch (Exception e2) {
        return Collections.synchronizedMap(new HashMap());
      }
    }
  }

  /**
   * INTERNAL: Creates new concurrent java.util.Set instance.
   */
  public static Set createConcurrentSet() {
    try {
      Class klass = Class.forName("EDU.oswego.cs.dl.util.concurrent.CopyOnWriteArraySet");
      return (Set)klass.newInstance();
    } catch (Exception e1) {
      return Collections.synchronizedSet(new HashSet());
    }
  }

  /**
   * INTERNAL: Creates new Set that contains the elements from the
   * input collection that the decider deems ok.
   */
  public static Set filterSet(Collection coll, DeciderIF decider) {
    if (coll.isEmpty()) return Collections.EMPTY_SET;
    Set result = new HashSet(coll.size());
    Iterator iter = coll.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
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
  public static List removeDuplicates(List list) {
    int size = list.size();
    for (int index=0; index < size; index++) {
      // remove all but first occurrence
      Object elem = list.get(index);
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

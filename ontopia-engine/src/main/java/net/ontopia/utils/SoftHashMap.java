
// $Id: SoftHashMap.java,v 1.10 2005/06/19 12:31:49 grove Exp $

package net.ontopia.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Set;
import java.util.AbstractSet;
import java.util.NoSuchElementException;

import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

/**
 * INTERNAL: A Map implementation that uses SoftReferences to
 * reference keys.
 */

public class SoftHashMap extends AbstractMap {

  static private class SoftKey extends SoftReference {
    private int hc; // hash code of key object

    private SoftKey(Object k) {
      super(k);
      hc = k.hashCode();
    }

    private static SoftKey create(Object k) {
      if (k == null) return null;
      else return new SoftKey(k);
    }

    private SoftKey(Object k, ReferenceQueue q) {
      super(k, q);
      hc = k.hashCode();
    }

    private static SoftKey create(Object k, ReferenceQueue q) {
      if (k == null) return null;
      else return new SoftKey(k, q);
    }

    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof SoftKey)) return false;
      Object t = this.get();
      Object u = ((SoftKey)o).get();
      if ((t == null) || (u == null)) return false;
      if (t == u) return true;
      return t.equals(u);
    }

    public int hashCode() {
      return hc;
    }

  }

  protected Map hash;
  protected ReferenceQueue queue = new ReferenceQueue();

  protected void processQueue() {
    Object sk;
    while ((sk = queue.poll()) != null) {
      hash.remove(sk);
    }
  }

  public SoftHashMap(int initialCapacity, float loadFactor) {
    hash = new HashMap(initialCapacity, loadFactor);
  }

  public SoftHashMap(int initialCapacity) {
    hash = new HashMap(initialCapacity);
  }

  public SoftHashMap() {
    hash = new HashMap();
  }

  public SoftHashMap(Map t) {
    this(Math.max(2*t.size(), 11), 0.75f);
    putAll(t);
  }

  public int size() {
    return entrySet().size();
  }

  public boolean isEmpty() {
    return entrySet().isEmpty();
  }

  public boolean containsKey(Object key) {
    return hash.containsKey(SoftKey.create(key));
  }

  public Object get(Object key) {
    return hash.get(SoftKey.create(key));
  }

  public Object put(Object key, Object value) {
    processQueue();
    return hash.put(SoftKey.create(key, queue), value);
  }

  public Object remove(Object key) {
    processQueue();
    return hash.remove(SoftKey.create(key));
  }

  public void clear() {
    processQueue();
    hash.clear();
  }

  static private class Entry implements Map.Entry {
    private Map.Entry ent;
    private Object key;

    Entry(Map.Entry ent, Object key) {
      this.ent = ent;
      this.key = key;
    }

    public Object getKey() {
      return key;
    }

    public Object getValue() {
      return ent.getValue();
    }

    public Object setValue(Object value) {
      return ent.setValue(value);
    }

    private static boolean valEquals(Object o1, Object o2) {
      return (o1 == null) ? (o2 == null) : o1.equals(o2);
    }

    public boolean equals(Object o) {
      if (! (o instanceof Map.Entry)) return false;
      Map.Entry e = (Map.Entry)o;
      return (valEquals(key, e.getKey())
              && valEquals(getValue(), e.getValue()));
    }

    public int hashCode() {
      Object v;
      return (((key == null) ? 0 : key.hashCode())
              ^ (((v = getValue()) == null) ? 0 : v.hashCode()));
    }

  }

  private class EntrySet extends AbstractSet {
    Set hashEntrySet = hash.entrySet();

    public Iterator iterator() {

      return new Iterator() {
          Iterator hashIterator = hashEntrySet.iterator();
          Entry next = null;

          public boolean hasNext() {
            while (hashIterator.hasNext()) {
              Map.Entry ent = (Map.Entry)hashIterator.next();
              SoftKey sk = (SoftKey)ent.getKey();
              Object k = null;
              if ((sk != null) && ((k = sk.get()) == null)) {
                continue;
              }
              next = new Entry(ent, k);
              return true;
            }
            return false;
          }

          public Object next() {
            if ((next == null) && !hasNext())
              throw new NoSuchElementException();
            Entry e = next;
            next = null;
            return e;
          }

          public void remove() {
            hashIterator.remove();
          }

        };
    }

    public boolean isEmpty() {
      return !(iterator().hasNext());
    }

    public int size() {
      int j = 0;
      for (Iterator i = iterator(); i.hasNext(); i.next()) j++;
      return j;
    }

    public boolean remove(Object o) {
      processQueue();
      if (!(o instanceof Map.Entry)) return false;
      Map.Entry e = (Map.Entry)o;
      Object ev = e.getValue();
      SoftKey sk = SoftKey.create(e.getKey());
      Object hv = hash.get(sk);
      if ((hv == null)
          ? ((ev == null) && hash.containsKey(sk)) : hv.equals(ev)) {
        hash.remove(sk);
        return true;
      }
      return false;
    }

    public int hashCode() {
      int h = 0;
      for (Iterator i = hashEntrySet.iterator(); i.hasNext();) {
        Map.Entry ent = (Map.Entry)i.next();
        SoftKey sk = (SoftKey)ent.getKey();
        Object v;
        if (sk == null) continue;
        h += (sk.hashCode()
              ^ (((v = ent.getValue()) == null) ? 0 : v.hashCode()));
      }
      return h;
    }

  }

  private Set entrySet = null;

  public Set entrySet() {
    if (entrySet == null) entrySet = new EntrySet();
    return entrySet;
  }

}

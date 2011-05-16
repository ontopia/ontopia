
// $Id: LazySet.java,v 1.5 2004/11/19 09:06:24 grove Exp $

package net.ontopia.topicmaps.impl.tmapi2;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TMObjectIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class LazySet<T> extends AbstractSet<T> {

  protected TopicMapImpl tm;
  protected Collection<?> other;

  public LazySet(TopicMapImpl tm, Collection<?> other) {
    this.tm = tm;
    this.other = other;
  }

  @SuppressWarnings("unchecked")
  public Iterator<T> iterator() {
    return new LazySetIterator(other.iterator());
  }

  public int size() {
    return other.size();
  }

  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  class LazySetIterator implements Iterator {

    Iterator iter;

    LazySetIterator(Iterator iter) {
      this.iter = iter;
    }

    public boolean hasNext() {
      return iter.hasNext();
    }

    public Object next() {
      Object n = iter.next();
      if (n instanceof LocatorIF) {
        return tm.wrapLocator((LocatorIF) n);
      } else {
        return tm.wrapTMObject((TMObjectIF) n);
      }
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}

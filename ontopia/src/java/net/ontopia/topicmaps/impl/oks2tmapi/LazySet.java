
// $Id: LazySet.java,v 1.5 2004/11/19 09:06:24 grove Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class LazySet extends AbstractSet {

  protected TopicMap tm;
  protected Collection other;

  public LazySet(TopicMap tm, Collection other) {
    this.tm = tm;
    this.other = other;
  }

  public Iterator iterator() {
    return new LazySetIterator(other.iterator());
  }

  public int size() {
    return other.size();
  }
  
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

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
      if (n instanceof LocatorIF)
	return new Locator((LocatorIF)n);
      else
	return tm.wrapTMObject(n);
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}

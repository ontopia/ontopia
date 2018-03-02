/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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
  @Override
  public Iterator<T> iterator() {
    return new LazySetIterator(other.iterator());
  }

  @Override
  public int size() {
    return other.size();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  class LazySetIterator implements Iterator {

    protected Iterator iter;

    LazySetIterator(Iterator iter) {
      this.iter = iter;
    }

    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    @Override
    public Object next() {
      Object n = iter.next();
      if (n instanceof LocatorIF) {
        return tm.wrapLocator((LocatorIF) n);
      } else {
        return tm.wrapTMObject((TMObjectIF) n);
      }
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}

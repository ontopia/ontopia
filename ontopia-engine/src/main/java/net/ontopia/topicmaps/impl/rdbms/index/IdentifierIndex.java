/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2017 The Ontopia Project
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

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.persistence.proxy.QueryCollection;
import net.ontopia.topicmaps.core.index.IdentifierIndexIF;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Transformer;

public class IdentifierIndex extends RDBMSIndex implements IdentifierIndexIF {

  public IdentifierIndex(IndexManager imanager) {
    super(imanager);
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiers() {
    Object[] params = new Object[] { transaction.getTopicMap() };
    return new LazyTransformedCollection<String, LocatorIF>(new QueryCollection<String>(transaction.getTransaction(),
                                 "IdentifierIndexIF.getItemIdentifiers_size", params,
                                 "IdentifierIndexIF.getItemIdentifiers", params),
            new StringToLocatorTransformer());
  }

  @Override
  public Collection<LocatorIF> getItemIdentifiersByPrefix(String prefix) {
    Object[] params = new Object[] { transaction.getTopicMap(), prefix + "%" };
    return new LazyTransformedCollection<String, LocatorIF>(new QueryCollection<String>(transaction.getTransaction(),
                                 "IdentifierIndexIF.getItemIdentifiersByPrefix_size", params,
                                 "IdentifierIndexIF.getItemIdentifiersByPrefix", params),
            new StringToLocatorTransformer());
  }

  @Override
  public Collection<LocatorIF> getSubjectIdentifiers() {
    Object[] params = new Object[] { transaction.getTopicMap() };
    return new LazyTransformedCollection<String, LocatorIF>(new QueryCollection<String>(transaction.getTransaction(),
                                 "IdentifierIndexIF.getSubjectIdentifiers_size", params,
                                 "IdentifierIndexIF.getSubjectIdentifiers", params),
            new StringToLocatorTransformer());
  }

  @Override
  public Collection<LocatorIF> getSubjectIdentifiersByPrefix(String prefix) {
    Object[] params = new Object[] { transaction.getTopicMap(), prefix + "%" };
    return new LazyTransformedCollection<String, LocatorIF>(new QueryCollection<String>(transaction.getTransaction(),
                                 "IdentifierIndexIF.getSubjectIdentifiersByPrefix_size", params,
                                 "IdentifierIndexIF.getSubjectIdentifiersByPrefix", params),
            new StringToLocatorTransformer());
  }
  
  private class LazyTransformedCollection<I, O> extends AbstractCollection<O> {

    private final Collection<I> source;
    private final Transformer<I, O> transformer;
    
    public LazyTransformedCollection(Collection<I> source, Transformer<I, O> transformer) {
      this.source = source;
      this.transformer = transformer;
      
    }

    @Override
    public Iterator<O> iterator() {
      return IteratorUtils.transformedIterator(source.iterator(), transformer);
    }

    @Override
    public int size() {
      return source.size();
    }
  }
  
  private class StringToLocatorTransformer implements Transformer<String, LocatorIF> {

    @Override
    public LocatorIF transform(String input) {
      return URILocator.create(input);
    }
  }
}

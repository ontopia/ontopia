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

package net.ontopia.topicmaps.impl.tmapi2.index;


import java.util.Collection;

import net.ontopia.topicmaps.core.index.NameIndexIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.impl.tmapi2.Check;
import net.ontopia.topicmaps.impl.tmapi2.LazySet;
import net.ontopia.topicmaps.impl.tmapi2.TopicMapImpl;
import net.ontopia.topicmaps.utils.PSI;

import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Variant;
import org.tmapi.index.LiteralIndex;

/**
 * Implementation of the {@link LiteralIndex}
 * INTERNAL: OKS->TMAPI 2 object wrapper.
 */

public class LiteralIndexImpl implements LiteralIndex {
  private final TopicMapImpl topicMap;
  private final OccurrenceIndexIF occurrenceIndex;
  private final NameIndexIF nameIndex;

  public LiteralIndexImpl(TopicMapImpl topicMap) {
    this.topicMap = topicMap;
    occurrenceIndex = (OccurrenceIndexIF) topicMap.getWrapped().getIndex(
        "net.ontopia.topicmaps.core.index.OccurrenceIndexIF");
    nameIndex = (NameIndexIF) topicMap.getWrapped().getIndex(
        "net.ontopia.topicmaps.core.index.NameIndexIF");

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getNames(java.lang.String)
   */
  @Override
  public Collection<Name> getNames(String value) {
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }

    return new LazySet<Name>(topicMap, nameIndex.getTopicNames(value));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getOccurrences(java.lang.String)
   */
  @Override
  public Collection<Occurrence> getOccurrences(String value) {
    Check.valueNotNull(value);

    return new LazySet<Occurrence>(topicMap, occurrenceIndex.getOccurrences(
        value, PSI.getXSDString()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getOccurrences(org.tmapi.core.Locator)
   */
  @Override
  public Collection<Occurrence> getOccurrences(Locator value) {
    Check.valueNotNull(value);

    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }

    return new LazySet<Occurrence>(topicMap, occurrenceIndex.getOccurrences(
        value.toExternalForm()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getOccurrences(java.lang.String,
   * org.tmapi.core.Locator)
   */
  @Override
  public Collection<Occurrence> getOccurrences(String value, Locator locator) {
    Check.valueNotNull(value);
    Check.locatorNotNull(locator);

    return new LazySet<Occurrence>(topicMap, occurrenceIndex.getOccurrences(
        value, topicMap.unwrapLocator(locator)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getVariants(java.lang.String)
   */
  @Override
  public Collection<Variant> getVariants(String value) {
    Check.valueNotNull(value);

    return new LazySet<Variant>(topicMap, nameIndex.getVariants(value, PSI.getXSDString()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getVariants(org.tmapi.core.Locator)
   */
  @Override
  public Collection<Variant> getVariants(Locator value) {
    Check.valueNotNull(value);

    return new LazySet<Variant>(topicMap, nameIndex.getVariants(value.toExternalForm()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.LiteralIndex#getVariants(java.lang.String,
   * org.tmapi.core.Locator)
   */
  @Override
  public Collection<Variant> getVariants(String value, Locator datatype) {
    Check.valueNotNull(value);
    Check.datatypeNotNull(datatype);

    return new LazySet<Variant>(topicMap, nameIndex.getVariants(value, topicMap
        .unwrapLocator(datatype)));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#close()
   */
  @Override
  public void close() {
    // no-op
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#isAutoUpdated()
   */
  @Override
  public boolean isAutoUpdated() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#isOpen()
   */
  @Override
  public boolean isOpen() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#open()
   */
  @Override
  public void open() {
    // no-op
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.tmapi.index.Index#reindex()
   */
  @Override
  public void reindex() {
    // no-op
  }

}

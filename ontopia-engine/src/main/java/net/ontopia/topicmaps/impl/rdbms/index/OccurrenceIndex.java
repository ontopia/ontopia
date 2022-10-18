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

package net.ontopia.topicmaps.impl.rdbms.index;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import org.apache.commons.collections4.iterators.TransformIterator;

/**
 * INTERNAL: The rdbms occurrence index implementation.
 */
public class OccurrenceIndex extends RDBMSIndex implements OccurrenceIndexIF {

  OccurrenceIndex(IndexManagerIF imanager) {
    super(imanager);
  }

  // ---------------------------------------------------------------------------
  // OccurrenceIndexIF
  // ---------------------------------------------------------------------------

  @Override
  public Collection<OccurrenceIF> getOccurrences(String value) {
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrences", new Object[] { getTopicMap(), value });
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrences(String value, TopicIF occurrenceType) {
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesByType", new Object[] { getTopicMap(), value, occurrenceType });
  }

  @Override
  public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype) {
    if (datatype == null) { return Collections.emptySet(); }
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesByDataType", new Object[] { getTopicMap(), value, datatype.getAddress() });
  }

  @Override
  public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype, TopicIF occurrenceType) {
    if (datatype == null) { return Collections.emptySet(); }
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesByDataTypeAndType", new Object[] { getTopicMap(), value, datatype.getAddress(), occurrenceType });
  }

  @Override
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix) {
    //! String ltval = prefix + Character.MAX_VALUE;
    String ltval = (prefix == null ? null : prefix.substring(0, prefix.length()- 1) + (char)(prefix.charAt(prefix.length()-1)+1));
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesBetween", new Object[] { getTopicMap(), prefix, ltval });
  }
  
  @Override
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, LocatorIF datatype) {
    if (datatype == null) { return Collections.emptySet(); }
    //! String ltval = prefix + Character.MAX_VALUE;
    String ltval = (prefix == null ? null : prefix.substring(0, prefix.length()- 1) + (char)(prefix.charAt(prefix.length()-1)+1));
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesBetween_datatype", new Object[] { getTopicMap(), prefix, ltval, datatype.getAddress() });
  }

  @Override
  public Iterator<String> getValuesGreaterThanOrEqual(String value) {
    Collection<OccurrenceIF> coll = (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesGreaterThanOrEqual", new Object[] { getTopicMap(), value });
    return new TransformIterator<>(coll.iterator(), OccurrenceIF::getValue);
  }  

  @Override
  public Iterator<String> getValuesSmallerThanOrEqual(String value) {
    Collection<OccurrenceIF> coll = (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesLessThanOrEqual", new Object[] { getTopicMap(), value });
    return new TransformIterator<>(coll.iterator(), OccurrenceIF::getValue);
  }  
}

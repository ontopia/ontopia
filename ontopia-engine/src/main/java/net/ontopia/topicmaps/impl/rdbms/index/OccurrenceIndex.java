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

import java.util.Iterator;
import java.util.Collection;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.DataTypes;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.impl.utils.IndexManagerIF;
import net.ontopia.topicmaps.core.index.OccurrenceIndexIF;
import net.ontopia.utils.GrabberIF;
import net.ontopia.utils.GrabberIterator;

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

  public Collection<OccurrenceIF> getOccurrences(String value) {
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrences", new Object[] { getTopicMap(), value });
  }
  
  public Collection<OccurrenceIF> getOccurrences(String value, LocatorIF datatype) {
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesByDataType", new Object[] { getTopicMap(), value, datatype.getAddress() });
  }

  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix) {
    //! String ltval = prefix + Character.MAX_VALUE;
    String ltval = (prefix == null ? null : prefix.substring(0, prefix.length()- 1) + (char)(prefix.charAt(prefix.length()-1)+1));
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesBetween", new Object[] { getTopicMap(), prefix, ltval });
  }
  
  public Collection<OccurrenceIF> getOccurrencesByPrefix(String prefix, LocatorIF datatype) {
    //! String ltval = prefix + Character.MAX_VALUE;
    String ltval = (prefix == null ? null : prefix.substring(0, prefix.length()- 1) + (char)(prefix.charAt(prefix.length()-1)+1));
    return (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesBetween_datatype", new Object[] { getTopicMap(), prefix, ltval, datatype.getAddress() });
  }

  public Iterator<String> getValuesGreaterThanOrEqual(String value) {
    Collection<OccurrenceIF> coll = (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesGreaterThanOrEqual", new Object[] { getTopicMap(), value });
    return new GrabberIterator<OccurrenceIF, String>(coll.iterator(), new GrabberIF<OccurrenceIF, String>() {
        public String grab(OccurrenceIF o) {
          return o.getValue();
        }
      });
  }  

  public Iterator<String> getValuesSmallerThanOrEqual(String value) {
    Collection<OccurrenceIF> coll = (Collection<OccurrenceIF>)executeQuery("OccurrenceIndexIF.getOccurrencesLessThanOrEqual", new Object[] { getTopicMap(), value });
    return new GrabberIterator<OccurrenceIF, String>(coll.iterator(), new GrabberIF<OccurrenceIF, String>() {
        public String grab(OccurrenceIF o) {
          return o.getValue();
        }
      });
  }  
}

/*
 * #!
 * Ontopia DB2TM
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

package net.ontopia.topicmaps.db2tm;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: This tuple reader wraps an underlying tuple reader, and
 * collapses a sequence of actions for the same key into a single
 * final action. Tuples are coming through ordered by key first, then
 * by sequence.
 */
public class ChangelogReaderWrapper implements ChangelogReaderIF {
  private final ChangelogReaderIF source;
  private int[] keycols; // contains index in relation of each key column

  // used for tracking next tuple
  private String[] tuple;
  
  // used for tracking previous tuple
  private String prevorder; // ?
  private ChangeType prevchange;
  private String[] prevtuple;

  private static Logger log = LoggerFactory.getLogger(ChangelogReaderWrapper.class);
  
  public ChangelogReaderWrapper(ChangelogReaderIF source,
                                Relation relation) {
    this.source = source;

    String[] pkey = relation.getPrimaryKey();
    this.keycols = new int[pkey.length];
    for (int ix = 0; ix < pkey.length; ix++) {
      keycols[ix] = relation.getColumnIndex(pkey[ix]);
    }
    if (pkey.length == 0) {
      // this will cause the reader to ignore rows, because they will
      // look the same (equalsKey will proclaim all rows equal since
      // none of the values in their zero-length keys differ)
      throw new DB2TMException("Must specify primary key on '" +
                               relation.getName() + "'");
    }
  }
  
  @Override
  public ChangeType getChangeType() {
    return prevchange;
  }

  @Override
  public String getOrderValue() {
    return prevorder;
  }

  @Override
  public String[] readNext() {
    // INVARIANT:
    //  (a) prevtuple is null, tuple is null, ready to start on first row
    //  (b) prevtuple holds previous passed-on row, tuple holds next
    //  (c) prevtuple holds previous passed-on row, tuple is null; that is,
    //      we've reached the end of the stream

    // it could be that we are finished (c), in which case, return null
    if (prevtuple != null && tuple == null) {
      return null;
    }
    
    // it could be that we haven't started yet (a), in which case,
    // kickstart things
    if (prevtuple == null && tuple == null) {
      tuple = source.readNext();
    }
    
    // now read new tuples until we find one belonging to a new key
    while (true) {
      if (log.isTraceEnabled()) {
        log.trace("State: {} Tuple: ({})", prevchange, (tuple == null ? "null" : StringUtils.join(tuple, "|")));
      }
      
      // move one row forwards
      prevtuple = tuple;
      if (tuple != null) {
        prevchange = source.getChangeType();
        prevorder = source.getOrderValue();
      }
      
      tuple = source.readNext();

      // did we just move onto a new key?
      if (!equalsKey(prevtuple, tuple) ||
          (tuple == null && prevtuple == null)) {
        break;
      }
    }

    // notice how we are now back to INVARIANT as stated above
    return prevtuple;
  }

  @Override
  public void close() {
    source.close();
  }

  // INTERNAL HELPERS
  
  // this code is based on the assumption that the primary key is
  // always the first n values in the tuple, where n is the length
  // of the primary key. for now, the code does produce such tuples.
  private boolean equalsKey(String[] tuple1, String[] tuple2) {
    if ((tuple1 == null && tuple2 != null) ||
        (tuple1 != null && tuple2 == null)) {
      return false;
    }
    if (tuple1 == null && tuple2 == null) {
      return true;
    }

    for (int ix = 0; ix < keycols.length; ix++) {
      if (!Objects.equals(tuple1[keycols[ix]], tuple2[keycols[ix]])) {
        return false;
      }
    }
    return true;
  }  
}

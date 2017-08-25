/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import net.ontopia.topicmaps.query.core.QueryResultIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferedQueryResult 
        implements BufferedQueryResultIF {
  
  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(BufferedQueryResult.class
          .getName());

  protected QueryResultIF queryResult;      
  protected Collection buffer;
  protected Object bufferedRow[];
  protected Iterator bufferIt;
  protected boolean inBuffer;
  protected String query;
  
  public BufferedQueryResult(QueryResultIF queryResult, String query) {
    this.queryResult = queryResult;
    this.query = query;
    
    buffer = new ArrayList();
    bufferIt = buffer.iterator();
    bufferedRow = null;
    inBuffer = true;
  }
  
  public void close() {
    // no-op
  }
  
  public String getColumnName(int ix) {
    return queryResult.getColumnName(ix);
  }
  
  public String[] getColumnNames() {
    return queryResult.getColumnNames();
  }
  
  public int getIndex(String colname) {
    return queryResult.getIndex(colname);
  }
  
  public Object getValue(int ix) {
    if (inBuffer)
      return bufferedRow[ix];
    return queryResult.getValue(ix);
  }
  
  public Object getValue(String colname) {
    return getValue(getIndex(colname));
  }
  
  public Object[] getValues() {
    if (inBuffer)
      return bufferedRow;
    return queryResult.getValues();
  }
  
  public Object[] getValues(Object values[]) {
    values = getValues();
    return values;
  }
  
  public int getWidth() {
    return queryResult.getWidth();
  }
  
  public boolean next() {
    // Get all results from buffer (until the end).
    // Then get results from queryResult, adding them to the buffer.
    boolean retVal;
    
    if (bufferIt != null && bufferIt.hasNext()) {
      bufferedRow = (Object[])bufferIt.next();
      retVal = true;
    } else {
      bufferIt = null;
      retVal = queryResult.next();
      
      if (retVal)
        buffer.add(queryResult.getValues());
        
      inBuffer = false;
    }
    
    return retVal;
  }
  
  public String getQuery() {
    return query;
  }
  
  public void restart() {
    bufferIt = buffer.iterator();
    bufferedRow = null;
    inBuffer = true;    
  }
}

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

public class BufferedQueryResult 
        implements BufferedQueryResultIF {
  
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
  
  @Override
  public void close() {
    // no-op
  }
  
  @Override
  public String getColumnName(int ix) {
    return queryResult.getColumnName(ix);
  }
  
  @Override
  public String[] getColumnNames() {
    return queryResult.getColumnNames();
  }
  
  @Override
  public int getIndex(String colname) {
    return queryResult.getIndex(colname);
  }
  
  @Override
  public Object getValue(int ix) {
    if (inBuffer)
      return bufferedRow[ix];
    return queryResult.getValue(ix);
  }
  
  @Override
  public Object getValue(String colname) {
    return getValue(getIndex(colname));
  }
  
  @Override
  public Object[] getValues() {
    if (inBuffer)
      return bufferedRow;
    return queryResult.getValues();
  }
  
  @Override
  public Object[] getValues(Object values[]) {
    values = getValues();
    return values;
  }
  
  @Override
  public int getWidth() {
    return queryResult.getWidth();
  }
  
  @Override
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
  
  @Override
  public String getQuery() {
    return query;
  }
  
  @Override
  public void restart() {
    bufferIt = buffer.iterator();
    bufferedRow = null;
    inBuffer = true;    
  }
}

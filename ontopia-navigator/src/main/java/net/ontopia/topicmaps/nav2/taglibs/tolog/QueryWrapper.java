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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Generic Tolog Tag that has support for executing one query.
 */
public class QueryWrapper {
  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(QueryWrapper.class.getName());

  protected ArrayList lookAhead = new ArrayList();
  
  private Collection users;
  
  private ContextTag contextTag;
  
  // Query from which to make a query result        
  private String query;
  private QueryResultIF queryResult;
  
  private QueryProcessorIF queryProcessor;
  
  private Object currentRow[];
  private Object nextRow[];
  
  // Differences in the queryResult between nextRow and currentRow.
  private boolean differences[];
  
  private boolean totalGroupBy[];        
          
  private ContextManagerIF contextManager;
  
  /**
   * Default constructor. Creates executes the inputQuery in the
    * given PageContext and moves to the first row.
   */
  public QueryWrapper(PageContext pageContext, String inQuery) 
          throws NavigatorRuntimeException {
    contextTag = FrameworkUtils.getContextTag(pageContext);
    
    if (contextTag == null) {
      throw new NavigatorRuntimeException("<tolog:*> tags must be nested"
              + " directly or indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found");
    }
    
    contextManager = contextTag.getContextManager();
      
    this.query = inQuery;
        
    
    if (query == null) {
      throw new NavigatorRuntimeException("QueryWrapper must get a non-null"
                                          + "'query' argument");
    }

    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("QueryWrapper found no "
              + "topic map.");
    }
        
    // Create a QueryProcessorIF for the topicmap.
    queryProcessor = contextTag.getQueryProcessor();
    queryResult = contextTag.getQueryResult(query);
    
    if (queryResult == null)
      try {
        // Execute query, using any arguments from the context manager.
        queryResult = queryProcessor.execute(query, 
                        new ContextManagerScopingMapWrapper(contextManager),
                        contextTag.getDeclarationContext());
      } catch (InvalidQueryException e) {
        log.info("Parsing of query '" + query + "' failed with message: " + e);
        throw new NavigatorRuntimeException(e);
      }
    else {
      BufferedQueryResultIF bufferedQueryResult =
              ((BufferedQueryResultIF)queryResult);
      bufferedQueryResult.restart();
      query = bufferedQueryResult.getQuery();
    }
      
    users = new HashSet();
    
    // Move (internally) nextRow to the first result row.
    next();
  }
  
  protected void updateTotalGroupBy(boolean groupBy[]) {
    if (totalGroupBy == null) {
      totalGroupBy = new boolean[groupBy.length];
    }
    for (int i = 0; i < totalGroupBy.length; i++) {
      totalGroupBy[i] |= groupBy[i];
    }
  }
  
  protected void setUsedBy(ForEachTag user) {
    users.add(user);
  }
  
  protected boolean usedBy(ForEachTag user) {
    return users.contains(user);
  }
  
  protected boolean fullyGrouped() {
    for (int i = 0; i < totalGroupBy.length; i++) {
      if (!totalGroupBy[i]) {
        return false;
      }
    }
    return true;
  }

  /**
    * Finds out on which columns two arrays of Objects differ.
    * Uses the .toString() method to determine if they're the same.
    * @return boolean[] each entry true iff the inputs differ at that index.
    */
  private void computeDifferences() {
    if (currentRow == null || nextRow == null) {
      differences = null;
    } else {
      differences = new boolean[currentRow.length];
      for (int i = 0 ; i < currentRow.length; i++) {
        differences[i] = !sameElements(currentRow[i], nextRow[i]);
      }
    }
  }

  /**
    * Test if two given elements are equal, i.e. the same or otherwise equivalent.
  */
  private boolean sameElements(Object elem1, Object elem2) {
    return Objects.equals(elem1, elem2);
  }
    
  /**
    * @return true if the query result has changed with respect in any of the
    * columns corresponding to the entries of groupColumns.
    * This happens iff for any particular index diffs and groupColumns both
    * contain the value true.
    */
  protected boolean relevantDifferences(boolean groupColumns[]) {
    if (groupColumns == null || differences == null) {
      return false;
    }
  
    boolean retVal= false;
    for (int i = 0; i < groupColumns.length; i++) {
      retVal |= (groupColumns[i] && differences[i]);
    }
    return retVal;
  }

  public ContextManagerIF getContextManager() {
    return contextManager;
  }
  
  public boolean hasNext() {
    return nextRow != null;
  }
  
  /** Moves one step forward in the result set of the query.
  */
  public void next() {
    currentRow = nextRow;
    if (!lookAhead.isEmpty()) {
      nextRow = (Object[])lookAhead.remove(0);
    } else {
      nextRow = queryResult.next() ? queryResult.getValues() : null;
    }
    computeDifferences();
  }
  
  public Object[] getCurrentRow() {
    return currentRow;
  }
  
  public int getWidth() {
    return queryResult.getWidth();
  }
  
  public int getIndex(String columnName) {
    return queryResult.getIndex(columnName);
  }
  
  public String getQuery() {
    return query;
  }
  
  public ParsedQueryIF parseQuery() throws NavigatorRuntimeException {
    ParsedQueryIF parsedQuery;
    
    DeclarationContextIF declarationContext = contextTag
            .getDeclarationContext();
    
    if (declarationContext == null) {
      throw new NavigatorRuntimeException("QueryWrapper found no"
              + " DeclaractionContextIF on the ContextTag");    
    }    
            
    try {
      parsedQuery = queryProcessor.parse(query, declarationContext);
    } catch (InvalidQueryException e) {
      throw new NavigatorRuntimeException(e);
    }
    return parsedQuery;
  }
  
  /** 
   * Bind (some of) the names of the columns of the result to the current row. 
   * Only bind those columns corresponding to a true entry in groupColumns.
   * e.g. column 3 is bound if groupColumns[3] is true.
   */
  protected void bindVariables(boolean groupColumns[]) throws JspTagException {
    String columnNames[] = queryResult.getColumnNames();
    for (int i = 0; i < groupColumns.length; i++) {
      if (groupColumns[i]) {
        contextManager.setValue(columnNames[i]
                , currentRow[i] == null
                ? Collections.EMPTY_LIST
                : currentRow[i]);
      }
    }
  }

  protected boolean isOnlyChild(boolean[] parentGroupColumns, boolean[] childGroupColumns) {
    // look ahead to see if the current child is the only direct child of the parent

    // at last row
    if (nextRow == null) {
      return true;
    }

    // next row is different
    if (!equalGroup(parentGroupColumns, currentRow, nextRow)) {
      return true;
    }
    if (!equalGroup(childGroupColumns, currentRow, nextRow)) {
      return false;
    }
    
    // check existing look ahead
    int length = lookAhead.size();
    for (int i=0; i < length; i++) {
      Object[] futureRow = (Object[])lookAhead.get(i);
      if (!equalGroup(parentGroupColumns, currentRow, futureRow)) {
        return true;
      }
      if (!equalGroup(childGroupColumns, currentRow, futureRow)) {
        return false;
      }            
    }

    // peek further
    while (queryResult.next()) {      
      Object[] futureRow = queryResult.getValues();      
      lookAhead.add(futureRow);
      if (!equalGroup(parentGroupColumns, currentRow, futureRow)) {
        return true;
      }
      if (!equalGroup(childGroupColumns, currentRow, futureRow)) {
        return false;
      }      
    }    
    return true;
  }

  protected boolean equalGroup(boolean[] groupColumns, Object[] row1, Object[] row2) {
    for (int i=0; i < row1.length; i++) {
      if (groupColumns[i] && !Objects.equals(row1[i], row2[i])) {
        return false;
      }        
    }
    return true;
  }
  
}

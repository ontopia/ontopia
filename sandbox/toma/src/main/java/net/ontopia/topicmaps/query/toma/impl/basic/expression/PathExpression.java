/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.Collection;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.basic.path.AssocPath;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryTracer;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;

/**
 * INTERNAL: Path expression to be evaluated by the {@link BasicQueryProcessor}.
 */
public class PathExpression extends AbstractPathExpression implements
    BasicExpressionIF {

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    QueryTracer.enter(this);
    initResultSets(context);
    ResultSet rs = createNewResultSet(context);
    if (!isEmpty()) {
      Row row = rs.createRow();
      evaluateElement(context, PathElementIF.TYPE.NONE, rs, row, 0, 0);
    }
    QueryTracer.leave(rs);
    return rs;
  }
  
  private void initResultSets(LocalContext context) {
    for (int i=0; i<getPathLength(); i++) {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(i);
      element.initResultSet(context);
    }
  }

  /**
   * Fill the ResultSet with a fully evaluated PathExpression. The evaluation is
   * done in a recursive manner.
   * 
   * @param context the evaluation context.
   * @param rs the ResultSet to store rows.
   * @param row the current row used for evaluation.
   * @param pathDepth the index of the current PathElement.
   * @param colIndex the current column index in the row.
   * @param input the input for the current PathElement.
   * @throws InvalidQueryException if the PathElement could not be evaluated.
   */
  private void evaluateElement(LocalContext context, Object input,
      ResultSet rs, Row row, int pathDepth, int colIndex)
      throws InvalidQueryException {
    
    BasicPathElementIF element = (BasicPathElementIF) getPathElement(pathDepth);
    Collection<?> result = null;
    if (input != null) {
      result = element.evaluate(context, input);
    }

    // if the current PathElement returned nothing, we can fill the rest of
    // the row with null's.
    if (result == null || result.isEmpty()) {
      // This is necessary, because association paths are always implicitly EXISTS
      // FIXME: this is a hack, as it breaks NOT EXISTS and EXISTS queries, fix
      if (element instanceof AssocPath) {
        return;
      }
      
      for (int idx = colIndex; idx < row.getColumnCount(); idx++) {
        row.setValue(idx, null);
      }
      rs.addRow(row);
      return;
    }

    // iterate over all the result values
    Row curRow;
    for (Object val : result) {
      try {
        curRow = (Row) row.clone();
      } catch (CloneNotSupportedException e) {
        // should not happen, as Row implements Cloneable
        throw new InvalidQueryException(e);
      }

      Object last = val;
      int newCol = colIndex;
      
      // if the PathElement returned an array
      if (val instanceof Object[]) {
        Object[] coll = (Object[]) val;
        int idx = 0;
        for (Object obj : coll) {
          if (idx < element.getResultSize()) {
            curRow.setValue(colIndex + idx++, obj);
          }
          last = obj;
        }
        newCol = colIndex + idx;
      } else {
        if (element.getResultSize() > 0) {
          curRow.setValue(newCol++, val);
        }
      }
      
      if (pathDepth >= (getPathLength() - 1)) {
        curRow.setLastValue(last);
        rs.addRow(curRow);
      } else {
        evaluateElement(context, last, rs, curRow, pathDepth + 1, newCol);
      }
    }
  }

  /**
   * Create a {@link ResultSet} that is suitable for this PathExpression.
   * 
   * @return an empty ResultSet
   */
  private ResultSet createNewResultSet(LocalContext context) {
    int size = 0;
    // first, check the correct width for this PathExpression.
    for (int idx = 0; idx < getPathLength(); idx++) {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(idx);
      size += element.getResultSize();
    }

    // if we have a non-empty PathExpression, add a column to store the result.
    if (getPathLength() > 0) {
      size++;
    }

    ResultSet rs = new ResultSet(size, false);
    // set the column names according to the definition of the PathExpression.
    for (int colIdx = 0, idx = 0; idx < getPathLength(); idx++) {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(idx);
      String[] columns = element.getColumnNames();
      for (String col : columns) {
        rs.setColumnName(colIdx++, col);
      }
    }

    // the result column
    if (getPathLength() > 0) {
      rs.setColumnName(size - 1, "RESULT");
    }

    return rs;
  }
}

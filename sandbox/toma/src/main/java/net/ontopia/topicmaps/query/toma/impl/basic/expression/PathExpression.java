package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.Collection;
import java.util.LinkedList;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicQueryProcessor;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;

/**
 * INTERNAL: Path expression to be evaluated by the {@link BasicQueryProcessor}.
 */
public class PathExpression extends AbstractPathExpression implements
    BasicExpressionIF {

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    ResultSet rs = createNewResultSet();
    if (!isEmpty()) {
      Row row = rs.createRow();
      evaluateElement(context, PathElementIF.TYPE.NONE, rs, row, 0, 0);
    }
    return rs;
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

  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    Collection<Object> coll = new LinkedList<Object>();
    if (!isEmpty()) {
      evaluateElement(context, 0, input, coll);
    }
    return coll;
  }

  /**
   * Evaluate the PathExpression in a recursive manner, i.e. iterate over the
   * PathElement(s) and at the last element, store the results in a collection.
   * 
   * @param context the evaluation context.
   * @param index the current PathElement index.
   * @param input the input for this stage of the PathExpression.
   * @param coll the collection to store the results.
   * @throws InvalidQueryException if a PathElement could not be evaluated.
   */
  private void evaluateElement(LocalContext context, int index, Object input,
      Collection<Object> coll) throws InvalidQueryException {
    BasicPathElementIF element = (BasicPathElementIF) getPathElement(index);
    Collection<?> result = element.evaluate(context, input);
    if (result == null || result.size() == 0) {
      coll.add(null);
    } else {
      // if we are at the last element -> break condition
      if (index == (getPathLength() - 1)) {
        coll.addAll(result);
      } else {
        for (Object obj : result) {
          evaluateElement(context, index + 1, obj, coll);
        }
      }
    }
  }

  /**
   * Create a {@link ResultSet} that is suitable for this PathExpression.
   * 
   * @return an empty ResultSet
   */
  private ResultSet createNewResultSet() {
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

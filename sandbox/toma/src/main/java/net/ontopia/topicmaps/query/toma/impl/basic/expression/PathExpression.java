package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.Collection;
import java.util.LinkedList;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicPathElementIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractPathExpression;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;

/**
 * INTERNAL:
 * TODO: missing documentation
 */
public class PathExpression extends AbstractPathExpression implements
    BasicExpressionIF {

  private ResultSet createNewResultSet()
  {
    int size = 0;
    for (int idx = 0; idx < getPathLength(); idx++) {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(idx);
      size += element.getResultSize();
    }
    
    if (getPathLength() > 0) {
      size++;
    }
    
    ResultSet rs = new ResultSet(size, false);
    for (int colIdx = 0, idx = 0; idx < getPathLength(); idx++) {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(idx);
      String[] columns = element.getColumnNames();
      for (String col : columns) {
        rs.setColumnName(colIdx++, col);
      }
    }
    
    if (getPathLength() > 0) {
      rs.setColumnName(size - 1, "RESULT");
    }
    
    return rs;
  }
  
  public ResultSet evaluate(LocalContext context) {
    ResultSet rs = createNewResultSet();
    Row r = rs.createRow();
    evaluateElement(context, rs, r, 0, 0, PathElementIF.TYPE.NONE);
    return rs;
  }

  @SuppressWarnings("unchecked")
  private void evaluateElement(LocalContext context, ResultSet rs, Row row,
      int pathDepth, int rowIndex, Object input) 
  {
    if (pathDepth < getPathLength()) 
    {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(pathDepth);
      Collection<?> result = null;

      if (input != null) {
        try {
          result = element.evaluate(context, input);
        } catch (InvalidQueryException e) {
          // TODO: better error handling
          e.printStackTrace();
        }
      }

      if (result == null || result.isEmpty()) {
        for (int idx = rowIndex; idx < row.getColumnCount(); idx++) {
          row.setValue(idx, null);
        }
        rs.addRow(row);
      } else {
        for (Object o : result) {
          try {
            Row newRow = (Row) row.clone();
            if (o instanceof Collection) {
              Collection<?> coll = (Collection<?>) o;
              int idx = 0;
              Object last = null;
              for (Object obj : coll) {
                if (idx < element.getResultSize()) {
                  newRow.setValue(rowIndex + idx++, obj);
                } 
                last = obj;
              }
              evaluateElement(context, rs, newRow, pathDepth + 1, rowIndex + idx, last);
            } else {
              int newIndex = rowIndex;
              if (element.getResultSize() > 0) {
                newRow.setValue(newIndex++, o);
              }
              evaluateElement(context, rs, newRow, pathDepth + 1, newIndex, o);
            }
          } catch (Exception e) {
            // TODO: better error handling
            e.printStackTrace();
          }
        }
      }
    } else {
      if (rowIndex < row.getColumnCount()) {
        row.setValue(rowIndex, input);
      }
      rs.addRow(row);
    }
  }

  public Collection<?> evaluate(LocalContext context, Object input) {
    return evaluate(context, 0, input);
  }

  private Collection<?> evaluate(LocalContext context, int pathStart,
      Object input) {
    Collection<Object> coll = new LinkedList<Object>();
    if (pathStart >= getPathLength()) {
      coll.add(input);
    } else {
      evaluateElement(context, pathStart, input, coll);
    }
    return coll;
  }

  private void evaluateElement(LocalContext context, int index, Object input,
      Collection<Object> coll) {
    if (index < getPathLength()) {
      BasicPathElementIF element = (BasicPathElementIF) getPathElement(index);
      try {
        Collection<?> result = element.evaluate(context, input);
        if (result == null || result.size() == 0) {
          coll.add(null);
        } else {
          for (Object obj : result) {
            evaluateElement(context, index + 1, obj, coll);
          }
        }
      } catch (InvalidQueryException e) {
        // TODO: error handling
      }
    } else {
      coll.add(input);
    }
  }
}

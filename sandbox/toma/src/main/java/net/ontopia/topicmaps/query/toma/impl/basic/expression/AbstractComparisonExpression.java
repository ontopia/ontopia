package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

/**
 * INTERNAL: abstract base class for all comparison expressions.
 */
public abstract class AbstractComparisonExpression extends
    AbstractBinaryExpression {

  protected AbstractComparisonExpression(String name) {
    super(name);
  }
  
  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2) 
      return null;
    
    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);
      
    ResultSet rs1 = left.evaluate(context);
    ResultSet rs2 = right.evaluate(context);

    // FIXME: take care if the two resultsets share a column
    ResultSet rs = new ResultSet(rs1, rs2);

    for (Object row1 : rs1) {
      Object o1 = ((Row) row1).getLastValue();
      String str1 = Stringifier.toString(o1);
      for (Object row2 : rs2) {
        Object o2 = ((Row) row2).getLastValue();
        String str2 = Stringifier.toString(o2);
        if (satisfiesExpression(str1, str2)) {
          Row row3 = rs.mergeRow((Row) row1, (Row) row2);
          rs.addRow(row3);
        }
      }
    }

    context.addResultSet(rs);
    return rs;
  }

  /**
   * Checks whether the two string satisfy the expression.
   * 
   * @param s1 the first string
   * @param s2 the second string
   * @return true if the expression is satisfied, false otherwise.
   */
  protected abstract boolean satisfiesExpression(String s1, String s2);
}

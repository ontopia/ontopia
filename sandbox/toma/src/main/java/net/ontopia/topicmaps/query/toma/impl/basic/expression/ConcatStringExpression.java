package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.Iterator;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;

public class ConcatStringExpression extends AbstractExpression 
  implements BasicExpressionIF {

  public ConcatStringExpression() {
    super("||");
  }
  
  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 2) 
      return null;
    
    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);
      
    ResultSet rs1 = left.evaluate(context);
    ResultSet rs2 = right.evaluate(context);

    ResultSet rs = new ResultSet(rs1, rs2);
    String colName = rs1.getColumnName(rs1.getColumnCount() - 1) + " || " + rs2.getColumnName(rs2.getColumnCount() - 1);
    rs.addColumn(colName);
    
    Iterator<Row> it1 = rs1.iterator();
    Iterator<Row> it2 = rs2.iterator();

    Row row1 = null, row2 = null;
    while (it1.hasNext() || it2.hasNext()) {
      String s1 = "";
      String s2 = "";
      
      if (it1.hasNext()) {
        row1 = it1.next();
        s1 = Stringifier.toString(row1.getLastValue());
      }
      
      if (it2.hasNext()) {
        row2 = it2.next();
        s2 = Stringifier.toString(row2.getLastValue());
      }
      
      Row newRow = rs.mergeRow(row1, row2);
      newRow.setValue(rs.getColumnCount() - 1, s1 + s2);
      rs.addRow(newRow);
    }
    
    return rs;
  }
  
  public boolean validate() throws AntlrWrapException {
    if (getChildCount() != 2) {
      throw new AntlrWrapException(
          new InvalidQueryException("expression '" + getName()
              + "' needs to have two children."));
    }
    return true;
  }
}

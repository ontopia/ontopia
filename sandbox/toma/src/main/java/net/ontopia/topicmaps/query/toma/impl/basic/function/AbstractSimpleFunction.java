package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.lang.annotation.Inherited;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicFunctionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractFunction;

public abstract class AbstractSimpleFunction extends AbstractFunction implements
    BasicFunctionIF {
  
  public AbstractSimpleFunction(String name, int maxParameters) {
    super(name, maxParameters);
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    if (getChildCount() != 1) 
      return null;
    
    BasicExpressionIF child = (BasicExpressionIF) getChild(0);
    ResultSet rs = child.evaluate(context);
    
    for (Object r : rs) {
      Row row = (Row) r;
      row.setValue(rs.getColumnCount() - 1, evaluate(row.getLastValue()));
    }
    
    return rs;
  }

  public abstract String evaluate(Object obj);
}

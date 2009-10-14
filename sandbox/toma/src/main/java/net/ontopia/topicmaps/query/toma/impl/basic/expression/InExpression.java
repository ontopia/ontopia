package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractExpression;

/**
 * INTERNAL: IN expression, checks whether the result of an expression
 * matches any of the specified values.
 * 
 * TODO: sub-select is not yet working.  
 */
public class InExpression extends AbstractExpression implements BasicExpressionIF {
  
  public InExpression() {
    // an IN expression can have an arbitrary number of children 
    super("IN", -1);
  }

  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    // if the value list is empty, we have nothing to do, 
    // return an empty result.
    if (getChildCount() < 1) {
      return null;
    }
    
    // get the expression to be evaluated
    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    ResultSet rsLeft = left.evaluate(context);

    List<Object> allObjects = new LinkedList<Object>();
    for (int i = 1; i<getChildCount(); i++) {
      BasicExpressionIF expr = (BasicExpressionIF) getChild(i);
      ResultSet rsRight = expr.evaluate(context);
      
      // add the result column of each value in the IN list to
      // to list of all objects.
      allObjects.addAll(rsRight.getValues(rsRight.getLastIndex()));
    }

    Set<String> allStrings = new HashSet<String>();
    for (Object o : allObjects) {
      allStrings.add(Stringifier.toString(o));
    }
      
    ResultSet rs = new ResultSet(rsLeft);
    for (Object row : rsLeft) {
      Object o = ((Row) row).getLastValue();
      String str = Stringifier.toString(o);
      if (allStrings.contains(str)) {
        rs.addRow((Row) row);
      }
    }
    
    return rs;
  }
  
  public Collection<?> evaluate(LocalContext context, Object input)
      throws InvalidQueryException {
    throw new InvalidQueryException(
        "Internal error, tried to evaluate an 'IN' expression with a given input.");
  }
}

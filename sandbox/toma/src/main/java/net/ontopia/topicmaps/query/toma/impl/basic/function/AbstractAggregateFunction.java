package net.ontopia.topicmaps.query.toma.impl.basic.function;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicFunctionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractFunction;

/**
 * INTERNAL: Abstract base class for normal functions used for the
 * {@link BasicQueryProcessor}.
 */
public abstract class AbstractAggregateFunction extends AbstractFunction implements
    BasicFunctionIF {

  public AbstractAggregateFunction(String name, int maxParameters) {
    super(name, maxParameters, true);
  }
  
  /**
   * TODO: this function is currently not optimized, as it basically calculates
   * back the possible result values for a given input value.
   * 
   * need to optimize this!
   */
  public ResultSet evaluate(LocalContext context) throws InvalidQueryException {
    // all functions need to have exactly one child
    if (getChildCount() != 1)
      return null;

    // get the child and evaluate it
    BasicExpressionIF child = (BasicExpressionIF) getChild(0);
    ResultSet rs = child.evaluate(context);

    return rs;
  }
  
  /* old grouping code for aggregate functions
  private void grouping() {
    ResultSet result = new ResultSet(2, false);
    result.setColumnName(0, rs.getColumnName(0));
    result.setColumnName(1, "RESULT");
    
    Map<Object, Collection> objectMap = new HashMap<Object, Collection>();
    Iterator<Row> it = rs.iterator();
    while (it.hasNext()) {
      Row r = it.next();
      Object o = r.getFirstValue();
      if (objectMap.containsKey(o)) {
        objectMap.get(o).add(r.getLastValue());
      } else {
        Collection c = new LinkedList();
        c.add(r.getLastValue());
        objectMap.put(o, c);
      }
    }

    for (Entry<Object, Collection> item : objectMap.entrySet()) {
      Row r = result.createRow();
      r.setValue(0, item.getKey());
      r.setValue(1, evaluate(item.getValue()));
      result.addRow(r);
    }
  }*/
}

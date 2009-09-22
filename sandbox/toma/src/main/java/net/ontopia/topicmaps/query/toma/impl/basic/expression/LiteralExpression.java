package net.ontopia.topicmaps.query.toma.impl.basic.expression;

import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractLiteral;

/**
 * INTERNAL: Literal expression, transforms a literal into a resultset to be
 * used for further evaluation.
 */
public class LiteralExpression extends AbstractLiteral implements
    BasicExpressionIF {

  public LiteralExpression(String value) {
    super(value);
  }

  public ResultSet evaluate(LocalContext context) {
    ResultSet rs = new ResultSet(1, false);
    rs.setColumnName(0, "LITERAL");
    Row row = rs.createRow();
    row.setValue(0, getValue());
    rs.addRow(row);
    return rs;
  }
}
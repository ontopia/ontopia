package net.ontopia.topicmaps.query.toma.impl.basic;

import net.ontopia.topicmaps.query.toma.parser.ast.AbstractLiteral;

public class Literal extends AbstractLiteral implements BasicExpressionIF {

  public Literal(String value) {
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

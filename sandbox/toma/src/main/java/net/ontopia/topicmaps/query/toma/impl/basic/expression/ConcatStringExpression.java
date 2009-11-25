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

import java.util.Iterator;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.utils.Stringifier;

/**
 * INTERNAL: Concatenate String expression, similar to the || operator
 * in SQL queries.  
 */
public class ConcatStringExpression extends AbstractBinaryExpression {

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
    String colName = rs1.getColumnName(rs1.getColumnCount() - 1) + " || "
        + rs2.getColumnName(rs2.getColumnCount() - 1);
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
}

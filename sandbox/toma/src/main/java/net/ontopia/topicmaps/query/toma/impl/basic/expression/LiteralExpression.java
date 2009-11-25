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

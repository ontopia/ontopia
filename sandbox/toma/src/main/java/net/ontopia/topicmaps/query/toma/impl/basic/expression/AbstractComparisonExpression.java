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

import java.util.LinkedList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.impl.basic.BasicExpressionIF;
import net.ontopia.topicmaps.query.toma.impl.basic.LocalContext;
import net.ontopia.topicmaps.query.toma.impl.basic.ResultSet;
import net.ontopia.topicmaps.query.toma.impl.basic.Row;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryTracer;
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

    QueryTracer.enter(this);
    
    BasicExpressionIF left = (BasicExpressionIF) getChild(0);
    BasicExpressionIF right = (BasicExpressionIF) getChild(1);

    ResultSet rs1 = left.evaluate(context);
    ResultSet rs2 = right.evaluate(context);

    List<String> sharedColumns = rs1.getSharedColumns(rs2);
    List<Pair<Integer, Integer>> cols = new LinkedList<Pair<Integer, Integer>>();
    for (String col : sharedColumns) {
      cols.add(new Pair<Integer, Integer>(rs1.getColumnIndex(col), rs2
          .getColumnIndex(col)));
    }
    
    // TODO: improve code if the resultsets share at least one column
    ResultSet rs = new ResultSet(rs1, rs2);

    for (Row row1 : rs1) {
      Object o1 = row1.getLastValue();
      String str1 = Stringifier.toCompare(o1);
      for (Row row2 : rs2) {
        if (!checkSharedColumns(row1, row2, cols)) continue;
        
        Object o2 = row2.getLastValue();
        String str2 = Stringifier.toCompare(o2);
        if (satisfiesExpression(str1, str2)) {
          Row row3 = rs.mergeRow(row1, row2);
          rs.addRow(row3);
        }
      }
    }

    context.addResultSet(rs);
    QueryTracer.leave(rs);
    return rs;
  }

  public static class Pair<T, V> {
    public T a;
    public V b;
    
    public Pair(T a, V b) {
      this.a = a;
      this.b = b;
    }
  }

  private boolean checkSharedColumns(Row r1, Row r2,
      List<Pair<Integer, Integer>> sharedColumns) {
    if (!sharedColumns.isEmpty()) {
      for (Pair<Integer, Integer> p : sharedColumns) {
        if (!r1.getValue(p.a).equals(r2.getValue(p.b))) {
          return false;
        }
      }
      return true;
    } else {
      return true;
    }
  }

  /**
   * Checks whether the two string satisfy the expression.
   * 
   * @param s1
   *          the first string
   * @param s2
   *          the second string
   * @return true if the expression is satisfied, false otherwise.
   */
  protected abstract boolean satisfiesExpression(String s1, String s2);
}

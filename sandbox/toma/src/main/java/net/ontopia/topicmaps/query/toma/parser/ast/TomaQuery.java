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
package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.toma.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Represents a full TOMA query in the AST. A TOMA query consists of
 * an arbitrary number of {@link SelectStatement} that can be combined using set
 * operations (union, intersect, except), an ordering of columns, as well as
 * limit and offset values to further constrain the output of the query.
 */
public class TomaQuery extends AbstractExpression implements ExpressionIF {
  private ArrayList<SelectStatement> statements;
  private ArrayList<QueryOrder> orderBy;
  private int limit;
  private int offset;

  /**
   * Create a new TOMA query.
   */
  public TomaQuery() {
    super("TOMA", 0);
    statements = new ArrayList<SelectStatement>();
    orderBy = new ArrayList<QueryOrder>();
    limit = -1;
    offset = -1;
  }

  /**
   * Add a new select statement.
   * 
   * @param statement the statement to be added.
   */
  public void addStatement(SelectStatement statement) {
    statements.add(statement);
  }

  /**
   * Get the number of select statements.
   * 
   * @return the number of select statements.
   */
  public int getStatementCount() {
    return statements.size();
  }

  /**
   * Get the select statement at a certain index.
   * 
   * @param idx the specified index.
   * @return the select statement at the specified index or null if the index is
   *         not valid.
   */
  public SelectStatement getStatement(int idx) {
    try {
      return statements.get(idx);
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  /**
   * Add a column ordering to this query.
   * 
   * @param column the column to be sorted.
   * @param sorting the sort order to be used.
   */
  public void addOrderBy(int column, QueryOrder.SORT_ORDER sorting) {
    orderBy.add(new QueryOrder(column, sorting));
  }

  /**
   * Get the defined orderings for this query.
   * 
   * @return a list of all query orderings.
   */
  public List<QueryOrder> getOrderBy() {
    return orderBy;
  }

  /**
   * Get a list of all select clauses of the first select expression
   * in a TOMA query.
   * 
   * @return a list of all select clauses.
   */
  public ArrayList<ExpressionIF> getSelectExpressions() {
    ArrayList<ExpressionIF> result = new ArrayList<ExpressionIF>();
    if (statements.size() > 0) {
      SelectStatement stmt = statements.get(0);
      for (int idx = 0; idx < stmt.getSelectCount(); idx++) {
        result.add(stmt.getSelect(idx));
      }
    }
    return result;
  }

  /**
   * Get offset value for this query.
   * 
   * @return the offset value.
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Set the offset value for this query.
   * 
   * @param offset the offset value to be set.
   * @throws AntlrWrapException if a negative offset is given.
   */
  public void setOffset(int offset) throws AntlrWrapException {
    if (offset < 0) {
      throw new AntlrWrapException(new InvalidQueryException(
          "Negative offset not valid."));
    }
    this.offset = offset;
  }

  /**
   * Set the limit value for this query.
   * 
   * @param limit the limit value to be set.
   * @throws AntlrWrapException if a negative limit is given.
   */
  public void setLimit(int limit) throws AntlrWrapException {
    if (limit < 0) {
      throw new AntlrWrapException(new InvalidQueryException(
      "Negative limit not valid."));
    }
    this.limit = limit;
  }

  /**
   * Get the limit value for this query.
   * 
   * @return the limit value.
   */
  public int getLimit() {
    return limit;
  }

  /**
   * Get a string representation of the AST.
   * 
   * @return the AST in string representation.
   */
  public String getParseTree() {
    IndentedStringBuilder buf = new IndentedStringBuilder(2);
    fillParseTree(buf, 0);
    return buf.toString();
  }

  @Override
  public boolean validate() throws AntlrWrapException {
    super.validate();
    
    // validate select statements
    for (SelectStatement stmt : statements) {
      stmt.validate();
    }
    
    // check if orderBy columns are valid
    SelectStatement stmt = statements.get(0);
    int sc = stmt.getSelectCount();
    for (QueryOrder order : orderBy) {
      if (order.getColumn() < 1 || order.getColumn() > sc) {
        throw new AntlrWrapException(new InvalidQueryException(
            "order by references an invalid column"));
      }
    }
      
    return true;
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(      TOMA)", level);

    for (SelectStatement stmt : statements) {
      stmt.fillParseTree(buf, level + 1);
    }

    if (orderBy.size() > 0) {
      buf.append("(  ORDER BY)", level + 1);
      for (QueryOrder q : orderBy) {
        buf.append("(    COLUMN) [" + q.getColumn() + "]", level + 2);
        buf.append("(   SORTING) [" + q.getOrder() + "]", level + 2);
      }
    }

    if (getLimit() >= 0)
      buf.append("(     LIMIT) [" + getLimit() + "]", level + 1);
    if (getOffset() >= 0)
      buf.append("(    OFFSET) [" + getOffset() + "]", level + 1);
  }
}

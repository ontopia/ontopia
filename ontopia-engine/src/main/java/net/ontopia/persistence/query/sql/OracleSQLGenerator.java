/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.persistence.query.sql;

import java.util.List;
import java.util.Map;

/**
 * INTERNAL: Oracle SQL statement generator.
 */

public class OracleSQLGenerator extends GenericSQLGenerator {

  OracleSQLGenerator(Map properties) {
    super(properties);
  }

  public SQLStatementIF createSQLStatement(SQLQuery query) {
    // NOTE: the following does not work, since ROWNUM > 1 always fails
    
    // // Use ROWNUM expression when query has limit/offset
    // SQLExpressionIF filter = query.getFilter();
    // int limit = query.getLimit();
    // int offset = query.getOffset();
    // 
    // if (limit > 0 && offset > 0) {
    //   SQLVerbatimExpression ve_offset = new SQLVerbatimExpression("ROWNUM > " + offset);
    //   SQLVerbatimExpression ve_limit = new SQLVerbatimExpression("ROWNUM <= " + (offset + limit));
    //   query.setFilter(new SQLAnd(filter, ve_offset, ve_limit));
    // } else if (limit > 0) {
    //   SQLVerbatimExpression ve_limit = new SQLVerbatimExpression("ROWNUM <= " + limit);
    //   query.setFilter(new SQLAnd(filter, ve_limit));
    // } else if (offset > 0) {
    //   SQLVerbatimExpression ve_offset = new SQLVerbatimExpression("ROWNUM > " + offset);
    //   query.setFilter(new SQLAnd(filter, ve_offset));
    // }
    
    return super.createSQLStatement(query);
  }

  protected String createStatement(SQLExpressionIF filter, List selects, boolean distinct, int offset, int limit, 
                                   List orderby, boolean issetquery, BuildInfo info) {
    String sql = super.createStatement(filter, selects, distinct, offset, limit, orderby, issetquery, info);

    // LIMIT x OFFSET y clause
    if (limit > 0 && offset > 0) {
      StringBuilder sb = new StringBuilder("select * from ( select a.*, rownum rnum from (");
      sb.append(sql);
      sb.append(") a where rownum <= ").append(offset+limit); // max
      sb.append(") where rnum >= ").append(offset+1); // min
      return sb.toString();

    } else if (limit > 0) {
      StringBuilder sb = new StringBuilder("select a.* from (");
      sb.append(sql);
      sb.append(") a where rownum <= ").append(limit); // max
      return sb.toString();

    } else if (offset > 0) {
      StringBuilder sb = new StringBuilder("select * from ( select a.*, rownum rnum from (");
      sb.append(sql);
      sb.append(") a ) where rnum >= ").append(offset+1); // min
      return sb.toString();
      
    } else {
      return sql;
    }
    
  }
  
  protected void fromSQLLeftOuterJoin(SQLJoin join, BuildInfo info) {
    // Do nothing, since it is being specified in the where clause
  }
  
  protected void fromSQLRightOuterJoin(SQLJoin join, BuildInfo info) {
    // Do nothing, since it is being specified in the where clause
  }
  
  protected void whereSQLLeftOuterJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    whereSQLLeftOuterJoin_ORACLE(join, sql, info);
  }
  
  protected void whereSQLRightOuterJoin(SQLJoin join, StringBuilder sql, BuildInfo info) {
    whereSQLRightOuterJoin_ORACLE(join, sql, info);
  }

  protected String getSetOperator(int operator) {
    if (operator == SQLSetOperation.EXCEPT)
      return "minus";
    if (operator == SQLSetOperation.EXCEPT_ALL)
      return "minus all";
    else
      return super.getSetOperator(operator);
  }
  
  protected StringBuilder createOffsetLimitClause(int offset, int limit, BuildInfo info) {    
    // no-op, since oracle uses ROWNUM for this. see elsewhere.
    return null;
  }

  public boolean supportsLimitOffset() {
    return true;
  }

  protected void whereSQLFalse(SQLFalse expr, StringBuilder sql, BuildInfo info) {
    sql.append("1 = 2");
  }
  
}

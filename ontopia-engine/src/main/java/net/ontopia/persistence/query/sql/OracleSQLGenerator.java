
// $Id: OracleSQLGenerator.java,v 1.11 2005/07/12 09:37:40 grove Exp $

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
      StringBuffer sb = new StringBuffer("select * from ( select a.*, rownum rnum from (");
      sb.append(sql);
      sb.append(") a where rownum <= ").append(offset+limit); // max
      sb.append(") where rnum >= ").append(offset+1); // min
      return sb.toString();

    } else if (limit > 0) {
      StringBuffer sb = new StringBuffer("select a.* from (");
      sb.append(sql);
      sb.append(") a where rownum <= ").append(limit); // max
      return sb.toString();

    } else if (offset > 0) {
      StringBuffer sb = new StringBuffer("select * from ( select a.*, rownum rnum from (");
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
  
  protected void whereSQLLeftOuterJoin(SQLJoin join, StringBuffer sql, BuildInfo info) {
    whereSQLLeftOuterJoin_ORACLE(join, sql, info);
  }
  
  protected void whereSQLRightOuterJoin(SQLJoin join, StringBuffer sql, BuildInfo info) {
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
  
  protected StringBuffer createOffsetLimitClause(int offset, int limit, BuildInfo info) {    
    // no-op, since oracle uses ROWNUM for this. see elsewhere.
    return null;
  }

  public boolean supportsLimitOffset() {
    return true;
  }

  protected void whereSQLFalse(SQLFalse expr, StringBuffer sql, BuildInfo info) {
    sql.append("1 = 2");
  }
  
}

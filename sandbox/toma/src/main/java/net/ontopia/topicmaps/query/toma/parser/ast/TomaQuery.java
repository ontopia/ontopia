package net.ontopia.topicmaps.query.toma.parser.ast;

import java.util.ArrayList;
import java.util.List;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL:
 */
public class TomaQuery extends AbstractExpression implements ExpressionIF
{
  private ArrayList<SelectStatement> statements;
  private ArrayList<QueryOrder> orderBy;
  private int limit;
  private int offset;

  public TomaQuery() {
    super("TOMA");
    statements = new ArrayList<SelectStatement>();
    orderBy = new ArrayList<QueryOrder>();
    limit = -1;
    offset = -1;
  }

  public void addStatement(SelectStatement statement) {
    statements.add(statement);
  }

  public int getStatementCount() {
    return statements.size();
  }

  public SelectStatement getStatement(int idx) {
    return statements.get(idx);
  }

  public void addOrderBy(int column, QueryOrder.SORT_ORDER sorting) {
    orderBy.add(new QueryOrder(column, sorting));
  }
  
  public List<String> getOrderByVariables() {
    List<String> result = new ArrayList<String>();
    if (statements.size() > 0) {
      SelectStatement stmt = statements.get(0);
      
      for (QueryOrder order : orderBy) {
        int column = order.getColumn();
        ExpressionIF expr = stmt.getSelect(column);
        result.add(expr.toString());
      }
    }
    return result;
  }

  public List<String> getSelectedVariables() {
    List<String> result = new ArrayList<String>();
    if (statements.size() > 0) {
      SelectStatement stmt = statements.get(0);
      for (int idx=0; idx<stmt.getSelectCount(); idx++) {
        ExpressionIF expr = stmt.getSelect(idx);
        //result.add(expr.getRoot().toString());
      }
    }
    return result;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getLimit() {
    return limit;
  }

  public String getParseTree() {
    IndentedStringBuilder buf = new IndentedStringBuilder(2);
    fillParseTree(buf, 0);
    return buf.toString();
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(      TOMA)", level);

    for (SelectStatement stmt : statements)
    {
      stmt.fillParseTree(buf, level+1);
    }

    if (orderBy.size() > 0)
    {
      buf.append("(  ORDER BY)", level+1);
      for (QueryOrder q : orderBy)
      {
        buf.append("(    COLUMN) [" + q.getColumn() + "]", level+2);
        buf.append("(   SORTING) [" + q.getOrder() + "]", level+2);
      }
    }

    if (getLimit() >= 0)
      buf.append("(     LIMIT) [" + getLimit() + "]", level+1);
    if (getOffset() >= 0)
      buf.append("(    OFFSET) [" + getOffset() + "]", level+1);
  }
}

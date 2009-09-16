package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL:
 */
public class QueryOrder 
{
  public enum SORT_ORDER
  {
    ASC,
    DESC
  };
  
  private int column;
  private SORT_ORDER order;

  public QueryOrder(int column) {
    this(column, SORT_ORDER.ASC);
  }
  
  public QueryOrder(int column, SORT_ORDER order) {
    this.column = column;
    this.order = order;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public SORT_ORDER getOrder() {
    return order;
  }

  public void setOrder(SORT_ORDER order) {
    this.order = order;
  }
}

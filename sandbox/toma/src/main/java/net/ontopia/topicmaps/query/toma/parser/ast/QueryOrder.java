package net.ontopia.topicmaps.query.toma.parser.ast;

/**
 * INTERNAL: Represents the query order of a specific column in TOMA query.
 * 
 * Two different {@link SORT_ORDER} are defined:
 * <ul>
 * <li>ASC - ascending
 * <li>DESC - descending
 * </ul>
 * 
 * The default sort order is ASC.
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

  /**
   * Create a default ordering for a specific column.
   * @param column the column to be sorted.
   */
  public QueryOrder(int column) {
    this(column, SORT_ORDER.ASC);
  }
  
  /**
   * Create a specific ordering fo a column.
   * @param column the column to be sorted.
   * @param order the ordering to be used.
   */
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

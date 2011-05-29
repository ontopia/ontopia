
package net.ontopia.persistence.query.sql;


/**
 * INTERNAL: SQL order by statement. The order-by instance wraps a
 * SQLValueIF or SQLAggregateIF instance and specifies whether the
 * ordering should be ascending or descending.
 */

public class SQLOrderBy {

  public static final int ASCENDING = 1;
  public static final int DESCENDING = 2;

  protected SQLAggregateIF aggregate;
  protected SQLValueIF value;
  protected int order;
  
  public SQLOrderBy(SQLValueIF value, int order) {
    this.value = value;
    this.order = order;
  }
  
  public SQLOrderBy(SQLAggregateIF aggregate, int order) {
    this.aggregate = aggregate;
    this.order = order;
  }
  
  public int getOrder() {
    return order;
  }

  public boolean isAggregate() {
    return (aggregate != null);
  }

  public SQLAggregateIF getAggregate() {
    return aggregate;
  }

  public void setAggregate(SQLAggregateIF aggregate) {
    this.aggregate = aggregate;
  }
  
  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
  }

  public String toString() {
    if (aggregate == null)
      return value + (order == ASCENDING ? " asc" : " desc");
    else 
      return aggregate + (order == ASCENDING ? " asc" : " desc");
  }
  
}

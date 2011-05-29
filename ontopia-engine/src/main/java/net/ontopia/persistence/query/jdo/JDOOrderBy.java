
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: JDOQL order by statement. The order-by instance wraps a
 * JDOValueIF or JDOAggregateIF instance and specifies whether the
 * ordering should be ascending or descending.
 */

public class JDOOrderBy {

  public static final int ASCENDING = 1;
  public static final int DESCENDING = 2;

  protected JDOAggregateIF aggregate;
  protected JDOValueIF value;
  protected int order;
  
  public JDOOrderBy(JDOValueIF value, int order) {
    this.value = value;
    this.order = order;
  }
  
  public JDOOrderBy(JDOAggregateIF aggregate, int order) {
    this.aggregate = aggregate;
    this.order = order;
  }
  
  public int getOrder() {
    return order;
  }

  public boolean isAggregate() {
    return (aggregate != null);
  }

  public JDOAggregateIF getAggregate() {
    return aggregate;
  }
  
  public JDOValueIF getValue() {
    return value;
  }

  public int hashCode() {
    if (isAggregate())      
      return aggregate.hashCode() + order;
    else
      return value.hashCode() + order;
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOOrderBy) {
      JDOOrderBy other = (JDOOrderBy)obj;
      if (isAggregate()) {
        if (!other.isAggregate())
          return false;
        return (aggregate.equals(other.aggregate)  &&
                order == other.order);
      } else {
        if (other.isAggregate())
          return false;
        return (value.equals(other.value)  &&
                order == other.order);        
      }
    }
    return false;
  }

  public String toString() {
    if (aggregate == null)
      return value + (order == ASCENDING ? " ascending" : " descending");
    else 
      return aggregate + (order == ASCENDING ? " ascending" : " descending");
  }
  
}






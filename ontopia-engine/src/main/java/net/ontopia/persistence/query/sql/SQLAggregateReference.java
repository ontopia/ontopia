// $Id: SQLAggregateReference.java,v 1.3 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;


/**
 * INTERNAL: Represents a set of columns from a given table. The
 * columns are grouped for a reason. Columns are often used as join
 * criteria.
 */

public class SQLAggregateReference implements SQLAggregateIF {

  protected String alias;
  protected SQLAggregateIF refagg;
 
  public SQLAggregateReference(SQLAggregateIF refagg) {
    if (refagg == null) throw new IllegalArgumentException("Aggregate cannot be null.");
    this.refagg = refagg;
  }
  
  public int getType() {
    return refagg.getType();
  }

  public SQLValueIF getValue() {
    return refagg.getValue();
  }

  public void setValue(SQLValueIF value) {
    refagg.setValue(value);
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isReference() {
    return true;
  }
  
  public SQLAggregateIF getReference() {
    return refagg;
  }

  public int hashCode() {
    return refagg.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLAggregateReference) {
      SQLAggregateReference other = (SQLAggregateReference)obj;
      if (refagg.equals(other.getReference()))
        return true;
    }
    return false;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("ref:");
    sb.append("(");
    sb.append(refagg);
    sb.append(")");
    return sb.toString();
  }
    
}

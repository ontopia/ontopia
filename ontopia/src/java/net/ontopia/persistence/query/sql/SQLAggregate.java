// $Id: SQLAggregate.java,v 1.5 2004/05/21 12:29:18 grove Exp $

package net.ontopia.persistence.query.sql;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents an aggregate function in a SQL query.
 */

public class SQLAggregate implements SQLAggregateIF {

  protected int type;
  protected SQLValueIF value;
  protected String alias;
  
  public SQLAggregate(SQLValueIF value, int type) {
    if (value == null)
      throw new NullPointerException("Aggregate function variable cannot not be null.");
    this.value = value;
    this.type = type;
  }
  
  public int getType() {
    return type;
  }

  public SQLValueIF getValue() {
    return value;
  }

  public void setValue(SQLValueIF value) {
    this.value = value;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public boolean isReference() {
    return false;
  }
  
  public SQLAggregateIF getReference() {
    throw new UnsupportedOperationException("SQLAggregateIF is not a reference, so this method should not be called.");
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    switch (type) {
    case COUNT:
      sb.append("count");
      break;
    default:
      throw new OntopiaRuntimeException("Unknown aggregate function type: " + type);
    }
    sb.append("(");
    sb.append(value);
    sb.append(")");
    return sb.toString();
  }
  
}






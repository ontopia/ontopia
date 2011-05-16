// $Id: JDOAggregate.java,v 1.5 2002/06/27 15:43:47 grove Exp $

package net.ontopia.persistence.query.jdo;

import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: JDOQL aggregate function.
 */

public class JDOAggregate implements JDOAggregateIF {

  protected int type;
  protected JDOValueIF value;

  public JDOAggregate(JDOValueIF value, int type) {
    if (value == null)
      throw new NullPointerException("Aggregate function variable cannot not be null.");
    this.value = value;
    this.type = type;
  }
  
  public int getType() {
    return type;
  }
  
  public JDOValueIF getValue() {
    return value;
  }

  public int hashCode() {
    return value.hashCode() + type;
  }

  public boolean equals(Object obj) {
    if (obj instanceof JDOAggregateIF) {
      JDOAggregateIF other = (JDOAggregateIF)obj;    
      return (type == other.getType() ||
              value.equals(other.getValue()));
    }
    return false;
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







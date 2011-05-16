// $Id: SQLTuple.java,v 1.20 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import java.util.Arrays;
import java.util.List;

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: SQL value: tuple. Represents a list of nested SQL
 * values. The arity is the same as the total arity of its nested
 * values.<p>
 */

public class SQLTuple implements SQLValueIF {
    
  protected SQLValueIF[] values;
  protected int arity;
  protected int value_arity;
  protected String alias;

  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLTuple(List values) {
    this((SQLValueIF[])values.toArray(new SQLValueIF[values.size()]));
  }
  
  public SQLTuple(SQLValueIF[] values) {
    if (values == null)
      throw new IllegalArgumentException("Tuples values cannot be null.");    
    this.values = values;
    // Compute arity
    // TODO: Should this rather be done on demand instead?
    for (int i=0; i < values.length; i++) {
      arity = arity + values[i].getArity();
      value_arity = value_arity + values[i].getValueArity();
    }
  }

  public int getType() {
    return TUPLE;
  }
  
  public int getArity() {
    return arity;
  }

  public int getValueArity() {
    return value_arity;
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
  
  public SQLValueIF getReference() {
    throw new UnsupportedOperationException("SQLValueIF is not a reference, so this method should not be called.");
  }

  public SQLValueIF[] getValues() {
    return values;
  }

  public void setValues(SQLValueIF[] values) {
    this.values = values;
  }

  public Class getValueType() {    
    return vtype;
  }

  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  public FieldHandlerIF getFieldHandler() {
    return fhandler;
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  public boolean equals(Object obj) {
    if (obj instanceof SQLTuple) {
      SQLTuple other = (SQLTuple)obj;
      if (Arrays.equals(values, other.getValues()))
        return true;
    }
    return false;
  }

  public String toString() {    
    StringBuffer sb = new StringBuffer();
    sb.append("tuple:").append(arity).append(":(");
    for (int i=0; i < values.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(values[i]);
    }
    sb.append(')');
    return sb.toString();
  }
  
}

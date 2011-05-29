
package net.ontopia.persistence.query.sql;

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: SQL value: parameter. Represents a parameter in a
 * query. A parameter has an arity of one or more.
 */

public class SQLParameter implements SQLValueIF {

  protected String name;
  protected int arity;
  protected String alias;
  
  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLParameter(String name, int arity) {
    if (name == null)
      throw new NullPointerException("A SQL parameter must have a name.");
    if (arity < 1)
      throw new IllegalArgumentException("The arity of a SQL parameter must be 1 or more; " + arity + " specified.");
    this.name = name;
    this.arity = arity;
  }

  public int getType() {
    return PARAMETER;
  }

  public int getArity() {
    return arity;
  }

  public int getValueArity() {
    return 1;
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
  
  public String getName() {
    return name;
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

  public int hashCode() {
    return name.hashCode();
  }

  public boolean equals(Object obj) {
    if (obj instanceof SQLParameter) {
      SQLParameter other = (SQLParameter)obj;    
      return (name.equals(other.getName()));
    }
    return false;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i < arity; i++) {
      if (i > 1) sb.append(", ");
      sb.append("?");
      sb.append(getName());
    }
    return sb.toString();
  }
  
}

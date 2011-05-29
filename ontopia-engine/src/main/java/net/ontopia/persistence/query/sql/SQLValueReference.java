
package net.ontopia.persistence.query.sql;

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: A reference to another SQLValueIF. Note that the
 * SQLValueIF must be referenceable (e.g. have an alias).
 */

public class SQLValueReference implements SQLValueIF {

  protected SQLValueIF refvalue;
  protected String alias;
  
  public SQLValueReference(SQLValueIF refvalue) {
    if (refvalue == null) throw new IllegalArgumentException("Referenced SQLValueIF cannot be null.");
    this.refvalue = refvalue;
  }
  
  public int getType() {
    return refvalue.getType();
  }

  public int getArity() {
    return refvalue.getArity();
  }

  public int getValueArity() {
    return refvalue.getArity();
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
  
  public SQLValueIF getReference() {
    return refvalue;
  }

  public Class getValueType() {    
    return refvalue.getValueType();
  }

  public void setValueType(Class vtype) {
    refvalue.setValueType(vtype);
  }

  public FieldHandlerIF getFieldHandler() {
    return refvalue.getFieldHandler();
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    refvalue.setFieldHandler(fhandler);
  }
  
  public int hashCode() {
    return refvalue.hashCode();
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLValueReference) {
      SQLValueReference other = (SQLValueReference)obj;
      if (refvalue.equals(other.getReference()))
        return true;
    }
    return false;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("ref:");
    sb.append("(");
    sb.append(refvalue);
    sb.append(")");
    return sb.toString();
  }
    
}


package net.ontopia.persistence.query.sql;

import java.sql.Types;

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;
import net.ontopia.persistence.proxy.SQLTypes;

/**
 * INTERNAL: SQL value: primitive. Represents a primitive value of one
 * of the standard SQL types. A primitive always has an arity of 1.<p>
 
 * <b>Warning:</b> Null should be represented using the SQLNull
 * class.<p>
 *
 * @see java.sql.Types
 */

public class SQLPrimitive implements SQLValueIF {
    
  protected Object value;
  protected int sql_type;
  protected String alias;

  protected Class vtype;
  protected FieldHandlerIF fhandler;

  public SQLPrimitive(Object value, int sql_type) {
    if (value == null)
      throw new IllegalArgumentException("Primitive value cannot be null (SQL type: " + sql_type  + ").");
    
    this.sql_type = sql_type;
    this.value = value;
  }

  public int getType() {
    return PRIMITIVE;
  }
  
  public int getArity() {
    return 1;
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
  
  public int getSQLType() {
    return sql_type;
  }

  public Object getValue() {
    return value;
  }

  public Class getValueType() {    
    return (vtype == null ? SQLTypes.getType(sql_type) : vtype);
  }

  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  public FieldHandlerIF getFieldHandler() {
    return (fhandler == null ? new DefaultFieldHandler(sql_type) : fhandler);
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  public String toString() {
    switch (getSQLType()) {
    case Types.VARCHAR:
      return "'" + getValue() + "'";
    default:
      return getValue().toString();
    }
  }
  
}

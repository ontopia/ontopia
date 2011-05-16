
// $Id: SQLNull.java,v 1.10 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.sql;

import net.ontopia.persistence.proxy.DefaultFieldHandler;
import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: SQL value: null. Represents the SQL null value.<p>
 */

public class SQLNull implements SQLValueIF {

  protected String alias;

  protected Class vtype;
  protected FieldHandlerIF fhandler;
  
  public SQLNull() {
  }

  public int getType() {
    return NULL;
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

  public Class getValueType() {    
    return vtype;
  }

  public void setValueType(Class vtype) {
    this.vtype = vtype;
  }

  public FieldHandlerIF getFieldHandler() {
    return (fhandler == null ? new DefaultFieldHandler(java.sql.Types.NULL) : fhandler);
  }

  public void setFieldHandler(FieldHandlerIF fhandler) {
    this.fhandler = fhandler;
  }

  public int hashCode() {
    return 123; // Just some random number
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof SQLNull)
      return true;
    else
      return false;
  }
  
  public String toString() {
    return "null";
  }
  
}

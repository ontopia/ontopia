
package net.ontopia.persistence.query.jdo;

import net.ontopia.persistence.proxy.PersistentIF;

/**
 * INTERNAL: JDOQL value: object. Class used to represent object
 * instances recognized by the storage system, e.g. PersistentIFs.
 */

public class JDOObject implements JDOValueIF {

  protected Object value;
  
  public JDOObject(Object value) {
    if (value == null)
      throw new IllegalArgumentException("Object value cannot be null.");
    
    this.value = value;
  }

  public int getType() {
    return OBJECT;
  }

  public Class getValueType() {
    if (value instanceof PersistentIF)
      return (Class)((PersistentIF)value)._p_getIdentity().getType();
    else
      return value.getClass();
  }

  public Object getValue() {
    return value;
  }
  
  public int hashCode() {
    return value.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOObject) {
      JDOObject other = (JDOObject)obj;    
      return value.equals(other.value);
    }
    return false;
  }

  public String toString() {
    return value.toString();
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}







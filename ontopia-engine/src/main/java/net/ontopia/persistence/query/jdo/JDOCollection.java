
package net.ontopia.persistence.query.jdo;

import java.util.Collection;

/**
 * INTERNAL: JDOQL value: collection. Class used to represent
 * collections of object instances.
 */

public class JDOCollection implements JDOValueIF {

  protected Collection coll;
  protected Class eltype;
  
  public JDOCollection(Collection coll, Class eltype) {
    if (coll == null)
      throw new IllegalArgumentException("Collection cannot be null.");
    if (eltype == null)
      throw new IllegalArgumentException("Element type cannot be null.");
    
    this.coll = coll;
    this.eltype = eltype;
  }

  public int getType() {
    return COLLECTION;
  }

  public Class getValueType() {
    return coll.getClass();
  }

  public Class getElementType() {
    return eltype;
  }

  public Collection getValue() {
    return coll;
  }
  
  public int hashCode() {
    return coll.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj instanceof JDOCollection) {
      JDOCollection other = (JDOCollection)obj;    
      return coll.equals(other.coll);
    }
    return false;
  }

  public String toString() {
    return coll.toString();
  }

  public void visit(JDOVisitorIF visitor) {
  }
  
}

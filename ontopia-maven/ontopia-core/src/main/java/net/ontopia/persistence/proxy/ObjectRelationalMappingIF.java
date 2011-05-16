// $Id: ObjectRelationalMappingIF.java,v 1.2 2002/11/29 20:50:18 grove Exp $

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: An object relational mapping wrapper class used by the
 * RDBMS proxy implementation.
 */

public interface ObjectRelationalMappingIF {

  /**
   * INTERNAL: Get the class info by object type.
   */  
  public ClassInfoIF getClassInfo(Object type);

  /**
   * INTERNAL: Returns true if the object type has a class descriptor.
   */  
  public boolean isDeclared(Object type);
  
}






// $Id: JDOValueIF.java,v 1.14 2004/06/08 09:21:32 grove Exp $

package net.ontopia.persistence.query.jdo;

/**
 * INTERNAL: Represents a value in a JDO query. A value will return
 * the value it represents when evaluated.
 */

public interface JDOValueIF {

  /**
   * INTERNAL: Constant referring to the {@link JDONull} class.
   */
  public static final int NULL = 0;

  /**
   * INTERNAL: Constant referring to the {@link JDOField} class.
   */
  public static final int FIELD = 1;
  /**
   * INTERNAL: Constant referring to the {@link JDOVariable} class.
   */
  public static final int VARIABLE = 2;
  /**
   * INTERNAL: Constant referring to the {@link JDOParameter} class.
   */
  public static final int PARAMETER = 3;
  /**
   * INTERNAL: Constant referring to the {@link JDOPrimitive} class.
   */
  public static final int PRIMITIVE = 4;
  /**
   * INTERNAL: Constant referring to the {@link JDOObject} class.
   */
  public static final int OBJECT = 5;
  /**
   * INTERNAL: Constant referring to the {@link JDOCollection} class.
   */
  public static final int COLLECTION = 6;
  
  /**
   * INTERNAL: Constant referring to the {@link JDOString} class.
   */
  public static final int STRING = 7;

  /**
   * INTERNAL: Constant referring to the {@link JDONativeValue} class.
   */
  public static final int NATIVE_VALUE = 8;
  /**
   * INTERNAL: Constant referring to the {@link JDOFunction} class.
   */
  public static final int FUNCTION = 9;

  /**
   * INTERNAL: Returns the type of JDO value indicated by one of the
   * constants in the {@link JDOValueIF} interface.
   */
  public int getType();

  /**
   * INTERNAL: Allows the value to be visited. This method is used for
   * retrieval of nested data in expressions.
   */
  public void visit(JDOVisitorIF visitor);
  
}

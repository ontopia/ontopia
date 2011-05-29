
package net.ontopia.persistence.query.jdo;


/**
 * INTERNAL: Represents an expression in a JDO query. An expression
 * will return a boolean value when evaluated.
 */

public interface JDOExpressionIF {

  // Boolean expressions

  /**
   * INTERNAL: Constant referring to the {@link JDOAnd} class.
   */
  public static final int AND = 1;
  /**
   * INTERNAL: Constant referring to the {@link JDOOr} class.
   */
  public static final int OR = 2;
  /**
   * INTERNAL: Constant referring to the {@link JDONot} class.
   */
  public static final int NOT = 3;
  /**
   * INTERNAL: Constant referring to the {@link JDOBoolean} class.
   */
  public static final int BOOLEAN = 4;
  /**
   * INTERNAL: Constant referring to the {@link JDOValueExpression} class.
   */
  public static final int VALUE_EXPRESSION = 5;

  // Operators

  /**
   * INTERNAL: Constant referring to the {@link JDOEquals} class.
   */
  public static final int EQUALS = 101;
  /**
   * INTERNAL: Constant referring to the {@link JDONotEquals} class.
   */
  public static final int NOT_EQUALS = 102;

  // Collection methods

  /**
   * INTERNAL: Constant referring to the {@link JDOContains} class.
   */
  public static final int CONTAINS = 110;
  /**
   * INTERNAL: Constant referring to the {@link JDOIsEmpty} class.
   */
  public static final int IS_EMPTY = 111;

  // String methods

  /**
   * INTERNAL: Constant referring to the {@link JDOStartsWith} class.
   */
  public static final int STARTS_WITH = 201;
  /**
   * INTERNAL: Constant referring to the {@link JDOEndsWith} class.
   */
  public static final int ENDS_WITH = 202;
  /**
   * INTERNAL: Constant referring to the {@link JDOLike} class.
   */
  public static final int LIKE = 203;

  // Set operations

  /**
   * INTERNAL: Constant referring to the {@link JDOSetOperation} class.
   */
  public static final int SET_OPERATION = 501;
  
  /**
   * INTERNAL: Returns the type of JDO expression indicated by one of
   * the constants in the {@link JDOExpressionIF} interface.
   */
  public int getType();

  /**
   * INTERNAL: Allows the value to be visited. This method is used for
   * retrieval of nested data in expressions.
   */
  public void visit(JDOVisitorIF visitor);
  
  //! /**
  //!  * INTERNAL: Returns the nested expression if any. If no subexpression
  //!  * null is returned.
  //!  */
  //! public JDOExpressionIF[] getNested();

  //! public int getValueArity();
  //! public JDOValueIF[] getValues();
  
}







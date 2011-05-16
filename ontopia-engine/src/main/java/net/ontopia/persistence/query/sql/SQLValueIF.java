// $Id: SQLValueIF.java,v 1.12 2004/06/08 09:21:33 grove Exp $

package net.ontopia.persistence.query.sql;

import net.ontopia.persistence.proxy.FieldHandlerIF;

/**
 * INTERNAL: Represents a SQL value.
 */

public interface SQLValueIF {

  /**
   * INTERNAL: Constant referring to the {@link SQLNull} class.
   */
  public static final int NULL = 0;
  
  /**
   * INTERNAL: Constant referring to the {@link SQLTuple} class.
   */
  public static final int TUPLE = 1;
  /**
   * INTERNAL: Constant referring to the {@link SQLColumns} class.
   */
  public static final int COLUMNS = 2;
  /**
   * INTERNAL: Constant referring to the {@link SQLPrimitive} class.
   */
  public static final int PRIMITIVE = 3;
  /**
   * INTERNAL: Constant referring to the {@link SQLParameter} class.
   */
  public static final int PARAMETER = 4;
  /**
   * INTERNAL: Constant referring to the {@link SQLVerbatim} class.
   */
  public static final int VERBATIM = 5;
  /**
   * INTERNAL: Constant referring to the {@link SQLFunction} class.
   */
  public static final int FUNCTION = 6;

  /**
   * INTERNAL: Returns the value class type. The type is represented
   * by one of the constants in the {@link SQLValueIF} interface.
   */
  public int getType();

  /**
   * INTERNAL: Returns the [column] arity of the value. The number
   * represents the number of "columns" the value spans, i.e. its
   * composite width.
   */
  public int getArity();

  /**
   * INTERNAL: Returns the value arity of the value. This number
   * refers to the number of nested values this value contains
   * including itself. Most values therefore have a value arity of
   * 1. Nested values may have an arity higher than 1. SQLTuple is
   * currently the only nested value type.
   */
  public int getValueArity();

  /**
   * INTERNAL: The <i>column</i> alias to use if this value is
   * included in the projection. The SQL select syntax is typically
   * like "select value as <calias> from foo".
   */
  public String getAlias();

  /**
   * INTERNAL: Sets the column alias.
   */
  public void setAlias(String alias);

  /**
   * INTERNAL: Returns true if this value is a reference to another.
   */
  public boolean isReference();

  /**
   * INTERNAL: Returns the referenced value if one exists.
   */
  public SQLValueIF getReference();

  /**
   * INTERNAL: Returns the value type.
   */
  public Class getValueType();

  /**
   * INTERNAL: Sets the value type.
   */
  public void setValueType(Class vtype);

  /**
   * INTERNAL: Returns the field handler for the columns.
   */
  public FieldHandlerIF getFieldHandler();

  /**
   * INTERNAL: Sets the field handler for the value.
   */
  public void setFieldHandler(FieldHandlerIF fhandler);
  
}

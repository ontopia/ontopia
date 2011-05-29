
package net.ontopia.topicmaps.webed.impl.basic;

/**
 * INTERNAL: Container for storing information about an input field used for
 * displaying an form as part of the user interface.
 */
public interface FieldInformationIF {

  /**
   * INTERNAL: Gets the name of the input field.
   */
  public String getName();

  /**
   * INTERNAL: Gets the type of the input field. Allowed values are
   * "text" (default) and "textarea".
   */
  public String getType();

  /**
   * INTERNAL: Gets the maximum number of characters allowed for this
   * field to be typed in by the user.
   */
  public String getMaxLength();

  /**
   * INTERNAL: Gets the number of character columns for this field.
   */
  public String getColumns();

  /**
   * INTERNAL: Gets the number of rows for this field.
   */
  public String getRows();

}






// $Id: FieldInfoIF.java,v 1.20 2008/12/04 11:26:01 lars.garshol Exp $

package net.ontopia.persistence.proxy;


/**
 * INTERNAL: A field descriptor-like class that is used by the RDBMS
 * proxy implementation to access the information it needs about the
 * object relational field descriptor in an optimized manner.<p>
 *
 * A field info is also able to perform the read and write operations
 * required by field handlers. In addition it contains an optimized
 * representation of the related field descriptor.
 *
 * The field info implementations also knows how to interact with the
 * data repository using the FieldHandlerIF interface.<p>
 */

public interface FieldInfoIF extends FieldHandlerIF {

  /**
   * Flag indicating that the field represents a 1:1 relationship.
   */
  public static final int ONE_TO_ONE = 1;

  /**
   * Flag indicating that the field represents a 1:M relationship.
   */

  public static final int ONE_TO_MANY = 2;

  /**
   * Flag indicating that the field represents a M:M relationship.
   */
  public static final int MANY_TO_MANY = 3;

  /**
   * INTERNAL: Gets the field name.
   */
  public String getName();

  /**
   * INTERNAL: Gets the value field index of this field. This is the
   * id (index) used by transactions and persistent instances to refer
   * to this particular object field.
   */
  public int getIndex();

  /**
   * INTERNAL: Gets the field cardinality.
   */
  public int getCardinality();

  /**
   * INTERNAL: Returns true if this field is read-only field.
   */
  public boolean isReadOnly();

  /**
   * INTERNAL: Returns true if the field is a collection field (has a
   * cardinality of 1:1 or 1:M).
   */
  public boolean isCollectionField();

  //! NOTE: This method is now part of the FieldHandlerIF interface.
  //! /**
  //!  * INTERNAL: Returns true if the field references an object identity
  //!  * field.
  //!  */
  //! public boolean isIdentityField();

  /**
   * INTERNAL: Returns true if the field is a primitive field. Field
   * value must be of primitive type.
   */
  public boolean isPrimitiveField();
  
  /**
   * INTERNAL: Returns true if the field is a reference field. Field
   * value must be of identifiable type.
   */
  public boolean isReferenceField();

  /**
   * INTERNAL: Returns true if the field is an aggregate field. Field
   * value must be of aggregate type.
   */
  public boolean isAggregateField();
  
  /**
   * INTERNAL: Gets the class info for the field's object type.
   */
  public ClassInfoIF getParentClassInfo();

  /**
   * INTERNAL: Gets the class info for the field's value type. Note
   * that primitive value classes don't have a class info.
   */
  public ClassInfoIF getValueClassInfo();

  /**
   * INTERNAL: Gets the field value class. For primitive fields the
   * primitive wrapper class is returned.
   */
  public Class getValueClass();
  
  /**
   * INTERNAL: Gets the table in which the field value is stored (aka
   * the master table).
   */
  public String getTable();

  /**
   * INTERNAL: Returns the number of columns that the field spans.
   */
  //! public int getColumnCount();
  
  /**
   * INTERNAL: Returns the names of the columns that the field spans.
   */
  public String[] getValueColumns();

  /**
   * INTERNAL: Gets the field value from the given object.
   */
  public Object getValue(Object object) throws Exception;

  /**
   * INTERNAL: Sets the field value for the given object.
   */
  public void setValue(Object object, Object value) throws Exception;

  /// -- Copied from FieldDescriptor:
  
  /**
   * INTERNAL: Gets the name of the table which needs to be joined to
   * order to access the field value from the master table.
   */
  public String getJoinTable();

  /**
   * INTERNAL: Gets the columns in the join table that contains the
   * keys of the referencing object.
   */
  public String[] getJoinKeys();

  /**
   * INTERNAL: Gets the columns in the join table that contains the
   * keys of the referenced object.
   */
  public String[] getManyKeys();
  
}

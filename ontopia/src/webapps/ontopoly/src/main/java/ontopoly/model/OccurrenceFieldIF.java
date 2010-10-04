
package ontopoly.model;

/**
 * Represents an occurrence field.
 */
public interface OccurrenceFieldIF extends FieldDefinitionIF {

  /**
   * Gets the occurrence type to which this field is assigned.
   * 
   * @return the occurrence type.
   */
  public OccurrenceTypeIF getOccurrenceType();

  /**
   * Returns the data type of the occurrence type.
   */
  public DataTypeIF getDataType();

  /**
   * Returns the assigned height of the occurrence text field.
   */
  public int getHeight();

  /**
   * Returns the assigned width of the occurrence text field.
   */
  public int getWidth();

}

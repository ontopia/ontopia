
package ontopoly.model;

/**
 * Represents a cardinality that can be assigned to a field.
 */
public interface CardinalityIF extends OntopolyTopicIF {

  /**
   * True if cardinality is 0..1 or 1..1.
   */
  public boolean isMaxOne();

  /**
   * True if cardinality is 0..* or 1..*.
   */
  public boolean isMaxInfinite();

  /**
   * True if cardinality is 0..* or 0..1.
   */
  public boolean isMinZero();

  /**
   * True if cardinality is 1..* or 1..1.
   */
  public boolean isMinOne();

}

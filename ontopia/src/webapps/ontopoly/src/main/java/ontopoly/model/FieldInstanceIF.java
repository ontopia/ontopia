
package ontopoly.model;

import java.util.Collection;

/**
 * Represents a populated field attached to an instance topic.
 */
public interface FieldInstanceIF {

  /**
   * Returns the assigned field of which this is an instance.
   */
  public FieldAssignmentIF getFieldAssignment();

  /**
   * Returns the topic this field instance is attached to.
   */
  public OntopolyTopicIF getInstance();

  /**
   * Returns a collection of Objects.
   */
  public Collection getValues();

  /**
   * Add a new FieldValue object.
   */
  public void addValue(Object value, LifeCycleListenerIF listener);

  /**
   * Removes the value.
   */
  public void removeValue(Object value, LifeCycleListenerIF listener);

}

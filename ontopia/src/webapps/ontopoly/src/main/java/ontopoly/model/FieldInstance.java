// $Id: FieldInstance.java,v 1.2 2009/02/13 11:48:36 geir.gronmo Exp $

package ontopoly.model;

import java.util.Collection;

/**
 * Represents a populated field attached to an instance topic.
 */
public class FieldInstance {

  private Topic instance;
  private FieldAssignment fieldAssignment;

  public FieldInstance(Topic instance, FieldAssignment fieldAssignment) {
    this.instance = instance;
    this.fieldAssignment = fieldAssignment;
  }

  /**
   * Returns the assigned field of which this is an instance.
   */
  public FieldAssignment getFieldAssignment() {
    return fieldAssignment;
  }

  /**
   * Returns the topic this field instance is attached to.
   */
  public Topic getInstance() {
    return instance;
  }

  /**
   * Returns a collection of Objects.
   */
  public Collection getValues() {
    return getFieldAssignment().getFieldDefinition().getValues(getInstance());
  }

  /**
   * Set a new FieldValue object.
   */
  // public void setValue(Object value) {
  // throw new UnsupportedOperationException();
  // }
  /**
   * Add a new FieldValue object.
   */
  public void addValue(Object value, LifeCycleListener listener) {
    getFieldAssignment().getFieldDefinition().addValue(this, value, listener);
  }

  /**
   * Removes the value.
   */
  public void removeValue(Object value, LifeCycleListener listener) {
    getFieldAssignment().getFieldDefinition().removeValue(this, value, listener);
  }

//  /**
//   * Removes all values.
//   */
//  public void clear() {
//    throw new UnsupportedOperationException();
//  }

}

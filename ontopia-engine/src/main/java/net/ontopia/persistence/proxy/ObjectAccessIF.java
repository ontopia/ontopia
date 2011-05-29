
package net.ontopia.persistence.proxy;


/**
 * INTERNAL: A object access implementation for manipulation of
 * identifiable objects.
 */

public interface ObjectAccessIF {

  /**
   * INTERNAL: Returns a handle for the specified value. Use this
   * method when you do not know the the object value is. The handle
   * can be used in the other methods to access information about the
   * object. NOTE: an exception is thrown when the identity is
   * unknown.
   */
  public Object getObject(IdentityIF identity);

  /**
   * INTERNAL: Returns the identity of the specified object handle.
   */
  public IdentityIF getIdentity(Object object);
  
  /**
   * INTERNAL: Returns the type of the specified object handle. Note
   * that this method returns the same value as
   * getIdentity(object).getType().
   */
  public Object getType(Object object);
  
  /**
   * INTERNAL: Returns the object field value.
   */
  //! public Object getValue(Object object, int field);
  public Object getValue(Object object, FieldInfoIF finfo);
  
  //! /**
  //!  * INTERNAL: Sets the object field to the given value.
  //!  */
  //! public void setValue(Object object, int field, Object value);
  //! 
  //! /**
  //!  * INTERNAL: Adds the value to the object collection field.
  //!  */
  //! public void addValue(Object object, int field, Object value);
  //! 
  //! /**
  //!  * INTERNAL: Removes the value from the object collection field.
  //!  */
  //! public void removeValue(Object object, int field, Object value);

  /**
   * INTERNAL: Returns true if the specified object is dirty.
   */
  public boolean isDirty(Object object);

  /**
   * INTERNAL: Returns true if the specified object field is dirty.
   */
  public boolean isDirty(Object object, int field);

  /**
   * INTERNAL: Returns the index of the next dirty field from and
   * including the start index. Method returns -1 if there are no
   * dirty fields.
   */
  public int nextDirty(Object object, int start);

  /**
   * INTERNAL: Returns the index of the next dirty field from and
   * including start, up until end, but not including end. Method
   * returns -1 if there are no more dirty fields.
   */
  public int nextDirty(Object object, int start, int end);

  /**
   * INTERNAL: Marks the dirty fields as being flushed (stored in the
   * database).
   */
  public void setDirtyFlushed(Object object, int field);
  
}

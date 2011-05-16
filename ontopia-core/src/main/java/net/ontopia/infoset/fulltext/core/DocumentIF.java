
// $Id: DocumentIF.java,v 1.8 2005/07/07 13:15:08 grove Exp $

package net.ontopia.infoset.fulltext.core;

import java.util.Collection;

/**
 * INTERNAL: Represents an indexable unit of information. A document
 * contains named fields which can have values of the types String or
 * Reader.<p>
 */

public interface DocumentIF {
  
  /**
   * INTERNAL: Returns the field with the specified name.
   */
  public FieldIF getField(String name);
  
  /**
   * INTERNAL: Returns all the fields of this document.
   *
   * @return A collection of FieldIF objects.
   */
  public Collection<FieldIF> getFields();

  /**
   * INTERNAL: Adds the given field to the document.
   */
  public void addField(FieldIF field);

  /**
   * INTERNAL: Removes the given field from the document.
   */
  public void removeField(FieldIF field);
  
}

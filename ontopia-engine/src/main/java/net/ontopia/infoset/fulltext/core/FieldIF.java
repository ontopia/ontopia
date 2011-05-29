
package net.ontopia.infoset.fulltext.core;

import java.io.Reader;

/**
 * INTERNAL: Represents a name value pair that can be attached to a
 * document.<p>
 */

public interface FieldIF {

  /**
   * INTERNAL: Returns the name of the field.
   */
  public String getName();

  /**
   * INTERNAL: Returns the String value of the field. Note that null is
   * returned if the field has a reader set.
   */
  public String getValue();

  /**
   * INTERNAL: Returns the Reader value of the field. Note that null is
   * returned if the field has a value set.
   */
  public Reader getReader();

  /**
   * INTERNAL: Returns true if the field is to be stored in the index
   * for return with search hits.
   */
  public boolean isStored();

  /**
   * INTERNAL: Returns true if the field is to be indexed, so that it
   * may be searched on.
   */
  public boolean isIndexed();

  /**
   * INTERNAL: Returns true if the field is to be tokenized prior to
   * indexing.
   */
  public boolean isTokenized();
  
}






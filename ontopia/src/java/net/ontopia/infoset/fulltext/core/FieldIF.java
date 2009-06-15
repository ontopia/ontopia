// $Id: FieldIF.java,v 1.7 2005/07/07 13:15:09 grove Exp $

package net.ontopia.infoset.fulltext.core;

import java.io.Reader;

/**
 * PUBLIC: Represents a name value pair that can be attached to a
 * document.<p>
 */

public interface FieldIF {

  /**
   * PUBLIC: Returns the name of the field.
   */
  public String getName();

  /**
   * PUBLIC: Returns the String value of the field. Note that null is
   * returned if the field has a reader set.
   */
  public String getValue();

  /**
   * PUBLIC: Returns the Reader value of the field. Note that null is
   * returned if the field has a value set.
   */
  public Reader getReader();

  /**
   * PUBLIC: Returns true if the field is to be stored in the index
   * for return with search hits.
   */
  public boolean isStored();

  /**
   * PUBLIC: Returns true if the field is to be indexed, so that it
   * may be searched on.
   */
  public boolean isIndexed();

  /**
   * PUBLIC: Returns true if the field is to be tokenized prior to
   * indexing.
   */
  public boolean isTokenized();
  
}






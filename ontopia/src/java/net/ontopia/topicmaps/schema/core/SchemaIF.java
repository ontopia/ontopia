// $Id: SchemaIF.java,v 1.4 2002/05/29 13:38:43 hca Exp $

package net.ontopia.topicmaps.schema.core;

import net.ontopia.infoset.core.LocatorIF;

/**
 * PUBLIC: This interface is implemented by objects that represent
 * complete topic map schemas.
 */
public interface SchemaIF {

  /**
   * PUBLIC: Returns a validator object that can be used to validate
   * topic map objects against the schema.
   */
  public SchemaValidatorIF getValidator();

  /**
   * PUBLIC: Returns the address of the schema. The address may be
   * null.
   */
  public LocatorIF getAddress();
  
}






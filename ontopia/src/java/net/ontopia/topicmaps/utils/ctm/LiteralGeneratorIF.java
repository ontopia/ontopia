
// $Id: LiteralGeneratorIF.java,v 1.3 2009/02/27 12:01:23 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import net.ontopia.infoset.core.LocatorIF;

public interface LiteralGeneratorIF {

  public String getLiteral();
  
  public LocatorIF getDatatype();

  /**
   * Returns a locator if the literal is a locator. Otherwise it
   * throws an exception.
   */
  public LocatorIF getLocator();
  
  public LiteralGeneratorIF copyLiteral();
  
}

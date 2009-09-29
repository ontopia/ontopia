// $Id: AbstractLocator.java,v 1.10 2002/05/29 13:38:36 hca Exp $

package net.ontopia.infoset.impl.basic;

import java.io.Serializable;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: The abstract locator class. All locator implementations should
 * extend this class.
 */
@SuppressWarnings("serial")
public abstract class AbstractLocator implements LocatorIF, Serializable  {

  // -------------------------------------------------------------------------
  // LocatorIF implementation
  // -------------------------------------------------------------------------

  /**
   * PUBLIC: Two LocatorIFs are considered equal if they have the same
   * address and notation properties.
   */
  public boolean equals(Object object) {
    try {
      LocatorIF locator = (LocatorIF)object;
      return getAddress().equals(locator.getAddress()) &&
	getNotation().equalsIgnoreCase(locator.getNotation());
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

  /**
   * PUBLIC: Returns the hashcode of the address property. All
   * subclasses of AbstractLocator must use the same hashCode
   * implementation in order to guarantee interoperability. E.g. when
   * looking up LocatorIFs in Maps.
   */
  public int hashCode() {
    return getAddress().hashCode();
  }
  
  public String toString() {
    return getNotation() + "|" + getAddress();
  }
  
}






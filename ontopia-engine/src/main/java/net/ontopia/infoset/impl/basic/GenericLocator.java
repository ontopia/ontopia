
package net.ontopia.infoset.impl.basic;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: A generic locator class. Addresses of any notation can be
 * used with this locator class. No normalization or absolutization is
 * done.
 */

public class GenericLocator extends AbstractLocator implements Externalizable {

  static final long serialVersionUID = 3066225165288209215L;

  protected String notation;
  protected String address;

  /**
   * INTERNAL: No-argument constructor used by serialization. Do not
   * use this constructor in application code.
   */
  public GenericLocator() {    
  }

  public GenericLocator(String notation, String address)
    throws IllegalArgumentException {
    
    if (notation == null)
      throw new IllegalArgumentException("The notation argument cannot be null.");
    if (address == null)
      throw new IllegalArgumentException("The address argument cannot be null.");
    this.notation = notation;
    this.address = address;
  }

  // -----------------------------------------------------------------------------
  // LocatorIF implementation
  // -----------------------------------------------------------------------------
  
  public String getNotation() {
    return notation;
  }

  public String getAddress() {
    return address;
  }

  public LocatorIF resolveAbsolute(String address) {
    // Since this locator is general we cannot make the address
    // absolute, so we'll just return a new locator with the same
    // address instead.
    return new GenericLocator(notation, address);
  }

  public String getExternalForm() {
    // in a generic locator we don't know the syntax rules, so we
    // have to just return the string as we got it
    return address;
  }

  // -----------------------------------------------------------------------------
  // Misc
  // -----------------------------------------------------------------------------

  public int hashCode() {
    return address.hashCode();
  }

  public boolean equals(Object object) {
    try {
      LocatorIF locator = (LocatorIF)object;
      return address.equals(locator.getAddress()) &&
        notation.equalsIgnoreCase(locator.getNotation());
    } catch (ClassCastException e) {
      return false; // In case the object is not a locator
    } catch (NullPointerException e) {
      return false; // In case the object is null
    }
  }

  // -----------------------------------------------------------------------------
  // Externalization
  // -----------------------------------------------------------------------------
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(notation);
    out.writeUTF(address);
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    notation = in.readUTF();
    address = in.readUTF();
  }
  
}

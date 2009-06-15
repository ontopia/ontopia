
// $Id: Locator.java,v 1.5 2006/05/08 11:56:28 grove Exp $

package net.ontopia.topicmaps.impl.oks2tmapi;

import java.util.*;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: OKS->TMAPI object wrapper.
 */

public class Locator implements org.tmapi.core.Locator {

  LocatorIF other;
  
  Locator(LocatorIF other) {
    this.other = other;
  }

  LocatorIF getWrapped() {
    return other;
  }

  public String getReference() {
    return other.getAddress();
  }

  public String getNotation() {
    return other.getNotation();
  }

  public org.tmapi.core.Locator resolveRelative(String reference) {
    LocatorIF resolved = other.resolveAbsolute(reference);
    return new Locator(resolved);
  }

  public String toExternalForm() {
    return other.getExternalForm();    
  }

  public boolean equals(Object o) {
    if (o instanceof Locator) {
      Locator loc = (Locator)o;
      return getReference().equals(loc.getReference()) &&
        getNotation().equals(loc.getNotation());
    }
    return false;
  }

  public int hashCode() {
    return other.hashCode();
  }
  
}

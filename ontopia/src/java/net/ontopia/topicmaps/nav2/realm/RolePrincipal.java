
// $Id: RolePrincipal.java,v 1.2 2006/01/12 11:10:48 larsga Exp $

package net.ontopia.topicmaps.nav2.realm;

import java.io.Serializable;
import java.security.Principal;

import org.apache.log4j.Logger;

public class RolePrincipal implements Principal, Serializable {
  
  // initialization of logging facility
  private static Logger log = Logger.getLogger(RolePrincipal.class.getName());
  
  private String name;

  public RolePrincipal(String name) {
    if (name == null)
      throw new NullPointerException("illegal null input");    
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public String toString() {
    return("RolePrincipal:  " + name);
  }

  public boolean equals(Object o) {
    if (o == null)
      return false;
    
    if (this == o)
      return true;
    
    if (!(o instanceof RolePrincipal))
      return false;
    
    RolePrincipal that = (RolePrincipal) o;
    return getName().equals(that.getName());
  }

  public int hashCode() {
    return name.hashCode();
  }
}

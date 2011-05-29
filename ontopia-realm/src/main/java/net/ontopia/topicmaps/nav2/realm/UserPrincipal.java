
package net.ontopia.topicmaps.nav2.realm;

import java.io.Serializable;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPrincipal implements Principal, Serializable {

  public static final long serialVersionUID = 502L;
  
  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(UserPrincipal.class.getName());
  
  private String name;

  public UserPrincipal(String name) {
    if (name == null)
      throw new NullPointerException("illegal null input");    
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  @Override
  public String toString() {
    return("UserPrincipal:  " + name);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    
    if (this == o)
      return true;
    
    if (!(o instanceof UserPrincipal))
      return false;

    UserPrincipal that = (UserPrincipal) o;
    return getName().equals(that.getName());
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}

package ontopoly;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import ontopoly.model.FieldInstance;
import ontopoly.model.Topic;


public abstract class OntopolyAccessStrategy implements Serializable {

  public enum Privilege { EDIT, READ_ONLY, NONE };
	
  public boolean isEnabled() {
	  return true;
  }
  
  public User autoAuthenticate(HttpServletRequest request) {
    return null;
  }
  
  public User authenticate(String username, String password) {
    return new User(username, false);
  }

  public Privilege getPrivilege(User user, Topic topic) {
    return Privilege.EDIT;
  }

  public Privilege getPrivilege(User user, FieldInstance fieldInstance) {
    return Privilege.EDIT;
  }

}

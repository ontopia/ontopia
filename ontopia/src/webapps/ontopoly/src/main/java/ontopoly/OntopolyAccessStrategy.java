package ontopoly;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import ontopoly.model.FieldInstance;
import ontopoly.model.FieldsView;
import ontopoly.model.Topic;


public abstract class OntopolyAccessStrategy implements Serializable {

  public boolean isEnabled() {
	  return true;
  }
  
  public User autoAuthenticate(HttpServletRequest request) {
    return null;
  }
  
  public User authenticate(String username, String password) {
    return new User(username, false);
  }

  public boolean editable(User user, Topic topic) {
    return true;
  }

  public boolean editable(User user, FieldInstance fieldInstance) {
    return true;
  }

}

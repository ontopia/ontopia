package ontopoly;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldsView;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;

public class OntopolyAccessStrategy implements Serializable {
  
  public String autoAuthenticate(HttpServletRequest request) {
    return null;
  }
  
  public boolean authenticate(String username, String password) {
    return true;
  }

  public boolean editable(String username, Topic topic, FieldsView fieldsView, String url) {
    return true;
  }

  public boolean isEnabled() {
    return true;
  }

}

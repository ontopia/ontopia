package ontopoly;

import javax.servlet.http.HttpServletRequest;

import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.FieldsView;
import net.ontopia.topicmaps.nav2.webapps.ontopoly.model.Topic;
import net.ontopia.utils.ObjectUtils;

public class BergenKommuneAccessStrategy extends OntopolyAccessStrategy {
  
  public String autoAuthenticate(HttpServletRequest request) {
    return extractUserName(request);
  }
  
  protected String extractUserName(HttpServletRequest hsr) {
    // HACK: this method is hardcoded to OSSO authentication
    String header = hsr.getHeader("OSSO-USER-DN");
    // header = "cn=orcladmin,cn=users, dc=iktfou,dc=no,"; // FIXME: overridden
    if (header != null) {
      String cn = "cn=";
      int cn_ix = header.indexOf(cn);
//      System.out.println("CNIX: " + cn_ix);
      if (cn_ix > -1) {
        String comma = ",";
        int startIndex = cn_ix+cn.length();
        int comma_ix = header.indexOf(comma, startIndex);
//        System.out.println("COIX: " + comma_ix);
        if (comma_ix > -1) {
          int endIndex = comma_ix;
//          System.out.println("CCIX: " + startIndex + ":" + endIndex);
          if (startIndex != endIndex)
            return header.substring(startIndex, endIndex);
        }
      }
    }
    return null;
  }
  
  public boolean authenticate(String username, String password) {
    return ObjectUtils.equals(username, password);
  }

  public boolean editable(String username, Topic topic, FieldsView fieldsView, String url) {
    return true;
  }

}

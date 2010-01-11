package util;

import com.liferay.portal.model.User;

/**
 * This class' only prupose is to act as a connector between the liferay datamodel and ontopia. It provides information to ontopia in a way, that
 * they can be processed directly. Extensions in terms of getting more data from liferay regarding users should be done here.
 */
public class UserData implements UuidIdentifiableIF{

  private String _username;
  private String _uuid;
  
  private int _userId;
  
  public UserData(User user){
    setUuid(user.getUuid());
    setUsername(user.getEmailAddress());
  }
  
  private UserData(){
    super();
  }
  
  // Setter and Getter
  public void setUsername(String username) {
    _username = username;
  }
  public String getUsername() {
    return _username;
  }
  public void setUuid(String uuid) {
    _uuid = uuid;
  }
  public String getUuid() {
    return _uuid;
  }

  public void setUserId(int userId) {
    _userId = userId;
  }

  public int getUserId() {
    return _userId;
  }
}

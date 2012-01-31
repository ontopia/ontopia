package ontopoly;

public class User {

  private String username;
  private boolean autoLogin;
    
  public User(String username, boolean autoLogin) {
    this.username = username;
    this.autoLogin = autoLogin;
  }

  /**
   * Returns the user name.
   */
  public String getName() {
    return username;
  }

  /**
   * Returns the full name of the user. Will fall back to the username if not exists. 
   */
  public String getFullname() {
    return getName();
  }

  /**
   * Returns true if the user was automatically logged in.
   */
  public boolean isAutoLogin() {
    return autoLogin;
  }  
}

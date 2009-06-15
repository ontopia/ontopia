package ontopoly;

public abstract class User {

  /**
   * Returns the user name.
   * @return
   */
  public abstract String getName();
  
  /**
   * Returns the full name of the user. Will fall back to the username if not exists. 
   * @return
   */
  public abstract String getFullname();

  /**
   * Returns true if the user was automatically logged in.
   * @return
   */
  public abstract boolean isAutoLogin();
  
}

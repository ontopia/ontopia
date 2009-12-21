package ontopoly;

import ontopoly.model.Topic;
import ontopoly.pages.AbstractProtectedOntopolyPage;
import ontopoly.pages.SignInPage;

import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebSession;

public class OntopolySession extends WebSession {

  private OntopolyAccessStrategy accessStrategy;
  
  private boolean shortcutsEnabled = false;
  private boolean annotationEnabled = false;
  private boolean administrationEnabled = false;
  private User user;
  
  protected OntopolySession(Request request, OntopolyAccessStrategy accessStrategy) {
    super(request);

    this.accessStrategy = accessStrategy;
    
    // attempt to automatically login user
    if (accessStrategy.isEnabled()) {
      WebRequest wr = (WebRequest)request;
      String username = this.accessStrategy.autoAuthenticate(wr.getHttpServletRequest());
      if (username != null)
        setUserAuthenticated(username, true);
    }
    // set locale
//    setLocale(new Locale("no"));
  }
  
  public boolean isShortcutsEnabled() {
    return shortcutsEnabled;
  }
  
  public void setShortcutsEnabled(boolean shortcutsEnabled) {
    this.shortcutsEnabled = shortcutsEnabled;
  }
  
  public boolean isAnnotationEnabled() {
    return annotationEnabled;
  }
  
  public void setAnnotationEnabled(boolean annotationEnabled) {
    this.annotationEnabled = annotationEnabled;
  }

  public void setAdministrationEnabled(boolean administrationEnabled) {
    this.administrationEnabled = administrationEnabled;
  }

  public boolean isAdministrationEnabled() {
    return administrationEnabled;
  }

  public String getUserName() {
    return (user == null ? null : user.getName());
  }
  
  protected User getUser() {
    return user;
  }
  
  protected void setUser(User user) {
    this.user = user;
  }

  public LockManager.Lock lock(Topic topic, String lockerId) {
    // compose locker id and lock key
    String lockKey = getLockKey(topic);
    
    // get lock
    return OntopolyContext.getLockManager().lock(lockKey, lockerId);    
  }
  
  public String getLockerId(Request request) {
    User user = getUser();
    String lockerId = (user == null ? null : user.getName());
    if (lockerId == null)
      lockerId = getApplication().getSessionStore().getSessionId(request, true); // WARNING: will create new session
    return lockerId;
  }
  
  protected String getLockKey(Topic topic) {
    String topicMapId = topic.getTopicMap().getId();
    String topicId = topic.getId();
    return topicMapId + ":" + topicId;      
  }
  
  public boolean isAccessStrategyEnabled() {
    return accessStrategy.isEnabled();
  }
  
  /*
   * Session is authenticated if there is a user object.
   */
  public boolean isAuthenticated() {
    //!return true; // NOTE: authentication disabled
    if (accessStrategy.isEnabled())
      return user != null;
    else
      return true;
  }
  
  public boolean isAutoLogin() {
    return !accessStrategy.isEnabled() || (user != null && user.isAutoLogin());
  }
  
  @Override
  public IAuthorizationStrategy getAuthorizationStrategy() {
    return new OntopolyAuthorizationStrategy();
  }
  
  public static class OntopolyAuthorizationStrategy implements IAuthorizationStrategy, IUnauthorizedComponentInstantiationListener {
    public boolean isInstantiationAuthorized(Class componentClass) {
      if (AbstractProtectedOntopolyPage.class.isAssignableFrom(componentClass)) {
        return ((OntopolySession)Session.get()).isAuthenticated(); 
      } 
      return true;
    }
    public boolean isActionAuthorized(Component c, Action action) {
      return true; 
    }
    public void onUnauthorizedInstantiation(Component component) { 
      throw new RestartResponseAtInterceptPageException(SignInPage.class); 
    } 
  }
  
  /**
   * Authenticates a user given the username and password.
   * @param username
   * @param password
   * @return true if the user was authenticated.
   */
  public boolean authenticate(String username, String password) {
    // TODO: actually authenticate user
    boolean authenticated = accessStrategy.authenticate(username, password);
    if (authenticated)
      setUserAuthenticated(username, false);
    return authenticated;
  }
  
  /**
   * Registers the user object in the session.
   * @param username
   */
  protected void setUserAuthenticated(String username, boolean autoLogin) {
    setUser(createUser(username, autoLogin));    
  }
  
  /**
   * Factory method for creating user objects.
   * @param username
   * @return
   */
  protected User createUser(String username, boolean autoLogin) {
    return new DefaultUser(username, autoLogin);
  }

  protected static class DefaultUser extends User {
    private String username;
    private boolean autoLogin;
    DefaultUser(String username, boolean autoLogin) {
      this.username = username;
      this.autoLogin = autoLogin;
    }
    @Override
    public String getName() {
      return username;
    }
    @Override
    public String getFullname() {
      return getName();
    }
    @Override
    public boolean isAutoLogin() {
      return autoLogin;
    }
  }
  
}

package ontopoly;

import ontopoly.model.Topic;
import ontopoly.pages.AbstractProtectedOntopolyPage;
import ontopoly.pages.SignInPage;

import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
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
  
  protected OntopolySession(Request request, Response response, OntopolyApplication ontopolyApplication) {
    super(request);

    this.accessStrategy = ontopolyApplication.newAccessStrategy();
    
    // attempt to automatically login user
    if (accessStrategy.isEnabled()) {
      WebRequest wr = (WebRequest)request;
      User authenticatedUser = this.accessStrategy.autoAuthenticate(wr.getHttpServletRequest());
      if (authenticatedUser != null)
        setUser(user);
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
        boolean authenticated = ((OntopolySession)Session.get()).isAuthenticated(); 
        return authenticated;
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
    User authenticatedUser = accessStrategy.authenticate(username, password);
    if (authenticatedUser != null) {
      setUser(authenticatedUser);
      return true;
    } else {    
      return false;
    }
  }
  
}

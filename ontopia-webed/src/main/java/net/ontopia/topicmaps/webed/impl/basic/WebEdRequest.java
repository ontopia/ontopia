
// $Id: WebEdRequest.java,v 1.4 2006/07/10 12:40:14 larsga Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.webed.core.WebEdRequestIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;

/**
 * INTERNAL: Default implementation of WebEdRequestIF.
 */
public class WebEdRequest implements WebEdRequestIF {
  private Map actionmap;
  private UserIF user;
  private ServletContext context;
  private boolean actionsExecuted;
  private HttpServletRequest request;

  public WebEdRequest(UserIF user, Map actionmap, ServletContext context,
                      HttpServletRequest request) {
    this.user = user;
    this.actionmap = actionmap;
    this.context = context;
    this.request = request;
    this.actionsExecuted = false;
  }
  
  public ActionParametersIF getActionParameters(String name) {
    return (ActionParametersIF) actionmap.get(name);
  }

  public UserIF getUser() {
    return user;
  }

  public ServletContext getServletContext() {
    return context;
  }

  public HttpServletRequest getHttpRequest() {
    return request;
  }

  public boolean getActionsExecuted() {
    return actionsExecuted;
  }

  public void setActionsExecuted(boolean executed) {
    this.actionsExecuted = executed;
  }    
}

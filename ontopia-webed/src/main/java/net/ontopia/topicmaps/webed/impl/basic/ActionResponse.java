
// $Id: ActionResponse.java,v 1.15 2003/12/22 19:14:36 larsga Exp $

package net.ontopia.topicmaps.webed.impl.basic;

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.webed.core.*;
import net.ontopia.topicmaps.webed.impl.utils.ReqParamUtils;

/**
 * INTERNAL: Default implementation of an action response.
 */
public class ActionResponse implements ActionResponseIF {

  protected Map params;
  protected String forwardurl;
  protected HttpServletRequest request;
  protected HttpServletResponse response;
  
  public ActionResponse(HttpServletRequest request, HttpServletResponse response) {
    this.params = new HashMap();
    this.request = request;
    this.response = response;
  }

  // --- implementation of ActionResponseIF

  public void addParameter(String key, String value) {
    params.put(key, value);
  }
  
  public Map getParameters() {
    return params;
  }

  public String getParameter(String key) {
    return (String) params.get(key);
  }

  public void addMessage(String message) {
    UserIF user =
      (UserIF) request.getSession().getAttribute(NavigatorApplicationIF.USER_KEY);
    user.addLogMessage(message);
  }

  public void setForward(String relativeUrl) {
    int pos = relativeUrl.indexOf("?");
    if (pos == -1) {
      forwardurl = relativeUrl;
      return;
    }

    forwardurl = relativeUrl.substring(0, pos);
    Map params = ReqParamUtils.parseURLQuery(relativeUrl.substring(pos+1));
    Iterator it = params.keySet().iterator();
    while (it.hasNext()) {
      String param = (String) it.next();
      addParameter(param, (String) params.get(param));
    }
  }

  public String getForward() {
    return forwardurl;
  }
  
  // --- undocumented methods

  public ServletContext getServletContext() {
    return request.getSession().getServletContext();
  }

  public HttpServletRequest getHttpServletRequest() {
    return request;
  }

  public HttpServletResponse getHttpServletResponse() {
    return response;
  }
  
}

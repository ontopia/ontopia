/*
 * #!
 * Ontopia Webed
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

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

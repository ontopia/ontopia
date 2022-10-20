/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav2.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.impl.framework.User;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.query.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.QName;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related utility class providing some helper
 * methods needed to easier for examples access user information.
 */
public final class FrameworkUtils {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(FrameworkUtils.class.getName());

  /**
   * INTERNAL: Gets the context tag from the request scope.
   */
  public static ContextTag getContextTag(PageContext pageContext) {
    return getContextTag(pageContext.getRequest());
  }
  
  /**
   * INTERNAL: Gets the context tag from the request.
   */
  public static ContextTag getContextTag(ServletRequest request) {
    return (ContextTag)
      request.getAttribute(NavigatorApplicationIF.CONTEXT_KEY);
  }

  /**
   * INTERNAL: Gets user object out of session scope.
   */
  public static UserIF getUser(PageContext pageContext) {
    return getUser(pageContext, true);
  }

  /**
   * INTERNAL: Gets user object out of session scope.
   */
  public static UserIF getUser(PageContext pageContext, boolean create) {
    try {
      Object obj = pageContext.getAttribute(NavigatorApplicationIF.USER_KEY,
					    PageContext.SESSION_SCOPE);
      if (obj != null && obj instanceof UserIF) {
        return (UserIF) obj;
      } else {
        // if no user object exists just create a new one
        return (create ? createUserSession(pageContext) : null);
      }

    } catch (java.lang.IllegalStateException e) {

      // sessions not allowed in page, so get the user from the request scope instead
      Object obj = pageContext.getAttribute(NavigatorApplicationIF.USER_KEY,
					    PageContext.REQUEST_SCOPE);
      if (obj != null && obj instanceof UserIF) {
        return (UserIF) obj;
      } else {
        // if no user object exists just create a new one
        return (create ? createUserSession(pageContext, PageContext.REQUEST_SCOPE) : null);
      }

    }
  }

  /**
   * INTERNAL: Create new user object in session scope.
   */
  public static UserIF createUserSession(PageContext pageContext) {
    return createUserSession(pageContext, PageContext.SESSION_SCOPE);
  }

  /**
   * INTERNAL: Create new user object in given scope.
   */
  public static UserIF createUserSession(PageContext pageContext, int scope) {

    NavigatorConfigurationIF navConf = NavigatorUtils.getNavigatorApplication(pageContext).getConfiguration();
    // try to retrieve the user name from the request, otherwise null
    String username = null;
    if (pageContext.getRequest() instanceof HttpServletRequest) {
      username = ((HttpServletRequest) pageContext.getRequest()).getRemoteUser();
    }
    // create new user object
    UserIF user = new User(username, navConf);
    // set MVS settings
    user = setDefaultMVS(navConf, user);
    // set user object to session scope
    pageContext.setAttribute(NavigatorApplicationIF.USER_KEY, user, scope);
    log.debug("New user object ('" + user.getId() + "') created and bound in scope ( " + scope + ").");
    return user;
  }

  /**
   * INTERNAL: Reset MVS settings in user object in session scope.
   */
  public static void resetMVSsettingsInUserSession(PageContext pageContext) {
    NavigatorConfigurationIF navConf = NavigatorUtils.getNavigatorApplication(pageContext).getConfiguration();

    //! UserIF user = (UserIF) pageContext.getAttribute(NavigatorApplicationIF.USER_KEY,
    //!                                                 PageContext.SESSION_SCOPE);
    UserIF user = getUser(pageContext, false);
    if (user == null) {
      return; // ignore if no user
    }

    // reset MVS settings
    user = setDefaultMVS(navConf, user);
    // set user object to session scope (TODO: support other contexts?)
    pageContext.setAttribute(NavigatorApplicationIF.USER_KEY, user, PageContext.SESSION_SCOPE);
    log.info("MVS settings in user session has been reset.");
  }

  /**
   * INTERNAL: Evaluates a string of space-separated variable names as a list
   * of collections, and returns it.
   */
  public static List evaluateParameterList(PageContext pageContext,
                                            String params)
    throws JspTagException {
    if (params != null && !params.equals("")) {
      return getMultipleValuesAsList(params, pageContext);
    } else {
      return Collections.EMPTY_LIST;
    }
  }
  
  /**
   * INTERNAL: Returns the values retrieved from the given variable
   * names or qnames in the order given.
   *
   * @param params - variable names or qnames, separated by whitespaces.
   */
  private static List getMultipleValuesAsList(String params, 
                                              PageContext pageContext)
    throws JspTagException {
    log.debug("getMultipleValuesAsList");
    // find parsecontext
    NavigatorPageIF ctxt = (NavigatorPageIF)
      pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY,
                               PageContext.REQUEST_SCOPE);
    ParseContextIF pctxt = (ParseContextIF) ctxt.getDeclarationContext();

    // get the values
    String[] names = StringUtils.split(params);
    List varlist = new ArrayList(names.length);
    for (int i = 0; i < names.length; i++) {
      Collection values;
      
      if (names[i].indexOf(':') != -1) {
        // it's a qname
        try {
          values = Collections.singleton(pctxt.getObject(new QName(names[i])));
        } catch (AntlrWrapException e) {
          throw new JspTagException(e.getException().getMessage() +
                                    " (in action parameter list)");
        }
      } else {
        // it's a variable name
        values = InteractionELSupport.extendedGetValue(names[i], pageContext);
      }
      
      varlist.add(values);
    }
    return varlist;
  } 

  
  // ------------------------------------------------------------
  // internal helper methods
  // ------------------------------------------------------------

  /**
   * INTERNAL: Resets MVS settings for given user object.
   */
  private static UserIF setDefaultMVS(NavigatorConfigurationIF navConf,
                                      UserIF user) {
    // get defaults from config and set them
    if (navConf.getDefaultModel() != null
        && !navConf.getDefaultModel().equals("")) {
      user.setModel( navConf.getDefaultModel() );
    }
    if (navConf.getDefaultView() != null
        && !navConf.getDefaultView().equals("")) {
      user.setView( navConf.getDefaultView()  );
    }
    if (navConf.getDefaultSkin() != null
        && !navConf.getDefaultSkin().equals("")) {
      user.setSkin( navConf.getDefaultSkin() );
    }
    
    return user;
  }
  
}

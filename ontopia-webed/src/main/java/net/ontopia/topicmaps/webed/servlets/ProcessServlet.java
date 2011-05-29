
package net.ontopia.topicmaps.webed.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.core.OSLSchemaAwareIF;
import net.ontopia.topicmaps.webed.impl.actions.DefaultAction;
import net.ontopia.topicmaps.webed.impl.actions.DummyAction;
import net.ontopia.topicmaps.webed.impl.basic.ActionContext;
import net.ontopia.topicmaps.webed.impl.basic.ActionContextIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionError;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPage;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPageIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionGroupIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;
import net.ontopia.topicmaps.webed.impl.basic.ActionParameters;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionResponse;
import net.ontopia.topicmaps.webed.impl.basic.ActionValidationException;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.NoActionDataFoundException;
import net.ontopia.topicmaps.webed.impl.basic.ParamRuleIF;
import net.ontopia.topicmaps.webed.impl.basic.UnavailableSessionException;
import net.ontopia.topicmaps.webed.impl.basic.WebEdRequest;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.NamedLockManager;
import net.ontopia.topicmaps.webed.impl.utils.Parameters;
import net.ontopia.topicmaps.webed.impl.utils.ReqParamUtils;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.utils.DebugUtils;
import net.ontopia.utils.OntopiaRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Central entry point to the web application for processing a HTTP
 * request and forwarding to the appropriate page depending on the executed
 * actions.
 */
public final class ProcessServlet extends HttpServlet {

  private static final long serialVersionUID = 1115457802266399626L;

  // --- initialize logging facility.
  static Logger logger = LoggerFactory.getLogger(ProcessServlet.class.getName());

  /**
   * INTERNAL: Handles a HTTP GET request.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * INTERNAL: Handles a HTTP POST request.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * INTERNAL: Internal method which is for handling all of the incoming HTTP
   * requests. The following request parameters have to be available:
   * <ul>
   * <li>Constants.RP_TOPICMAP_ID: holds the ID of the topicmap working with</li>
   * <li>Constants.RP_ACTIONGROUP: containing the name of the current action
   * group</li>
   * <li>Other request parameters may be set and are specific to the current
   * action group.</li>
   * </ul>
   * </p>
   * <p>
   * Note: See the action configuration file
   * <code>WEB-INF/config/actions.xml</code> for more information.
   */
  protected void processRequest(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    // get hold of navigator application
    NavigatorApplicationIF navApp = getNavigatorApplication();

    // IMPORTANT: never call getParameter before doing the decoding
    // below (see bug #2096 for more information)
    
    // decode request parameters
    String charEnc = getCharacterEncoding(navApp);
    Parameters params = getParameters(request, navApp, charEnc);

    // unlock form
    String operation = request.getParameter(Constants.RP_OPERATION);
    if (operation != null && operation.equals(Constants.RPVAL_UNLOAD)) {
      unlock(request, false);
      return;
    } else if (operation != null && operation.equals(Constants.RPVAL_UNLOCK)) {
      unlock(request, true);
      return;
    }
    
    // initialization
    logrequest(params);

    if (params.get("ag") == null) {
      logger.error("Request parameter 'ag' is null, which should never happen."
          + "Therefore error-logging the following. (indented)");
      String indentation = "  ";
      errorLogrequest(params, indentation);
      logger.error(indentation + "request.getHeader(\"user-agent\")"
          + request.getHeader("user-agent"));
      logger.error(indentation + "request.getHeader(\"referer\")"
          + request.getHeader("referer"));
      logger.error(indentation + "getRemoteUser(): " + request.getRemoteUser());
      logger.error(indentation + "getRemoteAddr(): " + request.getRemoteAddr());
    }

    List nonCriticalErrors = new ArrayList();

    UserIF user = getUser(request);

    // make sure that user still owns form lock
    NamedLockManager lockMan = null;
    String lock_varname = params.get(Constants.RP_LOCKVAR);
    if (lock_varname != null && !lock_varname.equals("") && 
                                !lock_varname.endsWith("-unlocked")) {
      lockMan = TagUtils.getNamedLockManager(getServletContext());

      if (!lockMan.ownsLock(user, lock_varname)) {
        NoActionDataFoundException e = new NoActionDataFoundException("Lock on form expired (lock id=" + lock_varname + ").");
        Map attrs = new HashMap();
        attrs.put("referer", request.getHeader("referer"));
        attrs.put("linkforward", params.get("linkforward")); 
        e.setUserObject(attrs);
        throw e;
      }
    }
    
    // request preparation
    ActionContextIF actionCtxt = new ActionContext(user, params);

    // first retrieve action registry (built from action configuration file)
    ActionRegistryIF registry;
    try {
      registry = TagUtils.getActionRegistry(request);
    } catch (javax.servlet.jsp.JspTagException e) {
      throw new OntopiaRuntimeException(e);
    }

    if (registry == null)
      throw new ServletException("The action registry is not available, "
          + "please verify your action configuration file.");

    String actionGroupName = params.get(Constants.RP_ACTIONGROUP);
    if (actionGroupName == null)
      throw new ServletException("The request parameter '"
          + Constants.RP_ACTIONGROUP + "' must "
          + "contain a declared action group name.");

    ActionGroupIF actionGroup = registry.getActionGroup(actionGroupName);
    if (actionGroup == null)
      logger.warn("No action group was found for '" + actionGroup + "'.");

    ActionForwardPageIF forwardPage = null;
    ActionResponseIF acresponse = new ActionResponse(request, response);

    // repeat required parameters (actions may override)
    for (int ix = 0; ix < Constants.OBJ_REQPARAMS.length; ix++) {
      String param = Constants.OBJ_REQPARAMS[ix];
      String value = params.get(param);
      if (value != null && !value.equals(""))
        acresponse.addParameter(param, value);
    }

    // retrieve rw topicmap from navigator application
    String topicmapId = params.get(Constants.RP_TOPICMAP_ID);
    TopicMapIF topicmap = null;
    TopicMapStoreIF store = null;
    try { // txn block

      try {
        topicmap = (topicmapId == null ? 
            null : navApp.getTopicMapById(topicmapId, false));
      } catch (NavigatorRuntimeException e) {
        throw new UnavailableException(e.getMessage());
      }
      if (topicmap == null)
        logger.error("Topic map (" + topicmapId + ") is NOT available.");
      else
        logger.info("Topic map (" + topicmapId + ") is available.");

      // get topic map store and synchronize on it
      store = (topicmap == null ? null : topicmap.getStore());
      Object lock = (store == null ? new Object() : store);
      
      synchronized (lock) {

        // and set it as a request attribute (can be retrieved via actionCtxt)
        request.setAttribute(Constants.RA_TOPICMAP, topicmap);

        // setup schema information
        Map schemaRegistry = TagUtils.getSchemaRegistry(getServletContext());
        OSLSchema schema = (OSLSchema) schemaRegistry.get(topicmapId);

        // --- (A): find all actions to be executed
        // Create WebEdRequest object
        Map actionmap = new HashMap();
        WebEdRequest werequest = new WebEdRequest(user, actionmap,
                                                  getServletContext(),
                                                  request);

        // collect all action information
        ActionData exclusive = null;
        List actions = new ArrayList();
        List conditionalActions = new ArrayList(); // will only run if others run
        
        Iterator it;
        try {
          it = actionCtxt.getAllActions().iterator();
          while (it.hasNext()) {
            ActionData data = (ActionData) it.next();
            ActionInGroup action = data.getAction();

            String param = data.getFieldName();            
            String[] values = actionCtxt.getParameterValues(param);
           
            // add action parameters to webed request's action map
            ActionParametersIF aparams = new ActionParameters(param, values, params.getFile(param),
                                                              TagUtils.deserializeParameters(data.getParameters(), topicmap),
                                                              topicmap, werequest);
            actionmap.put(data.getAction().getName(), aparams);
            
            // check to see if action is to be executed
            if (!isValueEqual(param, values, data.getValue())) {
              if (data.getRunIfNoChanges())
                actions.add(data); // this should run even if there are no
              // changes
              else
                conditionalActions.add(data); // only run this if another action
              // runs
              
              if (action.isExclusive())
                // continue so we complete actionmap, then act on this later
                exclusive = data;
            }
          }
        } catch (NoActionDataFoundException e) {
          Map attrs = new HashMap();
          attrs.put("referer", request.getHeader("referer"));
          attrs.put("linkforward", params.get("linkforward")); 
          e.setUserObject(attrs);
          throw e;
        }
        
        if (exclusive != null) {
          // this action is exclusive, so only execute that, and skip all others
          logger.debug("Action " + exclusive
              + " was exclusive; not running any others");
          actions = Collections.singletonList(exclusive);
          conditionalActions = Collections.EMPTY_LIST;
        }

        // put actions in right order (and filter them, apparently)
        actions = sortActions(actions, actionGroup);

        // add conditional actions, if any
        if (changesMade(actions) && !conditionalActions.isEmpty()) {
          logger.debug("Other actions are run, so conditional actions are run, too");
          actions.addAll(conditionalActions); // adding conditional actions to list

          // resort, to get new actions into right order
          actions = sortActions(actions, actionGroup);
        }

        // --- (B): execute actions
        
        // trigger all actions to be triggered
        it = actions.iterator();
        while (it.hasNext()) {
          ActionData data = (ActionData) it.next();
          boolean actionHadErrors = false;
          
          String param = data.getFieldName();
          String[] values = actionCtxt.getParameterValues(param);
          
          ActionInGroup action = data.getAction();
          
          if (action.getAction() instanceof OSLSchemaAwareIF)
            ((OSLSchemaAwareIF) action.getAction()).setSchema(schema);
          
          ActionParametersIF aparams = new ActionParameters(param, values, params.getFile(param),
                                                            TagUtils.deserializeParameters(data.getParameters(), topicmap),
                                                            topicmap, werequest);
          
          try {
            logger.debug("Invoking action " + aparams + ", " + action);
            action.getAction().perform(aparams, acresponse);

          } catch (ActionRuntimeException e) {
            logger.error("Action " + aparams + " raised error", e);              
            if (e.getCritical())
              throw e;
            else { 
              actionHadErrors = true;
              nonCriticalErrors.add(new ActionError(e, data, values));
            }
          }
          
          // record that we've executed an action (doing it here so 
          // run-if-changes=false actions won't trigger themselves)
          werequest.setActionsExecuted(true);
          
          // executing sub-actions
          Iterator it2 = data.getSubActions().iterator();
          while (it2.hasNext()) {
            ActionData subdata = (ActionData) it2.next();
            ActionInGroup subaction = subdata.getAction();
            if (subaction instanceof OSLSchemaAwareIF)
              ((OSLSchemaAwareIF) subaction).setSchema(schema);
            
            ActionParametersIF subparams = new ActionParameters(null, null, null,
                                                                TagUtils.deserializeParameters(subdata.getParameters(), topicmap),
                                                                topicmap, werequest);
            try {
              logger.debug("Invoking sub-action of " + param + ": " + subaction);
              subaction.getAction().perform(subparams, acresponse);
            } catch (ActionRuntimeException e) {
              logger.error("Sub-action of " + params + " raised error: " + subaction, e);
              if (e.getCritical())
                throw e;
              else {
                actionHadErrors = true;
                nonCriticalErrors.add(new ActionError(e, subdata));
              }
            }
          }
          
          // find forward behaviour
          ActionForwardPageIF tmpForward = actionGroup.getForwardPage(action, actionHadErrors);
          
          if (tmpForward != null) {
            // this is a principal action
            if (forwardPage != null)
              logger.error("More than one principal action found: " + param);
            else {
              forwardPage = tmpForward;
              logger.debug("Action " + action.getName() + " is principal; "
                  + "request succeeded; found forward " + forwardPage);
            }
          }
        } // while
        
        // handle forward logic
        if (acresponse.getForward() != null) {
          // one of the actions set the forward; this then overrides
          // everything else
          forwardPage = new ActionForwardPage(acresponse.getForward(),
              acresponse.getParameters());
          logger.debug("An action set the forward: " + forwardPage);
        } else if (forwardPage == null) {
          logger.debug("No forward page found; getting default");
          forwardPage = actionGroup
          .getDefaultForwardPage(nonCriticalErrors.isEmpty() ? Constants.FORWARD_SUCCESS
              : Constants.FORWARD_FAILURE);
        }
        
        // commit transaction
        if (store != null) {
          store.commit();
          logger.debug("Transaction committed");
        }
      } // synchronized

    } catch (Throwable e) {
      logger.error("There was an exception during form submission", e);
      // rollback transaction
      if (store != null) {
        store.abort();
        logger.debug("Transaction rolled back");
      }
      if (e instanceof RuntimeException)
        throw (RuntimeException)e;
      else
        throw new OntopiaRuntimeException(e);

    } finally {
      // Remove the request data from the cache
      user.removeWorkingBundle(params.get(Constants.RP_REQUEST_ID));
      // return topic map to navigator application
      if (navApp != null && topicmap != null)
        navApp.returnTopicMap(topicmap);
      
      // --- (C) Release variable from lock manager

      if (lockMan != null) {
        lockMan.unlock(user, lock_varname, false);
      }
      
    } // try: txn block    

    // --- (D) Constructing the URL to forward to
    if (forwardPage == null) {
      if (nonCriticalErrors.isEmpty())
        forwardPage = actionGroup
        .getDefaultForwardPage(Constants.FORWARD_SUCCESS);
      else
        forwardPage = actionGroup
        .getDefaultForwardPage(Constants.FORWARD_FAILURE);
    }
    logger.debug("Forward page is: " + forwardPage);

    if (!nonCriticalErrors.isEmpty())
      request.getSession().setAttribute("nonCriticalErrors", nonCriticalErrors);
    
    // A default forward page is now REQUIRED in the actionConfig.dtd
    // So removed test for null forward page

    String url = forwardPage.getURL();
    StringBuffer urlBuffer = new StringBuffer(url);
    urlBuffer.append('?');

    // merge request parameters
    Map mergedParams = new HashMap(acresponse.getParameters());
    mergedParams.putAll(forwardPage.getParameters());

    // HACK: needed by ontopoly embedded
    request.setAttribute("oksResponseParams", mergedParams);
    request.setAttribute("oksForwardPage", url);
    
    // append request parameters
    urlBuffer.append(ReqParamUtils.params2URLQuery(mergedParams, params, charEnc));

    // [finished building the URL of the 'forward' request]
    // --- (E) Apply request parameter rule to the request string
    String relativeURL = urlBuffer.toString();
    logger.debug("Initial forward URL: " + relativeURL);

    String nextActionTemplate = forwardPage.getNextActionTemplate();
    ParamRuleIF paramRule = forwardPage.getNextActionParamRule();

    if ((nextActionTemplate != null && !nextActionTemplate.equals(""))
        || paramRule != null) {
      logger.debug("Applying param rule: " + paramRule);
      relativeURL = paramRule.generate(actionCtxt, null,
          nextActionTemplate, relativeURL);
    }

    // only let linkforward override if there are no errors
    if (nonCriticalErrors.isEmpty()) {
      String linkforward = params.get("linkforward");
      if (linkforward != null && !linkforward.equals("")) {
        logger.debug("Changing relativeURL (" + relativeURL + ") to linkforward("
            + linkforward + ")");
        relativeURL = linkforward;
      }
    }

    Boolean embedded = (Boolean)request.getAttribute("ProcessServlet.embedded");
    if (embedded == null || !embedded.booleanValue()) {
      logger.debug("Forward to " + relativeURL);
      response.sendRedirect(relativeURL);
    } else {
      logger.debug("In embedded mode, so no forward is done.");
    }
  }

  private String getCharacterEncoding(NavigatorApplicationIF navApp) {
    // ensure that request character encoding decoded correctly (bug #622)
    String charEnc = navApp.getConfiguration().getProperty(
        "defaultCharacterEncoding");
    if (charEnc != null && charEnc.trim().equals(""))
      charEnc = null;
    return charEnc;
  }

  private UserIF getUser(HttpServletRequest request)
      throws UnavailableSessionException {
    // retrieve user object from session
    UserIF user = (UserIF) request.getSession().getAttribute(
        NavigatorApplicationIF.USER_KEY);
    if (user == null)
      throw new UnavailableSessionException("No user session available, "
          + "please log in (again).");
    return user;
  }

  private Parameters getParameters(HttpServletRequest request, 
                                   NavigatorApplicationIF navApp,
                                   String charenc) 
      throws IOException, ServletException {
    // decode request into Parameters object
    Parameters params = ReqParamUtils.decodeParameters(request, charenc);

    return params;
  }

  private NavigatorApplicationIF getNavigatorApplication() {
    NavigatorApplicationIF navApp = NavigatorUtils
        .getNavigatorApplication(getServletContext());
    if (navApp == null)
      logger.warn("NavigationApplication object is NOT available.");
    return navApp;
  }

  private void unlock(HttpServletRequest request, boolean forced) 
      throws ServletException, IOException {
    UserIF user = getUser(request);
    NavigatorApplicationIF navApp = getNavigatorApplication();
    Parameters params = getParameters(request, navApp,
                                      getCharacterEncoding(navApp));
    String lock_varname = params.get(Constants.RP_LOCKVAR);
    if (lock_varname != null && !lock_varname.equals("")) {
      NamedLockManager lockMan = TagUtils
          .getNamedLockManager(getServletContext());
    
      if (lockMan != null) {
        lockMan.unlock(user, lock_varname, forced);
      }
    }
  }

  // Internal methods

  private boolean changesMade(List actions) {
    Iterator it = actions.iterator();
    while (it.hasNext()) {
      ActionData data = (ActionData) it.next();
      ActionIF action = data.getAction().getAction();
      if (action.getClass().equals(DummyAction.class) ||
          action.getClass().equals(DefaultAction.class))
        continue;

      return true; // a non-dummy action will be run
    }
    return false; // couldn't find a non-dummy action
  }

  private void logrequest(Parameters params) {
    Iterator it = params.getNames().iterator();
    while (it.hasNext()) {
      String paramname = (String) it.next();
      logger.debug("Param '" + paramname + "': '"
          + DebugUtils.toString(params.getValues(paramname)) + "'");
    }
  }

  private void errorLogrequest(Parameters params, String indentation) {
    Iterator it = params.getNames().iterator();
    while (it.hasNext()) {
      String paramname = (String) it.next();
      logger.error(indentation + "Param '" + paramname + "': '"
          + DebugUtils.toString(params.getValues(paramname)) + "'");
    }
  }

  private boolean isValueEqual(String param, String[] values, Set value) {
    // value can be:
    // - AlwaysDifferentObject (meaning, say no, and run the action)
    // - Set String (checkbox)
    // - Set String (list)
    // - Set String (field)
    // - Set null (button; will be different if button were pressed)
    Set current = new HashSet();
    if (values == null)
      current.add(null);
    else
      for (int ix = 0; ix < values.length; ix++)
        current.add(values[ix]);

    boolean result = value.equals(current);
    logger.debug("Action " + param + " had value " + value + "; now " + current
        + (result ? "; will not run" : "; will run"));
    return result;
  }

  // 'actions' list contains ActionData
  // actionGroup knows the correct order of the ActionIFs in the ActionData
  // returns list of ActionData
  private List sortActions(List actions, ActionGroupIF actionGroup) {
    List sorted = new ArrayList();

    Iterator it = actionGroup.getActions().iterator();
    while (it.hasNext()) {
      ActionInGroup action = (ActionInGroup) it.next();

      Iterator it2 = actions.iterator();
      while (it2.hasNext()) {
        ActionData data = (ActionData) it2.next();
        if (action.equals(data.getAction()))
          sorted.add(data);
      }
    }

    return sorted;
  }
}

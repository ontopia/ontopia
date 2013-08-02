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

package net.ontopia.topicmaps.webed.taglibs.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.webed.impl.basic.ActionDataSet;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPageIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionGroupIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.utils.ActionData;
import net.ontopia.topicmaps.webed.impl.utils.LockResult;
import net.ontopia.topicmaps.webed.impl.utils.NamedLockManager;
import net.ontopia.topicmaps.webed.impl.utils.TagUtils;
import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.VelocityContext;

/**
 * INTERNAL: Custom tag that represents an (HTML) input form holding different
 * elements for modification of topic map object values.
 */
public class FormTag extends BodyTagSupport {
  // initialization of logging facility
  private static final String CATEGORY_NAME = FormTag.class.getName();
  private static Logger logger = LoggerFactory.getLogger(CATEGORY_NAME);

  /**
   * The default location where the velocity template can be retrieved from.
   */
  protected final static String TEMPLATE_FILE = "form.vm";

  /**
   * The attribute key under which the associated bean giving access to this
   * form is stored.
   */
  protected final static String NAME = Constants.FORM_EDIT_NAME;

  /**
   * The id of the tag. This value will be used as the value of an ID attribute
   * in the generated output.
   */
  protected String idattr;
  protected String klass;

  /**
   * State of the form that specifies whether it is read-only or not.
   */
  protected String readonly;

  /**
   * The action group this form uses, important for the contained input fields
   * and buttons.
   */
  protected String actiongroup;

  /**
   * (Optional) Name of variable which should be tried to lock, because of the
   * modifications that may be involved in this form.
   */
  protected String lockVarname;
  protected Boolean nested;
  
  /**
   * If a action URI is specified directly the default process servlet ACTION is
   * overwritten.
   * 
   * @see net.ontopia.topicmaps.webed.impl.basic.Constants#PROCESS_SERVLET
   */
  protected String action_uri;

  /**
   * If target is specified it will override the default TARGET.
   */
  protected String target;

  /**
   * The name of the field to receive focus, if any.
   */
  protected String focus; // currently unused (not set)

  protected String enctype;
  protected String requestId;
  private Collection validationRules;
  private boolean outputSubmitFunc = false;
  public static final String REQUEST_ID_ATTRIBUTE_NAME = "FormTag."
      + Constants.RP_REQUEST_ID;

  public int doStartTag() throws JspException {
    NavigatorPageIF contextTag = FrameworkUtils.getContextTag(pageContext);
    if (contextTag == null)
      throw new JspTagException("<webed:form> must be nested"
          + " within a <tolog:context> tag, but no"
          + " <tolog:context> was found.");

    if (TagSupport.findAncestorWithClass(this, FormTag.class) != null)
      throw new JspTagException("<webed:form> cannot be nested");

    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

    boolean readonly =
      InteractionELSupport.getBooleanValue(this.readonly, false, pageContext);
    request.setAttribute(Constants.OKS_FORM_READONLY, new Boolean(readonly));

    // put the name of the action group to page scope
    // to allow child tags to access this information
    TagUtils.setActionGroup(pageContext, actiongroup);

    TagUtils.setCurrentFormTag(request, this);

    requestId = TagUtils.createRequestId();

    // -- try to lock variable
    UserIF user = FrameworkUtils.getUser(pageContext);
    if (lockVarname != null) {
      ActionRegistryIF registry = TagUtils.getActionRegistry(pageContext);
      if (registry == null)
        throw new JspException("No action registry! Check actions.xml for " +
                               "errors; see log for details.");
      
      Collection lockColl = InteractionELSupport
          .extendedGetValue(lockVarname, pageContext);

      NamedLockManager lockMan = TagUtils
          .getNamedLockManager(pageContext.getServletContext());
      LockResult lockResult = lockMan.attemptToLock(user, lockColl,
          lockVarname, pageContext.getSession());
      lockVarname = lockResult.getName();
      
      Collection unlockable = lockResult.getUnlockable();
      request.setAttribute(Constants.LOCK_RESULT, lockResult);
      
      if (!unlockable.isEmpty()) {
        logger.warn("Unable to lock contents of variable '" + lockVarname
            + "'." + unlockable);
        // forward to error page if variable is locked
        ActionGroupIF ag = registry.getActionGroup(actiongroup);
        ActionForwardPageIF forwardPage = ag.getLockedForwardPage();
        if (forwardPage != null && forwardPage.getURL() != null) {
          String fwd_url = forwardPage.getURL();
          logger.info("Forward to lock error page: " + fwd_url);
          try {
            ((HttpServletResponse) pageContext.getResponse())
                .sendRedirect(fwd_url);
          } catch (IOException ioe) {
            logger.error("Problem occurred while forwarding: "
                + ioe.getMessage());
            throw new JspException("I/O-Problem while forwarding to '"
                + fwd_url + "': " + ioe);
          }
          return SKIP_PAGE;
        } else {
          logger
              .warn("No forward page found for lock situation. Setting form to be read-only");
          request.setAttribute(Constants.OKS_FORM_READONLY, Boolean.TRUE);
        }
      } else {
        logger.info("Locked contents of variable '" + lockVarname + "'.");
      }
    }

    // register a new action data set
    pageContext.setAttribute(FormTag.REQUEST_ID_ATTRIBUTE_NAME, requestId,
        PageContext.REQUEST_SCOPE);
    TagUtils.createActionDataSet(pageContext);

    return EVAL_BODY_BUFFERED;
  }
  
  /**
   * Renders the input form element with it's content.
   */
  public int doAfterBody() throws JspException {
    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

    TagUtils.setCurrentFormTag(request, null);

    VelocityContext vc = TagUtils.getVelocityContext(pageContext);
    // attributes of the form element
    vc.put("name", NAME);
    String id = idattr;
    if (id == null)
      id = requestId;
    // Use requestId by default, to make sure there always is an id.
    vc.put("idattr", id);
    // -- class
    if (klass != null) vc.put("class", klass);

    validationRules = getFieldValidationRules();
    if (!validationRules.isEmpty()) {
      vc.put("performFieldValidation", Boolean.TRUE);
      vc.put("validationRules", validationRules);
      vc.put("onsubmit", "return validate('" + id + "');");
    } else vc.put("onsubmit", "return true;");

    vc.put("outputSubmitFunc", getOutputSubmitFunc());
    
    // reset the outputSubmitFunc variable
    setOutputSubmitFunc(false);
    
    // -- action
    String context_name = request.getContextPath();
    String default_action = context_name + "/" + Constants.PROCESS_SERVLET;
    vc.put("action", (action_uri != null) ? action_uri : default_action);
    ActionRegistryIF registry = TagUtils.getActionRegistry(pageContext);
    // -- target (only in the case of a multi framed web app)
    if (registry == null)
      throw new JspException(
          "No action registry available! Check actions.xml for errors; see log for details.");
    vc.put("target", target);
    // -- enctype
    if (enctype != null)
      vc.put("enctype", enctype);
    // -- nested
    if (nested != null)
      vc.put("nested", nested);

    if (lockVarname != null)
      vc.put(Constants.RP_LOCKVAR, lockVarname);

    NavigatorPageIF contextTag = FrameworkUtils.getContextTag(pageContext);

    // add hidden parameter value pairs to identify the request
    String topicmap_id = request.getParameter(Constants.RP_TOPICMAP_ID);
    if (topicmap_id == null && contextTag != null) {
      // if not set try to retrieve it from the nav context
      NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
      TopicMapIF tm = contextTag.getTopicMap();
      if (tm != null)
        topicmap_id = navApp.getTopicMapRefId(tm);
    }
    vc.put(Constants.RP_TOPICMAP_ID, topicmap_id);
    vc.put(Constants.RP_TOPIC_ID, request
        .getParameter(Constants.RP_TOPIC_ID));
    vc.put(Constants.RP_ASSOC_ID, request
        .getParameter(Constants.RP_ASSOC_ID));
    vc.put(Constants.RP_ACTIONGROUP, actiongroup);
    vc.put(Constants.RP_REQUEST_ID, requestId);

    // FIXME: Do we really need this line? Probably not, since each control
    // should now itself be responisible for determining whether it should be
    // readonly. Hence the individual control can overrule the form setting.
    // vc.put("readonly", new Boolean(TagUtils.isFormReadOnly(request)));

    // content inside the form element
    BodyContent body = getBodyContent();
    vc.put("content", body.getString());

    // render JavaScript to set the input focus (if required)
    if (focus != null) {
      String focus_elem = focus;
      StringBuffer focus_ref = new StringBuffer("[");
      if (focus_elem.indexOf('[') > 0) {
        StringTokenizer st = new StringTokenizer(focus_elem, "[");
        if (st.countTokens() == 2) {
          focus_elem = st.nextToken();
          focus_ref.append(st.nextToken());
        }
      }
      vc.put("focus_elem", focus_elem);
      if (focus_ref.length() > 1)
        vc.put("focus_ref", focus_ref.toString());
      else
        vc.put("focus_ref", "");
    }

    // all variables are now set, proceed with outputting
    TagUtils.processWithVelocity(pageContext, TEMPLATE_FILE, getBodyContent().getEnclosingWriter(), vc);

    // clear read-only state
    request.removeAttribute(Constants.OKS_FORM_READONLY);

    return SKIP_BODY;
  }

  private Boolean getOutputSubmitFunc() {
    // Make sure we only output the functiononce , even if we  have multiple forms
    // within a page.
    Boolean value = (Boolean) pageContext.getRequest().getAttribute("OKS_FORM_OUTPUT_SUBMIT_FUNC");
    
    if (value == null && outputSubmitFunc) {
      pageContext.getRequest().setAttribute("OKS_FORM_OUTPUT_SUBMIT_FUNC", Boolean.FALSE);
      return Boolean.TRUE;
    }
    
    return Boolean.FALSE;
  }

  private Collection getFieldValidationRules() {

    ArrayList rules = new ArrayList();

    UserIF user = FrameworkUtils.getUser(pageContext);
    String requestId = TagUtils.getRequestId(pageContext);
    if (requestId != null) {
      ActionDataSet ads = (ActionDataSet) user.getWorkingBundle(requestId);
      for (Iterator iter = ads.getAllActionData().iterator(); iter.hasNext();) {
        ActionData data = (ActionData) iter.next();
        if (data.getMatchExpression() != null) {
          rules.add(new ValidationRule(data));
        }
      }
    }

    return rules;
  }

  /**
   * Releases any acquired resources.
   */
  public void release() {
    super.release();
    idattr = null;
    readonly = null;
    actiongroup = null;
    action_uri = null;
    target = null;
    lockVarname = null;
    enctype = null;
    outputSubmitFunc = false;
  }

  // ------------------------------------------------------------
  // tag attribute accessors
  // ------------------------------------------------------------

  public void setId(String idattr) {
    this.idattr = idattr;
  }

  public void setReadonly(String readonly) {
    this.readonly = readonly;
  }
  
  /**
   * Sets the class attribute of the tag. This value will be used as
   * the value of the 'class' attribute in the generated output.
   */
  public void setClass(String klass) {
    this.klass = klass;
  }

  public void setActiongroup(String actiongroup) {
    this.actiongroup = actiongroup;
  }

  public String getActiongroup() {
    return actiongroup;
  }

  public void setActionURI(String action_uri) {
    this.action_uri = action_uri;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public void setLock(String lock_varname) {
    this.lockVarname = lock_varname;
  }

  public void setNested(String nested) {
    this.nested = Boolean.valueOf(nested);
  }

  public void setEnctype(String enctype) {
    this.enctype = enctype;
  }

  protected void setOutputSubmitFunc(boolean b) {
    outputSubmitFunc = b;
  }

  /**
   * INTERNAL: Internal class encapsulating validation rules
   */
  public static final class ValidationRule {

    private String pattern;
    private String fieldValue;
    private String fieldName;

    public ValidationRule(ActionData data) {
      fieldName = data.getFieldName();

      Object value = data.getValue().iterator().next();
      if (value instanceof String)
        fieldValue = (String) value;
      else
        fieldValue = null;

      pattern = data.getMatchExpression();
    }

    public String getFieldName() {
      return fieldName;
    }

    public String getFieldValue() {
      return fieldValue;
    }

    public String getPattern() {
      return pattern;
    }

    public String getEscapedPattern() {
      return StringUtils.replace(StringUtils.replace(pattern, '\\', "\\\\"), '"',
          "\\\"");
    }

  } 
}

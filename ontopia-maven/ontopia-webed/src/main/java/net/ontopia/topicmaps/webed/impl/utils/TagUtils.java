
// $Id: TagUtils.java,v 1.52 2008/03/18 09:10:44 geir.gronmo Exp $

package net.ontopia.topicmaps.webed.impl.utils;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapReferenceIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.UserIF;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.utils.ContextUtils;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.query.parser.AntlrWrapException;
import net.ontopia.topicmaps.query.parser.ParseContextIF;
import net.ontopia.topicmaps.query.parser.QName;
import net.ontopia.topicmaps.schema.core.SchemaSyntaxException;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchema;
import net.ontopia.topicmaps.schema.impl.osl.OSLSchemaReader;
import net.ontopia.topicmaps.webed.impl.basic.ActionDataSet;
import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.taglibs.form.FormTag;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;
import net.ontopia.topicmaps.impl.utils.TMRevitalizer;
import net.ontopia.topicmaps.impl.utils.TMRevitalizerIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.xml.sax.Locator;

/**
 * INTERNAL: Utilities used by the tag classes.
 */
public final class TagUtils {

  // initialization of logging facility
  private static Logger log = LoggerFactory.getLogger(TagUtils.class.getName());

  /**
   * This variable is used to assign unique IDs to requests and
   * actions. Do NOT access it directly, to avoid synchronization
   * issues like bug #2021.
   */
  private static long counter = 0;
  
  /**
   * The default location where the velocity templates are stored in.
   */
  protected final static String VELOCITY_TEMPLATE_PATH = "/WEB-INF/templates/";

  /**
   * Gets the name of the action group as an attribute value (residing
   * in the page scope).
   */
  public static String getActionGroup(PageContext pageContext) {
    return (String) pageContext.getAttribute(Constants.RA_ACTIONGROUP,
                                             PageContext.REQUEST_SCOPE);
  }

  /**
   * Sets the name of the action group (an attribute is set in the
   * page scope).
   */
  public static void setActionGroup(PageContext pageContext,
                                    String actionGroup) {
    pageContext.setAttribute(Constants.RA_ACTIONGROUP, actionGroup,
                             PageContext.REQUEST_SCOPE);
  }
  
  /**
   * Gets the topic map object from the given <code>obj_name</code> by
   * requesting the context manager retrieved by the given
   * <code>pageContext</code>.
   */
  public static TMObjectIF getTMObject(PageContext pageContext,
                                       String obj_name)
    throws JspTagException {
    
    Object obj = ContextUtils.getSingleValue(obj_name, pageContext);
    if (obj == null)
      return null;
    if (!(obj instanceof TMObjectIF))
      throw new JspTagException("Object with name '" + obj_name + "' " +
                                "cannot be casted to TMObjectIF.");
    return ((TMObjectIF) obj);
  }

  public static Map getSchemaRegistry(ServletContext servletContext) {
    Map schemas = (Map)servletContext.getAttribute(Constants.AA_SCHEMAS);
    if (schemas != null)
      return schemas;

    // Read in schemas for the topicmaps and provide them to the app context
    String schemasRootDir = servletContext.getInitParameter(Constants.SCTXT_SCHEMAS_ROOTDIR);
    if (schemasRootDir != null)
      schemasRootDir = servletContext.getRealPath(schemasRootDir);
    
    schemas = new HashMap();
    if (schemasRootDir == null) {
      servletContext.setAttribute(Constants.AA_SCHEMAS, schemas);
      log.debug("No schema directory configured; registry empty");
      return schemas;
    }

    log.debug("Reading schemas from " + schemasRootDir);
    TopicMapRepositoryIF repository = NavigatorUtils.getTopicMapRepository(servletContext);
    Collection refkeys = repository.getReferenceKeys();
    Iterator iter = refkeys.iterator();
    while (iter.hasNext()) {
      String refkey = (String) iter.next();
      TopicMapReferenceIF reference = repository.getReferenceByKey(refkey);
      String tmid = reference.getId();
      try {
        OSLSchemaReader reader = new OSLSchemaReader(new File(schemasRootDir, tmid + ".osl"));
        OSLSchema schema = (OSLSchema) reader.read();
        schemas.put(tmid, schema);
        log.info("Loaded schema for " + tmid);
      } catch (java.io.IOException e) {
        log.info("Warning: " + e.getMessage());
      } catch (SchemaSyntaxException e) {
        log.error("Schema syntax error: " + e.getMessage());
        Locator loc = e.getErrorLocation();
        log.error("Location: " + loc.getSystemId() + ":" + 
                  loc.getLineNumber() + ":" + loc.getColumnNumber() + ".");
      }
    }

    servletContext.setAttribute(Constants.AA_SCHEMAS, schemas);
    return schemas;
  }

  public static synchronized NamedLockManager getNamedLockManager(ServletContext servletContext) {
    String identifier = servletContext.getInitParameter("lockmanager");
    if (identifier == null) identifier = "";    
    return LockManagers.getLockManager(identifier);
  }
  
  public static ActionRegistryIF getActionRegistry(ServletRequest request)
    throws JspTagException {
    ServletContext servletContext = ((HttpServletRequest)request).getSession().getServletContext();
    ActionRegistryIF registry = (ActionRegistryIF)servletContext
      .getAttribute(Constants.AA_REGISTRY);
    if (registry != null)
      return registry;

    // Read in Action Configuration and set it to application context
    String cfgpath = servletContext.getInitParameter(Constants.SCTXT_CONFIG_PATH);
    if (cfgpath == null)
      cfgpath = "classpath:actions.xml";

    log.debug("Start reading action configuration from " + cfgpath);
    
    String str_delay = servletContext.getInitParameter(Constants.SCTXT_RELOAD_DELAY);
    long delay = 6000; // every 6 seconds by default
    if (str_delay != null) {
      try {
        delay = Long.parseLong(str_delay) * 1000; // value in milliseconds
      } catch (NumberFormatException e) {
        delay = -1;
        log.warn("Warning: Falling back to no config re-reading, " +e);
      }
    }    
    String ctxtPath = ((HttpServletRequest)request).getContextPath();
    String realpath = servletContext.getRealPath("");
    ActionConfigurator aconf =
      new ActionConfigurator(ctxtPath, realpath, cfgpath, delay);
    ActionConfigRegistrator registrator =
      new ActionConfigRegistrator(servletContext);

    //!aconf.addObserver(registrator);
    //!aconf.readAndWatchRegistry();

    // HACK to make loading config files from classpath work
    aconf.readRegistryConfiguration();
    registry = aconf.getRegistry();
    registrator.configurationChanged(registry); 
    
    log.debug("Setup action configuration for the web editor and assigned it to application context.");
    
    return registry;
  }

  /**
   * INTERNAL: Utility for attaching an ID to an action.
   */
  public static String getActionID(PageContext pageContext,
                                   String action_name, String group_name,
                                   Set value)
   throws JspTagException {
    return registerData(pageContext, action_name, group_name, (List) null, value);
  }
  
  /**
   * INTERNAL: Evaluates a string of space-separated variable names as a list
   * of collections, and returns it.
   */
  public static List evaluateParameterList(PageContext pageContext,
                                            String params)
    throws JspTagException {
    if (params != null && !params.equals(""))
       return getMultipleValuesAsList(params, pageContext);
    else
      return Collections.EMPTY_LIST;
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

    // Replace sequences of special characters like \n and \t with single space.
    // Needed since StringUtils.split() treats special characters as tokens.
    String paramsNormalized = StringUtils.normalizeWhitespace(params.trim());
    
    // get the values
    String[] names = StringUtils.split(paramsNormalized);
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
      } else
        // it's a variable name
        values = InteractionELSupport.extendedGetValue(names[i], pageContext);
      
      varlist.add(values);
    }
    return varlist;
  } 

  /**
   * INTERNAL: Creates the field name used by a particular action and
   * registers the data used by the action in the user session.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    String params, Set value)
    throws JspTagException {

    return registerData(pageContext, action_name, group_name, params, null,
                        value);
  }

  /**
   * INTERNAL: Creates the field name used by a particular action and
   * registers the data used by the action in the user session.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    String params, Set value,
                                    boolean run_if_no_changes)
    throws JspTagException {

    List paramlist = evaluateParameterList(pageContext, params);
    return registerData(pageContext, action_name, group_name, paramlist, null,
                        value, false, run_if_no_changes);
  }

  /**
   * INTERNAL: Creates the field name used by a particular action and
   * registers the data used by the action in the user session.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    String params, List sub_actions, Set value)
    throws JspTagException {
    List paramlist = evaluateParameterList(pageContext, params);
    return registerData(pageContext, action_name, group_name, paramlist,
                        sub_actions, value);
  }
  
  /**
   * INTERNAL: Creates the field name used by a particular action and
   * registers the data used by the action in the user session.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    List paramlist, Set value)
    throws JspTagException {
    return registerData(pageContext, action_name, group_name, paramlist, null,
                        value);
  }
  
  /**
   * INTERNAL: Creates the field name used by a particular action and
   * registers the data used by the action in the user session.
   */
  public static String registerData(PageContext pageContext,
                                    String action_name, String group_name,
                                    List paramlist, List sub_actions, Set value) 
    throws JspTagException {
    return registerData(pageContext, action_name, group_name, paramlist, sub_actions,
                        value, false, true);
  }

  /**
   * INTERNAL: Creates the field name used by a particular action and registers
   * the data used by the action in the user session.
   * 
   * @param pageContext the context of the page being rendered
   * @param action_name the name of the action
   * @param group_name the name of the action group
   * @param paramlist the parameters to the action (list of collections)
   * @param sub_actions the sub-actions of this action, if any
   * @param value the current value of the form control
   * @param create_ads_if_not_exists whether to create ActionDataSet
   *                                 if not found
   * @return the name of the form field that will trigger the action
   */
  private static String registerData(PageContext pageContext,
      String action_name, String group_name, List paramlist, List sub_actions,
      Set value, boolean create_ads_if_not_exists, boolean run_if_no_changes)
      throws JspTagException {
  
    // retrieve action
    ActionRegistryIF registry = getActionRegistry(pageContext);
    ActionInGroup action = ActionUtils.getAction(registry, group_name,
                                                 action_name);
    if (action == null)
      throw new JspTagException("Unknown action '" + action_name + "' in group"
          + " '" + group_name + "', please check configuration.");
  
    // compute name
    String name = action.getName() + getNextCounterId();
  
    // register action with field name
    ActionData data = new ActionData(action, TagUtils.serializeParameters(paramlist), value,
                                     sub_actions, name);
    data.setRunIfNoChanges(run_if_no_changes);
  
    ActionDataSet ads = getActionDataSet(pageContext, create_ads_if_not_exists);
    ads.addActionData(data);
  
    log.info("Attached action data to field " + name);
  
    return name;
  }

  /**
   * INTERNAL: Creates an ActionData wrapper for the given action and
   * parameters.
   *
   * @param paramlist A whitespace-separated list of navigator variable names
   */
  public static ActionData makeActionData(PageContext pageContext,
                                          String action_name, String group_name,
                                          String params)
    throws JspTagException {
    
    ActionRegistryIF registry = getActionRegistry(pageContext);
    ActionInGroup action = ActionUtils.getAction(registry, group_name, action_name);
    if (action == null)
      throw new JspTagException("Unknown action '" + action_name + "' in group" +
                                " '" + group_name + "', please check configuration.");

    List paramlist = evaluateParameterList(pageContext, params);
    return new ActionData(action, TagUtils.serializeParameters(paramlist));
  }

  /**
   * INTERNAL: Creates a new request ID, guaranteed to be unique
   * throughout the lifetime of the web application (that is, until
   * server restart).
   */
  public static String createRequestId() {
    return "rid" + Long.toString(getNextCounterId());
  }

  /**
   * INTERNAL: Produces a new unique ID and advances the internal
   * counter. Created as the easiest way to synchronize access to the
   * internal counter.
   */
  private static synchronized long getNextCounterId() {
    return counter++;
  }
  
  /**
   * INTERNAL: Retrieves the action data set for the current form.
   */
  public static ActionDataSet getActionDataSet(PageContext pageContext) {
    return getActionDataSet(pageContext, false); 
  }

  public static ActionDataSet createActionDataSet(PageContext pageContext) {
    return getActionDataSet(pageContext, true);
  }

  private static ActionDataSet getActionDataSet(PageContext pageContext,
                                               boolean create) {
    UserIF user = FrameworkUtils.getUser(pageContext);
    String requestId = TagUtils.getRequestId(pageContext);
    if (requestId == null) 
      throw new OntopiaRuntimeException("No request id assigned. Binding action "
          + "outside <webed:form> tag?");
    
    ActionDataSet ads = (ActionDataSet) user.getWorkingBundle(requestId);
            
    if (ads == null && create) {
      ads = new ActionDataSet(requestId);
      log.debug("Adding ActionDataSet to request id: " + requestId);
      user.addWorkingBundle(requestId, ads);
    } else if (ads == null && !create)
      throw new OntopiaRuntimeException("No action data set. Binding action "
          + "outside <webed:form> tag?");

    return ads;
  }
  
  // -----------------------------------------------------------------------
  // Velocity related helper methods
  // -----------------------------------------------------------------------
  
  public static VelocityContext getVelocityContext(PageContext pageContext) {
    return new VelocityContext();
  }

  protected static VelocityEngine getVelocityEngine(ServletContext scontext) {
    VelocityEngine vengine = (VelocityEngine)scontext.getAttribute(Constants.SCTXT_VELOCITY_ENGINE);

    if (vengine == null) {

      // create a new velocity engine
      vengine = new VelocityEngine();

      // --- try to get properties from an own file
      String relPath = scontext.getInitParameter(Constants.SCTXT_VELOPROPS_PATH);
      if (relPath != null) {
        String velocityPropPath = scontext.getRealPath(relPath);
        log.info("Initialising velocity from property file: " + velocityPropPath);
        // load in properties
        Properties props = new Properties();
        try {
          props.load( new java.io.FileInputStream(velocityPropPath) );
        } catch (java.io.IOException ioe) {
          throw new OntopiaRuntimeException(ioe);
        }
        // pre-cat the real directory
        String path = props.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH, null);
        if (path != null) {
          path = scontext.getRealPath(path);
          props.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);
        } else {
          // no directory set, use default loader (classpath) for default templates
          props.setProperty("resource.loader", "class");
          props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
          props.setProperty("class.resource.loader.cache", "true");
        }
        try {
          vengine.init(props);
        } catch (Exception e) {
          throw new OntopiaRuntimeException(e);
        }
      } else {
        log.info("Initializing velocity with default properties.");
        // use class resource loaders
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty("class.resource.loader.cache", "true");
        // use log4j logging system
        props.setProperty("runtime.log.system", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem" );
        props.setProperty("runtime.log.logsystem.log4j.category", "net.ontopia.velocity");
        props.setProperty("runtime.log", "velocity.log");
        try {
          vengine.init(props);
        } catch (Exception e) {
          throw new OntopiaRuntimeException(e);
        }
      }

      // add velocity engine to servlet context
      scontext.setAttribute(Constants.SCTXT_VELOCITY_ENGINE, vengine);
    }
    return vengine;
  }

  public static void processWithVelocity(PageContext pageContext, String template_file, 
                                         Writer writer, VelocityContext vc) {
    try {
      VelocityEngine vengine = getVelocityEngine(pageContext.getServletContext());
      vengine.mergeTemplate(template_file, org.apache.velocity.runtime.RuntimeSingleton.getString(Velocity.INPUT_ENCODING, Velocity.ENCODING_DEFAULT), vc, writer);
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * Gets the action registry object from application scope.
   */
  public static ActionRegistryIF getActionRegistry(PageContext pageContext)   
    throws JspTagException {

    return getActionRegistry(pageContext.getRequest());
  }

  public static String getRequestId(PageContext pageContext) {
    return (String)pageContext.getAttribute(FormTag.REQUEST_ID_ATTRIBUTE_NAME, 
                                            PageContext.REQUEST_SCOPE);
  }

  /**
   * INTERNAL: Returns true if the form is read-only.
   * @param request The current request object
   * @return a boolean object indicating whether the form is read-only or not
   */
  public static boolean isFormReadOnly(ServletRequest request) {
    Boolean value = (Boolean) request.getAttribute(Constants.OKS_FORM_READONLY);
    return value != null && value.booleanValue();
  }
 
  public static LockResult getReadOnlyLock(HttpServletRequest request) {
    LockResult retVal = (LockResult)request.getAttribute(Constants.LOCK_RESULT);
    if (retVal == null)
      log.warn("TagUtils.getReadOnlyLock returning null.");
    return retVal;
  }
  
  public static boolean isComponentReadOnly(ServletRequest request,
                                            boolean componentIsReadOnly) {
    return componentIsReadOnly || isFormReadOnly(request);
  }

  public static boolean isComponentReadOnly(PageContext pageContext,
                                            String compReadOnlyAttr) {
    ServletRequest request = pageContext.getRequest();
    if (compReadOnlyAttr == null)
      return isFormReadOnly(request);
    else if (compReadOnlyAttr.equals("false"))
      return false;
    else if (isFormReadOnly(request))
      return true;
    else
      return InteractionELSupport.getBooleanValue(compReadOnlyAttr, false, 
                                                  pageContext);
  }

  public static FormTag getCurrentFormTag(ServletRequest request) {
    return (FormTag) request.getAttribute("OKS_FORM");
  }

  public static void setCurrentFormTag(ServletRequest request, FormTag tag) {
    request.setAttribute("OKS_FORM", tag);
  }

  // -- parameter serialization and deserialization

  public static List serializeParameters(List parameters) {
    return parameters;
  }

  public static List deserializeParameters(List parameters, TopicMapIF topicmap) {
    // revitalize parameters
    TMRevitalizerIF revitalizer = new TMRevitalizer(topicmap);
    return (List)revitalizer.revitalize(parameters);
  }
}


package net.ontopia.topicmaps.webed.impl.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPage;
import net.ontopia.topicmaps.webed.impl.basic.ActionForwardPageIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionGroup;
import net.ontopia.topicmaps.webed.impl.basic.ActionGroupIF;
import net.ontopia.topicmaps.webed.impl.basic.ActionInGroup;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistry;
import net.ontopia.topicmaps.webed.impl.basic.ActionRegistryIF;
import net.ontopia.topicmaps.webed.impl.basic.Constants;
import net.ontopia.topicmaps.webed.impl.basic.FieldInformation;
import net.ontopia.topicmaps.webed.impl.basic.FieldInformationIF;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformation;
import net.ontopia.topicmaps.webed.impl.basic.ImageInformationIF;
import net.ontopia.topicmaps.webed.impl.basic.ParamRuleIF;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.SAXTracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * INTERNAL: Parse an XML instance which stores information about the
 * action configuration which builds up basically action groups hold
 * inside a so-called action registry.
 * <p>
 * Note: Assumes a well-formed and valid XML instance according the DTD.
 */
public class ActionConfigContentHandler extends SAXTracker {

  // --- define a logging category.
  static Logger log = LoggerFactory.getLogger(ActionConfigContentHandler.class.getName());

  // --- global storage
  ActionRegistryIF registry;
  String contextPath;
    
  // --- helper variables while processing
  String currentActionType;
  ActionForwardPageIF currentForwardPage;
  String currentForwardName;
  String currentForwardType;
  
  String currentActionName;
  String currentActionClass;
  String currentActionExclusive;
  ActionGroupIF currentActionGroup;
  
  // key: shortName, value: FQCN
  Map classMap;
  
  // key: forwardName, value: ForwardPageResponseComposite
  Map globalForwards;

  // key: forwardName, value: ForwardPageResponseComposite
  Map currentForwards;
  
  // key: ActionInGroup, value: ForwardPageResponseComposite
  Map currentForwardRules;
  
  // key: actionName, value: ActionInGroup
  Map currentActions;

  // key: Class, value: ActionIF
  Map actionCache;
  
  /**
   * default constructor.
   */
  public ActionConfigContentHandler(String contextPath) {
    this.contextPath = contextPath;
    this.actionCache = new HashMap();
    this.registry = new ActionRegistry();
  }

  /**
   * Gets the action registry object.
   */
  public ActionRegistryIF getRegistry() {
    return registry;
  }
  
  // --------------------------------------------------------------
  // override methods from SAXTracker
  // --------------------------------------------------------------
  
  public void startElement(String nsuri, String lname, String qname,
                           Attributes attrs) {

    try {
    
    // ==== classMap ========================================
    if (lname == "classMap") {
      classMap = new HashMap();
    } // ----------------------------------------------------
    else if (lname == "class") {
      String shortcut = attrs.getValue("", "shortcut");
      String fullname = attrs.getValue("", "fullname");
      classMap.put(shortcut, fullname);
    } // === buttonMap ======================================
    else if (lname == "image") {
      String name = attrs.getValue("", "name");
      // generate a absolute path regarding to the web application or use the absolute src if provided.
      String src = attrs.getValue("", "absolutesrc");
      if (src == null) src = contextPath + attrs.getValue("", "src");
            
      String width = attrs.getValue("", "width");
      String height = attrs.getValue("", "height");
      String border = attrs.getValue("", "border");
      String align = attrs.getValue("", "align");

      ImageInformationIF img = new ImageInformation(name, src, width,
                                                    height, border, align);
      registry.addImage(img);
      
    } // ==== fieldMap ======================================
    else if (lname == "field") {
      String name = attrs.getValue("", "name");
      String type = attrs.getValue("", "type");
      String maxlength = attrs.getValue("", "maxlength");
      String columns = attrs.getValue("", "columns");
      String rows = attrs.getValue("", "rows");
      FieldInformationIF field = new FieldInformation(name, type, maxlength,
                                                      columns, rows);
      registry.addField(field);
    } // ==== globalForwards ================================
    else if (lname == "globalForwards") {
      globalForwards = new HashMap();
    } // ----------------------------------------------------
    //                 also allowed as element in actionGroup
    else if (lname == "forward") {
      // --- required
      currentForwardName = attrs.getValue("", "name");
      String path = attrs.getValue("", "path");
      // --- optional
      // type: success|failure|all(default)
      currentForwardType = attrs.getValue("", "type");
      if (currentForwardType == null)
        currentForwardType = "all";
      // frame: edit|search
      String frame = attrs.getValue("", "frame");
      if (frame == null)
        frame = "";
      String nextActionRef = attrs.getValue("", "nextAction"); // null allowed
      String nextActionTempl = null;
      // retrieve the action belonging to this name and
      // use the action name as the nextActionTemplate
      if (nextActionRef != null) {
        ActionInGroup nextAction = (ActionInGroup) currentActions.get(nextActionRef);
        nextActionTempl = nextAction.getName();
      }
      // paramRule: short cut to class name
      String paramRuleStr = attrs.getValue("", "paramRule"); // null allowed
      ParamRuleIF paramRule = null;
      if (paramRuleStr!=null && !paramRuleStr.equals("")) {
        paramRule = (ParamRuleIF) getClassInstance(paramRuleStr);
      }
      // finally create instance

      currentForwardPage = new ActionForwardPage(getAbsolutePathFor(path), frame, nextActionTempl,
                                                 paramRule);
    } // ----------------------------------------------------
    else if (lname == "reqParam") {
      String name = attrs.getValue("", "name");
      // value is optional, if not fixed, it will be retrieved
      // from request scope.
      String value = attrs.getValue("", "value");
      currentForwardPage.addParameter(name, value);
    } // ----------------------------------------------------
    else if (lname == "actionType") {
      currentActionType = attrs.getValue("", "class");
    } // ----------------------------------------------------
    else if (lname == "actionGroup") {
      String name = attrs.getValue("", "name");
      currentActionGroup = new ActionGroup(name);
      currentActions = new HashMap();
      currentForwards = new HashMap();
    } // ----------------------------------------------------
    else if (lname == "inputField" || lname == "action"){
      currentActionName = attrs.getValue("", "name");
      currentActionClass = attrs.getValue("", "class");
      currentActionExclusive = attrs.getValue("", "exclusive");
    } // ----------------------------------------------------
    else if (lname == "forwardRules") {
      currentForwardRules = new HashMap();
    } // ----------------------------------------------------
    else if (lname == "forwardDefault") {
      // first try if attribute 'forward' is set
      String defaultForwardName = attrs.getValue("", "forward");
      if (defaultForwardName != null) {
        ForwardPageResponseComposite fpr = getForward(defaultForwardName);
        if (fpr == null)
          throw new OntopiaRuntimeException("forwardDefault had undefined forward '" +
                                            defaultForwardName + "' at " +
                                            getPosition());
        currentActionGroup.setDefaultForwardPage(fpr.getResponseType(),
                                                 fpr.getForwardPage());
      } else {
        // secondly try to resolve 'path' attribute
        String path = attrs.getValue("", "path");
        if (path == null) {
          String msg = "Either attribute 'path' or 'forward' has to be set (" +
                       getPosition() + ")";
          log.error(msg);
          throw new OntopiaRuntimeException(msg);
        }
        // paramRule: short cut to class name
        String paramRuleStr = attrs.getValue("", "paramRule"); // null allowed
        ParamRuleIF paramRule = null;
        if (paramRuleStr!=null && !paramRuleStr.equals("")) {
          paramRule = (ParamRuleIF) getClassInstance(paramRuleStr);
        }
        ActionForwardPageIF fp = new ActionForwardPage(getAbsolutePathFor(path), paramRule);
        currentActionGroup.setDefaultForwardPage(Constants.FORWARD_SUCCESS,
                                                 fp);
        currentForwardPage = fp;
      }
      
    } // ----------------------------------------------------
    else if (lname == "forwardLocked") {
      // try to resolve 'path' attribute
      String path = attrs.getValue("", "path");
      if (path == null) {
        String msg = "forwardLocked: attribute 'path' has to be specified at " +
                     getPosition();
        log.error(msg);
        throw new OntopiaRuntimeException(msg);
      }
      String frame = attrs.getValue("", "frame");
      ActionForwardPageIF fp = new ActionForwardPage(getAbsolutePathFor(path), frame);
      currentActionGroup.setLockedForwardPage(fp);
    } // ----------------------------------------------------
    else if (lname == "forwardRule") {
      String forwardName = attrs.getValue("", "forward");
      String actionName = attrs.getValue("", "action");
      ForwardPageResponseComposite fprc = getForward(forwardName);
      if (fprc == null)
        throw new OntopiaRuntimeException("forwardRule referenced undefined forward: " + forwardName + " (" + getPosition() + ")");
      currentForwardRules.put(getAction(actionName), fprc);
    }
    super.startElement(nsuri, lname, lname, attrs);

    } catch (Exception e) {
      e.printStackTrace();
      throw new OntopiaRuntimeException(e);
    }
  }

  private String getAbsolutePathFor(String path) {

    // If the passed in "path" parameter is a relative URL then
    // return it as an absolute URL with respect to the WebApp location.
    // If the passed in "path" parameter is already absolute, then
    // return it as it is.
    
    if(!path.startsWith("/")) return contextPath + "/" + path;
    return path;
  }

  
  public void endElement(String nsuri, String lname, String qname) {

    try {
      
    super.endElement(nsuri, lname, lname);

    // ======================================================
    if (lname == "forward") {
      int respType = Constants.FORWARD_GENERIC;
      if (currentForwardType.equals("success"))
        respType = Constants.FORWARD_SUCCESS;
      else if (currentForwardType.equals("failure"))
        respType = Constants.FORWARD_FAILURE;
      // ---
      ForwardPageResponseComposite fpr =
        new ForwardPageResponseComposite(currentForwardPage, respType);
      if (this.isParent("globalForwards")) {
        globalForwards.put(currentForwardName, fpr);
      } else {
        // otherwise it has to be element of actionGroup
        currentForwards.put(currentForwardName, fpr);
      }
    } // ====================================================
    else if (lname == "actionGroup") {
      registry.addActionGroup(currentActionGroup);
    } // ====================================================
    else if (lname == "action") {
      boolean exclusive = (currentActionExclusive != null &&
                           currentActionExclusive.trim().equalsIgnoreCase("true"));
      
      ActionIF action = createActionInstance(currentActionClass);
      ActionInGroup aig = new ActionInGroup(action, currentActionName, exclusive);
      currentActionGroup.addAction(aig);
      currentActions.put(currentActionName, aig);
    } // ====================================================
    else if (lname == "inputField") {
      ActionIF action = createActionInstance(currentActionClass);
      ActionInGroup aig = new ActionInGroup(action, currentActionName, false);
      currentActionGroup.addAction(aig);
    } // ====================================================
    else if (lname == "forwardRules") {
      Iterator it = currentForwardRules.keySet().iterator();
      while (it.hasNext()) {
        ActionInGroup action = (ActionInGroup) it.next();
        ForwardPageResponseComposite fpr =
          (ForwardPageResponseComposite) currentForwardRules.get(action);

        if (fpr == null)
          throw new OntopiaRuntimeException
            ("No forward page response for forward rule on " + getPosition());
        
        currentActionGroup.setForwardPage(action, fpr.getResponseType(),
                                          fpr.getForwardPage());
      }
    }
    

    } catch (Exception e) {
      e.printStackTrace();
      throw new OntopiaRuntimeException(e);
    }
  }
  
  // ----------------------------------------------------------------
  // internal helper methods
  // ----------------------------------------------------------------

  private String getPosition() {
    if (locator == null)
      return "<unknown>";
    else
      return locator.getSystemId() + ":" + locator.getLineNumber() + ":" +
             locator.getColumnNumber();
  }
  
  private ActionIF createActionInstance(String klassShortcut) {
    try {
      // try to get the class for the action classname
      Class klass = getClass(klassShortcut);
      ActionIF action = (ActionIF) actionCache.get(klass);
      if (action != null)
        return action;
      
      // try to instantiate an object of this action class
      action = (ActionIF) klass.newInstance();
      actionCache.put(klass, action);
      return action;
    } catch (Exception e) {
      // --- exceptions are of type:
      // ClassNotFoundException, NoSuchMethodException, InstantiationException,
      // IllegalAccessException, InvocationTargetException

      log.error("Cannot create action instance for '" + klassShortcut +
                "' (FQCN: " + classMap.get(klassShortcut) + ")", e );
      throw new OntopiaRuntimeException("Action object for '" + klassShortcut +
                                        "' cannot be instantiated.", e);
    }
  }
  
  private ActionInGroup getAction(String actionName) {
    ActionInGroup action = (ActionInGroup) currentActions.get(actionName);
    if (action == null)
      throw new OntopiaRuntimeException("Action " + actionName +" not found.");
    return action;
  }
  
  private ForwardPageResponseComposite getForward(String forwardName) {
    // first try to lookup local forward
    ForwardPageResponseComposite forward = (ForwardPageResponseComposite)
      currentForwards.get(forwardName);
    // if not available, try to lookup global forward
    if (forward == null)
      forward = (ForwardPageResponseComposite)
        globalForwards.get(forwardName);
    return forward;
  }

  private Class getClass(String shortcut) {
    String classname = (String) classMap.get(shortcut);
    if (classname == null) {
      log.error("Class shortname '" + shortcut + "' not declared.");
      throw new OntopiaRuntimeException("Class shortname '" + shortcut +
                                        "' not declared.");
    }
  
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return Class.forName(classname, true, classLoader);
    } catch (Exception e) {
        log.error("Cannot create instance for class " + classname);
        throw new OntopiaRuntimeException("Instantiation of " + classname
                                          +" failed.", e);
    }
  }

  private Object getClassInstance(String shortcut) {
    Class klass = getClass(shortcut);
    try {
      return klass.newInstance();
    } catch (Exception e) {
      log.error("Cannot instance for class " + classMap.get(shortcut) +
                " with empty constructor.");
      throw new OntopiaRuntimeException("Instantiation of " + shortcut
                                        +" failed.", e);
    }
  }
  
  // ----------------------------------------------------------------
  // internal helper class
  // ----------------------------------------------------------------
  
  class KeyValuePair {
    public String key;
    public String value;
    KeyValuePair(String key, String value) {
      this.key = key;
      this.value = value;
    }
  }
  
}

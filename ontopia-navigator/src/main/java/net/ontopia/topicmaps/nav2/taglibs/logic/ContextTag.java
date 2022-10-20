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

package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.NonexistentObjectException;
import net.ontopia.topicmaps.nav2.impl.basic.ContextManager;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;
import net.ontopia.topicmaps.nav2.taglibs.tolog.Context;
import net.ontopia.topicmaps.nav2.taglibs.tolog.ContextManagerMapWrapper;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;

/**
 * INTERNAL: Logic Tag for establishing the outermost lexical scope in
 * which all computation happens and the embedded tags are executed.
 * <p>
 * Use this tag as root tag for <b>all</b> tags defined in the Tag
 * Libraries of the Ontopia Navigator Framework (2nd Generation).
 */
public class ContextTag extends TagSupport
  implements TryCatchFinally, NavigatorPageIF {

  private static final long serialVersionUID = -1545242249688762632L;
  public static String TOPICMAPID_REQUEST_ATTRIBUTE = "ContextTag.topicmapid";
  
  // members
  private ContextManagerIF contextManager;
  private NavigatorApplicationIF navApp;  
  private Map functions;
  private Map queryResults;

  private TopicMapIF topicmap;
  private QueryProcessorIF queryProcessor;
  private Collection tmObjects;
  
  private String topicmapID;
  private String[] objectIDs;
  
  private DeclarationContextIF declarationContext;

  private Object pcontext; // parent context tag
  private Object pontopia; // parent ontopia attribute
  private Object pontopiacontext; // parent ontopiacontext attribute

  // tag attributes
  private String tmParamName;
  private String objParamName;
  private String varObjName;
  private String varTMName;
  private String attrTopicmapID;
  private boolean readonly = true;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    this.contextManager = new ContextManager(pageContext);
    this.functions = new HashMap();
    this.queryResults = new HashMap();

    // --- try to retrieve application wide configuration
    navApp = getNavigatorApplication();

    // retrieve parent context tag and parent ontopia attribute
    pcontext = pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY, 
                                        PageContext.REQUEST_SCOPE);
    pontopia = pageContext.getAttribute("ontopia", PageContext.REQUEST_SCOPE);
    if (pontopia == null) {
      pontopia = pageContext.getAttribute("oks", PageContext.REQUEST_SCOPE);
    }
      
    pontopiacontext = pageContext.getAttribute("ontopiacontext", PageContext.REQUEST_SCOPE);
    if (pontopiacontext == null) {
      pontopia = pageContext.getAttribute("okscontext", PageContext.REQUEST_SCOPE);
    }

    // set this instance to page context, so normal JSPs can access information
    pageContext.setAttribute(NavigatorApplicationIF.CONTEXT_KEY, this,
                             PageContext.REQUEST_SCOPE);

    // character encoding of the response stream to browser is set by
    // the framework.ResponseTag. we need to interpret it as the same.
    // see bug #541.
    ServletRequest request = pageContext.getRequest();
    String charenc = navApp.getConfiguration().getProperty("defaultCharacterEncoding");
    try {
      if (charenc != null && !charenc.equals("")) {
        JSPEngineWrapper.setRequestEncoding(request, charenc);
      }
    } catch (java.io.UnsupportedEncodingException e) {
      throw new net.ontopia.utils.OntopiaRuntimeException(e);
    }
    
    // --- Set value of topicmap ID 
    if (attrTopicmapID != null) {
      topicmapID = attrTopicmapID;
    } else {
      if (tmParamName != null) {
        // get the value from request parameter
        topicmapID = request.getParameter(tmParamName);
      } else {
        topicmapID = null;
      }
    }
    // get topicmap id from request attribute if not specified at this point
    if (topicmapID == null) {
      topicmapID = (String)request.getAttribute(ContextTag.TOPICMAPID_REQUEST_ATTRIBUTE);
    }
    
    // --- Set value(s) of object ID
    if (objParamName != null) {
      // get value(s) from request parameter
      objectIDs = request.getParameterValues(objParamName);
      if (objectIDs == null) {
        throw new NavigatorRuntimeException("Object ID is not specified "
                + "by parameter '" + objParamName
                + "', but needed to process this page.");
      }
    } else {
      objectIDs = null;
    }
    
    // --- try to retrieve RO topicmap belonging to topicmap ID
    topicmap = null;
    if (topicmapID != null) {
      topicmap = navApp.getTopicMapById(topicmapID, readonly);
      if (topicmap == null) {
        throw new NavigatorRuntimeException("Topicmap with ID '" + topicmapID +
                                            "' could not be loaded, " +
                                            "maybe wrong topicmap ID.");
      }
    }
    
    // Make the ontopia variables available to the PageContext and hence to JSTL.
    ContextManagerMapWrapper cmw = new ContextManagerMapWrapper(contextManager);
    pageContext.setAttribute("ontopia",  cmw, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("oks",  cmw, PageContext.REQUEST_SCOPE);

    // Make the ontopiacontext request attribute is available
    Context ctx = new Context(this);
    pageContext.setAttribute("ontopiacontext", ctx, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("okscontext", ctx, PageContext.REQUEST_SCOPE);

    // --- try to retrieve topic object(s) belonging to object ID(s)
    if (topicmap != null) {

      if (objectIDs == null) {
        tmObjects = Collections.EMPTY_LIST;
      } else {
        tmObjects = new ArrayList(objectIDs.length);
        for (int i = 0; i < objectIDs.length; i++) {
          TMObjectIF tmObject = NavigatorUtils.stringID2Object(topicmap, objectIDs[i]);
          if (tmObject == null) {
            throw new NonexistentObjectException(objectIDs[i], topicmapID);
          }
          tmObjects.add(tmObject);
        } // for
        contextManager.setDefaultValue(tmObjects);
        if (varObjName != null) {
          contextManager.setValue(varObjName, tmObjects);
        }
      }

      // --- setup topicmap object 
      if (objParamName == null) { 
        // set default value to this topicmap object
        contextManager.setDefaultValue(topicmap);
      }
      if (varTMName != null) {
        contextManager.setValue(varTMName, topicmap);
      }

      try {
        declarationContext = QueryUtils.parseDeclarations(topicmap, "");
      } catch (InvalidQueryException e) {
        // Do nothing ...
        // since an empty declaration should be valid for any topicmap
      }
    }
    
    return EVAL_BODY_INCLUDE;
  }

  /**
   * Process the end tag for this instance.
   */
  @Override
  public int doEndTag() throws JspTagException {
    // put out debug message
    //log.debug("\\\\\\ end of context-tag. - ");

    // return topic map to navigator application
    if (navApp != null && topicmap != null) {
      navApp.returnTopicMap(topicmap);
      navApp = null;
      topicmap = null;
      queryProcessor = null;
    }

    // put back parent context tag and ontopia attribute
    // NOTE: null values to setAttribute not allowed! bug #1551
    if (pcontext != null) {
      pageContext.setAttribute(NavigatorApplicationIF.CONTEXT_KEY, pcontext,
                               PageContext.REQUEST_SCOPE);
    } else {
      pageContext.removeAttribute(NavigatorApplicationIF.CONTEXT_KEY, 
                                  PageContext.REQUEST_SCOPE);
    }
    if (pontopia != null) {
      pageContext.setAttribute("ontopia", pontopia, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("oks", pontopia, PageContext.REQUEST_SCOPE);
    } else {
      pageContext.removeAttribute("ontopia", PageContext.REQUEST_SCOPE);
      pageContext.removeAttribute("oks", PageContext.REQUEST_SCOPE);
    }
    if (pontopiacontext != null) {
      pageContext.setAttribute("ontopiacontext", pontopiacontext, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("okscontext", pontopiacontext, PageContext.REQUEST_SCOPE);
    } else {
      pageContext.removeAttribute("ontopiacontext", PageContext.REQUEST_SCOPE);
      pageContext.removeAttribute("okscontext", PageContext.REQUEST_SCOPE);
    }

    // reset members
    contextManager = null;
    functions = null;
    queryResults = null;
    topicmap = null;
    queryProcessor = null;
    tmObjects = null;
    topicmapID = null;
    objectIDs = null;
    readonly = true;
    
    return EVAL_PAGE;
  }

  // -----------------------------------------------------------------
  // get/set methods for tag attributes
  // -----------------------------------------------------------------
  
  /**
   * Tells the tag which request parameter contains the
   * ID of the topic map in the context.
   *
   * @param tmParam String which specifies a
   *                Request parameter name.
   */
  public void setTmparam(String tmParam) {
    this.tmParamName = tmParam;
  }

  public String getTmparam() {
    return tmParamName;
  }
  
  /**
   * If set, it tells the tag what request parameter contains
   * the object ID of the object to set as the value of the default
   * variable. If not set, the default variable is set to the topic
   * map.
   *
   * @param objParam String which specifies a
   *                 Request parameter name.
   */
  public void setObjparam(String objParam) {
    this.objParamName = objParam;
  }

  public String getObjparam() {
    return objParamName;
  }
  
  /**
   * The variable name which the object will be set as the
   * value of.
   *
   * @param varObjName String which specifies a variable name
   *                for the object.
   */
  public void setSet(String varObjName) {
    this.varObjName = varObjName;
  }

  /**
   * The variable name which the topic map will be set as the
   * value of.
   *
   * @param varTMName String which specifies a variable name
   *                for the topicmap.
   */
  public void setSettm(String varTMName) {
    this.varTMName = varTMName;
  }

  /**
   * If set, the ID taken from this attribute is the ID of
   * the topic map.
   *
   * @param topicmap String which specifies a Topic map ID.
   */
  public void setTopicmap(String topicmap) {
    this.attrTopicmapID = topicmap;
  }

  /**
   * A boolean flag to tell the tag to fetch a read-only or a
   * read-write topic map.
   *
   * @param readonly boolean value; true if read-only. The default is true.
   */
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  // --- get methods for members which are not JSP tag attributes

  /**
   * Get Topic map object identifiers.
   */
  public String[] getObjectIDs() {
    return objectIDs;
  }

  /**
   * Gets the topic map objects retrieved by the context tag.
   *
   * @return collection of TopicIF objects.
   */
  public Collection getObjects() {
    return tmObjects;
  }
  
  // -----------------------------------------------------------------
  // NavigatorPageIF implementation
  // -----------------------------------------------------------------

  @Override
  public ContextManagerIF getContextManager() {
    return contextManager;
  }
 
  @Override
  public void registerFunction(FunctionIF function) {    
    functions.put(function.getName(), function);
  }

  @Override
  public void registerFunction(String name, FunctionIF function) {
    functions.put(name, function);
  }

  @Override
  public FunctionIF getFunction(String name) {
    return (FunctionIF) functions.get(name);
  }

  public void registerQueryResult(String name, QueryResultIF queryResult) {
    queryResults.put(name, queryResult);
  }

  public QueryResultIF getQueryResult(String name) {
    return (QueryResultIF) queryResults.get(name);
  }

  public TopicMapRepositoryIF getTopicMapRepository() {
    return getNavigatorApplication().getTopicMapRepository();
  }

  @Override
  public NavigatorApplicationIF getNavigatorApplication() {
    // Look up the navigator application
    return NavigatorUtils.getNavigatorApplication(pageContext);
  }

  @Override
  public NavigatorConfigurationIF getNavigatorConfiguration() {
    return getNavigatorApplication().getConfiguration();
  }

  @Override
  public TopicMapIF getTopicMap() {
    return topicmap;
  }

  public String getTopicMapId() {
    return topicmapID;
  }
  
  @Override
  public QueryProcessorIF getQueryProcessor() {
    if (queryProcessor == null) {
      // construct new query processor instance
      queryProcessor = QueryUtils.getQueryProcessor(topicmap);
    }

    return queryProcessor;
  }
  
  @Override
  public PageContext getPageContext() {
    return pageContext;
  }
  
  @Override
  public DeclarationContextIF getDeclarationContext() {
    return declarationContext;
  }
  
  public void setDeclarationContext(DeclarationContextIF declarationContext) {
    this.declarationContext = declarationContext;
  }
  
  // -----------------------------------------------------------------
  // TryCatchFinally implementation
  // -----------------------------------------------------------------

  @Override
  public void doCatch(Throwable t) throws Throwable {
    throw t;
  }

  @Override
  public void doFinally() {
    // NOTE: no need to rollback transaction because it is read-only
    if (navApp != null && topicmap != null) {
      navApp.returnTopicMap(topicmap);
      navApp = null;
      topicmap = null;
      queryProcessor = null;
    }

    // put back parent context tag and ontopia attribute
    if (pcontext != null) {
      pageContext.setAttribute(NavigatorApplicationIF.CONTEXT_KEY, pcontext,
                               PageContext.REQUEST_SCOPE);
    } else {
      pageContext.removeAttribute(NavigatorApplicationIF.CONTEXT_KEY,
                                  PageContext.REQUEST_SCOPE);
    }

    if (pontopia != null) {
      pageContext.setAttribute("ontopia", pontopia, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("oks", pontopia, PageContext.REQUEST_SCOPE);
    } else {
      pageContext.removeAttribute("ontopia", PageContext.REQUEST_SCOPE);
      pageContext.removeAttribute("oks", PageContext.REQUEST_SCOPE);
    }
    if (pontopiacontext != null) {
      pageContext.setAttribute("ontopiacontext", pontopiacontext, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("okscontext", pontopiacontext, PageContext.REQUEST_SCOPE);
    } else {
      pageContext.removeAttribute("ontopiacontext", PageContext.REQUEST_SCOPE);
      pageContext.removeAttribute("okscontext", PageContext.REQUEST_SCOPE);
    }
  }

  // -----------------------------------------------------------------
  // Unit test code
  // -----------------------------------------------------------------

  /**
   * Special setter used only for unit testing purposes. The JSP
   * containers will never call this constructor, nor should anyone
   * else.
   */
  public void _setTopicMap(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  /**
   * Special setter used only for unit testing purposes. The JSP
   * containers will never call this constructor, nor should anyone
   * else.
   */
  public void setContextManager(ContextManagerIF ctxtmgr) {
    this.contextManager = ctxtmgr;
  }

}

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

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.ModuleIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Logic Tag for creating template functions specified by a
 * module file, which may be called from elsewhere in the JSP page
 * using the <code>call</code> tag.
 * 
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.CallTag
 */
public class IncludeTag extends TagSupport {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(IncludeTag.class.getName());

  // tag attributes
  private String fileName;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    ServletContext ctxt = pageContext.getServletContext();
    
    // get Context Tag and app-wide config
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
    // try to get module from object pool otherwise load it

    try {
      if (log.isDebugEnabled()) {
        log.debug("Trying to resolve '" + fileName + "'.");
      }
      //! URL location = ctxt.getResource(fileName);

      // TODO: This code should be refactored into a separate utility
      // method, since it is generic.

      // Get the absolute path to the included file.
      String url;
      // Filename is relative to the request context path which
      // itself is relative to the webapp real path. Note that .. is
      // used because of the fact that the context path also
      // includes the webapp name.
      
      // NOTE: This sucks because all requests are not HttpServletRequests.
      ServletRequest request = pageContext.getRequest();
      if (request instanceof HttpServletRequest) {
        String realpath = ctxt.getRealPath(fileName);
        if (realpath == null) {
          throw new NavigatorRuntimeException("Could not resolve file attribute '" + fileName + "' in <logic:include> tag. Make sure that the web application is deployed in exploded mode.");
        }
        url = "file:" + realpath;
      } else {
        throw new NavigatorRuntimeException("Unsupported ServletRequest type: " + request);
      }
      
      URL location = new URL(url);      
      if (log.isDebugEnabled()) {
        log.debug("Including file: " + location);
      }
      
      ModuleIF module = navApp.getModule(location);
      // register functions and set them up
      Iterator iter = module.getFunctions().iterator();
      synchronized(module) {
        while (iter.hasNext()) {
          FunctionIF function = (FunctionIF) iter.next();
          // function.setParent() [only for root tag]
          // function.setPageContext()  [for all tags]
          contextTag.registerFunction(function);
          if (log.isDebugEnabled()) {
            log.debug("registered function: " + function.toString());
          }
        }
      }
      
    } catch (MalformedURLException e) {
      log.error("logic:include filename is not valid: " + fileName);
    }
    
    // empty tag
    return SKIP_BODY;
  }
  
  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // overwrite default behaviour
    // do not set parent to null!!!
  }
    
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Sets the filename of the module (which is a function
   * collection) to read in.
   *
   * @param fileName - The filename is interpreted relative to the URI
   *                   of the parent page.
   */
  public void setFile(String fileName) {
    this.fileName = fileName;
  }
  
}

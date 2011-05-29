
package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.ontojsp.*;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.ModuleIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.impl.basic.Function;
import net.ontopia.topicmaps.nav2.impl.basic.Module;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.utils.ontojsp.TaglibTagFactory;

import org.xml.sax.SAXException;
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
  private static Logger log = LoggerFactory.getLogger(IncludeTag.class.getName());

  // file separator character
  private final static String FILE_SEPARATOR =
    System.getProperty("file.separator");
  
  // tag attributes
  private String fileName;
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    ServletContext ctxt = pageContext.getServletContext();
    
    // get Context Tag and app-wide config
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    NavigatorApplicationIF navApp = contextTag.getNavigatorApplication();
    // try to get module from object pool otherwise load it

    try {
      if (log.isDebugEnabled())
        log.debug("Trying to resolve '" + fileName + "'.");
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
        String context_path = ((HttpServletRequest)request).getContextPath();
        String realpath = ctxt.getRealPath(fileName);
        if (realpath == null)
          throw new NavigatorRuntimeException("Could not resolve file attribute '" + fileName + "' in <logic:include> tag. Make sure that the web application is deployed in exploded mode.");
        url = "file:" + realpath;
      } else {
        throw new NavigatorRuntimeException("Unsupported ServletRequest type: " + request);
      }
      
      URL location = new URL(url);      
      if (log.isDebugEnabled())
        log.debug("Including file: " + location);
      
      ModuleIF module = navApp.getModule(location);
      // register functions and set them up
      Iterator iter = module.getFunctions().iterator();
      synchronized(module) {
        while (iter.hasNext()) {
          FunctionIF function = (FunctionIF) iter.next();
          // function.setParent() [only for root tag]
          // function.setPageContext()  [for all tags]
          contextTag.registerFunction(function);
          if (log.isDebugEnabled())
            log.debug("registered function: " + function.toString());
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
   * @param filename - The filename is interpreted relative to the URI
   *                   of the parent page.
   */
  public void setFile(String fileName) {
    this.fileName = fileName;
  }
  
}

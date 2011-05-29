
package net.ontopia.topicmaps.nav2.taglibs.logic;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.utils.ontojsp.*;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.FunctionIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.impl.basic.Function;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.utils.ontojsp.TaglibTagFactory;

import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Logic Tag for creating a template function, which may be
 * called from elsewhere in the JSP page using the <code>call</code>
 * tag.
 * 
 * @see net.ontopia.topicmaps.nav2.taglibs.logic.CallTag
 * @deprecated use net.ontopia.topicmaps.nav2.taglibs.logic.IncludeTag instead
 */
public class FunctionTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(FunctionTag.class.getName());
  
  // tag attributes
  private String functionName;
  private String paramNames;
  private Collection params;
  private String fileName;
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {
    
    // get Context Tag
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // read in function from file
    JSPTreeNodeIF root = null;
    try {
      root = readinFunction(fileName);
    } catch (IOException ioe) {
      String msg = "FunctionTag: error while reading in function file from " +
        fileName + ": " + ioe.getMessage();
      throw new NavigatorRuntimeException(msg, ioe);
    } catch (SAXException saxe) {
      String msg = "FunctionTag: error while parsing function file (" +
        fileName + "): " + saxe.getMessage();
      throw new NavigatorRuntimeException(msg, saxe);
    }
    // construct new function object and register it
    FunctionIF function = new Function(functionName, root, params);
    contextTag.registerFunction(function);
    log.debug("registered function: " + function.toString());

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
  
  /**
   * INTERNAL: read in external JSP file that will be used as a
   * function.
   */
  protected JSPTreeNodeIF readinFunction(String filename)
    throws IOException, SAXException {
    
    JSPPageReader reader = new JSPPageReader(new File(filename));
    JSPTreeNodeIF root = reader.read();
    return root;
  }
  
  // -----------------------------------------------------------------
  // set methods for tag attributes
  // -----------------------------------------------------------------

  /**
   * INTERNAL: Sets name of this function.
   */
  public void setName(String functionName) {
    this.functionName = functionName;
  }

  /**
   * INTERNAL: Sets parameter names for this function which should be
   * separated by whitespaces.
   */
  public void setParams(String paramNames) {
    // TODO: check if this is needed at all?
    //       maybe just for validation purposes?
    this.paramNames = paramNames;
    // split them up
    // params = NavigatorUtils.string2Collection(paramValueNames);
    this.params = Collections.EMPTY_LIST;
  }

  /**
   * INTERNAL: Sets the filename of the function to read in.  This is
   * relative to the URI of the parent page.
   */
  public void setFile(String relativeFileName) {
    this.fileName = relativeFileName;
  }

}

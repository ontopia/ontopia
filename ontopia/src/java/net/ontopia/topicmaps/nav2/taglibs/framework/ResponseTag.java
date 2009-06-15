
// $Id: ResponseTag.java,v 1.7 2003/02/04 10:59:29 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.framework;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;

import org.apache.log4j.Logger;

/**
 * INTERNAL: Framework related tag used for setting response headers.
 */
public class ResponseTag extends TagSupport {

  // initialization of logging facility
  private static Logger log = Logger.getLogger(ResponseTag.class.getName());

  // tag attributes
  protected String content_type;
  protected String charset;
  
  /**
   * Process the start tag for this instance.
   */
  public int doStartTag() throws JspTagException {

    // Get navigator application
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);

    // Get navigator configuration
    NavigatorConfigurationIF navConf = navApp.getConfiguration();

    // Get content type
    String ctype = content_type;
    if (content_type == null)
      ctype = navConf.getProperty(NavigatorConfigurationIF.DEF_CONTENT_TYPE, "text/html");

    // Get character encoding
    String charEnc = charset;
    if (charEnc == null)
      charEnc = navConf.getProperty(NavigatorConfigurationIF.DEF_CHAR_ENCODING, "utf-8");

    // Get response instance
    ServletResponse response = pageContext.getResponse();
    if (response != null && !response.isCommitted())

      // Set Content-type header
      if (ctype != null) {
        if (charEnc != null) {
          log.debug("set content-type to: " + ctype + "; charset=" + charEnc);
          response.setContentType(ctype + "; charset=" + charEnc);
        } else {
          log.debug("set content-type to: " + ctype);
          response.setContentType(ctype);
        }
      } else {
        log.debug("not setting content-type");
      }

    // empty tag has not to eval anything
    return SKIP_BODY;
  }
  
  // -------------------------------------------------------
  // set methods for tag attributes
  // -------------------------------------------------------

  /**
   * INTERNAL: Sets the response content type.
   */
  public void setContentType(String content_type) {
    this.content_type = content_type;
  }
  
  /**
   * INTERNAL: Set character encoding.
   */
  public void setCharset(String charset) {
    this.charset = charset;
  }
  
}

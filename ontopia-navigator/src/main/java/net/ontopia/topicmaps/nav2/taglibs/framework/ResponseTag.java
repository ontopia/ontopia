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

package net.ontopia.topicmaps.nav2.taglibs.framework;


import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorConfigurationIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Framework related tag used for setting response headers.
 */
public class ResponseTag extends TagSupport {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(ResponseTag.class.getName());

  // tag attributes
  protected String content_type;
  protected String charset;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {

    // Get navigator application
    NavigatorApplicationIF navApp = NavigatorUtils.getNavigatorApplication(pageContext);

    // Get navigator configuration
    NavigatorConfigurationIF navConf = navApp.getConfiguration();

    // Get content type
    String ctype = content_type;
    if (content_type == null) {
      ctype = navConf.getProperty(NavigatorConfigurationIF.DEF_CONTENT_TYPE, "text/html");
    }

    // Get character encoding
    String charEnc = charset;
    if (charEnc == null) {
      charEnc = navConf.getProperty(NavigatorConfigurationIF.DEF_CHAR_ENCODING, "utf-8");
    }

    // Get response instance
    ServletResponse response = pageContext.getResponse();
    if (response != null && !response.isCommitted()) {
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

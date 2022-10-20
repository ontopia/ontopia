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

package net.ontopia.topicmaps.nav.taglibs.template;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;

/**
 * INTERNAL: Gets a string for use in a JSP template (view) via a key
 * defined in a "put" tag on the original JSP (model).
 *
 * <h3>Example</h3>
 * <pre>
 * &lt;html&gt;
 *   &lt;head&gt;
 *     &lt;title&gt;
 *       &lt;template:get name='title'/&gt;
 *     &lt;/title&gt;
 *   &lt;/head&gt;
 *   ...
 * </pre>
 */
public class GetTag extends AbstractTemplateTag {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(GetTag.class.getName());

  private boolean fallback = false;

  /**
   * Sets the name of the string to "get". It must match a value set
   * already by the PutTag.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the flag that defines whether the tag content should be used
   * as fallback if no corresponding PutTag was found.
   * @since 5.4.0
   */
  public void setFallback(boolean fallback) {
    this.fallback = fallback;
  }

  @Override
  public int doStartTag() throws JspException {
    if (log.isDebugEnabled()) {
      log.debug("doStartTag, name: " + name);
    }

    PageParameter param = getParameter();
    if (param == null) {
      return fallback ? EVAL_BODY_BUFFERED : SKIP_BODY;
    }
    
    String content = param.getContent();
    if (content == null) {
      content = "";
    }

    int splitpos = content.indexOf(SplitTag.TOKEN);

    if (splitpos != -1) {
      try {
        pageContext.getOut().print(content.substring(0, splitpos));
        if (log.isDebugEnabled()) {
          log.debug(name + ": wrote " + splitpos + " chars");
        }
      }
      catch(java.io.IOException ex) {
        throw new NavigatorRuntimeException("Exception occurred when writing content.", ex);
      }
      return EVAL_BODY_BUFFERED;
    } else {
      return SKIP_BODY;
    }
  }

  @Override
  public int doEndTag() throws JspException {
    if (log.isDebugEnabled()) {
      log.debug("doEndTag, name: " + name);
    }
    
    PageParameter param = getParameter();
    if (param == null) {
      if (fallback) {
        try {
          getBodyContent().writeOut(pageContext.getOut());
        } catch(java.io.IOException ex) {
          throw new NavigatorRuntimeException("Exception occurred when writing content.", ex);
        }
      }
      resetMembers();
      return EVAL_PAGE;
    }
    
    String content = param.getContent();
    if (content == null) {
      content = "";
    }

    int splitpos = content.indexOf(SplitTag.TOKEN);
    
    if (param.isDirect()) {
      try {
        if (splitpos == -1) {
          pageContext.getOut().print(content);
          if (log.isDebugEnabled()) {
            log.debug(name + ": wrote " + content.length() + " chars");
          }
        } else {
          BodyContent bodyContent = getBodyContent();
          bodyContent.writeOut(pageContext.getOut());

          int start = splitpos + SplitTag.TOKEN.length();
          pageContext.getOut().print(content.substring(start));
          if (log.isDebugEnabled()) {
            log.debug(name + ": wrote " + (content.length() - start) + " chars (split)");
          }
        }
      }
      catch(java.io.IOException ex) {
        throw new NavigatorRuntimeException("Exception occurred when writing content.", ex);
      }
    } else {
      if (splitpos != -1) {
        throw new JspException("Split slots must be direct");
      }

      try {
        // --> java.io.IOException: Illegal to flush within a custom tag 
        // pageContext.getOut().flush();
        // ** System.out.println("---before include " + content + ".");
        if (log.isDebugEnabled()) {
          log.debug("including resource '" + content + "'.");
        }
        pageContext.include(content);
        // ** System.out.println("---after include " + content + ".");
        // ================ NOTE =======================================
        // This should throw an IOException if resource is not available
        // Tomcat 4.x has problems, see bug report at
        // http://nagoya.apache.org/bugzilla/show_bug.cgi?id=8200
      }
      catch(Exception ex) {
        throw new NavigatorRuntimeException("Exception occurred when including content.", ex);
      }
    }

    resetMembers();
    return EVAL_PAGE;
  }
  
  private void resetMembers() {
    // tag attributes;
    name = null;
    fallback = false;
  }

}

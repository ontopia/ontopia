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

/**
 * INTERNAL: Labels a string on a JSP content (model) page which can be
 * used by GetTag for use on a template (view) page.
 * 
 * <h3>Examples</h3>
 * <li><h4>Including the body:</h4>
 * <code>
 * &lt;template:put name='title'&gt;My Title&lt;/template:put&gt;
 * </code></li>
 *
 * <li><h4>Including the value of the "content" attribute:</h4>
 * <code>
 * &lt;template:put name='title' direct="true" content='My Title'/&gt;
 * </code></li>
 *
 * <li><h4>Including a file:</h4>
 * <code>
 * &lt;template:put name='title' content='/fragments/mytitle.txt'/&gt;
 * </code></li>
 */
public class PutTag extends AbstractTemplateTag {
  
  // Define a logging category.
  private static final Logger log = LoggerFactory.getLogger(PutTag.class.getName());

  // tag attributes
  private String content;
  private boolean direct;

  @Override
  public int doStartTag() throws JspException {
    if (content != null) {
      if (log.isDebugEnabled()) {
        log.debug("doStartTag: register variable '"+name+"'.");
      }

      putParameter(content, direct);
      
      return SKIP_BODY;
    } else {
      if (log.isDebugEnabled()) {
        log.debug("doStartTag: evaluate body");
      }
      return EVAL_BODY_BUFFERED;
    }
  }

  @Override
  public int doAfterBody() throws JspException {
    BodyContent bodyContent = getBodyContent();
    String content = bodyContent.getString();

    // save body content into params after evaluation
    if (log.isDebugEnabled()) {
      log.debug("doAfterBody: register variable '"+name+"'.");
    }

    putParameter(content, true);

    bodyContent.clearBody();
    return SKIP_BODY;
  }

  @Override
  public int doEndTag() {
    resetMembers();
    return EVAL_PAGE;
  }

  // --- Attribute setters
  
  /**
   * Sets the name of the string.
   */
  public void setName(String s) {
    name = s;
  }
  
  /**
   * Sets the content of the string which can either be a path to a
   * file or the string itself, depending on the value of the "direct"
   * attribute.
   */
  public void setContent(String s) {
    content = s;
  }
  
  /**
   * Sets a flag which, if set to "true", will interpret the content
   * string directly.  If not, the tag expects a file path to be in
   * content. Default value is false.
   */
  public void setDirect(String s) {
    direct = s.equalsIgnoreCase("true");
  }
  
  /** 
   * Sets a flag which, if set to "true", will make the tag ignore the
   * "content" attribute and use the body content of the tag. Default
   * value is false.
   *
   * @deprecated attribute no longer neccessary to use becaue the
   * existence of a content attribute tells the tag whether to use the
   * body content or not.
   */
  public void setBody(String s) {
    // ignore
  }
  
  // --- Internal
  
  private void resetMembers() {
    // tag attributes
    name = null;
    content = null;
    direct = false;
  }
  
}

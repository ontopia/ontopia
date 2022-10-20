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

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Tolog Tag for testing if a variable is bound and non-null
 * or if a query gives a non-empty result.  If the variable is bound
 * and non-null, then the body is evaluated once.  If the query-result
 * is non-empty, binds the query variables to the first row and
 * evaluates the body once.
 */
public class IfTag extends QueryExecutingTag {

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(IfTag.class.getName());

  // tag attributes
  protected String var;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    if (contextTag == null) {
      throw new JspTagException("<tolog:if> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    }
    
    this.contextManager = contextTag.getContextManager();
    
    // establish new lexical scope
    contextManager.pushScope();
  
    if (query == null && var == null) {
      throw new JspTagException("<tolog:if> requires either a 'query' - "
              + "or a 'var' parameter, but got neither.\n");
    }
    if (var != null && query != null) {
      throw new JspTagException("<tolog:if> requires either a 'query' - "
              + "or a 'var' parameter, but got both.\n");
    }

    if (var != null) {                                      
      try {
        // If value of var is bound, then evaluate body.
        if (contextManager.getValue(var) == null ||
            contextManager.getValue(var).isEmpty()) {
          return SKIP_BODY;
        }
        return EVAL_BODY_BUFFERED;
      } catch (VariableNotSetException e) {
        return SKIP_BODY;
      }
    }
    // query != null   must be true. 
    super.doStartTag();
    
    // do not proceed if queryResult is empty
    if (queryResult.next()) {
      bindVariables();
      return EVAL_BODY_BUFFERED;
    }
    if (log.isDebugEnabled()) {
      log.debug("Empty query result : '" + query + "'");
    }
    return SKIP_BODY;
  }

  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    // establish old lexical scope, back to outside of the loop
    contextManager.popScope();

    return super.doEndTag();
  }
  
  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    super.release();
    
    // do not reset tag attributes (causes problems with tag pooling)
    // do not set parent to null!!!
  }
  
  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------
  
  public void setVar(String var) {
    this.var = var;
  }

}

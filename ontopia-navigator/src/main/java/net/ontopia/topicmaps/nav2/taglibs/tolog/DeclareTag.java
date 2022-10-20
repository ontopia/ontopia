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
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.utils.QueryUtils;

/**
 * INTERNAL: Tolog Tag for making a set of tolog declarations
 * available within the nearest ancestor ContextTag.
 */
public class DeclareTag extends BodyTagSupport {
  
  protected String declarations;
  
  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    return EVAL_BODY_BUFFERED;
  }
  
  /** 
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    declarations = getBodyContent().getString();
    return SKIP_BODY;
  }
  
  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    
    if (contextTag == null) {
      throw new JspTagException("<tolog:declare> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    }
    
    // get topicmap object on which we should compute 
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new NavigatorRuntimeException("DeclareTag found no "
              + "topic map.");
    }

    DeclarationContextIF declarationContext = contextTag
            .getDeclarationContext();
    try {
      declarationContext = QueryUtils.parseDeclarations(topicmap, declarations,
              declarationContext);
      
    } catch (InvalidQueryException e) {
      throw new JspTagException(e.getMessage());
    }
    contextTag.setDeclarationContext(declarationContext);
                  
    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    super.release();
  }
}

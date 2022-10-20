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
import javax.servlet.jsp.tagext.TagSupport;
import net.ontopia.topicmaps.nav2.impl.basic.JSPEngineWrapper;

/**
 * INTERNAL: Tag used to indicate where a nested template is to be split.
 *
 * @since 2.0
 */
public class SplitTag extends TagSupport {
  public static final String TOKEN =
    "^|~---------net.ontopia.topicmaps.nav.taglibs.template.SplitTag--------~|^";
  
  @Override
  public int doStartTag() throws JspException {
    PutTag parent = (PutTag) findAncestorWithClass(this, PutTag.class);
    if (parent == null) {
      throw new JspException("Split tag has no template:put ancestor.");
    }

    try {
      pageContext.getOut().print(TOKEN);
    } catch (java.io.IOException e) {
      throw JSPEngineWrapper.getJspException("Error writing split tag token", e);
    }
    
    return SKIP_BODY;
  }

  @Override
  public int doEndTag() {
    return EVAL_PAGE;
  }

}

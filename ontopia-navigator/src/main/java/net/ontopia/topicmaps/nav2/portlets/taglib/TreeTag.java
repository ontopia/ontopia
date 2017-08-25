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

package net.ontopia.topicmaps.nav2.portlets.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.TreeWidget;

public class TreeTag extends TagSupport {
  private String topquery;
  private String query;
  private String ownpage;
  private String nodepage;
  private String imageurl;

  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    TopicMapIF tm = contextTag.getTopicMap();

    TreeWidget widget = new TreeWidget(tm, query, topquery,
                                       ownpage, nodepage);
    widget.setImageUrl(imageurl);
    try {
      widget.run(pageContext, pageContext.getOut());
    } catch (IOException e) {
      throw new JspTagException(e.toString());
    } catch (InvalidQueryException e) {
      throw new JspTagException(e.toString());
    }
    
    return SKIP_BODY;
  }

  @Override
  public void release() {
    // no-op
  }

  // --- Setters

  public void setTopquery(String topquery) {
    this.topquery = topquery;
  }
  
  public void setQuery(String query) {
    this.query = query;
  }

  public void setOwnpage(String ownpage) {
    this.ownpage = ownpage;
  }
  
  public void setNodepage(String nodepage) {
    this.nodepage = nodepage;
  }

  public void setImageUrl(String imageurl) {
    this.imageurl = imageurl;
  }
}

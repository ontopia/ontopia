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
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL.
 */
public class QueryTag extends BodyTagSupport {

  private static final long serialVersionUID = -2505209031791690173L;

  // initialization of logging facility
  protected static Logger log = LoggerFactory.getLogger(QueryTag.class.getName());

  // tag attributes
  protected String query;
  protected String name;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    if (contextTag == null) {
      throw new JspTagException("<tolog:query> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> tag was found.");
    }

    contextTag.getContextManager().pushScope();

    if (name == null) {
      throw new JspTagException("<tolog:query> : Missing 'name'"
              + " attribute.\n");
    }

    return EVAL_BODY_BUFFERED;
  }

  /**
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    query = getBodyContent().getString();
    return SKIP_BODY;
  }

  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

    // Get the TopicMap from the context.
    TopicMapIF topicmap = contextTag.getTopicMap();
    if (topicmap == null) {
      throw new JspTagException("<tolog:query> : found no topic map.\n");
    }

    // Create a QueryProcessorIF for the topicmap.
    QueryProcessorIF queryProcessor = contextTag.getQueryProcessor();

    QueryResultIF queryResult;
    try {
      // Execute query, using any arguments from the context manager.
      queryResult = new BufferedQueryResult(queryProcessor.execute(query,
          new ContextManagerScopingMapWrapper(contextTag
              .getContextManager()), contextTag.getDeclarationContext()),
              query);
    } catch (InvalidQueryException e) {
      log.info("Parsing of query \"" + query + "\" failed with message: "
              + e);
      throw new NavigatorRuntimeException(e);
    }

    contextTag.registerQueryResult(name, queryResult);

    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // reset members

    // do *not* reset tag attributes
    // do not set parent to null!!!
  }

  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------

  public void setName(String name) {
    this.name = name;
  }
}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.utils.NavigatorUtils;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Tolog Tag for executing a query,
 * iterating over each object in a result collection and
 * creating new content for each iteration.
 */
public class SetTag extends QueryExecutingTag { //BodyTagSupport {

  private static final long serialVersionUID = -3009179502068590303L;

  // initialization of logging facility
  private static final Logger log = LoggerFactory.getLogger(SetTag.class.getName());

  // FIXME: replace this ugliness with a Map (but wait for tests)
  private static final String scopeNames[] = {
    "application",
    "session",
    "page",
    "request"
  };
  private static final int scopes[] = {
    PageContext.APPLICATION_SCOPE,
    PageContext.SESSION_SCOPE,
    PageContext.PAGE_SCOPE,
    PageContext.REQUEST_SCOPE
  };

  // members
  private Collection outValue;
  private String clonedVar;

  // tag attributes
  private String reqparam;
  private String scope;
  private Object value;
  private String var;


  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
    if (contextTag == null) {
      throw new JspTagException("<tolog:set> must be nested directly or"
              + " indirectly within a <tolog:context> tag, but no"
              + " <tolog:context> was found.");
    }

    // Get the TopicMap from the context.
    TopicMapIF topicmap = contextTag.getTopicMap();

    // Check that a valid combination of input parameters has been used.
    // Otherwise, JspTagException is thrown with a suitable error message.
    validateInputAttributes();

    clonedVar = var;

    if (query == null) {
      outValue = new ArrayList();
      if (reqparam != null) {
        String ids[] = pageContext.getRequest().getParameterValues(reqparam);

        if (ids == null || ids.length == 0) {
          outValue = Collections.EMPTY_LIST;
        } else {
          if (topicmap == null) {
            throw new JspTagException("<tolog:set> found no topic map");
          }
          DeclarationContextIF declarationContext = contextTag.getDeclarationContext();
          for (int i= 0; i < ids.length; i++) {
            TMObjectIF tempOutValue =
                    NavigatorUtils.stringID2Object(topicmap, ids[i], declarationContext);
            if (tempOutValue != null) {
              outValue.add(tempOutValue);
            }
          }
        }

      } else if (value != null) {
        if (value instanceof Collection) {
          outValue.addAll((Collection)value);
        } else {
          outValue.add(value);
        } 
      } else {
        return EVAL_BODY_BUFFERED;
      }
    } else {
      log.debug("Set found query");     
      // Create a QueryProcessorIF for the topicmap.
      if (topicmap == null) {
        throw new JspTagException("<tolog:set> found no topic map");
      }
      super.doStartTag();

      if (queryResult.getWidth() == 0) {
        throw new NavigatorRuntimeException("<tolog:set> : got a query result"
                + " with zero columns, instead of the expected: one."
                + "\nPlease check the query.");
      }

      if (queryResult.getWidth() >= 2) {
        throw new NavigatorRuntimeException("<tolog:set> : got a query result"
                + " with more than one column, instead of the expected: one."
                + " Please check the query.");
      }

      // Get 'outValue' from the first column of 'queryResult'.
      outValue = getColumn(queryResult, 0);
      // If no variable name given, use the first (only) column from the query.
      if (var == null) {
        clonedVar = queryResult.getColumnName(0);
      }
    }

    return SKIP_BODY;
  }

  /**
   * Check that the a correct combination of input parameters has been used.
   */
  private void validateInputAttributes()  throws JspTagException {
    if (query == null) {
      if (var == null) {
        throw new JspTagException("<tolog:set> : requires a 'var'- or a"
                + " 'query'-attribute (or both), but got neither.");
      }

      if (!(reqparam == null || value == null)) {
        throw new JspTagException("<tolog:set> : requires either a"
                + " 'query'-, a 'reqparam'- or a 'value'-attribute,"
                + " but got both.");
      }

    } else {
      if (reqparam != null || value != null) {
        throw new JspTagException("<tolog:set> : requires either a"
                + " 'query'-, a 'reqparam'- or a 'value'-attribute,"
                + " but got more than one of them.");
      }
    }
  }

  /**
   * Set a variable in the jstl environment to a given value.
   */
  private void setJstl(String var, Object val, int scope) {
    pageContext.setAttribute(var, val, scope);
  }

  /**
   * Set a variable in the ontopia environment to a given value.
   */
  private void setOntopia(String var, Object val, ContextManagerIF ctxmgr) {
    if (val instanceof Object[]) {

      Object[] jstlArray = (Object[])val;

      // Let the context manager create a collection to hold the array values.
      // Need one value to do this, so use first value in jstlArray (or null).
      ctxmgr.setValue(var, (jstlArray.length == 0)
              ? null
              : jstlArray[0]);
      Collection ontopiaValue = ctxmgr.getValue(var);

      // Add the rest of the values in jstlArray to ontopiaValue.
      for (int i = 1; i < jstlArray.length; i++) {
        ontopiaValue.add(jstlArray[i]);
      }
    } else if (val instanceof Collection) {
      ctxmgr.setValue(var, (Collection)val);
    } else {
      ctxmgr.setValue(var, val);
    }
  }

  private static int mapScope(String scopeName) throws JspTagException {
    for (int i = 0; i < scopeNames.length; i++) {
      if (scopeName.equals(scopeNames[i])) {
        return scopes[i];
      }
    }
    throw new JspTagException("Unrecognised scope attribute in <tolog:set ..."
            + "scope=\"" + scopeName + "\"");
  }

  /**
   * Get each value of a given column from queryResult, and make a collection
   * with the contents of that column.
   */
  private Collection getColumn(QueryResultIF queryResult, int column) {
    Collection retVal = new ArrayList();

    while (queryResult.next()) {
      retVal.add(queryResult.getValue(column));
    }

    return retVal;
  }
  
  /**
   * Actions after some body has been evaluated.
   */
  @Override
  public int doAfterBody() throws JspTagException {
    outValue = new ArrayList();
    // FIXME: It would be nice if the following if-test could be true only for
    // bodies that are actually empty (not the ones that just generate a 
    // zero-length string).
    if (!getBodyContent().getString().equals("")) {
      outValue.add(getBodyContent().getString());
    }
    
    return SKIP_BODY;
  }

  /**
   * Process the end tag.
   */
  @Override
  public int doEndTag() throws JspException {
    // Bind 'outValue' to var in appropriate scope.
    if (scope == null || scope.equals("ontopia") || scope.equals("oks")) {
      ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);
      ContextManagerIF ctxmgr = contextTag.getContextManager();
      setOntopia(clonedVar, outValue, ctxmgr);
    } else {
      setJstl(clonedVar, outValue, mapScope(scope));
    }

    return EVAL_PAGE;
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    // do *not* reset tag attributes
  }

  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------

  public void setReqparam(String reqparam) {
    this.reqparam = reqparam;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public void setVar(String var) {
    this.var = var;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}

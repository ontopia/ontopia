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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Abstract super-class of an Output-Producing Tag.
 */
public abstract class BaseOutputProducingTag extends TagSupport {

  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(BaseOutputProducingTag.class.getName());

  // members
  protected boolean escapeEntities;

  // tag attributes
  protected String variableName;
  protected String query;
  protected String fallbackValue;

  protected abstract String getName();

  public BaseOutputProducingTag() {
    this(true);
  }

  protected BaseOutputProducingTag(boolean escapeEntities) {
    log.debug("Constructing");
    // Whether the generated String should be escaped to care about
    // HTML/XML entities or not.
    this.escapeEntities = escapeEntities;
  }

  /**
   * Process the start tag for this instance.
   */
  @Override
  public int doStartTag() throws JspTagException {
    try {
      Object outObject = generateOutputObject();

      JspWriter out = pageContext.getOut();
      generateOutput(out, outObject);
    } catch (IOException ioe) {
      String msg = "Error in " + getName() + ": " + ioe.getMessage();
      log.error(msg);
      throw new NavigatorRuntimeException(msg, ioe);
    }

    return SKIP_BODY;
  }

  @Override
  public final int doEndTag() {
    return EVAL_PAGE;
  }

  /**
   * reset the state of the Tag.
   */
  @Override
  public void release() {
    // no-op
  }

  public abstract void generateOutput(JspWriter out, Object outObject)
    throws JspTagException, IOException;

  /**
   * Get the object to write out (either through a variable or query).
   */
  public Object generateOutputObject() throws JspTagException {
    if (query == null && variableName == null) {
      throw new NavigatorRuntimeException(getName() + " : requires"
              + " either a 'query' - or a 'var' parameter, but got"
              + " neither.\n");
    }
    if (variableName != null && query != null) {
      throw new NavigatorRuntimeException(getName() + " : requires"
              + " either a 'query' - or a 'var' parameter, but got both.\n");
    }

    Object outObject;
    if (variableName != null) {
      // This BaseOutputProducingTag should produce output from a variable.

      Collection coll = null;
      try {
        ContextTag contextTag = FrameworkUtils.getContextTag(pageContext);

        if (contextTag == null) {
          throw new JspTagException("<tolog:*> tags must be nested directly or"
                  + " indirectly within a <tolog:context> tag, but no"
                  + " <tolog:context> was found.");
        }

        coll = contextTag.getContextManager().getValue(variableName);
      } catch (VariableNotSetException e) {
        log.debug("didn't find " + variableName + ". Lookup in pageContext.");
        Object pageContextValue = InteractionELSupport
            .getValue(variableName, pageContext);
        if (pageContextValue == null) {
          throw new NavigatorRuntimeException(getName() + " :"
              + " The variable '" + variableName
              + "' is not set, and hence cannot be referenced.\n");
        }
        coll = Collections.singleton(pageContextValue);
      }

      if (coll.isEmpty()) {
        if (fallbackValue == null) {
          throw new NavigatorRuntimeException(getName() + " : requires"
                  + " a variable (attribute 'var') containing a non-empty"
                  + " Collection, but the Collection in the variable '"
                  + variableName + "' is empty. It is possible to get a"
                  + " fallback value by setting the 'fallback' attribute.\n");
        } else {
          return fallbackValue;
        }
      }

      outObject = coll.iterator().next();
      // outObject contains the element of the input variable.
    } else { // query != null   must be true.
      QueryWrapper queryWrapper = new QueryWrapper(pageContext, query);

      if (!queryWrapper.hasNext()) {
        if (fallbackValue == null) {
          throw new NavigatorRuntimeException(getName() + " :"
                  + " requires a query result of at least one row, but got an"
                  + " empty result set. It is possible to get a fallback value"
                  + " by setting the 'fallback' attribute.\n");
        } else {
          return fallbackValue;
        }
      }

      queryWrapper.next();
      Object firstRow[] = queryWrapper.getCurrentRow();

      if (firstRow.length != 1) {
        throw new NavigatorRuntimeException(getName() + " :"
                + " requires a query result of 1 column, but got "
                + firstRow.length + " columns.\n");
      }

      outObject = firstRow[0];
      // outObject contains the first (and only) column of the first row
      // of the query result.
    }

    return outObject;
  }
  
  // -----------------------------------------------------------------
  // set methods
  // -----------------------------------------------------------------

  public void setQuery(String query) {
    this.query = query;
  }

  public final void setVar(String variableName) {
    this.variableName = variableName;
  }

  public final void setFallback(String fallbackValue) {
    this.fallbackValue = fallbackValue;
  }

  // -----------------------------------------------------------------
  // internal methods
  // -----------------------------------------------------------------

  /**
   * INTERNAL: prints out string to specified JspWriter object
   * with respect if the entities should be escaped.
   */
  protected final void print2Writer(JspWriter out, String string)
    throws IOException {

    if (escapeEntities) {
      StringUtils.escapeHTMLEntities(string, out);
    } else {
      out.print( string );
    }
  }

}

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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.nav2.core.NavigatorRuntimeException;
import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;
import net.ontopia.topicmaps.nav2.utils.FrameworkUtils;
import net.ontopia.topicmaps.nav2.utils.Stringificator;

/**
 * INTERNAL: Tolog Tag for outputting content. Outputs the content of
 * a variable (attribute 'var' or the result of a query (attribute
 * 'query') If the variable is a collection or the query gives
 * multiple columns, then the first element/row is output. It is an
 * error for the query to produce multiple column (undeterministic).
 */
public class OutTag extends BaseOutputProducingTag {

  // tag attributes
  protected String scope;

  /**
   * Process the start tag for this instance.
   */
  @Override
  public void generateOutput(JspWriter out, Object outObject)
      throws JspTagException, IOException {

    print2Writer(out, taoToString(outObject, pageContext.getRequest(), scope));
  }

  /**
   * Converts a topic map object to a suitable String representation.
   * 
   * @param tao The Object to convert.
   * @param request To be used for stringification.
   * @param scope Used to select appropriate string for topics.
   * @return A suitable string representation for tao.
   */
  public String taoToString(Object tao, ServletRequest request,
      String scope) throws NavigatorRuntimeException {
    // If tao is a String - output as is, ...
    if (tao == null) {
      return "null";
    } else if (tao instanceof String) {
      return (String) tao;
      // ... LocatorIF - output the external form ...
    } else if (tao instanceof LocatorIF) {
      return ((LocatorIF) tao).getExternalForm();
      // ... topic - output like the old tag <output:name/> ...
    } else if (tao instanceof TopicIF) {
      ContextTag contextTag = FrameworkUtils.getContextTag(request);
      return (scope == null) ? Stringificator.toString(contextTag, tao)
          : Stringificator.toString(contextTag, tao, null, null, scope, null);
      // ... basename - output the name string ...
    } else if (tao instanceof TopicNameIF) {
      return ((TopicNameIF) tao).getValue();
      // ... variant name - output name string or locator output ...
    } else if (tao instanceof VariantNameIF) {
      LocatorIF loc = ((VariantNameIF) tao).getLocator();
      if (loc == null) {
        return ((VariantNameIF) tao).getValue();
      }
      return loc.getExternalForm();
      // ... OccurrenceIF - output the value or locator output ...
    } else if (tao instanceof OccurrenceIF) {
      LocatorIF loc = ((OccurrenceIF) tao).getLocator();
      if (loc == null) {
        return ((OccurrenceIF) tao).getValue();
      }
      return loc.getExternalForm();
      // By DEFAULT use the .toString() method.
    } else {
      return tao.toString();
    }
  }

  /**
   * Resets the state of the Tag.
   */
  @Override
  public void release() {
    super.release();
  }

  // -----------------------------------------------------------------
  // Set methods for tag attributes
  // -----------------------------------------------------------------

  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * @since 3.0
   */
  public void setEscape(String escape) {
    this.escapeEntities = (escape == null ||
                           escape.equalsIgnoreCase("true"));
  }

  @Override
  public String getName() {
    return getClass().getName();
  }
}

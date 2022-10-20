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

package net.ontopia.topicmaps.nav2.utils;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.entry.TopicMapRepositoryIF;
import net.ontopia.topicmaps.nav2.core.NavigatorApplicationIF;
import net.ontopia.topicmaps.nav2.core.NavigatorPageIF;
import net.ontopia.utils.CollectionUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * PUBLIC: Utility methods for working with the variable bindings in
 * the Navigator context.
 */
public final class ContextUtils {

  private ContextUtils() {
    // don't call me
  }
  
  /**
   * EXPERIMENTAL: Gets the topic map repository used by the navigator framework.
   *
   * @since 3.4
   */
  public static TopicMapRepositoryIF getRepository(ServletContext servletContext) {
    return NavigatorUtils.getNavigatorApplication(servletContext).getTopicMapRepository();
  }

  /**
   * EXPERIMENTAL: Get the topic map object the context tag is working
   * with. This method will give direct access to the same transaction
   * as the context tag is using.
   *
   * @since 3.4
   */
  public static TopicMapIF getTopicMap(ServletRequest request) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      request.getAttribute(NavigatorApplicationIF.CONTEXT_KEY);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    return ctxt.getTopicMap();
  }

  /**
   * EXPERIMENTAL: Get the topic map object the context tag is working
   * with. This method will give direct access to the same transaction
   * as the context tag is using.
   *
   * @since 3.2.1
   */
  public static TopicMapIF getTopicMap(PageContext pageContext) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY,
                               PageContext.REQUEST_SCOPE);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    return ctxt.getTopicMap();
  }
  
  /**
   * PUBLIC: Returns the value bound to a specific variable in the
   * current scope. The value returned will be null if the variable is
   * unknown.   
   */
  public static Collection getValue(String name, PageContext pageContext) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY,
                               PageContext.REQUEST_SCOPE);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    return ctxt.getContextManager().getValue(name);

  }

  /**
   * PUBLIC: Returns the value bound to a specific variable in the
   * current scope. The value returned will be defaultValue if the variable is
   * unknown.
   */
  public static Collection getValue(String name, PageContext pageContext,
      Collection defaultValue) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY, 
                               PageContext.REQUEST_SCOPE);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    return ctxt.getContextManager().getValue(name, defaultValue);
  }

  /**
   * PUBLIC: Returns the first value in the collection bound to a
   * specific variable in the current scope. The value returned will
   * be null if the variable is unknown or if the collection is empty.
   */
  public static Object getSingleValue(String name, PageContext pageContext) {
   return getSingleValue(name, pageContext.getRequest());
  }

  /**
   * PUBLIC: Returns the first value in the collection bound to a
   * specific variable in the current scope. The value returned will
   * be null if the variable is unknown or if the collection is empty.
   * @since 2.2.1
   */
  public static Object getSingleValue(String name, ServletRequest request) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      request.getAttribute(NavigatorApplicationIF.CONTEXT_KEY);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    Collection coll = ctxt.getContextManager().getValue(name);
    return CollectionUtils.getFirst(coll);
  }
  
  /**
   * PUBLIC: Sets the value of the named variable to the collection
   * given. The value can be null, which effectively unsets the variable.
   */
  public static void setValue(String name, PageContext pageContext,
                              Collection value) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY,
                               PageContext.REQUEST_SCOPE);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    ctxt.getContextManager().setValue(name, value);
  }

  /**
   * PUBLIC: Sets the value of the named variable to a collection
   * consisting only of the single value given.
   */
  public static void setSingleValue(String name, PageContext pageContext, 
                                    Object value) {
    NavigatorPageIF ctxt = (NavigatorPageIF)
      pageContext.getAttribute(NavigatorApplicationIF.CONTEXT_KEY,
                               PageContext.REQUEST_SCOPE);
    if (ctxt == null) {
      throw new OntopiaRuntimeException("Could not find navigator context.");
    }
    Collection coll = new ArrayList(1);
    coll.add(value);
    ctxt.getContextManager().setValue(name, coll);
  }

}

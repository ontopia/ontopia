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

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.entry.TopicMaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Servlet filter that creates a new topic map store for
 * each request.<p>
 *
 * The filter can be configured using the following configuration
 * parameters:<p>
 *
 * <em>topicMapId</em> - The id of the topic map to create a store for. Mandatory.<br>
 * <em>repositoryId</em> - The id of the topic map repository. The default is to use the default repository.<br>
 * <em>readOnly</em> - A boolean specifying if the store should be readonly. The default is true.<br>
 * <em>requestAttribute</em> - The name of the request attribute under which the store should be placed. The default can be retrieved through the <code>getDefaultRequestAttribute</code>. Override the default only when neccessary.<br>
 *
 * @since 3.4
 */

public class RequestAttributeStoreServletFilter implements Filter {

  private static final Logger log = LoggerFactory.getLogger(RequestAttributeStoreServletFilter.class.getName());
  
  protected FilterConfig filterConfig;
  
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }
  
  @Override
  public void destroy() {
    this.filterConfig = null;
  }
  
  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException,
                                                 ServletException {
    String repositoryId = getRepositoryId(request);
    String topicMapId = getTopicMapId(request);
    boolean readOnly = getReadOnly(request);
    String requestAttribute = getRequestAttribute(request);
    
    TopicMapStoreIF store;
    if (repositoryId == null) {
      store = TopicMaps.createStore(topicMapId, readOnly);
    } else {
      store = TopicMaps.createStore(topicMapId, readOnly, repositoryId);
    }      

    try {
      request.setAttribute(requestAttribute, store);
      chain.doFilter(request, response);
      if (!readOnly) {
        store.commit();
      }
    } catch (Exception e) {
      if (!readOnly) {
        store.abort();
      }      
      log.error("Exception thrown from doFilter.", e);      
    } finally {
      request.removeAttribute(requestAttribute);
      store.close();
    }
  }

  // --- public access
  
  public static String getDefaultRequestAttribute() {
    return "RequestAttributeStoreServletFilter.store";
  }

  // --- getters
  
  protected String getTopicMapId(ServletRequest request) {
    return filterConfig.getInitParameter("topicMapId");
  }

  protected String getRepositoryId(ServletRequest request) {
    return filterConfig.getInitParameter("repositoryId"); // optional; use default repository if not specified
  }

  protected boolean getReadOnly(ServletRequest request) {
    String readOnly = filterConfig.getInitParameter("readOnly"); // optional; default=true
    return (readOnly == null? true : Boolean.valueOf(readOnly).booleanValue());
  }

  protected String getRequestAttribute(ServletRequest request) {
    String requestAttribute = filterConfig.getInitParameter("requestAttribute"); // optional, undocumented for now
    return (requestAttribute == null ? getDefaultRequestAttribute() : requestAttribute);
  }
  
}

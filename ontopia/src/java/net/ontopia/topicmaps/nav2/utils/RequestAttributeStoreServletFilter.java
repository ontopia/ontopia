
// $Id: RequestAttributeStoreServletFilter.java,v 1.2 2007/08/24 13:22:43 lars.garshol Exp $

package net.ontopia.topicmaps.nav2.utils;

import java.io.*;
import javax.servlet.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.*;

import org.apache.log4j.*;

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

  static Logger log = Logger.getLogger(RequestAttributeStoreServletFilter.class.getName());
  
  protected FilterConfig filterConfig;
  
  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }
  
  public void destroy() {
    this.filterConfig = null;
  }
  
  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException,
                                                 ServletException {
    String repositoryId = getRepositoryId(request);
    String topicMapId = getTopicMapId(request);
    boolean readOnly = getReadOnly(request);
    String requestAttribute = getRequestAttribute(request);
    
    TopicMapStoreIF store;
    if (repositoryId == null)
      store = TopicMaps.createStore(topicMapId, readOnly);
    else
      store = TopicMaps.createStore(topicMapId, readOnly, repositoryId);      

    try {
      request.setAttribute(requestAttribute, store);
      chain.doFilter(request, response);
      if (!readOnly) store.commit();
    } catch (Exception e) {
      if (!readOnly) store.abort();      
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

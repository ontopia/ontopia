
// $Id: ThreadLocalStoreServletFilter.java,v 1.2 2007/08/24 13:23:57 lars.garshol Exp $

package net.ontopia.topicmaps.nav2.utils;

import java.io.*;
import javax.servlet.*;
import net.ontopia.topicmaps.core.*;
import net.ontopia.topicmaps.entry.*;

import org.apache.log4j.*;

/**
 * INTERNAL: Servlet filter that creates a new topic map store for
 * each request thread. The topic map store is stored in a thread
 * local.<p>
 *
 * The filter can be configured using the following configuration
 * parameters:<p>
 *
 * <em>topicMapId</em> - The id of the topic map to create a store for. Mandatory.<br>
 * <em>repositoryId</em> - The id of the topic map repository. The default is to use the default repository.<br>
 * <em>readOnly</em> - A boolean specifying if the store should be readonly. The default is true.<br>
 *
 * @since 3.4
 */

public class ThreadLocalStoreServletFilter implements Filter {

  static Logger log = Logger.getLogger(ThreadLocalStoreServletFilter.class.getName());

  private static ThreadLocal data = new ThreadLocal();
  
  private FilterConfig filterConfig;
    
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
    
    TopicMapStoreIF store;
    if (repositoryId == null)
      store = TopicMaps.createStore(topicMapId, readOnly);
    else
      store = TopicMaps.createStore(topicMapId, readOnly, repositoryId);      

    try {
      data.set(store);
      chain.doFilter(request, response);
      if (!readOnly) store.commit();
    } catch (Exception e) {
      if (!readOnly) store.abort();      
      log.error("Exception thrown from doFilter.", e);      
    } finally {
      data.set(null);
      store.close();
    }
  }

  // --- public access
  
  public static TopicMapStoreIF getStore() {
    return (TopicMapStoreIF)data.get();
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
  
}

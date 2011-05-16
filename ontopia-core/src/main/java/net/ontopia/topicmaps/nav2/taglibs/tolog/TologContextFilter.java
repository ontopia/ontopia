
// $Id: TologContextFilter.java,v 1.5 2006/01/12 11:12:33 larsga Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.ontopia.topicmaps.nav2.taglibs.logic.ContextTag;

/**
 * EXPERIMENTAL: This is a servlet filter that pushes a tolog context
 * onto the stack. The filter is to be used so that the nested filters
 * are to have access to the tolog context.
 * 
 * Example web.xml configuration:
 *  
 * <pre>
 * <filter> 
 *   &lt;filter-name>TologContextFilter&lt;/filter-name>
 *   &lt;filter-class>net.ontopia.topicmaps.nav2.taglibs.tolog.TologContextFilter&lt;/filter-class>
 *   &lt;init-param>
 *     &lt;param-name>topicmap&lt;/param-name>
 *     &lt;param-value>opera.xtm&lt;/param-value>
 *   &lt;/init-param>
 *   &lt;init-param>
 *     &lt;param-name>reqParam&lt;/param-name>
 *     &lt;param-value>topicmap&lt;/param-value>
 *   &lt;/init-param>
 * &lt;/filter>
 * &lt;filter-mapping>
 *   &lt;filter-name>TologContextFilter&lt;/filter-name> 
 *   &lt;url-pattern>/opera/*&lt;/url-pattern>
 * &lt;/filter-mapping>
 * </pre>
 * 
 * @since 3.0
 * 
 */
public class TologContextFilter implements Filter {

  protected String topicmapid_attribute;
  protected String topicmapid_parameter;
  protected String topicmapid;

  public void init(FilterConfig fconfig) throws ServletException {
    // override topic map request attribute name
    String aname = fconfig.getInitParameter("attribute");
    this.topicmapid_attribute = (aname == null ? ContextTag.TOPICMAPID_REQUEST_ATTRIBUTE: aname);  

    // override topic map request parameter name
    this.topicmapid_parameter = fconfig.getInitParameter("reqParam");

    // hardwire default topic map id
    this.topicmapid = fconfig.getInitParameter("topicmap");
  }

  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    // get hold of existing request attribute
    Object ptmid = request.getAttribute(topicmapid_attribute);
    
    try {
      String tmid = null;
      if (topicmapid_parameter != null)
        tmid = request.getParameter(topicmapid_parameter);
      if (tmid == null)
        tmid = topicmapid;

      // set request attribute
      request.setAttribute(topicmapid_attribute, tmid);
      
      // delegate to nested filters
      chain.doFilter(request, response);
      
    } finally {
      // reinstate old request attribute value
      request.setAttribute(topicmapid_attribute, ptmid);
    }
  }

  public void destroy() {
  }

}


package net.ontopia.topicmaps.utils.tmprefs;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class TopicMapPreferencesServletFilter implements Filter {

	private static final ThreadLocal<HttpServletRequest> requests = new ThreadLocal<HttpServletRequest>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// no-op
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			requests.set((HttpServletRequest) request);
		}
		chain.doFilter(request, response);
		requests.remove();
	}

	@Override
	public void destroy() {
		requests.remove();
	}

	public static HttpServletRequest getRequest() {
		return requests.get();
	}

}

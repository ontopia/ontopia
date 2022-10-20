/*
 * #!
 * Ontopoly Editor
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
package ontopoly;

import java.io.IOException;  

import javax.servlet.Filter;  
import javax.servlet.FilterChain;  
import javax.servlet.FilterConfig;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRequest;  
import javax.servlet.ServletResponse;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import javax.servlet.http.HttpServletResponseWrapper;  
  
public class OC4JContextFilter implements Filter {  
  
    protected FilterConfig config;  
  
    //Used to store the servlet mapping URL that is defined in the web.xml  
    private String servletMappingContext;  
  
    @Override
    public void destroy() {  
        config = null;  
        servletMappingContext = null;  
    }  
  
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {  
        HttpServletRequest httpRequest = (HttpServletRequest)request;  
        HttpServletResponse httpResponse = (HttpServletResponse)response;  
  
        if(servletMappingContext == null || servletMappingContext.length()==0)  
        {  
            //The getRequestURI returns the full context i.e. /sampleWicket/app/  
            setServletMappingContext(httpRequest.getRequestURI());  
        }  
  
        myServletResponse newResponse = new myServletResponse(httpResponse);  
        chain.doFilter(request, newResponse);  
    }  
  
    private void setServletMappingContext(String requestURI) {  
        //1. remove last / if it exists  
        if (requestURI.endsWith("/")) {
          requestURI = requestURI.substring(0, requestURI.length()-1);
        }  
  
        //2. get only the servlet mapping defined in web.xml i.e /app  
        requestURI = requestURI.substring(requestURI.lastIndexOf("/"));  
  
        //3. remove the prefixed /  
        requestURI = requestURI.substring(1);  
  
        //4. append the / at the end  
        servletMappingContext = requestURI+="/";  
    }  
  
    @Override
    public void init(FilterConfig config) throws ServletException {  
        this.config = config;  
    }  
  
    class myServletResponse extends HttpServletResponseWrapper  
    {  
        public myServletResponse(HttpServletResponse arg0) {  
            super(arg0);  
        }  
        @Override
        public void sendRedirect(String arg0) throws IOException {  
            // Add the context to the query string  
            super.sendRedirect(servletMappingContext+arg0);  
        }  
    }  
}  

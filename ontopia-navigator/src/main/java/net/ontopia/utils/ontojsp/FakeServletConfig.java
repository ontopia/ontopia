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

package net.ontopia.utils.ontojsp;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class FakeServletConfig implements ServletConfig {
  protected ServletContext context;
  protected Hashtable params;
  
  public FakeServletConfig(ServletContext context) {
    this(context, new Hashtable());
  }

  public FakeServletConfig(ServletContext context, Hashtable params) {
    this.context = context;
    this.params = params;
  }
  
  public ServletContext getServletContext() {
    return context;
  }

  public String getInitParameter(String name) {
    return (String)params.get(name);
  }

  public Enumeration getInitParameterNames() {
    return params.keys();
  }

  public String getServletName() {
    // TODO
    return null;
  }
}





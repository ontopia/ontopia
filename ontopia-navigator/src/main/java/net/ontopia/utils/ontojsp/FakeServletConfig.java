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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

public class FakeServletConfig implements ServletConfig {

  protected ServletContext context;
  protected Map<String, String> params;

  public FakeServletConfig(ServletContext context) {
    this(context, new HashMap<String, String>());
  }

  public FakeServletConfig(ServletContext context, Map<String, String> params) {
    this.context = context;
    this.params = params;
  }

  @Override
  public ServletContext getServletContext() {
    return context;
  }

  @Override
  public String getInitParameter(String name) {
    return params.get(name);
  }

  @Override
  public Enumeration<String> getInitParameterNames() {
    return Collections.enumeration(params.keySet());
  }

  @Override
  public String getServletName() {
    // TODO
    return null;
  }
}

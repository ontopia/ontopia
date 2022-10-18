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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * INTERNAL: Fake the ServletContext, needed for execution of servlets
 */
public class FakeHttpSession implements HttpSession {
  private Map<String, Object> attrs;
  private ServletContext context;
  private int maxInactiveInterval;
  private Collection<HttpSessionListener> listeners = new ArrayList<HttpSessionListener>();

  public FakeHttpSession(ServletContext context) {
    this(context, new HashMap<String, Object>());
  }
  
  public FakeHttpSession(ServletContext context, Map<String, Object> attrs) {
    this.context = context;
    this.attrs = attrs;
    maxInactiveInterval = -1;
  }
  
  @Override
  public Object getAttribute(String name) {
    return attrs.get(name);
  }
  
  @Override
  public Enumeration<String> getAttributeNames() {
    return Collections.enumeration(attrs.keySet());
  }
  
  @Override
  public void removeAttribute(String name) {
    attrs.remove(name);
  }
  
  @Override
  public void setAttribute(String name, Object value) {
    if (value == null) 
      attrs.remove(name);
    else 
      attrs.put(name, value);
  }
  
  @Override
  public Object getValue(String name) {
    // Note: deprecated
    return getAttribute(name);
  }
  
  @Override
  public String[] getValueNames() {
    // Note: deprecated
    return attrs.keySet().toArray(new String[attrs.keySet().size()]);
  }
  
  @Override
  public void putValue(String name, Object value) {
    // Note: deprecated
    setAttribute(name, value);
  }
  
  @Override
  public void removeValue(String name) {
    // Note: deprecated
    removeAttribute(name);
  }
  
  @Override
  public long getCreationTime() {
    // TODO
    return -1;
  }
  
  @Override
  public String getId() {
    // TODO
    return null;
  }
  
  @Override
  public long getLastAccessedTime() {
    // TODO
    return -1;
  }
  
  @Override
  public int getMaxInactiveInterval() {
    return maxInactiveInterval;
  }
  
  @Override
  public HttpSessionContext getSessionContext() {
    // Note: deprecated
    return null;
  }

  @Override
  public ServletContext getServletContext() {
    return context;
  }
  
  public void addSessionListener(HttpSessionListener listener) {
    listeners.add(listener);
}
  
  public void expire() {
     invalidate();
  }
  @Override
  public void invalidate() {
    for (HttpSessionListener listener : listeners) {
      listener.sessionDestroyed(new HttpSessionEvent(this));
    }
  }
  
  @Override
  public boolean isNew() {
    // TODO
    return true;
  }
  
  @Override
  public void setMaxInactiveInterval(int interval) {
    maxInactiveInterval = interval;
  }
            
}

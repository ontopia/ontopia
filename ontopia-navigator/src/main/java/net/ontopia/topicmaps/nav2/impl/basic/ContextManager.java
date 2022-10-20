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

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.jsp.PageContext;
  
import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
import net.ontopia.topicmaps.nav2.impl.framework.InteractionELSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Default Implementation of ContextManagerIF.
 */
public class ContextManager implements ContextManagerIF {
  
  // initialization of logging facility
  private static final Logger log = LoggerFactory
    .getLogger(ContextManager.class.getName());
  
  /**
   * INTERNAL: Name of the default value key which is needed by the
   * maps stored in the <code>ContextManager</code> scope-stacks.
   */
  private static final String DEFAULT_VALUE_KEY = "@@DEFAULT@@";

  /** Stack which contains the lexical scope */
  private Stack scopes;
  
  /** Current scoped set of variables (equivalent to peeking on stack) */
  private Map values;

  /** Reference to page context so we can look things up there */
  private PageContext pageContext;

  public ContextManager(PageContext pageContext) {
    // initialize root scope
    scopes = new Stack();
    pushScope();
    this.pageContext = pageContext;
  }

  // ------------------------------------------------------------------
  // implementation of ContextManagerIF interface
  // ------------------------------------------------------------------
  
  @Override
  public Collection getValue(String name) throws VariableNotSetException {
    // remove prefix (if there)
    name = cutoffPre(name);
    Object result = _getValue(name);
    if (result == null) {
      throw new VariableNotSetException(name);
    } else {
      return (Collection)result;
    }
  }

  @Override
  public Collection getValue(String name, Collection defaultValue) {
    // remove prefix (if there)
    name = cutoffPre(name);
    Object result = _getValue(name);
    if (result == null) {
      return defaultValue;
    } else {
      return (Collection)result;
    }
  }

  /**
   * INTERNAL: Helper method that does the actual variable value
   * lookup.
   */
  private Object _getValue(String name) {
    // first search in current lexical scope for value
    if (values.containsKey(name)) {
      return values.get(name);
    } else {
      // try to retrieve value from ancestor scope
      if (scopes.size() > 1) {
        for (int i = scopes.size()-2; i >= 0; i--) {
          Map ancestorValues = (Map)scopes.elementAt(i);
          if (ancestorValues != null && ancestorValues.containsKey(name)) {
            return ancestorValues.get(name);
          }
        } // for
      }

      // we still haven't found anything. try pageContext
      Object v = InteractionELSupport.getValue(name, pageContext);
      if (v == null || v instanceof Collection) {
        return v;
      } else {
        return Collections.singleton(v);
      }
    }
  }
  
  @Override
  public void setValue(String name, Collection coll) {
    values.put(cutoffPre(name), coll);
  }

  @Override
  public void setValueInScope(Object scope, String name, Collection coll) {
    int index = ((Integer) scope).intValue();
    if (index >= 0) {
      Map currValues = (Map) scopes.get(index);
      currValues.put(cutoffPre(name), coll);
      scopes.set(index, currValues);
    } else {
      log.warn("Cannot set value for variable '" + name + "', because " +
               "couldn't find scope.");
    }
  }
  
  @Override
  public void setValue(String name, Object obj) {
    if (obj == null) {
      return;
    }
    if (obj instanceof Collection) {
      setValue(name, (Collection) obj);
    } else {
      values.put(cutoffPre(name), Collections.singleton(obj));
    }
  }  
  
  @Override
  public Collection getDefaultValue() {
    return (Collection)_getValue(DEFAULT_VALUE_KEY);
  }

  @Override
  public void setDefaultValue(Collection coll) {
    setValue(DEFAULT_VALUE_KEY, coll);
  }

  @Override
  public void setDefaultValue(Object obj) {
    setValue(DEFAULT_VALUE_KEY, obj);
  }  

  @Override
  public Object getCurrentScope() {
    return scopes.size()-1;
  }
  
  @Override
  public void pushScope() {
    values = new HashMap();
    scopes.push(values);
  }

  @Override
  public void popScope() {
    scopes.pop();
    // get old values back from top of stack
    values = (Map) scopes.peek();
  }
  
  @Override
  public void clear() {
    values.clear();
    scopes.clear();
  }
  
  // ------------------------------------------------------------------
  // private helper method(s)
  // ------------------------------------------------------------------
  
  /**
   * INTERNAL: Cut off prefix ($) from given string (used to identify
   * variable names since OKS Version 1.4).
   */
  private static String cutoffPre(String name) {
    if (name == null) {
      return null;
    }
    if (name.charAt(0) == '$') {
      return name.substring(1);
    } else {
      return name;
    }
  }
  
}

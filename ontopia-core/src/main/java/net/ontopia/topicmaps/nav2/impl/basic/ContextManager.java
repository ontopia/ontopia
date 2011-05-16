
// $Id: ContextManager.java,v 1.22 2004/11/24 12:04:03 larsga Exp $

package net.ontopia.topicmaps.nav2.impl.basic;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Collection;
import java.util.Collections;

import net.ontopia.topicmaps.nav2.core.ContextManagerIF;
import net.ontopia.topicmaps.nav2.core.VariableNotSetException;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Default Implementation of ContextManagerIF.
 */
public class ContextManager implements ContextManagerIF {
  
  // initialization of logging facility
  private static Logger log = LoggerFactory
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

  public ContextManager() {
    // initialize root scope
    scopes = new Stack();
    pushScope();
  }

  // ------------------------------------------------------------------
  // implementation of ContextManagerIF interface
  // ------------------------------------------------------------------
  
  public Collection getValue(String name) throws VariableNotSetException {
    // remove prefix (if there)
    name = cutoffPre(name);
    Object result = _getValue(name);
    if (result == null)
      throw new VariableNotSetException(name);
    else
      return (Collection)result;
  }

  public Collection getValue(String name, Collection defaultValue) {
    // remove prefix (if there)
    name = cutoffPre(name);
    Object result = _getValue(name);
    if (result == null)
      return defaultValue;
    else
      return (Collection)result;
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
          if (ancestorValues != null && ancestorValues.containsKey(name))
            return ancestorValues.get(name);
        } // for
      }
    }
    return null;
  }
  
  public void setValue(String name, Collection coll) {
    values.put(cutoffPre(name), coll);
  }

  public void setValueInScope(Object scope, String name, Collection coll) {
    int index = ((Integer) scope).intValue();
    if (index >= 0) {
      Map currValues = (Map) scopes.get(index);
      currValues.put(cutoffPre(name), coll);
      scopes.set(index, currValues);
    } else
      log.warn("Cannot set value for variable '" + name + "', because " +
               "couldn't find scope.");
  }
  
  public void setValue(String name, Object obj) {
    if (obj == null) return;
    if (obj instanceof Collection)
      setValue(name, (Collection) obj);
    else 
      values.put(cutoffPre(name), Collections.singleton(obj));
  }  
  
  public Collection getDefaultValue() {
    return (Collection)_getValue(DEFAULT_VALUE_KEY);
  }

  public void setDefaultValue(Collection coll) {
    setValue(DEFAULT_VALUE_KEY, coll);
  }

  public void setDefaultValue(Object obj) {
    setValue(DEFAULT_VALUE_KEY, obj);
  }  

  public Object getCurrentScope() {
    return new Integer(scopes.size()-1);
  }
  
  public void pushScope() {
    values = new HashMap();
    scopes.push(values);
  }

  public void popScope() {
    scopes.pop();
    // get old values back from top of stack
    values = (Map) scopes.peek();
  }
  
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
    if (name == null)
      return null;
    if (name.charAt(0) == '$')
      return name.substring(1);
    else
      return name;
  }
  
}

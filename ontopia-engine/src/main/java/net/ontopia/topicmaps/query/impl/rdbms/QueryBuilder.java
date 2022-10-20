/*
 * #!
 * Ontopia Engine
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

package net.ontopia.topicmaps.query.impl.rdbms;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.persistence.query.jdo.JDOAggregate;
import net.ontopia.persistence.query.jdo.JDOAggregateIF;
import net.ontopia.persistence.query.jdo.JDONull;
import net.ontopia.persistence.query.jdo.JDOObject;
import net.ontopia.persistence.query.jdo.JDOParameter;
import net.ontopia.persistence.query.jdo.JDOQuery;
import net.ontopia.persistence.query.jdo.JDOString;
import net.ontopia.persistence.query.jdo.JDOValueIF;
import net.ontopia.persistence.query.jdo.JDOVariable;
import net.ontopia.topicmaps.query.parser.Pair;
import net.ontopia.topicmaps.query.parser.Parameter;
import net.ontopia.topicmaps.query.parser.TologQuery;
import net.ontopia.topicmaps.query.parser.Variable;

/**
 * INTERNAL: Class used to hold context information when building JDO
 * queries from tolog queries.
 */
public class QueryBuilder {

  //! public BindingContext bc;
  
  protected TologQuery query;
  protected QueryBuilder parent;
  protected Map attributes;
  protected Set unsupported = new HashSet();

  protected int vncounter = 1;

  // Query variables {varalias : type}
  
  // Temporary variables {varalias : type}
  protected Map variables = new HashMap();
  // Query parameters
  protected Map params = new HashMap();
  // Query processor
  protected QueryProcessor qp;

  public static Map typemap = new HashMap();
  static {
    // The type map is used to map interface types to actual types used by the O/R-mapper
    typemap.put(net.ontopia.topicmaps.core.AssociationIF.class, net.ontopia.topicmaps.impl.rdbms.Association.class);
    typemap.put(net.ontopia.topicmaps.core.AssociationRoleIF.class, net.ontopia.topicmaps.impl.rdbms.AssociationRole.class);
    typemap.put(net.ontopia.topicmaps.core.TopicNameIF.class, net.ontopia.topicmaps.impl.rdbms.TopicName.class);
    typemap.put(net.ontopia.topicmaps.core.OccurrenceIF.class, net.ontopia.topicmaps.impl.rdbms.Occurrence.class);
    typemap.put(net.ontopia.topicmaps.core.TopicIF.class, net.ontopia.topicmaps.impl.rdbms.Topic.class);
    typemap.put(net.ontopia.topicmaps.core.TopicMapIF.class, net.ontopia.topicmaps.impl.rdbms.TopicMap.class);
    typemap.put(net.ontopia.topicmaps.core.VariantNameIF.class, net.ontopia.topicmaps.impl.rdbms.VariantName.class);
  }

  QueryBuilder(TologQuery query, QueryProcessor qp) {
    this(query, null, qp);
  }
  
  QueryBuilder(TologQuery query, QueryBuilder parent, QueryProcessor qp) {
    this.query = query;
    this.parent = parent;
    this.qp = qp;
  }

  /* ---- Properties */

  public String getProperty(String name) {
    String propval = (qp == null ? null : qp.getProperty(name));
    if (propval == null && parent != null) { 
      return parent.getProperty(name);
    } else {
      return propval;
    }      
  }

  /* ---- Attributes */
  
  public Object getAttribute(Object attr) {
    if (attributes == null) {
      return null;
    }
    return attributes.get(attr);    
  }

  public void setAttribute(Object attr, Object value) {
    if (attributes == null) {
      attributes = new HashMap();
    }
    attributes.put(attr, value);
  }

  /* ---- Variables */
     
  public Class getVariableType(String name) {
    Object type = variables.get(name);
    if (type != null && typemap.containsKey(type)) {
      return (Class)typemap.get(type);
    } else {
      return (Class)type;
    }
  }

  protected Class getVariableTypeFromParent(String name) {
    if (parent == null) {
      return null;
    }
    Class vtype = parent.getVariableType(name);
    if (vtype != null) {
      return vtype;
    } else {
      return parent.getVariableTypeFromParent(name);
    }
  }

  public Map getVariables() {
    return variables;
  }

  public void setVariables(Map variables) {
    this.variables = variables;
  }
  
  public String[] getVariableNames() {
    String[] result = new String[variables.size()];
    variables.keySet().toArray(result);
    return result;
  }

  /* ---- Parameters */
     
  public Class getParameterType(String name) {
    Object type = params.get(name);
    if (type != null && typemap.containsKey(type)) {
      return (Class)typemap.get(type);
    } else {
      return (Class)type;
    }
  }

  protected Class getParameterTypeFromParent(String name) {
    if (parent == null) {
      return null;
    }
    Class ptype = parent.getParameterType(name);
    if (ptype != null) {
      return ptype;
    } else {
      return parent.getParameterTypeFromParent(name);
    }
  }

  public Map getParameters() {
    return params;
  }

  public void setParameters(Map params) {
    this.params = params;
  }

  public String[] getParameterNames() {
    String[] result = new String[params.size()];
    params.keySet().toArray(result);
    return result;
  }

  /* ---- Unsupported variables */

  public boolean isSupportedVariable(Variable var) {
    return !unsupported.contains(var);
  }

  public void addUnsupportedVariable(Variable var) {
    unsupported.add(var);
  }
  
  // --- Registration of JDO query components
  
  //! public void registerJDOVariables(JDOQuery jdoquery) {
  //!   // variables
  //!   if (variables.isEmpty()) return;
  //!   Iterator iter = variables.keySet().iterator();
  //!   while (iter.hasNext()) {
  //!     String varname = (String)iter.next();
  //!     Class vartype = getVariableType(varname);
  //!     if (vartype == null) {
  //!       vartype = getVariableTypeFromParent(varname);
  //!     }
  //!     jdoquery.addVariable(varname, vartype); // name : class
  //!   }
  //! }
  //! 
  //! public void registerJDOParameters(JDOQuery jdoquery) {
  //!   // parameters
  //!   if (params.isEmpty()) return;
  //!   Iterator iter = params.keySet().iterator();
  //!   while (iter.hasNext()) {
  //!     String parname = (String)iter.next();
  //!     Class partype = getParameterType(parname);
  //!     if (partype == null) {
  //!       partype = getParameterTypeFromParent(parname);
  //!     }
  //!     jdoquery.addParameter(parname, partype); // name : class
  //!   }
  //! }
  
  // selected     : [ A,    C, D       ] 
  // all items    : [ A, B, C, D, E, F ]
  // clause items : [    B, C, D,    F ] ==> [ null, B, C, D, null, F ]

  public void registerJDOSelect(JDOQuery jdoquery, Set varnames, boolean aggfunc) {
    // NOTE: query result being used directly (query not dependent on basic clauses)
    Collection counted = query.getCountedVariables();
    List selected = query.getSelectedVariables();

    for (int ix = 0; ix < selected.size(); ix++) {
      Variable var = (Variable)selected.get(ix);
      String varname = var.getName();
      
      // If variable is bound in query select it.
      if (varnames.contains(varname)) {
        if (aggfunc && counted.contains(var)) {
          jdoquery.addSelect(new JDOAggregate(new JDOVariable(varname), JDOAggregateIF.COUNT));
        } else {
          jdoquery.addSelect(new JDOVariable(varname));
        }
      } 
    }
  }

  public void registerJDOSelectDependent(JDOQuery jdoquery, Set varnames) {
    // NOTE: query result being passed on to basic clauses
    // FIXME: no need to select those that are not needed elsewhere!

    Iterator iter = varnames.iterator();
    while (iter.hasNext()) {
      JDOVariable jdovar = new JDOVariable((String)iter.next());
      
      // select variable
      jdoquery.addSelect(jdovar);
    }
  }
  
  //! public void registerJDOSelect2(JDOQuery jdoquery, boolean aggfunc) {
  //!   // select
  //!   Collection counted = query.getCountedVariables();
  //!   List selected = query.getSelectedVariables();
  //! 
  //!   //! System.out.println("SELECTED VARS: " + selected);
  //!   // FIXME: Don't register if JDOQuery is set-query?
  //!   for (int ix = 0; ix < selected.size(); ix++) {
  //!     Variable var = (Variable)selected.get(ix);
  //!     String varname = var.getName();
  //!     
  //!     // If variable is bound in query select it.
  //!     if (variables.containsKey(varname)) {
  //!       if (aggfunc && counted.contains(var))
  //!         jdoquery.addSelect(new JDOAggregate(new JDOVariable(varname), JDOAggregateIF.COUNT));
  //!       else
  //!         jdoquery.addSelect(new JDOVariable(varname));
  //!     } else {
  //!       // Variable not bound, so we'll select null
  //!       jdoquery.addSelect(new JDONull());
  //!     }
  //!   }
  //!   //! System.out.println("|SELECTED VARS|: " + jdoquery.getSelect());
  //! }
  
  public void registerJDOOrderBy(JDOQuery jdoquery, boolean aggfunc) {
    List orderby = query.getOrderBy();
    Collection counted = query.getCountedVariables();
    for (int ix = 0; ix < orderby.size(); ix++) {
      Variable var = (Variable)orderby.get(ix);
      String varname = var.getName();
      JDOVariable jdovar = new JDOVariable(varname);
      if (query.isOrderedAscending(varname)) {
        if (aggfunc && counted.contains(var)) {
          jdoquery.addAscending(new JDOAggregate(jdovar, JDOAggregateIF.COUNT));
        } else {
          jdoquery.addAscending(jdovar);
        }
      } else {
        if (aggfunc && counted.contains(var)) {
          jdoquery.addDescending(new JDOAggregate(jdovar, JDOAggregateIF.COUNT));
        } else {
          jdoquery.addDescending(jdovar);
        }
      }
    }
  }

  // --- NEW METHODS OR METHODS TO KEEP

  /**
   * INTERNAL: Create a temporary variable.
   */
  public JDOVariable createJDOVariable(String prefix, Class type) {
    // NOTE: Make sure that the names don't collide with the proper
    // variables
    while (true) {
      // Create new variable name
      String varname = prefix + (vncounter++);
      // Check if name collides with variable name
      if (variables.containsKey(varname)) {
        continue;
      }
      
      variables.put(varname, type);
      return new JDOVariable(varname);
    }
  }
  
  public JDOValueIF createJDOValue(Object argument) {
    if (argument == null) {
      return new JDONull();
    } else if (argument instanceof Variable) {
      // FIXME: have to map to variable ALIAS
      String varname = ((Variable)argument).getName();

      // TODO: is this neccessary?
      Object vtype = getVariableType(varname);
      if (vtype != null) {
        variables.put(varname, vtype);
      }
      
      return new JDOVariable(varname);

    } else if (argument instanceof Parameter) {
      String parname = ((Parameter)argument).getName();
      return new JDOParameter(parname);
      
    } else if (argument instanceof Pair) {
      // Note: Pairs should be handled inside predicate.
      throw new UnsupportedOperationException("Cannot create JDOValueIF from from Pair: " + argument);      

    } else if (argument instanceof String) {
      return new JDOString((String)argument);
    } else {
      return new JDOObject(argument);
    }
  }

  // --- Argument introspection
  
  public boolean isArgumentOfType(Object argument, Class type) {
    if (argument instanceof Variable) {
      return type.isAssignableFrom(getVariableType(((Variable)argument).getName()));
      
    } else if (argument instanceof Parameter) {
      return type.isAssignableFrom(getParameterType(((Parameter)argument).getName()));
      
    } else if (argument instanceof Pair) {
      // Note: Pairs should be handled inside predicate.
      throw new UnsupportedOperationException("Cannot figure out argument type from from Pair: " + argument);      

    } else {
      return type.isAssignableFrom(argument.getClass());
    }    
  }

  public Class getArgumentType(Object argument) {
    if (argument instanceof Variable) {
      return getVariableType(((Variable)argument).getName());
      
    } else if (argument instanceof Parameter) {
      return getParameterType(((Parameter)argument).getName());
      
    } else if (argument instanceof Pair) {
      // Note: Pairs should be handled inside predicate.
      throw new UnsupportedOperationException("Cannot figure out argument type from from Pair: " + argument);      
      
    } else {
      return argument.getClass();
    }    
  }
}

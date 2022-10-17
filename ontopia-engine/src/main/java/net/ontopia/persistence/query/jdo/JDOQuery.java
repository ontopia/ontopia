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

package net.ontopia.persistence.query.jdo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * INTERNAL: JDOQL complete query. Represents a complete JDO query.
 */

public class JDOQuery {

  protected JDOExpressionIF filter;

  protected boolean distinct = false;
  protected int limit = -1;
  protected int offset = -1;
  
  protected Map params;
  protected List param_names;
  protected Map variables;  
  protected List select = new ArrayList();  // never empty
  protected List orderby;

  public boolean isSetQuery() {
    return (getFilter() instanceof JDOSetOperation);
  }
                   
  public boolean getDistinct() {
    return distinct;
  }

  public void setDistinct(boolean distinct) {
    this.distinct = distinct;
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
  
  public List getParameterNames() {
    if (param_names == null)
      return Collections.EMPTY_LIST;
    else
      return param_names;
  }
  
  public boolean hasParameterName(String name) {
    if (params == null) return false;
    return params.containsKey(name);
  }

  public Class getParameterType(String name) {
    // Complain if parameter is unknown
    if (params == null || !params.containsKey(name))
      throw new OntopiaRuntimeException("Parameter with name '" + name + "' does not exist.");
    return (Class)params.get(name);
  }
  
  public void addParameter(String name, Class klass) {
    Objects.requireNonNull(klass, "JDO parameter class must not be null.");
    // Must be unique. Hides candidate class fields. Not 'this'.
    checkExistingName(name);
    if (params == null) params = new HashMap();
    params.put(name, klass);
    if (param_names == null) param_names = new ArrayList();
    param_names.add(name);
  }

  public int getVariableCount() {
    return (variables == null ? 0 : variables.size());
  }
  
  public Collection getVariableNames() {
    if (variables == null)
      return Collections.EMPTY_SET;
    else
      return variables.keySet();
  }

  public boolean hasVariableName(String name) {
    if (variables == null) return false;
    return variables.containsKey(name);
  }
  
  public Class getVariableType(String name) {
    // Complain if unknown variable
    if (variables == null || !variables.containsKey(name))
      throw new OntopiaRuntimeException("JDO variable '" + name + "' does not exist.");
    return (Class)variables.get(name);
  }
  
  public void addVariable(String name, Class klass) {
    Objects.requireNonNull(klass, "The class of JDO variable '" + name + "' must not be null.");
    // Must be unique and not conflict with parameter names. Hides
    // candidate class fields. Not 'this'.
    checkExistingName(name);
    if (variables == null) variables = new HashMap();
    variables.put(name, klass);
  }

  protected void checkExistingName(String name) throws RuntimeException {
    if (params != null && params.containsKey(name))
      throw new OntopiaRuntimeException("Parameter with name '" + name + "' already exists.");
    if (variables != null && variables.containsKey(name))
      throw new OntopiaRuntimeException("Variable with name '" + name + "' already exists.");
  }
  
  public JDOExpressionIF getFilter() {
    return filter;
  }
  
  public void setFilter(JDOExpressionIF filter) {
    this.filter = filter;
  }
  
  public List getSelect() {
    return select;
  }
  
  public String[] getSelectedColumnNames() {
    String[] colnames = new String[select.size()];
    for (int i=0; i < colnames.length; i++) {
      Object selected = select.get(i);
      if (selected instanceof JDOVariable)
	colnames[i] = ((JDOVariable)selected).getName();
      else if (selected instanceof JDOAggregate)
	colnames[i] = ((JDOVariable)((JDOAggregate)selected).getValue()).getName();
      else
	throw new OntopiaRuntimeException("Not able to figure out column name.");
    }
    return colnames;
  }

  public void addSelect(JDOValueIF value) {
    select.add(value);
  }
  
  public void addSelect(JDOAggregateIF aggregate) {
    select.add(aggregate);
  }
  
  public List getOrderBy() {
    if (orderby == null)
      return Collections.EMPTY_LIST;
    else
      return orderby;
  }

  public void addOrderBy(JDOOrderBy job) {
    if (orderby == null) orderby = new ArrayList();
    orderby.add(job);
  }
  
  public void addAscending(JDOValueIF value) {
    addOrderBy(new JDOOrderBy(value, JDOOrderBy.ASCENDING));
  }
  
  public void addDescending(JDOValueIF value) {
    addOrderBy(new JDOOrderBy(value, JDOOrderBy.DESCENDING));
  }
  
  public void addAscending(JDOAggregateIF aggregate) {
    addOrderBy(new JDOOrderBy(aggregate, JDOOrderBy.ASCENDING));
  }
  
  public void addDescending(JDOAggregateIF aggregate) {
    addOrderBy(new JDOOrderBy(aggregate, JDOOrderBy.DESCENDING));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("select ");
    if (getDistinct())
      sb.append("distinct ");
    if (select == null || select.isEmpty())
      sb.append('*');
    else
      StringUtils.join(select, ", ", sb);
    if (getFilter() != null) {
      sb.append(" from ");
      sb.append(getFilter());
    }
    List _orderby = getOrderBy();
    if (!_orderby.isEmpty()) {
      sb.append(" order by ");
      sb.append(StringUtils.join(_orderby, ", "));
    }
    return sb.toString();
  }
}

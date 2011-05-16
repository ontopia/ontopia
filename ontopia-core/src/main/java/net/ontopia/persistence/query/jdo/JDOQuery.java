// $Id: JDOQuery.java,v 1.29 2005/07/12 09:37:40 grove Exp $

package net.ontopia.persistence.query.jdo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StringUtils;

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

  public JDOQuery() {
  }

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
    if (klass == null)
      throw new NullPointerException("JDO parameter class must not be null.");    
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
    if (klass == null)
      throw new NullPointerException("The class of JDO variable '" + name + "' must not be null.");
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
    if (orderby == null) orderby = new ArrayList();
    orderby.add(new JDOOrderBy(value, JDOOrderBy.ASCENDING));
  }
  
  public void addDescending(JDOValueIF value) {
    if (orderby == null) orderby = new ArrayList();
    orderby.add(new JDOOrderBy(value, JDOOrderBy.DESCENDING));
  }
  
  public void addAscending(JDOAggregateIF aggregate) {
    if (orderby == null) orderby = new ArrayList();
    orderby.add(new JDOOrderBy(aggregate, JDOOrderBy.ASCENDING));
  }
  
  public void addDescending(JDOAggregateIF aggregate) {
    if (orderby == null) orderby = new ArrayList();
    orderby.add(new JDOOrderBy(aggregate, JDOOrderBy.DESCENDING));
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("select ");
    if (getDistinct())
      sb.append("distinct ");
    if (select == null || select.isEmpty())
      sb.append("*");
    else
      StringUtils.join(select, ", ", sb);
    if (getFilter() != null) {
      sb.append(" from ");
      sb.append(getFilter());
    }
    List _orderby = getOrderBy();
    if (!_orderby.isEmpty()) {
      sb.append(" order by ");
      StringUtils.join(_orderby, ", ", sb);
    }
    return sb.toString();
  }
}

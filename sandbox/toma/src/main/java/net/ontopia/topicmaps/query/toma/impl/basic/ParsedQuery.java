/**
 * Copyright (C) 2009 Space Applications Services
 *   <thomas.neidhart@spaceapplications.com>
 *
 * This file is part of the Ontopia project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.query.toma.impl.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.impl.basic.expression.PathExpression;
import net.ontopia.topicmaps.query.toma.impl.basic.path.VariablePath;
import net.ontopia.topicmaps.query.toma.parser.ast.AbstractVariable;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.FunctionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.PathElementIF;
import net.ontopia.topicmaps.query.toma.parser.ast.QueryOrder;
import net.ontopia.topicmaps.query.toma.parser.ast.TomaQuery;
import net.ontopia.topicmaps.query.toma.parser.ast.QueryOrder.SORT_ORDER;

/**
 * PUBLIC: implementation of the {@link ParsedQueryIF} interface for a TOMA
 * query processor.
 */
public class ParsedQuery implements ParsedQueryIF {

  private TomaQuery query;
  private BasicQueryProcessor processor;

  /**
   * Create a new parsed query object.
   * 
   * @param processor the query processor to use.
   * @param query the TOMA query itself.
   */
  protected ParsedQuery(BasicQueryProcessor processor, TomaQuery query) {
    this.processor = processor;
    this.query = query;
  }

  public QueryResultIF execute() throws InvalidQueryException {
    return processor.execute(query);
  }

  public QueryResultIF execute(Map<String, ?> arguments)
      throws InvalidQueryException {
    return execute();
  }

  /**
   * PUBLIC: Returns the variables in the select clause of the first select
   * statement in the TOMA query, in the order given there. A variable can occur
   * more than once in the resulting list.
   * 
   * @return the list of variables used in the select clause of the first select
   *         expression (in case there are multiple selects joined together in a
   *         UNION style).
   */
  public List<String> getSelectedVariables() {
    List<ExpressionIF> exprs = query.getSelectExpressions();
    List<String> vars = new LinkedList<String>();
    
    for (ExpressionIF expr : exprs) {
      String name = getVariableName(expr);
      if (name != null) {
        vars.add(name);
      }
    }
    return vars;
  }

  /**
   * INTERNAL: Get the name of the variable that participates in this
   * expression.
   * 
   * @param expr the expression.
   * @return the name of the first variable, or null if there is no variable.
   */
  private String getVariableName(ExpressionIF expr) {
    if (expr instanceof PathExpression) {
      if (((PathExpression) expr).isEmpty()) {
        return null;
      } else {
        PathElementIF start = ((PathExpression) expr).getPathElement(0);
        if (start instanceof VariablePath) {
          return ((AbstractVariable) start).getVarName();
        } else {
          return null;
        }
      }
    } else {
      if (expr.getChildCount() > 0) {
        return getVariableName(expr.getChild(0));
      } else {
        return null;
      }
    }
  }

  /**
   * PUBLIC: Returns the variables in the select clause of the first select
   * statement in the TOMA query, in no particular order.
   * 
   * @return a collection of all variables used in the select clause of the
   *         first select expression (in case there are multiple selects joined
   *         together in a UNION style).
   */
  public Collection<String> getAllVariables() {
    List<ExpressionIF> exprs = query.getSelectExpressions();
    Set<String> vars = new HashSet<String>();
    
    for (ExpressionIF expr : exprs) {
      String name = getVariableName(expr);
      if (name != null) {
        vars.add(name);
      }
    }
    return vars;
  }

  /**
   * PUBLIC: Returns all variables in the select clause of the first select
   * statement in the TOMA query, which are used in an aggregate function (e.g.
   * count).
   * 
   * @return a collection of all variables in the select clause of the first
   *         select expression, which are used in an aggregate function.
   */
  public Collection<String> getCountedVariables() {
    List<ExpressionIF> exprs = query.getSelectExpressions();
    Set<String> vars = new HashSet<String>();
    
    for (ExpressionIF expr : exprs) {
      if (expr instanceof FunctionIF) {
        if (((FunctionIF) expr).isAggregateFunction()) {
          String name = getVariableName(expr);
          if (name != null) {
            vars.add(name);
          }
        }
      }
    }
    return vars;
  }

  public List<String> getOrderBy() {
    List<QueryOrder> orders = query.getOrderBy();
    ArrayList<ExpressionIF> exprs = query.getSelectExpressions();
    
    List<String> vars = new LinkedList<String>();
    for (QueryOrder o : orders) {
      int column = o.getColumn();
      
      String name = getVariableName(exprs.get(column - 1));
      if (name != null) {
        vars.add(name);
      }
    }
    return vars;
  }

  public boolean isOrderedAscending(String name) {
    List<QueryOrder> orders = query.getOrderBy();
    ArrayList<ExpressionIF> exprs = query.getSelectExpressions();
    
    for (QueryOrder o : orders) {
      int column = o.getColumn();
      
      String n = getVariableName(exprs.get(column - 1));
      if (n != null && n.equals(name)) {
        return (o.getOrder() == SORT_ORDER.ASC);
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return query.getParseTree();
  }
}

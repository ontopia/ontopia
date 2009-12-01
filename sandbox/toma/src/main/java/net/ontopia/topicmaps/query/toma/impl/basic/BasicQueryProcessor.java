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

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.DeclarationContextIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.ParsedModificationStatementIF;
import net.ontopia.topicmaps.query.core.ParsedQueryIF;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.toma.impl.basic.function.AbstractAggregateFunction;
import net.ontopia.topicmaps.query.toma.impl.utils.QueryTracer;
import net.ontopia.topicmaps.query.toma.parser.LocalParseContext;
import net.ontopia.topicmaps.query.toma.parser.TomaParser;
import net.ontopia.topicmaps.query.toma.parser.ast.ExpressionIF;
import net.ontopia.topicmaps.query.toma.parser.ast.QueryOrder;
import net.ontopia.topicmaps.query.toma.parser.ast.SelectStatement;
import net.ontopia.topicmaps.query.toma.parser.ast.TomaQuery;

/**
 * PUBLIC: QueryProcessor implementation for the TOMA query language. This is
 * the basic implementation, that should be used for topic maps stored in a
 * memory backend. For database backends, this QueryProcessor will be quite
 * slow.
 */
public class BasicQueryProcessor implements QueryProcessorIF {

  private TopicMapIF topicmap;

  /**
   * Create a new query processor, that operates on the specified topic map.
   * 
   * @param topicmap the topic map to be used by this query processor.
   */
  public BasicQueryProcessor(TopicMapIF topicmap) {
    this.topicmap = topicmap;
  }

  public int update(String query) throws InvalidQueryException {
    throw new InvalidQueryException("Not implemented yet.");
  }
  
  public int update(String query, Map<String, ?> arguments,
      DeclarationContextIF context) throws InvalidQueryException {
    throw new InvalidQueryException("Not implemented yet.");
  }

  public int update(String query, Map<String, ?> arguments)
      throws InvalidQueryException {
    throw new InvalidQueryException("Not implemented yet.");
  }

  public QueryResultIF execute(String query, Map<String, ?> arguments,
      DeclarationContextIF context) throws InvalidQueryException {
    ParsedQueryIF pq = parse(query, context);
    return pq.execute();
  }

  public QueryResultIF execute(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    return execute(query, null, context);
  }

  public QueryResultIF execute(String query, Map<String, ?> arguments)
      throws InvalidQueryException {
    return execute(query, arguments, null);
  }

  /**
   * Executes the parsed TOMA query and returns a {@link QueryResultIF} object.
   *
   * @param query the parsed TOMA query.
   * @return the result of the query.
   * @throws InvalidQueryException if the query can not be evaluated properly.
   */
  public QueryResultIF execute(TomaQuery query) throws InvalidQueryException {
    ResultSet rs = evaluate(query);
    List<Row> rows = rs.getList();
    sort(rows, query.getOrderBy());
    QueryTracer.endQuery();
    return new QueryResult(rs.getColumnDefinitions(), rows, query.getLimit(),
        query.getOffset());
  }

  /**
   * Evaluates a {@link TomaQuery} and returns a {@link ResultSet}.
   * 
   * @param query the query to be evaluated
   * @return the {@link ResultSet}.
   * @throws InvalidQueryException if the query can not be evaluated properly.
   */
  public ResultSet evaluate(TomaQuery query) throws InvalidQueryException {
    ResultSet rs = null;
    QueryTracer.startQuery();
    for (int i = 0; i < query.getStatementCount(); i++) {
      SelectStatement stmt = query.getStatement(i);

      ResultSet curr = satisfy(stmt);
      curr = aggregate(stmt, curr);

      if (rs == null) {
        rs = curr;
      } else {
        switch (stmt.getUnionType()) {
        case UNION:
          rs.union(curr, true);
          break;
        case UNIONALL:
          rs.union(curr, false);
          break;
        case INTERSECT:
          rs.intersect(curr);
          break;
        case EXCEPT:
          rs.except(curr);
          break;
        }
      }
    }
    return rs;
  }
  
  /**
   * Returns a {@link ResultSet} containing all the matches of a single select
   * statement.
   * 
   * @param stmt the select statement to evaluate.
   * @return a {@link ResultSet} containing all matches.
   * @throws InvalidQueryException if the statement could not be evaluated.
   */
  private ResultSet satisfy(SelectStatement stmt) throws InvalidQueryException {
    BasicExpressionIF expr = (BasicExpressionIF) stmt.getClause();
    LocalContext context = new LocalContext(topicmap, this);

    // set column names
    ResultSet rs = new ResultSet(stmt.getSelectCount(), stmt.isDistinct());
    for (int i = 0; i < stmt.getSelectCount(); i++) {
      BasicExpressionIF selectPath = (BasicExpressionIF) stmt.getSelect(i);
      rs.setColumnName(i, selectPath.toString());
    }

    // evaluate the WHERE expression tree
    expr.evaluate(context);

    QueryTracer.enterSelect(null);
    //Row r = rs.createRow();
    //calculateMatches(context, 0, r, rs, stmt);
    calculateMatches(context, stmt, rs);
    QueryTracer.leaveSelect(null);

    return rs;
  }

  private void calculateMatches(LocalContext context, SelectStatement stmt,
      ResultSet rs) throws InvalidQueryException {
    BasicExpressionIF firstExpr = (BasicExpressionIF) stmt.getSelect(0);
    ResultSet firstRS = firstExpr.evaluate(context);
      
    Map<Integer, Integer> fillMap = new HashMap<Integer, Integer>();
    List<Integer> evalList = new ArrayList<Integer>();
    
    for (int idx=1; idx<stmt.getSelectCount(); idx++) {
      BasicExpressionIF expr = (BasicExpressionIF) stmt.getSelect(idx);
      String exprStr = expr.toString();
      if (firstRS.containsColumn(exprStr)) {
        fillMap.put(idx, firstRS.getColumnIndex(exprStr));
      } else {
        evalList.add(idx);
      }
    }

    LocalContext newContext = null;
    try {
      newContext = (LocalContext) context.clone();
    } catch (CloneNotSupportedException e) {
      throw new InvalidQueryException("Internal QueryProcessor error:\n", e);
    }
    
    ResultSet localResult = new ResultSet(firstRS);
    newContext.addResultSet(localResult);
    
    for (Row row : firstRS) {
      Row newRow = rs.createRow();
      
      Object val = row.getLastValue();
      // rows that contain a null value in their first column have to be removed
      // completely.
      if (val == null) {
        continue;
      } else {
        newRow.setValue(0, val);
      }
      
      // now fill the row with the values that can simply copied.
      for (Map.Entry<Integer, Integer> mapEntry : fillMap.entrySet()) {
        val = row.getValue(mapEntry.getValue());
        newRow.setValue(mapEntry.getKey(), val);
      }

      // the rest of the select expressions has to be evaluated
      if (!evalList.isEmpty()) {
        localResult.removeAllRows();
        localResult.addRow(row);
      
        calculateMatches(newContext, 0, evalList, newRow, rs, stmt);
      } else {
        rs.addRow(newRow);
      }
    }
  }

  private void calculateMatches(LocalContext context, int evalIndex,
      List<Integer> evalList, Row row, ResultSet rs, SelectStatement stmt)
      throws InvalidQueryException {
    int index = evalList.get(evalIndex);
    BasicExpressionIF expr = (BasicExpressionIF) stmt.getSelect(index);
    ResultSet values = expr.evaluate(context);

    LocalContext newContext = null;
    try {
      newContext = (LocalContext) context.clone();
    } catch (CloneNotSupportedException e) {
      throw new InvalidQueryException("Internal QueryProcessor error:\n", e);
    }

    ResultSet localResult = new ResultSet(values);
    newContext.addResultSet(localResult);

    int cnt = 0;
    for (Row r : values) {
      Object val = r.getLastValue();

      // for each value, a new row in the ResultSet will be created
      Row newRow = row;

      // only clone the row if there are more than 1 value in the ResultSet
      if (++cnt > 1) {
        try {
          newRow = (Row) row.clone();
        } catch (CloneNotSupportedException e) {
          throw new InvalidQueryException("Internal QueryProcessor error:\n", e);
        }
      }

      newRow.setValue(index, val);

      // update the context with the current variable binding
      localResult.removeAllRows();
      localResult.addRow(r);

      // if we are not at the end -> recursion
      if (evalIndex < (evalList.size() - 1)) {
        calculateMatches(newContext, evalIndex + 1, evalList, newRow, rs, stmt);
      } else {
        rs.addRow(newRow);
      }
    }
  }  

//  NOTE: old code to create the select matches. This one is considerably
//  slower than the new one.
//  
//  private void calculateMatches(LocalContext context, int index, Row row,
//      ResultSet rs, SelectStatement stmt) throws InvalidQueryException {
//    BasicExpressionIF expr = (BasicExpressionIF) stmt.getSelect(index);
//    ResultSet values = expr.evaluate(context);
//    try {
//      LocalContext newContext = (LocalContext) context.clone();
//      ResultSet localResult = new ResultSet(values);
//      newContext.addResultSet(localResult);
//      
//      for (Row r : values) {
//        Object val = r.getLastValue();
//        // ignore null values in the first select clause
//        if (val == null && index == 0) {
//          continue;
//        }
//
//        // for each value, a new row in the ResultSet will be created
//        Row newRow = (Row) row.clone();
//        newRow.setValue(index, val);
//
//        // update the context with the current variable binding
//        localResult.removeAllRows();
//        localResult.addRow(r);
//
//        // if we are not at the end -> recursion
//        if (index < (stmt.getSelectCount() - 1)) {
//          calculateMatches(newContext, index + 1, newRow, rs, stmt);
//        } else {
//          rs.addRow(newRow);
//        }
//      }
//    } catch (CloneNotSupportedException e) {
//      throw new InvalidQueryException("Internal QueryProcessor error:\n", e);
//    }
//  }

  /**
   * Returns a {@link ResultSet} that has been aggregated according to the given
   * {@link SelectStatement}.
   * 
   * @param stmt the {@link SelectStatement} containing the aggregation rules.
   * @param rs the {@link ResultSet} containing the matches.
   * @return an aggregated {@link ResultSet}.
   * @throws InvalidQueryException if the {@link ResultSet} could not be aggregated.
   */
  private ResultSet aggregate(SelectStatement stmt, ResultSet rs)
      throws InvalidQueryException {
    // if the select is aggregated (i.e. contains at least one aggregation
    // function), evaluate the aggregate functions now; otherwise just return
    // the ResultSet.
    if (stmt.isAggregated()) {
      // in case all select expressions are aggregated we can simply
      // evaluate the columns separately
      if (stmt.getSelectCount() == stmt.getAggregatedSelectCount()) {
        ResultSet result = new ResultSet(rs);
        Row aggregatedRow = result.createRow();
        for (int i = 0; i < stmt.getSelectCount(); i++) {
          // we know, that it has to be a BasicFunctionIF
          BasicFunctionIF expr = (BasicFunctionIF) stmt.getSelect(i);
          aggregatedRow.setValue(i, expr.aggregate(rs.getValues(i)));
        }
        result.addRow(aggregatedRow);
        return result;
      } else {
        int selectCount = stmt.getSelectCount();
        int aggregateCount = stmt.getAggregatedSelectCount();
        
        // collect all columns that are used for grouping (i.e. not contain an
        // aggregation function)
        int groupIdx[] = new int[selectCount - aggregateCount];
        for (int i = 0, j = 0; i < stmt.getSelectCount(); i++) {
          if (!(stmt.getSelect(i) instanceof AbstractAggregateFunction)) {
            groupIdx[j++] = i;
          }
        }
        
        ResultSet groupRS = new ResultSet(selectCount - aggregateCount, false);
        HashMap<Row, ResultSet> groupingMap = new HashMap<Row, ResultSet>(); 
        
        // group together all rows based on non-aggregated columns
        for (Row r : rs) {
          Row groupRow = groupRS.createRow();
          
          int j = 0;
          for (int idx : groupIdx) {
            groupRow.setValue(j++, r.getValue(idx));
          }
          
          ResultSet tmpRS = groupingMap.get(groupRow);
          if (tmpRS == null) {
            tmpRS = new ResultSet(rs);
            groupingMap.put(groupRow, tmpRS);
          }
          
          tmpRS.addRow(r);
        }

        // finally, collect all the group ResultSet's, evaluate the
        // AggregateFunction's on them and store the result in the final ResultSet.
        ResultSet result = new ResultSet(rs);
        for (ResultSet tmpRS : groupingMap.values()) {
          Row tmpRow = tmpRS.iterator().next();
          Row aggregatedRow = result.createRow();
          for (int i = 0; i < stmt.getSelectCount(); i++) {
            if (stmt.getSelect(i) instanceof AbstractAggregateFunction) {
              BasicFunctionIF expr = (BasicFunctionIF) stmt.getSelect(i);
              aggregatedRow.setValue(i, expr.aggregate(tmpRS.getValues(i)));
            } else {
              aggregatedRow.setValue(i, tmpRow.getValue(i));
            }
          }
          result.addRow(aggregatedRow);
        }
        return result;
      }
    } else {
      return rs;
    }
  }

  /**
   * Sort the matches according to the "order-by" definitions.
   * 
   * @param matches the query matches.
   * @param orderings the order-by definitions of the query.
   */
  private void sort(List<Row> matches, List<QueryOrder> orderings) {
    QueryTracer.enterOrderBy();
    if (!orderings.isEmpty())
      Collections.sort(matches, new RowComparator(orderings));
    QueryTracer.leaveOrderBy();
  }

  public QueryResultIF execute(String query) throws InvalidQueryException {
    return execute(query, null, null);
  }

  /**
   * Not supported, throws an {@link InvalidQueryException} if called.
   */
  public void load(Reader ruleset) throws InvalidQueryException, IOException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }

  /**
   * Not supported, throws an {@link InvalidQueryException} if called.
   */
  public void load(String ruleset) throws InvalidQueryException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }

  public ParsedQueryIF parse(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    ExpressionFactory ef = new ExpressionFactory();
    PathExpressionFactory pef = new PathExpressionFactory();
    LocalParseContext lc = new LocalParseContext(pef, ef);
    
    TomaQuery toma = TomaParser.parse(query, lc);
    
    // optimize query
    QueryOptimizer optimizer = new QueryOptimizer();
    for (int i = 0; i < toma.getStatementCount(); i++) {
      SelectStatement stmt = toma.getStatement(i);
      ExpressionIF whereClause = stmt.getClause();
      stmt.setClause(whereClause.optimize(optimizer));
    }

    return new ParsedQuery(this, toma);
  }

  public ParsedQueryIF parse(String query) throws InvalidQueryException {
    return parse(query, null);
  }

  /**
   * Not supported, throws an {@link InvalidQueryException} if called.
   */
  public ParsedModificationStatementIF parseUpdate(String statement,
      DeclarationContextIF context) throws InvalidQueryException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }

  /**
   * Not supported, throws an {@link InvalidQueryException} if called.
   */
  public ParsedModificationStatementIF parseUpdate(String statement)
      throws InvalidQueryException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }

  /**
   * Not supported, throws an {@link InvalidQueryException} if called.
   */
  public int update(String query, DeclarationContextIF context)
      throws InvalidQueryException {
    throw new InvalidQueryException("Not supported by this QueryProcessor");
  }
}

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

package net.ontopia.persistence.query.sql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * INTERNAL: SQL optimizer that removes table- and column references
 * that are redundant.
 */

public class RedundantTablesSQLOptimizer extends FilterSQLOptimizer {

  // RULE: 'select B.id from ... where A.id = B.id (A and B same table
  // and no other expressions referencing B' => replace B.id with A.id

  // 1. build data structures (analyze)
  //    TC = {t : (c1, cN)}
  //    TV = (t, tN); tables that are referenced by verbatim values/expressions
  //    CE = {c : (e1, eN)}
  // 2. figure out which tables are elegible and how to replace them
  // 3. loop over query and replace table references (filter)

  protected Map tcmap = new HashMap(10);
  protected Map cemap = new HashMap(10);
  protected Set tvset = new HashSet(3);

  protected Map rcmap = new HashMap();
  protected Map rtmap = new HashMap();

  @Override
  public SQLQuery optimize(SQLQuery query) {
    // analyze query
    Analyzer analyzer = new Analyzer();
    analyzer.analyze(query);

    //! System.out.println("TC: " + tcmap);
    //! System.out.println("TV: " + tvset);
    //! System.out.println("CE: " + cemap);

    // figure out which tables to replace
    Iterator iter = tcmap.keySet().iterator();
    while (iter.hasNext()) {
      SQLTable tbl = (SQLTable)iter.next();
      Set cols = (Set)tcmap.get(tbl);
      // table is eligible if single column reference and not part of
      // verbatim expression
      if (cols.size() == 1 && !tvset.contains(tbl)) {
	//! System.out.println("Table " + tbl + " eligible for removal.");	

	// find equals expression
	SQLColumns col1 = (SQLColumns)cols.iterator().next();
	
	Set ce = (Set)cemap.get(col1);
	if (ce != null) {

	  Iterator iter2 = ce.iterator();
	  while (iter2.hasNext()) {
	    SQLColumns col2 = (SQLColumns)iter2.next();
	    if (rcmap.containsKey(col2)) { // TODO: may need more steps here
	      col2 = (SQLColumns)rcmap.get(col2);
	    }
	    if (!rcmap.containsKey(col1)) {
	      //! System.out.println("Found " + col1 + " -> " + col2);	
	      rcmap.put(col1, col2);
	      rtmap.put(col1.getTable(), col2.getTable());
	    }
	  }
	}
      }
    }
    
    //! System.out.println("RTMAP: " + rtmap);	
    //! System.out.println("RCMAP: " + rcmap);	

    // optimize query
    super.optimize(query);

    return query;    
  }

  protected void addTableVerbatim(SQLTable tbl) {
    tvset.add(tbl);
  }

  protected void addTableColumns(SQLExpressionIF expr, SQLColumns cols) {
    // update tcmap
    addEntry(tcmap, cols.getTable(), cols);

    // update cemap
    if (expr != null && expr instanceof SQLEquals) {
      SQLEquals eq = (SQLEquals)expr;
      Object left = eq.getLeft();
      Object right = eq.getRight();
      
      if (left.equals(cols)) {
	if (!right.equals(cols) && right instanceof SQLColumns) {
    addEntry(cemap, cols, right);
  }
      } else if (left instanceof SQLColumns) {
	addEntry(cemap, cols, left);
      }
    }
  }

  protected void addEntry(Map map, Object key, Object value) {
    Set vals = (Set)map.get(key);
    if (vals == null) {
      vals = new HashSet(3);
      map.put(key, vals);
    }
    vals.add(value);
  }

  /* --- analysis -- */

  class Analyzer extends AbstractSQLAnalyzer {

    @Override
    protected void analyzeVerbatim(SQLExpressionIF expr, SQLVerbatim value) {
      SQLTable[] tables = value.getTables();
      for (int i=0; i < tables.length; i++) {
	addTableVerbatim(tables[i]);
      }
    }
    
    @Override
    protected void analyzeVerbatimExpression(SQLVerbatimExpression expr) {
      SQLTable[] tables = expr.getTables();
      for (int i=0; i < tables.length; i++) {
	addTableVerbatim(tables[i]);
      }
    }
    
    @Override
    protected void analyzeColumns(SQLExpressionIF expr, SQLColumns value) {
      addTableColumns(expr, value);
    }
  }

  /* --- optimization -- */

  @Override
  protected SQLValueIF filterValue(SQLValueIF value) {
    // ISSUE: should possibly prefer values in select clause, so that
    // the same column aliases can be kept

    // delegate to super class
    super.filterValue(value);
    // process column
    if (value.getType() == SQLValueIF.COLUMNS) {
      SQLColumns col1 = (SQLColumns)value;
      Object o = rcmap.get(col1);
      if (o != null) {
	SQLColumns col2 = (SQLColumns)o;
	// value types could be different
	if (!col1.getValueType().equals(col2.getValueType())) {
	  SQLColumns coln = new SQLColumns(col2.getTable(), col2.getColumns());
	  // keep alias
	  coln.setAlias((col2.getAlias() == null ? col1.getAlias() : col2.getAlias()));
	  // keep value type and field handler
	  coln.setValueType(col1.getValueType());
	  coln.setFieldHandler(col1.getFieldHandler());
	  return coln;
	} else {
	  // keep alias
	  if (col2.getAlias() == null) {
      col2.setAlias(col1.getAlias());
    }
	  return col2;
	}
      }
    }
    return value;

  }

}

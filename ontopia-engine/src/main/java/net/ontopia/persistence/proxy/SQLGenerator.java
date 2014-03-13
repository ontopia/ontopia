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

package net.ontopia.persistence.proxy;

import java.util.Collection;
import java.util.Iterator;

import net.ontopia.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Utility class that can generate SQL statements.
 */

public class SQLGenerator {

  // Define a logging category.
  static Logger log = LoggerFactory.getLogger(SQLGenerator.class.getName());
  
  // -----------------------------------------------------------------------------
  // Statement generators
  // -----------------------------------------------------------------------------

  /**
   * INTERNAL: Generates a SQL delete statement for the specified table
   * with a where clause referencing the given field infos.
   */
  public static String getDeleteStatement(String table, FieldInfoIF[] where_fields) {
    // Compute table columns
    String[] where_columns = FieldUtils.getColumns(where_fields);
    return getDeleteStatement(table, where_columns); 
  }
  
  /**
   * INTERNAL: Generates a SQL delete statement for the specified table
   * with a where clause referencing the given columns.
   */
  public static String getDeleteStatement(String table, String[] where_columns) {
    // Master table insert
    StringBuilder sb = new StringBuilder();
    sb.append("delete from ");
    sb.append(table);
    sb.append(" where ");
    for (int i=0; i < where_columns.length; i++) {
      if (i > 0) sb.append(" and ");
      sb.append(where_columns[i]);
      sb.append(" = ?");
    }
    return sb.toString();
  }
  
  /**
   * INTERNAL: Generates a SQL insert statement for the specified
   * table with a value clause referencing the given field infos.
   */
  public static String getInsertStatement(String table, FieldInfoIF[] value_fields) {
    // Compute table columns
    String[] value_columns = FieldUtils.getColumns(value_fields);
    return getInsertStatement(table, value_columns); 
  }
  
  /**
   * INTERNAL: Generates a SQL insert statement for the specified
   * table with a value clause referencing the given columns.
   */
  public static String getInsertStatement(String table, String[] value_columns) {
    // Master table insert
    StringBuilder sb = new StringBuilder();
    sb.append("insert into ");
    sb.append(table);
    sb.append(" (");
    for (int i=0; i < value_columns.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(value_columns[i]);
    }
    sb.append(") values (");
    for (int i=0; i < value_columns.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append("?");
    }
    sb.append(")");
    return sb.toString();
  }
  
  /**
   * INTERNAL: Generates a SQL update statement for the specified
   * table with a set clause and a where clause referencing the given
   * field infos.
   */
  public static String getUpdateStatement(String table, FieldInfoIF[] set_fields, FieldInfoIF[] where_fields) {
    // Compute table columns
    String[] set_columns = FieldUtils.getColumns(set_fields);
    String[] where_columns = FieldUtils.getColumns(where_fields);
    return getUpdateStatement(table, set_columns, where_columns); 
  }

  /**
   * INTERNAL: Generates a SQL update statement for the specified
   * table with a set clause and a where clause referencing the given
   * columns.
   */
  public static String getUpdateStatement(String table, String[] set_columns, String[] where_columns) {
    // Master table select
    StringBuilder sb = new StringBuilder();
    sb.append("update ");
    sb.append(table);
    sb.append(" set ");
    
    for (int i=0; i < set_columns.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(set_columns[i]);
      sb.append(" = ?");
    }

    if (where_columns.length > 0)
      sb.append(" where ");
    
    for (int i=0; i < where_columns.length; i++) {
      if (i > 0) sb.append(", ");
      sb.append(where_columns[i]);
      sb.append(" = ?");
    }
    return sb.toString();
  }
  
  /**
   * INTERNAL: Generates a SQL select statement for the specified
   * table with a select clause and a where clause referencing the
   * given field infos.
   */
  public static String getSelectStatement(String table, FieldInfoIF[] select_fields, 
					  FieldInfoIF[] where_fields, int multiple) {
    // Compute table columns
    String[] select_columns = FieldUtils.getColumns(select_fields);
    String[] where_columns = FieldUtils.getColumns(where_fields);
    return getSelectStatement(table, select_columns, where_columns, multiple); 
  }

  /**
   * INTERNAL: Generates a SQL select statement for the specified
   * table with a select clause and a where clause referencing the
   * given columns.
   */
  public static String getSelectStatement(String table, String[] select_columns, 
					  String[] where_columns, int multiple) {
    // Master table select
    StringBuilder sb = new StringBuilder();
    sb.append("select ");
    
    sb.append(StringUtils.join(select_columns, ", "));
    
    sb.append(" from ");
    sb.append(table);
    sb.append(" where ");
    if (multiple > 0) {
      if (where_columns.length > 1) throw new RuntimeException("Multiple conditions not allowed.");
      sb.append(where_columns[0]);
      sb.append(" in (");
      for (int i=0; i < multiple; i++) {
	if (i > 0 )
	  sb.append(", ?");
	else
	  sb.append('?');
      }
      sb.append(')');
      // WARNING: we are not filtering on 'S.col is not null' here
      // because a null element can never match the where column. this
      // is because null is not equal to itself.
    } else {
      sb.append(StringUtils.join(where_columns, " = ? "));
      sb.append(" = ?");
    }
    return sb.toString();
  }

  // scoped_id - 1:M key
  // data-joinkey t1.id -> t2.theme_id
  
  /**
   * INTERNAL: Generates a SQL select statement that joins two tables.
   */
  public static String getSelectStatement(String jointable, String datatable,
                                          String[] jointable_keys, String[] datatable_keys,
                                          String[] datatable_select_columns, String[] jointable_where_columns,
					  int multiple) {
    
    // select t1.id, t1.subject_address, t1.subject_notation, t1.topicmap_id
    //   from TM_TOPIC_SCOPE t1, TM_TOPIC t2
    //   where t1.id = t2.theme_id and t1.scoped_id = ?;

    StringBuilder sb = new StringBuilder();
    
    // select columns
    sb.append("select ");
    if (multiple > 0) {
      // must include where key when selecting multiple
      for (int i=0; i < jointable_where_columns.length; i++) {
	if (i > 0) sb.append(", ");
	sb.append("a.");
	sb.append(jointable_where_columns[i]);
	sb.append(" as a").append(i);
      }
      for (int i=0; i < datatable_select_columns.length; i++) {
	sb.append(", b.");
	sb.append(datatable_select_columns[i]);
	sb.append(" as b").append(i);
      }
    } else {
      for (int i=0; i < datatable_select_columns.length; i++) {
	if (i > 0) sb.append(", ");
	sb.append("b.");
	sb.append(datatable_select_columns[i]);
      }
    }

    // select tables
    sb.append(" from ");
    sb.append(jointable);
    sb.append(" a, ");
    sb.append(datatable);
    sb.append(" b");

    // join conditions
    sb.append(" where ");
    for (int i=0; i < jointable_keys.length; i++) {
      if (i > 0) sb.append(" AND ");
      sb.append("a.");
      sb.append(jointable_keys[i]);
      sb.append(" = b.");
      sb.append(datatable_keys[i]);
    }

    // where condition
    for (int i=0; i < jointable_where_columns.length; i++) {
      sb.append(" and a.");
      sb.append(jointable_where_columns[i]);
      if (multiple > 0) {
	sb.append(" in (");
	for (int x=0; x < multiple; x++) {
	  if (x > 0)
	    sb.append(", ?");
	  else
	    sb.append('?');
	}
	sb.append(')');
      } else
	sb.append(" = ?");
    }
    return sb.toString();    
  }

  public static String processMultipleLoadParameters(Collection identities, String sql) {
    int lix = sql.lastIndexOf('?');
    StringBuilder sb = new StringBuilder();
    sb.append(sql.substring(0, lix));

    int size = identities.size();
    Iterator iter = identities.iterator();
    for (int i=0; i < size; i++) {
      if (i > 0) sb.append(", ");
      IdentityIF identity = (IdentityIF)iter.next();
      // HACK: this assumes that arity of identity field is 1
      sb.append(identity.getKey(0));
    }

    sb.append(sql.substring(lix+1));
    return sb.toString();
  }

  public static void bindMultipleParameters(Iterator identities, FieldInfoIF finfo, 
					    java.sql.PreparedStatement stm, int batchSize) 
    throws java.sql.SQLException {
    bindMultipleParameters(identities, finfo, stm, 1, batchSize);
  }

  public static void bindMultipleParameters(Iterator identities, FieldInfoIF finfo, 
					    java.sql.PreparedStatement stm, int offset, int batchSize) 
    throws java.sql.SQLException {
    // bind parameters until iterator exhausted or batchSize reached
    int w = finfo.getColumnCount();
    int x = 0;
    for (; (x/w) < batchSize && identities.hasNext();) {
      IdentityIF identity = (IdentityIF)identities.next();
      finfo.bind(identity, stm, offset + x);
      x += w;
    }
    // if iterator exhausted fill remaining parameters with nulls
    if (!identities.hasNext()) {
      for (; (x/w) < batchSize;) {
	finfo.bind(null, stm, offset + x);
	x += w;      
      }
    }
  }

}





